plugins {
    kotlin("jvm") version "1.4.21"
    application
}

val jserverVersion = "1.0.2"
val jacksonVersion = "2.12.0"

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:$jacksonVersion")
    implementation("com.github.gr3gdev:jserver-core:$jserverVersion")
    implementation("com.github.gr3gdev:jserver-security:$jserverVersion")
    implementation("com.github.gr3gdev:jserver-thymeleaf:$jserverVersion")
    implementation(project(":sso-beans"))
    implementation(project(":sso-http-beans"))
    implementation(project(":sso-dao"))
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(15))
    }
}

tasks.withType(org.jetbrains.kotlin.gradle.tasks.KotlinCompile::class.java).all {
    sourceCompatibility = "11"
    targetCompatibility = "11"
    kotlinOptions {
        jvmTarget = "11"
    }
}

application {
    mainClass.set("com.github.gr3gdev.sso.SSOServerKt")
}
