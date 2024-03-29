plugins {
  kotlin("jvm") version "1.9.21"
  kotlin("plugin.serialization") version "1.9.22"

  id("org.openjfx.javafxplugin") version "0.1.0"
  id("application")
}

application {
  mainClass = "frontend.DarwinWorldApp"
  applicationDefaultJvmArgs = listOf(
    "--add-exports=javafx.controls/com.sun.javafx.scene.control.behavior=ALL-UNNAMED",
    "--add-exports=javafx.controls/com.sun.javafx.scene.control=ALL-UNNAMED",
    "--add-exports=javafx.base/com.sun.javafx.binding=ALL-UNNAMED",
    "--add-exports=javafx.graphics/com.sun.javafx.stage=ALL-UNNAMED",
    "--add-exports=javafx.controls/com.sun.javafx.scene.control.behavior=ALL-UNNAMED",
    "--add-opens=javafx.graphics/javafx.scene=ALL-UNNAMED"
  )
}

group = "org.darwinWorld"
version = "1.0-SNAPSHOT"

repositories {
  mavenCentral()
}

javafx {
  version = "21"
  modules("javafx.controls", "javafx.graphics")
}

val kotestVersion = "5.8.0"

dependencies {
  implementation("no.tornado:tornadofx:1.7.20")
  implementation("io.github.mkpaz:atlantafx-base:2.0.1")
  implementation("org.kordamp.ikonli:ikonli-javafx:12.3.1")
  implementation("org.kordamp.ikonli:ikonli-material2-pack:12.3.1")

  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.0-RC2")
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-javafx:1.8.0-RC2")

  implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")

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
