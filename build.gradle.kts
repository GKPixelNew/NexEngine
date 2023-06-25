plugins {
    id("su.nexmedia.project-conventions")
}

allprojects {
    apply(plugin = "su.nexmedia.project-conventions")

    repositories {
        mavenCentral()
    }

    publishing {
        publications {
            create<MavenPublication>("maven") {
                from(components["java"])
            }
        }
    }
}