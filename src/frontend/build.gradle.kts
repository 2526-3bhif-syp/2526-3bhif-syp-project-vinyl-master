plugins {
    id("application")
    id("org.openjfx.javafxplugin") version "0.1.0"
    id("com.gradleup.shadow") version "8.3.5"
}

dependencies {
    // Backend module
    implementation(project(":backend"))
    
    // JUnit 5 for testing
    testImplementation("org.junit.jupiter:junit-jupiter:5.11.4")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

javafx {
    version = "23.0.1"
    modules = listOf("javafx.controls", "javafx.fxml")
}

application {
    mainClass.set("at.htl.leonding.vinylmaster.ui.Main")
}
