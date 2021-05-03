plugins {
    kotlin("jvm") version "1.4.21"
}

val jserverVersion = "1.0.2"

dependencies {
    implementation(kotlin("stdlib"))
    implementation("com.github.gr3gdev:jserver-security:$jserverVersion")
}
