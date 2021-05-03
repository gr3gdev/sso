plugins {
    kotlin("jvm") version "1.4.21"
}

val hikariCpVersion = "4.0.3"
val postgresqlVersion = "42.1.4"

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("org.postgresql:postgresql:$postgresqlVersion")
    implementation("com.zaxxer:HikariCP:$hikariCpVersion")
    implementation(project(":sso-beans"))
}
