plugins {
    id("su.nexmedia.project-conventions")
    id("cc.mewcraft.publishing-conventions")
}

dependencies {
    compileOnly(libs.server.paper)

    api(libs.hikari)
    api(libs.authlib)
}
