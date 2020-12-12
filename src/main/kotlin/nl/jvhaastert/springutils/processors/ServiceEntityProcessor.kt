package nl.jvhaastert.springutils.processors

import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import nl.jvhaastert.springutils.annotations.ServiceEntity
import nl.jvhaastert.springutils.services.EntityService
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Service
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.SourceVersion
import javax.lang.model.element.TypeElement

class ServiceEntityProcessor : AbstractProcessor() {

    override fun getSupportedAnnotationTypes(): Set<String> = setOf(ServiceEntity::class.java.name)
    override fun getSupportedSourceVersion(): SourceVersion = SourceVersion.latestSupported()

    override fun process(annotations: Set<TypeElement>, roundEnv: RoundEnvironment): Boolean {
        annotations.forEach { processAnnotation(it, roundEnv) }
        return true
    }

    private fun processAnnotation(annotation: TypeElement, roundEnv: RoundEnvironment) {
        for (annotatedElement in roundEnv.getElementsAnnotatedWith(annotation)) {
            val entityPackageName = processingEnv.elementUtils.getPackageOf(annotatedElement).qualifiedName.toString()
            val entityName = annotatedElement.simpleName.toString()

            generateService(entityPackageName, entityName)
        }
    }

    private fun generateService(entityPackageName: String, entityName: String) {
        val basePackageName = entityPackageName.substringBeforeLast('.')
        val servicePackageName = "${basePackageName}.services"
        val className = ClassName(servicePackageName, "${entityName}Service")

        val entityClassName = ClassName(entityPackageName, entityName)
        val repositoryTypeName = CrudRepository::class.asTypeName().parameterizedBy(entityClassName, LONG)
        val serviceTypeName = EntityService::class.asTypeName().parameterizedBy(entityClassName, repositoryTypeName)

        val repositoryParameterSpec = ParameterSpec
            .builder("repository", repositoryTypeName)
            .build()

        val constructorSpec = FunSpec
            .constructorBuilder()
            .addParameter(repositoryParameterSpec)
            .build()

        val classTypeSpec = TypeSpec
            .classBuilder(className)
            .superclass(serviceTypeName)
            .primaryConstructor(constructorSpec)
            .addSuperclassConstructorParameter("%N", repositoryParameterSpec)
            .addAnnotation(Service::class)
            .build()

        val fileSpec = FileSpec
            .builder(servicePackageName, className.simpleName)
            .addType(classTypeSpec)
            .build()
        fileSpec.writeTo(processingEnv.filer)
    }

}
