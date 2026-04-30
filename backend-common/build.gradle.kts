plugins {
    kotlin("jvm")
    kotlin("plugin.jpa") version "2.2.21"
}

repositories {
    mavenCentral()
}

dependencies {
    // Зависимость на api-models, чтобы мапперы могли видеть DTO
    implementation(project(":api-models"))

    // JPA / Hibernate
    implementation("org.springframework.boot:spring-boot-starter-data-jpa:4.0.6")
    implementation("jakarta.persistence:jakarta.persistence-api:3.2.0")

    testImplementation(kotlin("test"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.withType<Test> {
    useJUnitPlatform()
}