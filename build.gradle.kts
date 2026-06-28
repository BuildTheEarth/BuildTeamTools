import com.palantir.gradle.gitversion.VersionDetails
import groovy.lang.Closure

plugins {
    java
    alias(libs.plugins.lombok)
    alias(libs.plugins.git.version)
    alias(libs.plugins.shadow)
}

repositories {
    mavenCentral()
    // mavenLocal() // NEVER use in Production/Commits!
    maven("https://repo.papermc.io/repository/maven-public/")

    maven("https://maven.buildtheearth.net/releases") // BuildTheEarth Projection

    maven("https://mvn.alps-bte.com/repository/alps-bte/") // AlpsLib

    maven("https://maven.enginehub.org/repo/") // WorldEdit

    maven("https://mvn.wesjd.net/") // Anvilgui

    maven("https://repo.bluecolored.de/releases") // BlueMap

    maven("https://repo.essentialsx.net/releases/")

    // Alps Lib Geo - can be removed once https://github.com/AlpsBTE/Alps-Lib/pull/17 is merged & version is set to 1.0.0
    maven("https://mvn.alps-bte.com/repository/alps-bte-snapshots/")

    maven("https://repo.lushplugins.org/releases") // PluginUpdater

    maven("https://jitpack.io") // Clipper2
}

dependencies {
    //implementation(libs.com.alpsbte.alpslib.alpslib.libpsterra) CURRENTLY BROKEN
    implementation(libs.alpslib.io)
    implementation(libs.alpslib.utils) {
        exclude(group = "com.github.cryptomorin", module = "XSeries")
    }
    implementation(libs.alpslib.geo)
    implementation(libs.alpsbte.canvas)
    implementation(libs.xseries)
    implementation(libs.anvilgui)
    implementation(libs.clipper2)
    implementation(libs.json)
    implementation(libs.googlecode.gson)
    implementation(libs.okhttp.jvm)
    implementation(libs.javaapiforkml) {
        exclude(group = "com.sun.xml.bind", module = "jaxb-xjc") // Else Remapping will yell of duplicated classes
    }
    implementation(libs.googlecode.json.simple)
    implementation(libs.bstats.bukkit)
    implementation(platform(libs.fawe.bom)) {
        exclude(group = "com.google.guava", module = "guava")
    }
    implementation(libs.buildtheearth.projection) {
        exclude(group = "com.google.guava", module = "guava")
    }
    implementation(libs.pluginupdater.common) {
        exclude(group = "com.google.guava", module = "guava")
    }
    implementation(libs.pluginupdater.paper)

    compileOnly("com.fastasyncworldedit:FastAsyncWorldEdit-Core")
    compileOnly("com.fastasyncworldedit:FastAsyncWorldEdit-Bukkit") { isTransitive = false }
    compileOnly(libs.paper.api)
    compileOnly(libs.bluemap.api)
    compileOnly("net.essentialsx:EssentialsX:2.19.0") {
        isTransitive = false
    }
}

fun Project.versionDetails(): VersionDetails {
    val closure = extra["versionDetails"] as? Closure<*>
        ?: error("Palantir git-version did not expose versionDetails")
    return closure.call() as VersionDetails
}
val details = versionDetails()

group = "net.buildtheearth"
version = "0.3.1" + "-SNAPSHOT+" + details.commitDistance + "-" + details.branchName + "-" + details.gitHash
description = "BuildTeamTools"
java.sourceCompatibility = JavaVersion.VERSION_21
java.targetCompatibility = JavaVersion.VERSION_21

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks.withType<Javadoc> {
    options.encoding = "UTF-8"
}

tasks.shadowJar {
    archiveClassifier = ""

    relocationPrefix = "net.buildtheearth.buildteamtools.shaded"
    enableAutoRelocation = true

    // Prevents the plugin itself from being relocated by autorelocate
    // This is needed because net.buildtheearth.projection shares the same package namespace
    relocate("net.buildtheearth.buildteamtools", "net.buildtheearth.buildteamtools")
}

tasks.assemble {
    dependsOn(tasks.shadowJar) // Ensure that the shadowJar task runs before the build task
}

tasks.jar {
    archiveClassifier = "UNSHADED"
    enabled = false // Disable the default jar task since we are using shadowJar
}

tasks.processResources {
    // work around IDEA-296490
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
    with(copySpec {
        from("src/main/resources/plugin.yml") {
            expand(
                mapOf(
                    "version" to project.version,
                    "description" to project.description
                )
            )
        }
    })
}
