plugins {
  id("core.kotlin")
  `maven-publish`
}

group = "no.chirico.core"

version = "0.0.1"

dependencies {
  implementation(platform(libs.spring.boot.dependencies))
  implementation(libs.archunit)
  implementation("org.junit.jupiter:junit-jupiter-api")
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
}
