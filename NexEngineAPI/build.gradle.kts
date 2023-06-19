plugins {
    id("su.nexmedia.project-conventions")
    id("net.kyori.indra") version "2.1.1"
}

description = "NexEngineAPI"

repositories {
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.20.1-R0.1-SNAPSHOT")
}

indra {
    javaVersions().target(17)
}
