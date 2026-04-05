plugins {
    id("java-library")
}

dependencies {
    // PostgreSQL Driver
    implementation("org.postgresql:postgresql:42.7.4")
    
    // HikariCP Connection Pool
    implementation("com.zaxxer:HikariCP:6.2.1")
    
    // Flyway Database Migration
    implementation("org.flywaydb:flyway-core:11.1.0")
    implementation("org.flywaydb:flyway-database-postgresql:11.1.0")
    
    // Dotenv for environment variables
    implementation("io.github.cdimascio:dotenv-java:3.0.2")
    
    // JUnit 5 for testing
    testImplementation("org.junit.jupiter:junit-jupiter:5.11.4")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    
    // H2 for testing (in-memory database)
    testImplementation("com.h2database:h2:2.3.232")
}
