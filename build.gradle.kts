plugins {
    kotlin("jvm") version "1.8.10"
    kotlin("plugin.serialization") version "1.8.10"
    id("com.google.devtools.ksp") version "1.8.10-1.0.9"
    id("io.gitlab.arturbosch.detekt") version "1.22.0"
    application
}

group = "ru.spbstu"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://repo.repsy.io/mvn/ithersta/tgbotapi")
}

val exposedVersion = "0.41.1"
dependencies {
    implementation("com.ithersta.tgbotapi:boot:0.1.6")
    implementation("com.ithersta.tgbotapi:commands:0.3.0")
    implementation("io.ktor:ktor-client-okhttp:2.2.3")
    implementation("org.jetbrains.exposed:exposed-core:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-kotlin-datetime:$exposedVersion")
    implementation("com.h2database:h2:2.1.214")
    implementation("org.slf4j:slf4j-simple:2.0.6")
    implementation("com.jayway.jsonpath:json-path:2.7.0")
    compileOnly("io.insert-koin:koin-annotations:1.1.1")
    ksp("io.insert-koin:koin-ksp-compiler:1.1.1")
    ksp("com.ithersta.tgbotapi:boot-ksp:0.1.11")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.9.2")
    testImplementation("io.insert-koin:koin-test-junit5:3.3.3")
    testImplementation("io.mockk:mockk:1.13.4")
    testImplementation("app.cash.turbine:turbine:0.12.1")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.6.4")
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(17)
}

application {
    mainClass.set("MainKt")
}
