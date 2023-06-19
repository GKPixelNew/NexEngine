plugins {
    java
    id("su.nexmedia.project-conventions")
}

description = "NMS"

repositories {
    maven("https://repo.papermc.io/repository/maven-public/")
    mavenCentral()
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.20.1-R0.1-SNAPSHOT")
    compileOnly("io.netty:netty-all:4.1.85.Final")
}
