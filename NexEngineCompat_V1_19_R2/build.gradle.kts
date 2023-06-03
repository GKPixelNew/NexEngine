plugins {
    id("su.nexmedia.project-conventions")
    alias(libs.plugins.paperweight.userdev)
    alias(libs.plugins.indra)
}

description = "NexEngineCompat_V1_19_R2"

dependencies {
    compileOnly(project(":NMS"))
    paperweight.paperDevBundle("1.19.3-R0.1-SNAPSHOT")
}

tasks {
    assemble {
        dependsOn(reobfJar)
    }
}

indra {
    javaVersions().target(17)
}
