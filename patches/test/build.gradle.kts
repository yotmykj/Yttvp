plugins {
    kotlin("jvm")
}

dependencies {
    implementation(project(":common"))
    implementation(project(":patcher-api"))
    implementation(kotlin("stdlib"))
}
