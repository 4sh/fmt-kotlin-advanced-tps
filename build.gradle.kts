plugins {
    alias(libs.plugins.kotlin.jvm)
}

group = "fmt.kotlin.advanced"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.kotlinx.datetime)
    implementation(libs.kotlinx.coroutines)
    testImplementation(libs.kotest.assertions)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(21)

    compilerOptions {
        freeCompilerArgs.add("-Xreturn-value-checker=full")
    }
}

// region Stacktrace recovery

val coroutinesDebugAgentJar by lazy {
    configurations.detachedConfiguration(
        project.dependencies.create(libs.kotlinx.coroutines.debug.get())
    ).apply {
        isTransitive = false
    }.singleFile
}

tasks.withType(Test::class.java) {
    jvmArgumentProviders.add(object : CommandLineArgumentProvider {
        @get:Classpath
        val agentClasspath = project.files(coroutinesDebugAgentJar)

        override fun asArguments() = listOf("-javaagent:${coroutinesDebugAgentJar.absolutePath}")
    })

    systemProperty("kotlinx.coroutines.debug.enable.creation.stack.trace", "true")
}

// endregion
