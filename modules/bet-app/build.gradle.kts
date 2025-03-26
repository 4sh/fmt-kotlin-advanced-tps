plugins {
    // Apply the shared build logic from a convention plugin.
    // The shared code is located in `buildSrc/src/main/kotlin/kotlin-jvm.gradle.kts`.
    id("buildsrc.convention.kotlin-jvm")

    // Apply the Application plugin to add support for building an executable JVM application.
    application
    alias(libs.plugins.ktor)

}

dependencies {
    implementation(libs.mongodb.driver.kotlin.coroutine)


    implementation("ch.qos.logback:logback-classic:1.5.12")
    implementation("org.litote.kmongo:kmongo-serialization:4.9.0")
    implementation("org.litote.kmongo:kmongo-coroutine-serialization:4.9.0")

    implementation(libs.kotlinxDatetime)

    implementation("io.ktor:ktor-client-apache")
    implementation("io.ktor:ktor-client-content-negotiation")
    implementation("io.ktor:ktor-server-core-jvm")
    implementation("io.ktor:ktor-server-content-negotiation")
    implementation("io.ktor:ktor-serialization-kotlinx-json")
    implementation("io.ktor:ktor-server-netty-jvm")
}

application {
    // Define the Fully Qualified Name for the application main class
    // (Note that Kotlin compiles `App.kt` to a class with FQN `com.example.app.AppKt`.)
    mainClass = "fmt.kotlin.advanced.http.BetAppKt"
}
