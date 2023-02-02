plugins {
    kotlin("jvm") version "1.8.0"
    id("io.gitlab.arturbosch.detekt") version "1.22.0"
}

group = "ru.spbstu"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.ithersta.tgbotapi:fsm:0.22.0")
    implementation("com.ithersta.tgbotapi:sqlite-persistence:0.3.0")
    implementation("com.ithersta.tgbotapi:commands:0.1.0")
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(8)
}
