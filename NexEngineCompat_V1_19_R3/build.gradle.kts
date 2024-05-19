plugins {
    id("su.nexmedia.project-conventions")
    id("io.papermc.paperweight.userdev") version "1.5.5"
    id("net.kyori.indra") version "2.1.1"
}

dependencies {
    compileOnly(project(":NMS"))
    paperweight.paperDevBundle("1.19.4-R0.1-SNAPSHOT")
}

tasks {
    assemble {
        dependsOn(reobfJar)
    }
}

indra {
    javaVersions().target(17)
}
