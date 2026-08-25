plugins {
    application
}

application {
    mainClass.set("com.yttv.patcher.cli.MainKt")
    applicationName = "yttv-patcher"
}

dependencies {
    implementation(project(":common"))
    implementation(project(":patcher-api"))
    implementation(project(":patcher-core"))
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.9.23")
    implementation("com.github.ajalt.clikt:clikt:4.2.2")
}

tasks.jar {
    manifest {
        attributes["Main-Class"] = "com.yttv.patcher.cli.MainKt"
    }
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    from({
        configurations.runtimeClasspath.get().filter { it.exists() }.map { if (it.isDirectory) it else zipTree(it) }
    })
}
