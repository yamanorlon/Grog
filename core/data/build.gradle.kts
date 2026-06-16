plugins {
    kotlin("jvm")
    `java-test-fixtures`
}

sourceSets {
    named("test") {
        resources.srcDir(project(":core:api").projectDir.resolve("src/main/resources"))
    }
    named("testFixtures") {
        resources.srcDir(project(":core:api").projectDir.resolve("src/main/resources"))
    }
}

dependencies {
    implementation(project(":core:domain"))
    implementation(project(":core:model"))

    implementation("org.jetbrains.exposed:exposed-core:0.53.0")
    implementation("org.jetbrains.exposed:exposed-dao:0.53.0")
    implementation("org.jetbrains.exposed:exposed-jdbc:0.53.0")
    implementation("org.jetbrains.exposed:exposed-java-time:0.53.0")

    implementation("org.postgresql:postgresql:42.7.5")

    testFixturesImplementation(project(":core:domain"))
    testFixturesImplementation("org.jetbrains.exposed:exposed-core:0.53.0")
    testFixturesImplementation("org.jetbrains.exposed:exposed-jdbc:0.53.0")
    testFixturesImplementation("org.jetbrains.exposed:exposed-java-time:0.53.0")
    testFixturesImplementation("org.postgresql:postgresql:42.7.5")
    testFixturesImplementation("org.testcontainers:junit-jupiter:1.21.0")
    testFixturesImplementation("org.testcontainers:postgresql:1.21.0")
    testFixturesImplementation("org.flywaydb:flyway-core:11.7.2")
    testFixturesImplementation("org.flywaydb:flyway-database-postgresql:11.7.2")
    testFixturesImplementation("org.junit.jupiter:junit-jupiter:5.12.2")

    testImplementation("org.junit.jupiter:junit-jupiter:5.12.2")
    testImplementation("io.mockk:mockk:1.14.2")
    testImplementation("org.testcontainers:junit-jupiter:1.21.0")
    testImplementation("org.testcontainers:postgresql:1.21.0")
    testImplementation("org.flywaydb:flyway-core:11.7.2")
    testImplementation("org.flywaydb:flyway-database-postgresql:11.7.2")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}
