group = "com.github.gr3gdev"
version = "0.1.0"

allprojects {
    repositories {
        maven {
            url = uri("https://maven.pkg.github.com/gr3gdev/jserver")
            credentials {
                username = System.getProperty("GITHUB_USERNAME")
                password = System.getProperty("GITHUB_TOKEN")
            }
        }
        maven {
            url = uri("https://maven.pkg.github.com/gr3gdev/jdbc-framework")
            credentials {
                username = System.getProperty("GITHUB_USERNAME")
                password = System.getProperty("GITHUB_TOKEN")
            }
        }
        mavenCentral()
    }
}
