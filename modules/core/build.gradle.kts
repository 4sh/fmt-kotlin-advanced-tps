plugins {
    kotlin("jvm") version "2.0.21"
    id("com.google.devtools.ksp") version "2.0.20-1.0.25"
}

dependencies {
    implementation(project(":modules:annotation-variance"))
    ksp(project(":modules:ksp-variance-processor"))

    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.6.1")
    testImplementation("io.kotest:kotest-assertions-core-jvm:5.9.1")
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}