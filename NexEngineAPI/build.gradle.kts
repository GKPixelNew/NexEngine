plugins {
    id("su.nexmedia.project-conventions")
    id("net.kyori.indra") version "2.1.1"
}

description = "NexEngineAPI"

repositories {
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.20.4-R0.1-SNAPSHOT")

    api("com.zaxxer:HikariCP:5.0.1")
    api("com.mojang:authlib:3.11.49")
    api("org.mongodb:mongodb-driver-sync:5.1.0")
}

indra {
    javaVersions().target(17)
}
