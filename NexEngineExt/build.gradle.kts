plugins {
    id("su.nexmedia.project-conventions")
    java
}

description = "NexEngineExt"

repositories {
    maven("https://repo.opencollab.dev/maven-snapshots/")
    maven("https://maven.citizensnpcs.co/repo")
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    maven("https://mvn.lumine.io/repository/maven-public/")
    maven("https://jitpack.io")
}

dependencies {
    compileOnly(project(":NexEngineAPI"))

    // Paper
    compileOnly("io.papermc.paper:paper-api:1.20.1-R0.1-SNAPSHOT")

    // 3rd party plugins that may contain random transitive dependencies
    compileOnly("me.clip:placeholderapi:2.11.3")
    compileOnly("com.github.MilkBowl:VaultAPI:1.7") { isTransitive = false }
    compileOnly("com.sk89q.worldguard:worldguard-bukkit:7.0.5") {
        exclude("org.bukkit")
    }
    compileOnly("net.citizensnpcs:citizensapi:2.0.32-SNAPSHOT") {
        exclude("ch.ethz.globis.phtree")
    }
    compileOnly("org.geysermc.floodgate:api:2.2.0-SNAPSHOT")
    compileOnly("io.lumine:Mythic-Dist:5.3.1") { isTransitive = false }
}
