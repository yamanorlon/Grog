plugins {
    kotlin("jvm")
    kotlin("plugin.serialization") version "2.2.21"
}

repositories {
    mavenCentral()
}

dependencies {
    // Для JSON сериализации (Jackson)
    implementation("com.fasterxml.jackson.core:jackson-databind:2.21.3")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.21.3")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.21.3")

    // Для аннотаций OpenAPI/Swagger (опционально, если нужно документирование)
    implementation("io.swagger.core.v3:swagger-annotations:2.2.28")

    testImplementation(kotlin("test"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.withType<Test> {
    useJUnitPlatform()
}