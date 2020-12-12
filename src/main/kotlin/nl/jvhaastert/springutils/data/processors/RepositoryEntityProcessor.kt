package nl.jvhaastert.springutils.data.processors

import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import nl.jvhaastert.springutils.data.annotations.RepositoryEntity
import org.springframework.data.repository.CrudRepository
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.SourceVersion
import javax.lang.model.element.TypeElement

class RepositoryEntityProcessor : AbstractProcessor() {

    override fun getSupportedAnnotationTypes(): Set<String> = setOf(RepositoryEntity::class.java.name)
    override fun getSupportedSourceVersion(): SourceVersion = SourceVersion.latestSupported()

    override fun process(annotations: Set<TypeElement>, roundEnv: RoundEnvironment): Boolean {
        annotations.forEach { processAnnotation(it, roundEnv) }
        return true
    }

    private fun processAnnotation(annotation: TypeElement, roundEnv: RoundEnvironment) {
        for (annotatedElement in roundEnv.getElementsAnnotatedWith(annotation)) {
            val entityPackageName = processingEnv.elementUtils.getPackageOf(annotatedElement).qualifiedName.toString()
            val entityName = annotatedElement.simpleName.toString()

            generateRepository(entityPackageName, entityName)
        }
    }

    private fun generateRepository(entityPackageName: String, entityName: String) {
        val basePackageName = entityPackageName.substringBeforeLast('.')
        val repositoryPackageName = "${basePackageName}.repositories"
        val className = ClassName(repositoryPackageName, "${entityName}Repository")

        val entityClassName = ClassName(entityPackageName, entityName)
        val jpaRepositoryTypeName = CrudRepository::class.asTypeName().parameterizedBy(entityClassName, LONG)

        val interfaceTypeSpec = TypeSpec
            .interfaceBuilder(className)
            .addSuperinterface(jpaRepositoryTypeName)
            .build()

        val fileSpec = FileSpec
            .builder(repositoryPackageName, className.simpleName)
            .addType(interfaceTypeSpec)
            .build()
        fileSpec.writeTo(processingEnv.filer)
    }

}
