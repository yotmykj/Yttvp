plugins {
    kotlin("jvm") version "2.0.21" apply false
}

allprojects {
    group = "com.yotmykj.yttvp"
    version = "1.0.0"
}

subprojects {
    apply(plugin = "org.jetbrains.kotlin.jvm")

    repositories {
        mavenCentral()
    }

    tasks.withType<Test>().configureEach {
        useJUnitPlatform()
    }

    kotlin {
        jvmToolchain(17)
    }

    dependencies {
        "testImplementation"("org.jetbrains.kotlin:kotlin-test:2.0.21")
        "testImplementation"("org.junit.jupiter:junit-jupiter:5.11.0")
    }
}
