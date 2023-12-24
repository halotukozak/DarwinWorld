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

val kotestVersion = "5.8.0"

dependencies {
//    implementation ("no.tornado:tornadofx")
//  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.0-RC2")

  testImplementation("org.jetbrains.kotlin:kotlin-test")
  testImplementation("io.kotest:kotest-runner-junit5:$kotestVersion")
  testImplementation("io.kotest:kotest-assertions-core:$kotestVersion")
}

tasks.test {
  useJUnitPlatform()
}

kotlin {
  jvmToolchain(21)
}