@file:Suppress("PropertyName")

import java.util.zip.ZipFile

plugins {
    id("java")
}

repositories {
    google()
    mavenCentral()
    maven(url = "https://nexus.scanbot.io/nexus/content/repositories/snapshots/")
    maven(url = "https://nexus.scanbot.io/nexus/content/repositories/releases/")
}

val defaultScanbotSdkVersion = "1.92.0"

val sdkVersion: String
    get() {
        val version = (project.findProperty("version") as? String) ?: defaultScanbotSdkVersion
        return if (version == "unspecified") {
            defaultScanbotSdkVersion
        } else {
            version
        }
    }

// This is a full set of Scanbot SDK artifacts. For simplicity unused ones can be commented/deleted.
dependencies {
    implementation("io.scanbot:sdk-package-4:$sdkVersion") { isChanging = true } // use required package as needed, e.g. `io.scanbot:sdk-package-1`
    implementation("io.scanbot:sdk-package-ui:$sdkVersion") { isChanging = true }
    implementation("io.scanbot:sdk-ml-docdetector:$sdkVersion") { isChanging = true }
    implementation("io.scanbot:sdk-barcode-assets:$sdkVersion") { isChanging = true }
    implementation("io.scanbot:sdk-mc-assets:$sdkVersion") { isChanging = true }
    implementation("io.scanbot:sdk-ehic-assets:$sdkVersion") { isChanging = true }
    implementation("io.scanbot:sdk-blur-assets:$sdkVersion") { isChanging = true }
    implementation("io.scanbot:sdk-genericdocument-assets:$sdkVersion") { isChanging = true }
    implementation("io.scanbot:sdk-generictext-assets:$sdkVersion") { isChanging = true }
    implementation("io.scanbot:sdk-licenseplate-assets:$sdkVersion") { isChanging = true }
    implementation("io.scanbot:sdk-ml-imageprocessor-assets:$sdkVersion") { isChanging = true }
}

val DEPENDENCIES_FOLDER_NAME = "deps-plain"

val isFull: Boolean
    get() = project.hasProperty("full")

fun isScanbotDependency(dependency: ResolvedDependency): Boolean {
    return dependency.moduleGroup == "io.scanbot"
          || dependency.moduleGroup == "crl.android.pdfwriter"
          || dependency.moduleGroup == "com.commonsware.cwac.camera"
}

tasks.create("printDeps") {
    doLast {

        val scanbotDependencies = mutableListOf<String>()
        val transitiveDependencies = mutableListOf<String>()

        project.configurations.implementation.get().resolvedConfiguration.lenientConfiguration.allModuleDependencies
            .toList()
            .sortedBy { it.name }
            .forEach {
                if (isScanbotDependency(it)) {
                    scanbotDependencies.add(it.name)
                } else {
                    transitiveDependencies.add(it.name)
                }
            }

        println("\n\tDependencies required by Scanbot SDK:\n")
        transitiveDependencies.forEach { println(it) }

        if (isFull) {
            println("\n\n\tScanbot SDK dependencies:\n")
            scanbotDependencies.forEach { println(it) }
        }
    }
}

tasks.create("fetchDeps", Copy::class.java) {
    val depsDirFile = file(DEPENDENCIES_FOLDER_NAME)

    doFirst { depsDirFile.deleteRecursively() }

    configurations.implementation.get().isCanBeResolved = true

    from(configurations.implementation) {
        if (isFull.not()) {
            exclude { it.name.endsWith(".jar") }
        }
    }
    into(depsDirFile)
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE

    doLast {
        if (isFull.not()) {
            depsDirFile.listFiles()?.filterNot { dependencyFile ->
                val aarFile = ZipFile(dependencyFile)
                val aarEntries = aarFile.entries()
                while (aarEntries.hasMoreElements()) {
                    val aarEntry = aarEntries.nextElement()
                    if (aarEntry.name == "AndroidManifest.xml") {
                        return@filterNot aarFile.getInputStream(aarEntry).reader().readLines().any { manifestLine ->
                            manifestLine.contains("package=\"io.scanbot")
                                  || manifestLine.contains("package=\"crl.android.pdfwriter")
                                  || manifestLine.contains("package=\"com.commonsware.cwac.camera")
                        }
                    }
                }
                false
            }?.forEach { it.delete() }
        }

        println("\nSUCCESS!\nDependencies artifacts were downloaded to file://${depsDirFile.absolutePath}")
    }
}
