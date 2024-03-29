import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.8.20"
    kotlin("plugin.serialization") version "1.8.20"
    id("com.google.devtools.ksp") version "1.8.20-1.0.11"
    id("io.gitlab.arturbosch.detekt") version "1.22.0"
    id("com.google.cloud.tools.jib") version "3.3.1"
    application
}

jib {
    container.workingDirectory = "/db"
}

group = "ru.spbstu"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://repo.repsy.io/mvn/ithersta/tgbotapi")
}

val exposedVersion = "0.41.1"
val ktorVersion = "2.3.0"
dependencies {
    implementation("com.ithersta.tgbotapi:boot:0.1.14")
    implementation("com.ithersta.tgbotapi:commands:0.3.0")
    implementation("io.ktor:ktor-client-okhttp:$ktorVersion")
    implementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")
    implementation("org.jetbrains.exposed:exposed-core:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-dao:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-kotlin-datetime:$exposedVersion")
    implementation("com.h2database:h2:2.1.214")
    implementation("commons-validator:commons-validator:1.7")
    implementation("org.apache.poi:poi:5.2.3")
    implementation("org.apache.poi:poi-ooxml:5.2.3")
    implementation("io.github.oshai:kotlin-logging-jvm:4.0.0-beta-28")
    implementation("org.slf4j:slf4j-simple:2.0.7")
    implementation("io.arrow-kt:arrow-core:1.2.0-RC")
    implementation("dev.inmo:krontab:1.0.0")
    compileOnly("io.insert-koin:koin-annotations:1.2.0")
    ksp("io.insert-koin:koin-ksp-compiler:1.2.0")
    ksp("com.ithersta.tgbotapi:boot-ksp:0.1.11")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.9.2")
    testImplementation("io.insert-koin:koin-test-junit5:3.4.0")
    testImplementation("io.mockk:mockk:1.13.5")
    testImplementation("app.cash.turbine:turbine:0.12.3")
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

tasks.withType(KotlinCompile::class.java).configureEach {
    kotlinOptions {
        freeCompilerArgs += "-Xcontext-receivers"
    }
}
