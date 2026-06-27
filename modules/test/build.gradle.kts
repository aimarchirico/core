plugins {
  id("core.kotlin")
  `java-library`
  `maven-publish`
}

group = "no.chirico.core"

version = "0.0.1"

dependencies {
  implementation(platform(libs.spring.boot.dependencies))
  // Exposed through BaseArchitectureTest's public API, so consumers get them
  // transitively on their test compile classpath.
  api(libs.archunit)
  api("org.junit.jupiter:junit-jupiter-api")
}

publishing {
  publications {
    create<MavenPublication>("mavenJava") {
      from(components["java"])
      groupId = "no.chirico.core"
      artifactId = "core-test"
      version = "0.0.1"
    }
  }
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
