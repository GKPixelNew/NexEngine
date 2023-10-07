plugins {
    id("su.nexmedia.project-conventions")
    id("cc.mewcraft.publishing-conventions")
    id("cc.mewcraft.deploy-conventions")
    id("cc.mewcraft.paper-plugins")
}

project.ext.set("name", "NexEngine")

dependencies {
    // server api
    compileOnly(libs.server.paper)

    // the "api" module
    // make it a separate module to avoid circular dependencies
    api(project(":NexEngineAPI"))

    // code that requires 3rd plugin dependencies
    // we put it in a separate module to avoid dependency pollution
    api(project(":NexEngineExt"))

    // nms modules
    api(project(":NMS"))
    implementation(project(":NexEngineCompat_V1_18_R2", configuration = "reobf"))
    implementation(project(":NexEngineCompat_V1_19_R3", configuration = "reobf"))
    implementation(project(":NexEngineCompat_V1_20_R1", configuration = "reobf"))

    // support custom item from various plugins
    compileOnly(libs.spatula.item)
}
