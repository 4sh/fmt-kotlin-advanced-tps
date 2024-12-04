plugins {
    alias(libs.plugins.kotlin.jvm)
}

dependencies {
    implementation(project(":modules:annotation-variance"))
    implementation(libs.ksp)
    implementation(libs.kotlinPoet)
    implementation(libs.kotlinPoet.ksp)
}

kotlin {
    jvmToolchain(21)
}
