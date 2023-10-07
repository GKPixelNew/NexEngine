// TODO https://stackoverflow.com/a/50153617 it looks like we can use `plugins {}` block here

rootProject.name = "NexEnginePlugin"

// The plugin
include(":NexEngine")

// Code that is somewhat standalone
include(":NexEngineAPI")

// Code related to random 3rd party plugins
include(":NexEngineExt")

// TODO make it a separate module
// include(":PlayerBlockTracker")

include(":NMS")
include(":NexEngineCompat_V1_18_R2")
include(":NexEngineCompat_V1_19_R3")
include(":NexEngineCompat_V1_20_R1")

// import common settings.gradle of Mewcraft projects
apply(from = "${System.getenv("HOME")}/MewcraftGradle/mirrors.settings.gradle.kts")
