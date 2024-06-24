plugins {
    kotlin("jvm") version "1.9.20"
    //id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "es.skepz"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/") {
        name = "papermc-repo"
    }
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21-R0.1-SNAPSHOT")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}

//tasks {
//    shadowJar {
//        relocate("org.jetbrains", "es.skepz.hydrogen.libs.org.jetbrains")
//        relocate("org.intellij", "es.skepz.hydrogen.libs.org.intellij")
//        relocate("com.google", "es.skepz.hydrogen.libs.com.google")
//        relocate("com.moandjiezana", "es.skepz.hydrogen.libs.com.moandjiezana")
//        relocate("kotlin", "es.skepz.hydrogen.libs.kotlin")
//    }
//}