plugins {
    kotlin("jvm") version "1.8.10"
    kotlin("plugin.serialization") version "1.8.10"
    kotlin("plugin.jpa") version "1.8.10"
    kotlin("plugin.allopen") version "1.8.10"
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

dependencies {
    implementation("com.ithersta.tgbotapi:fsm:0.23.0")
    implementation("com.ithersta.tgbotapi:sqlite-persistence:0.4.0")
    implementation("com.ithersta.tgbotapi:commands:0.2.0")
    implementation("io.ktor:ktor-client-okhttp:2.2.3")
    implementation("org.hibernate.orm:hibernate-core:6.1.6.Final")
    implementation("com.h2database:h2:2.1.214")
    implementation("org.jsoup:jsoup:1.15.3")
    implementation("com.jayway.jsonpath:json-path:2.7.0")
    compileOnly("io.insert-koin:koin-annotations:1.1.0")
    ksp("io.insert-koin:koin-ksp-compiler:1.1.0")
    testImplementation(kotlin("test"))
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

allOpen {
    annotations("javax.persistence.Entity", "javax.persistence.MappedSuperclass", "javax.persistence.Embedabble")
}
