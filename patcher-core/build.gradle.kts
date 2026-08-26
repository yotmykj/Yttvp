plugins {
    kotlin("jvm")
}

dependencies {
    implementation(project(":common"))
    implementation(project(":patcher-api"))
    implementation(kotlin("stdlib"))
    implementation("com.android.tools.build:apksig:8.2.2")
    implementation("com.android.tools:common:31.2.2")
}
