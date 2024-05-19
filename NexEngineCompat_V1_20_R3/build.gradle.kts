plugins {
    id("su.nexmedia.project-conventions")
    id("io.papermc.paperweight.userdev") version "1.6.3"
    id("net.kyori.indra") version "2.1.1"
}

description = "NexEngineCompat_V1_20_R3"

dependencies {
    compileOnly(project(":NMS"))
    paperweight.paperDevBundle("1.20.4-R0.1-SNAPSHOT")
}

tasks {
    assemble {
        dependsOn(reobfJar)
    }
}

indra {
    javaVersions().target(17)
}
