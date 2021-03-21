import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val zircon_version: String by project
val slf4j_version: String by project
val junit_version: String by project
val mockito_version: String by project
val assertj_version: String by project
val jackson_version: String by project
val mavenCentralRepositoryUsername: String by project
val mavenCentralRepositoryPassword: String by project

plugins {
    kotlin("jvm") version "1.4.31"
    id("org.jetbrains.dokka") version "1.4.30"
    id("com.github.johnrengelman.shadow") version "5.2.0"
    id("com.vanniktech.maven.publish") version "0.13.0"
}

repositories {
    mavenLocal()
    mavenCentral()
    jcenter()
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

dependencies {
    implementation("org.slf4j:slf4j-api:$slf4j_version")
    testImplementation("org.slf4j:slf4j-simple:$slf4j_version")

    api("org.hexworks.zircon:zircon.core-jvm:$zircon_version")
    implementation("org.hexworks.zircon:zircon.jvm.swing:$zircon_version")

    implementation("com.fasterxml.jackson.core:jackson-core:$jackson_version")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:$jackson_version")

    testImplementation("junit:junit:$junit_version")
    testImplementation("org.mockito:mockito-all:$mockito_version")
    testImplementation("org.assertj:assertj-core:$assertj_version")
}

tasks {
    named<ShadowJar>("shadowJar") {
        archiveBaseName.set("shadow")
        mergeServiceFiles()
        manifest {
            attributes(mapOf("Main-Class" to "com.example.MainKt"))
        }
    }
}

tasks {
    build {
        dependsOn(shadowJar)
    }
}

val jar by tasks.getting(Jar::class) {
    manifest {
        attributes["Main-Class"] = "com.example.MainKt"
    }
}


val compileKotlin: KotlinCompile by tasks

compileKotlin.kotlinOptions {
    freeCompilerArgs = listOf("-Xinline-classes")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

// Based on https://github.com/vanniktech/gradle-maven-publish-plugin/blob/master/README.md
// and https://github.com/vanniktech/gradle-maven-publish-plugin/issues/206#issuecomment-791152220
publishing {
    repositories {
        withType<MavenArtifactRepository> {
            if (name == "local") return@withType

            System.err.println("Version: $version")
            val releasesRepoUrl = "https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/"
            val snapshotsRepoUrl = "https://s01.oss.sonatype.org/content/repositories/snapshots/"
            val url = if (version.toString().endsWith("SNAPSHOT")) snapshotsRepoUrl else releasesRepoUrl
            this.url = uri(url.also { System.err.println("Using url: $url") })

            credentials {
                username = mavenCentralRepositoryUsername
                password = mavenCentralRepositoryPassword
            }
        }
    }
}

mavenPublish {
    nexus {
        baseUrl = "https://s01.oss.sonatype.org/service/local/"
        repositoryUsername = mavenCentralRepositoryUsername
        repositoryPassword = mavenCentralRepositoryPassword
    }
}
