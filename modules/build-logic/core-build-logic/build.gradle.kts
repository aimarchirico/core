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

// The kotlin-dsl (java-gradle-plugin) integration auto-creates the
// implementation publication (artifactId = project name "core-build-logic")
// plus a marker publication per precompiled script plugin (core.kotlin), so a
// consumer can apply `id("core.kotlin") version "<v>"`. We only declare where
// to publish; credentials come from the environment in CI.
publishing {
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/aimarchirico/core")
            credentials {
                username = System.getenv("GITHUB_ACTOR")
                password = System.getenv("GITHUB_TOKEN")
            }
        }
    }
}
