import org.gradle.internal.os.OperatingSystem
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val kotlinVersion = "1.3.50"
val xemanticOpenrndrVersion = "1.0-SNAPSHOT"

plugins {
    java
    kotlin("jvm") version("1.3.50")
}
group = "com.xemantic.art"
version = "1.0-SNAPSHOT"

val applicationMainClass = "com.xemantic.openrndr.template.XemanticOpenrndrTemplateKt"

val openrndrUseSnapshot = false
val openrndrVersion = if (openrndrUseSnapshot) "0.4.0-SNAPSHOT" else "0.3.36-rc7"
val openrndrOs = when (OperatingSystem.current()) {
    OperatingSystem.WINDOWS -> "windows"
    OperatingSystem.MAC_OS -> "macos"
    OperatingSystem.LINUX -> "linux-x64"
    else -> throw IllegalArgumentException("os not supported")
}

// supported features are: video, panel
val openrndrFeatures = setOf<String>() //"video", "panel", "kinect-v1"

val panelUseSnapshot = false
val panelVersion = if (panelUseSnapshot) "0.4.0-SNAPSHOT" else "0.3.17-m3"

val orxUseSnapshot = false
val orxVersion = if (orxUseSnapshot) "0.4.0-SNAPSHOT" else "0.3.38"

// supported features are: orx-camera, orx-compositor,orx-easing, orx-filter-extension,orx-file-watcher, orx-kinect-v1
// orx-integral-image, orx-interval-tree, orx-jumpflood,orx-kdtree, orx-mesh-generators,orx-midi, orx-no-clear,
// orx-noise, orx-obj, orx-olive

val orxFeatures = setOf<String>() //"orx-noise", "orx-kinect-v1"

repositories {
    mavenCentral()
    mavenLocal()
    maven(url = "https://dl.bintray.com/openrndr/openrndr")
}

fun DependencyHandler.orx(module: String): Any {
        return "org.openrndr.extra:$module:$orxVersion"
}

fun DependencyHandler.openrndr(module: String): Any {
    return "org.openrndr:openrndr-$module:$openrndrVersion"
}

fun DependencyHandler.openrndrNatives(module: String): Any {
    return "org.openrndr:openrndr-$module-natives-$openrndrOs:$openrndrVersion"
}

fun DependencyHandler.orxNatives(module: String): Any {
    return "org.openrndr.extra:$module-natives-$openrndrOs:$orxVersion"
}

fun DependencyHandler.xemanticOpenrndr(module: String): Any {
    return "com.xemantic.openrndr:xemantic-openrndr-$module:$xemanticOpenrndrVersion"
}

dependencies {
    compile(xemanticOpenrndr("core"))
//    compile(xemanticOpenrndr("color"))
    compile(xemanticOpenrndr("video"))

    runtime(openrndr("gl3"))
    runtime(openrndrNatives("gl3"))
//    compile(openrndr("core"))
//    compile(openrndr("svg"))
//    compile(openrndr("animatable"))
//    compile(openrndr("extensions"))
//    compile(openrndr("filter"))

//    compile("org.jetbrains.kotlinx", "kotlinx-coroutines-core","1.3.0-RC")

    if ("video" in openrndrFeatures) {
        compile(openrndr("ffmpeg"))
        runtime(openrndrNatives("ffmpeg"))
    }

    if ("panel" in openrndrFeatures) {
        compile("org.openrndr.panel:openrndr-panel:$panelVersion")
    }

    for (feature in orxFeatures) {
        compile(orx(feature))
    }

    if ("orx-olive" in orxFeatures) {
        compile("org.jetbrains.kotlin", "kotlin-scripting-compiler-embeddable")
    }

    if ("orx-kinect-v1" in orxFeatures) {
        runtime(orxNatives("orx-kinect-v1"))
    }

    implementation(kotlin("stdlib-jdk8"))
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
}
tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

tasks.withType<Jar> {
    manifest {
        attributes["Main-Class"] = applicationMainClass
        // Impl Version is important to detect if we are running from IDE or from jar archive
        attributes["Implementation-Version"] = archiveVersion
    }
    doFirst {
        from(configurations.compileClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
        from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
    }
    from("./") {
        include("media/**")
    }
    from("./src/main") {
        include("glsl/**")
    }
    exclude(listOf("META-INF/*.RSA", "META-INF/*.SF", "META-INF/*.DSA", "**/module-info*"))
    archiveFileName.set("application-$openrndrOs.jar")
}

tasks.create("run", JavaExec::class.java) {
    main = applicationMainClass
    classpath = sourceSets.main.get().runtimeClasspath
}.dependsOn(tasks.build)
