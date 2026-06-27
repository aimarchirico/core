plugins {
    id("no.chirico.core.build-logic")
    `maven-publish`
}

group = "no.chirico.core"
version = "0.0.1"

dependencies {
    implementation(platform(libs.spring.boot.dependencies))
    implementation("org.springframework.boot:spring-boot-starter-web")
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
            groupId = "no.chirico.core"
            artifactId = "core-security"
            version = "0.0.1"
        }
    }
}
