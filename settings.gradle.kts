plugins {
  id("org.gradle.toolchains.foojay-resolver-convention") version "0.5.0"
}
rootProject.name = "font2svg"

include(":font2svg-server")
include(":font2svg-core")
