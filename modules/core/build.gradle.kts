plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.ksp)
}

dependencies {
    implementation(project(":modules:annotation-variance"))
    ksp(project(":modules:ksp-variance-processor"))

    implementation(libs.kotlinx.datetime)
    testImplementation(libs.kotest.assertions)
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(21)
}
