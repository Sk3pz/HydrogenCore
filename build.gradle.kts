plugins {
    kotlin("jvm") version "1.9.10"
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "es.skepz"
version = "1.0.0"

repositories {

    mavenCentral()

    maven("https://repo.papermc.io/repository/maven-public/") {
        name = "Paper"
    }
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.20.1-R0.1-SNAPSHOT")
    implementation(kotlin("stdlib-jdk8"))
}


tasks {
    shadowJar {
        relocate("org.jetbrains", "es.skepz.hydrogen.libs.org.jetbrains")
        relocate("org.intellij", "es.skepz.hydrogen.libs.org.intellij")
        relocate("com.google", "es.skepz.hydrogen.libs.com.google")
        relocate("com.moandjiezana", "es.skepz.hydrogen.libs.com.moandjiezana")
        relocate("kotlin", "es.skepz.hydrogen.libs.kotlin")
    }
}