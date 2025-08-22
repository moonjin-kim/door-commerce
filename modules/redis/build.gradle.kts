plugins {
    `java-library`
    `java-test-fixtures`
}

dependencies {
    api("org.springframework.boot:spring-boot-starter-data-redis")
    api("com.fasterxml.jackson.core:jackson-databind:2.17.1")

    testFixturesImplementation("com.redis:testcontainers-redis")

}
