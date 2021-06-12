import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.5.10"
    kotlin("plugin.serialization") version "1.5.10"
}

group = "me.manoharprabhu"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    // implementation("org.junit.jupiter:junit-jupiter:5.7.0")
    implementation("junit:junit:4.12")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-cbor:1.2.1")
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnit()
}

tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "1.8"
}