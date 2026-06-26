plugins {
    `kotlin-dsl`
    `maven-publish`
}

group = "no.chirico.core"
version = "0.0.1"

java {
    sourceCompatibility = JavaVersion.VERSION_25
    targetCompatibility = JavaVersion.VERSION_25
}

dependencies {
    implementation(libs.kotlin.gradlePlugin)
    implementation(libs.kotlin.allopen)
    implementation(libs.kotlin.noarg)
    implementation(libs.spring.boot.gradlePlugin) {
        exclude(group = "io.spring.gradle", module = "dependency-management-plugin")
    }
    implementation(libs.ktfmt.gradlePlugin)
}

publishing {
    publications {
        create<MavenPublication>("plugin") {
            from(components["java"])
            groupId = "no.chirico.core"
            artifactId = "build-logic"
            version = "0.0.1"
        }
    }
}
