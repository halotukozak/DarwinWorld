plugins {
    kotlin("jvm") version "1.9.21"
//    id("org.openjfx.javafxplugin") version "0.1.0"
}

group = "org.oolab"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

//javafx {
//    modules("javafx.controls", "javafx.fxml")
//}

dependencies {
//    implementation ("no.tornado:tornadofx")

    testImplementation("org.jetbrains.kotlin:kotlin-test")
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(21)
}