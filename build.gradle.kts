plugins {
    kotlin("jvm") version "2.0.21"
}

group = "fmt.kotlin.advanced"
version = "1.0-SNAPSHOT"

allprojects {
    repositories {
        mavenCentral()
    }
}

dependencies {

}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}