import org.gradle.internal.os.OperatingSystem

plugins {
    kotlin("jvm") version "1.9.22"
}

group = "dev.yidafu.font2svg"
version = "unspecified"

val lwjglVersion = "3.3.3"

val lwjglNatives = Pair(
    System.getProperty("os.name")!!,
    System.getProperty("os.arch")!!
).let { (name, arch) ->
    when {
        arrayOf("Linux", "SunOS", "Unit").any { name.startsWith(it) } ->
            if (arrayOf("arm", "aarch64").any { arch.startsWith(it) })
                "natives-linux${if (arch.contains("64") || arch.startsWith("armv8")) "-arm64" else "-arm32"}"
            else if (arch.startsWith("ppc"))
                "natives-linux-ppc64le"
            else if (arch.startsWith("riscv"))
                "natives-linux-riscv64"
            else
                "natives-linux"
        arrayOf("Mac OS X", "Darwin").any { name.startsWith(it) }     ->
            "natives-macos${if (arch.startsWith("aarch64")) "-arm64" else ""}"
        arrayOf("Windows").any { name.startsWith(it) }                ->
            if (arch.contains("64"))
                "natives-windows${if (arch.startsWith("aarch64")) "-arm64" else ""}"
            else
                "natives-windows-x86"
        else                                                                            ->
            throw Error("Unrecognized or unsupported platform. Please set \"lwjglNatives\" manually")
    }
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("org.jetbrains.kotlin:kotlin-test")
    implementation(platform("org.lwjgl:lwjgl-bom:$lwjglVersion"))
    implementation("com.github.nwillc:ksvg:3.0.0")

    implementation("org.lwjgl", "lwjgl")
    implementation("org.lwjgl", "lwjgl-freetype")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.1")
    runtimeOnly("org.lwjgl", "lwjgl", classifier = lwjglNatives)
    runtimeOnly("org.lwjgl", "lwjgl-freetype", classifier = lwjglNatives)
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(17)
}