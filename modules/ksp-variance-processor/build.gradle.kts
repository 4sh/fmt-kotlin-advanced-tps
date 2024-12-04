plugins {
    kotlin("jvm")
}

dependencies {
    implementation(project(":modules:annotation-variance"))
    implementation("com.google.devtools.ksp:symbol-processing-api:2.3.9")
    implementation("com.squareup:kotlinpoet:2.3.0")
    implementation("com.squareup:kotlinpoet-ksp:2.3.0")
}

kotlin {
    jvmToolchain(21)
}
