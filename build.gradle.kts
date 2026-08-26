plugins {
    kotlin("jvm") version "2.0.21" apply false
}

allprojects {
    group = "com.yotmykj.yttvp"
    version = "1.0.0"

    repositories {
        mavenCentral()
    }
}

subprojects {
    plugins.withId("org.jetbrains.kotlin.jvm") {
        extensions.configure<org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension> {
            jvmToolchain(17)
        }

        tasks.withType<Test>().configureEach {
            useJUnitPlatform()
        }

        dependencies {
            add("testImplementation", "org.jetbrains.kotlin:kotlin-test:2.0.21")
            add("testImplementation", "org.junit.jupiter:junit-jupiter:5.11.0")
        }
    }
}
