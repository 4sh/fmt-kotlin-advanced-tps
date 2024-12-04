plugins {
    kotlin("jvm")
    id("com.google.devtools.ksp") version "2.3.9"
}

dependencies {
    implementation(project(":modules:annotation-variance"))
    ksp(project(":modules:ksp-variance-processor"))

    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.8.0")
    testImplementation("io.kotest:kotest-assertions-core-jvm:6.1.11")
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(21)
}
