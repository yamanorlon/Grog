plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    application
}

application {
    mainClass.set("com.yamanorlon.grog.ApplicationKt")
}

dependencies {
    implementation(project(":core:domain"))
    implementation(project(":core:data"))
    implementation(project(":core:model"))

    val ktorVersion = "3.4.3"
    implementation("io.ktor:ktor-server-core:$ktorVersion")
    implementation("io.ktor:ktor-server-netty:$ktorVersion")
    implementation("io.ktor:ktor-server-content-negotiation:$ktorVersion")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")
    implementation("io.ktor:ktor-server-status-pages:$ktorVersion")
    implementation("io.ktor:ktor-server-call-logging:$ktorVersion")
    implementation("io.ktor:ktor-server-default-headers:$ktorVersion")
    implementation("io.ktor:ktor-server-request-validation:$ktorVersion")
    implementation("io.ktor:ktor-server-openapi:$ktorVersion")
    implementation("io.ktor:ktor-server-swagger:$ktorVersion")
    implementation("io.ktor:ktor-server-routing-openapi:$ktorVersion")
    implementation("io.ktor:ktor-server-config-yaml:$ktorVersion")

    implementation("io.insert-koin:koin-ktor:4.0.4")
    implementation("io.insert-koin:koin-logger-slf4j:4.0.4")

    implementation("io.konform:konform-jvm:0.11.0")

    implementation("ch.qos.logback:logback-classic:1.5.18")
    implementation("net.logstash.logback:logstash-logback-encoder:8.1")

    implementation("io.ktor:ktor-server-metrics-micrometer:$ktorVersion")
    implementation("io.micrometer:micrometer-registry-prometheus:1.14.6")

    implementation("org.flywaydb:flyway-core:11.7.2")
    implementation("org.flywaydb:flyway-database-postgresql:11.7.2")

    implementation("org.jetbrains.exposed:exposed-core:0.53.0")
    implementation("org.jetbrains.exposed:exposed-jdbc:0.53.0")
    implementation("org.jetbrains.exposed:exposed-java-time:0.53.0")
    implementation("org.postgresql:postgresql:42.7.5")

    testImplementation("io.ktor:ktor-server-test-host:$ktorVersion")
    testImplementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
    testImplementation("io.ktor:ktor-client-core:$ktorVersion")
    testImplementation("io.ktor:ktor-client-cio:$ktorVersion")
    testImplementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")

    testImplementation("org.junit.jupiter:junit-jupiter:5.12.2")
    testImplementation(testFixtures(project(":core:data")))
    testImplementation("io.mockk:mockk:1.14.2")
    testImplementation("org.testcontainers:junit-jupiter:1.21.0")
    testImplementation("org.testcontainers:postgresql:1.21.0")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}
