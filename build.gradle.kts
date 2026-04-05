plugins {
    java
}

group = "at.htl.leonding"
version = "1.0.0"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

subprojects {
    apply(plugin = "java")
    
    repositories {
        mavenCentral()
    }
    
    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(21))
        }
    }
    
    tasks.withType<Test> {
        useJUnitPlatform()
    }
}
