plugins {
    id("su.nexmedia.project-conventions")
    id("net.kyori.indra") version "2.1.1"
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

description = "NexEngine"

repositories {
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    api(project(":NexEngineAPI"))

    // server api
    compileOnly("io.papermc.paper:paper-api:1.20.1-R0.1-SNAPSHOT")

    // nms modules
    api(project(":NMS"))
    implementation(project(":NexEngineCompat_V1_18_R2", configuration = "reobf"))
    implementation(project(":NexEngineCompat_V1_19_R3", configuration = "reobf"))
    implementation(project(":NexEngineCompat_V1_20_R1", configuration = "reobf"))

    // libs to be shaded
    compileOnly("io.netty:netty-all:4.1.86.Final")
    compileOnly("org.xerial:sqlite-jdbc:3.40.0.0")
    compileOnly("com.zaxxer:HikariCP:5.0.1")
    compileOnly("com.mojang:authlib:3.11.49")

    // code that requires 3rd plugin dependencies
    // we put it in a separate module to avoid dependency pollution
    api(project(":NexEngineExt"))
}

// TODO remove plugin.yml
/*bukkit {
    main = "su.nexmedia.engine.NexEngine"
    name = "NexEngine"
    version = "${project.version}"
    apiVersion = "1.17"
    authors = listOf("NightExpress")
    softDepend = listOf("Vault", "Citizens", "MythicMobs")
    load = STARTUP
    libraries = listOf("com.zaxxer:HikariCP:5.0.1", "it.unimi.dsi:fastutil:8.5.11")
}*/

tasks {
    build {
        dependsOn(shadowJar)
    }
    jar {
        archiveClassifier.set("noshade")
    }
    shadowJar {
        minimize {
            exclude(dependency("su.nexmedia:.*:.*"))
        }
        archiveFileName.set("NexEngine-${project.version}.jar")
        archiveClassifier.set("")
        destinationDirectory.set(file("$rootDir"))
    }
    processResources {
        filesMatching("**/paper-plugin.yml") {
            expand(mapOf(
                "version" to "${project.version}",
                "description" to project.description
            ))
        }
    }
}

indra {
    javaVersions().target(17)
}