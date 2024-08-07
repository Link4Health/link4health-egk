plugins {
  `kotlin-dsl`
}

kotlin {
  jvmToolchain(libs.versions.javaTarget.get().toInt())
}

