plugins {
    id("org.jetbrains.kotlin.jvm") version "1.9.23"
    `java-library`
    `java-test-fixtures`
}

dependencies {
    api("org.springframework.kafka:spring-kafka")

    testImplementation("org.springframework.kafka:spring-kafka-test")
    testImplementation("org.testcontainers:kafka")

    testFixturesImplementation("org.testcontainers:kafka")
}
