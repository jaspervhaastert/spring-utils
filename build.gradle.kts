plugins {
    kotlin("jvm") version "1.4.10"
    kotlin("plugin.spring") version "1.4.10"
    id("maven-publish")
}

group = "nl.jvhaastert"
version = "1.2.0"
java.sourceCompatibility = JavaVersion.VERSION_14

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("org.springframework:spring-context:5.3.1")
    implementation("org.springframework:spring-web:5.3.1")
    implementation("org.springframework.boot:spring-boot:2.4.0")
    implementation("org.springframework.data:spring-data-commons:2.4.2")
    implementation("org.springframework.security:spring-security-config:5.4.1")
    implementation("org.springframework.security:spring-security-web:5.4.1")
    implementation("org.apache.tomcat.embed:tomcat-embed-core:9.0.39")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.11.3")
    implementation("jakarta.validation:jakarta.validation-api:2.0.2")
    implementation("org.hibernate.validator:hibernate-validator:6.1.6.Final")
    implementation("com.squareup:kotlinpoet:1.7.2")
    implementation("io.jsonwebtoken:jjwt-api:0.11.2")

    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.11.2")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.11.2")

    testImplementation(platform("org.junit:junit-bom:5.7.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

java {
    withJavadocJar()
    withSourcesJar()
}

publishing {
    publications {
        create<MavenPublication>("java") {
            pom {
                name.set("Spring utils")
                url.set("https://github.com/jaspervhaastert/spring-utils")

                developers {
                    developer {
                        name.set("Jasper van Haastert")
                    }
                }

                scm {
                    url.set("https://github.com/jaspervhaastert/spring-utils")
                }

                licenses {
                    license {
                        name.set("MIT License")
                        url.set("https://spdx.org/licenses/MIT.html")
                    }
                }
            }

            from(components["java"])
        }
    }
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/jaspervhaastert/spring-utils")
            credentials {
                username = project.findProperty("github-packages.username") as String?
                password = project.findProperty("github-packages.key") as String?
            }
        }
    }
}

tasks.compileKotlin {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "14"
    }
}

tasks.test {
    useJUnitPlatform()
}
