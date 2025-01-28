plugins {
    `java-library`
    `maven-publish`
}

rootProject.version = "1.4.3"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.yaml", "snakeyaml", "2.2")

    implementation("ch.jalu", "typeresolver", "0.1.0")

    compileOnly("org.jetbrains", "annotations", "24.0.1")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))

    withSourcesJar()
    withJavadocJar()
}

val javaComponent: SoftwareComponent = components["java"]

tasks {
    publishing {
        publications {
            create<MavenPublication>("maven") {
                groupId = "ch.jalu"
                artifactId = "configme"

                from(javaComponent)
            }
        }

        repositories {
            maven {
                url = uri("https://repo.crazycrew.us/releases")

                credentials {
                    this.username = System.getenv("gradle_username")
                    this.password = System.getenv("gradle_password")
                }
            }
        }
    }

    compileJava {
        options.encoding = Charsets.UTF_8.name()
        options.release.set(21)
    }

    javadoc {
        options.encoding = Charsets.UTF_8.name()
    }

    processResources {
        filteringCharset = Charsets.UTF_8.name()
    }
}
