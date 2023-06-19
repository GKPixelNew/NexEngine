plugins {
    id("su.nexmedia.project-conventions")
}

allprojects {
    apply(plugin = "su.nexmedia.project-conventions")

    publishing {
        publications {
            create<MavenPublication>("maven") {
                from(components["java"])
            }
        }
    }
}