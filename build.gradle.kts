plugins {
    kotlin("jvm") version "1.4.21"
    application
    id("gregdev.gradle.docker") version "1.0.0"
}

group = "com.github.gr3gdev"
version = "0.1.0"

repositories {
    maven {
        url = uri("https://maven.pkg.github.com/gr3gdev/jserver")
        credentials {
            username = System.getProperty("GITHUB_USERNAME") as String?
            password = System.getProperty("GITHUB_TOKEN") as String?
        }
    }
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.12.0")
    implementation("com.github.gr3gdev:jserver-core:0.3.0")
    implementation("com.github.gr3gdev:jserver-security:0.3.0")
    implementation("org.postgresql:postgresql:42.1.4")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(15))
    }
}

application {
    mainClass.set("com.github.gr3gdev.sso.SSOServerKt")
}

docker {
    imageName = "gr3gdev/mysso"
    platforms = "linux/amd64,linux/arm64,linux/arm/v6,linux/arm/v7"
    dependsOn = "installDist"
    args = listOf("-p", "3000:3000")
}

tasks.withType(org.jetbrains.kotlin.gradle.tasks.KotlinCompile::class.java).all {
    sourceCompatibility = "11"
    targetCompatibility = "11"
    kotlinOptions {
        jvmTarget = "11"
    }
}

tasks {
    compileKotlin {
        copy {
            from("build/resources/main")
            into("build/classes/kotlin/main")
        }
    }
}