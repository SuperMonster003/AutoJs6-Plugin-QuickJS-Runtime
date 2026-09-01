import java.util.Properties

plugins {
    id("org.autojs.build.utils")
    id("org.autojs.build.versions")
    id("org.autojs.build.signs")
    id("org.autojs.build.jvm-convention")
    id("com.android.application")
}

val globalApplicationId = "io.github.supermonster003.autojs6.plugin.quickjs.runtime"
val pluginPermission = "org.autojs.permission.PLUGIN"
val androidTestPermissionName = providers.gradleProperty("androidTestPermissionName")
    .orElse(pluginPermission)
    .map(String::trim)
val requiredApiLevel = providers.gradleProperty("requiredApiLevel")
    .map(String::trim)
val supportedAbis = listOf("arm64-v8a", "armeabi-v7a", "x86_64", "x86")
var isSignsValid = false

android {
    namespace = globalApplicationId
    compileSdk = versions.sdkVersionCompile
    ndkVersion = "28.2.13676358"

    defaultConfig {
        applicationId = globalApplicationId
        minSdk = versions.sdkVersionMin
        targetSdk = versions.sdkVersionTarget
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        manifestPlaceholders["androidTestPermissionName"] = androidTestPermissionName.get().also { permissionName ->
            require(permissionName.matches(Regex("[A-Za-z][A-Za-z0-9_]*(\\.[A-Za-z][A-Za-z0-9_]*)+"))) {
                "androidTestPermissionName must be a fully-qualified Android permission name, but was '$permissionName'"
            }
        }
        requiredApiLevel.orNull?.let { apiLevel ->
            require(apiLevel.toIntOrNull() != null) {
                "requiredApiLevel must be an integer, but was '$apiLevel'"
            }
            testInstrumentationRunnerArguments["requiredApiLevel"] = apiLevel
        }
        versionCode = versions.appVersionCode
        versionName = versions.appVersionName

        resValue("string", "app_name", "QuickJS Runtime")
        resValue("string", "plugin_author", "SuperMonster003")
        resValue("string", "plugin_version_date", utils.getDateString("MMM d, yyyy", "GMT+08:00"))

        ndk {
            abiFilters += supportedAbis
        }

        externalNativeBuild {
            cmake {
                arguments += listOf("-DANDROID_STL=c++_static")
                cppFlags += listOf("-std=c++20", "-fvisibility=hidden")
            }
        }
    }

    signingConfigs {
        val props = Properties().also { properties ->
            File("${project.rootDir}/sign.properties").takeIf(File::exists)?.let { file ->
                file.inputStream().use(properties::load)
                isSignsValid = properties.isNotEmpty()
            }
        }
        if (isSignsValid) {
            create("release") {
                storeFile = props["storeFile"]?.let { file(it as String) }
                keyPassword = props["keyPassword"] as String
                keyAlias = props["keyAlias"] as String
                storePassword = props["storePassword"] as String
            }
        }
    }

    buildTypes {
        val releaseSigning = takeIf { isSignsValid }?.let { signingConfigs.getByName("release") }
        debug {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            releaseSigning?.let { signingConfig = it }
        }
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            releaseSigning?.let { signingConfig = it }
        }
    }

    externalNativeBuild {
        cmake {
            path = file("src/main/cpp/CMakeLists.txt")
            version = "3.22.1"
        }
    }

    splits {
        abi {
            isEnable = true
            reset()
            include(*supportedAbis.toTypedArray())
            isUniversalApk = true
        }
    }

    packaging {
        jniLibs.useLegacyPackaging = false
    }

    lint {
        abortOnError = true
    }

    buildFeatures {
        aidl = true
        resValues = true
        buildConfig = true
    }
}

dependencies {
    implementation(files("$rootDir/libs/common-plugin-api.aar"))
    implementation(projects.pluginApi.quickjsRuntimeApi)

    testImplementation(libs.junit)
    androidTestImplementation(libs.test.ext.junit)
    androidTestImplementation(libs.test.runner)
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
}

val pythonCommand = if (System.getProperty("os.name").startsWith("Windows", ignoreCase = true)) {
    "py"
} else {
    "python3"
}

tasks.register<Exec>("verifyQuickJsSource") {
    group = "verification"
    description = "Verifies the pinned official QuickJS source inventory."
    inputs.file(rootProject.file("tools/quickjs/source.lock.json"))
    inputs.file(rootProject.file("tools/quickjs/verify_source.py"))
    inputs.dir(layout.projectDirectory.dir("src/main/cpp/vendor/quickjs-2026-06-04"))
    commandLine(
        pythonCommand,
        "-B",
        rootProject.file("tools/quickjs/verify_source.py"),
    )
}

tasks.matching { it.name.startsWith("configureCMake") }.configureEach {
    dependsOn("verifyQuickJsSource")
}

tasks.register<Exec>("verifyDebugApkVariants") {
    group = "verification"
    description = "Verifies split APK inventory, native ABIs, and 16 KB ELF and ZIP alignment."
    dependsOn("assembleDebug", "assembleDebugAndroidTest")
    commandLine(
        pythonCommand,
        "-B",
        rootProject.file("tools/quickjs/verify_apk_variants.py"),
        layout.buildDirectory.dir("outputs/apk/debug").get().asFile,
        "debug",
        "--instrumentation-apk",
        layout.buildDirectory.file("outputs/apk/androidTest/debug/app-debug-androidTest.apk").get().asFile,
    )
}

tasks.register<Copy>("appendDigestToReleasedFiles") {
    description = "Collects signed ABI and universal APKs with CRC32 in each name."
    val ext = utils.FILE_EXTENSION_APK
    val src = layout.buildDirectory.dir("outputs/apk/release")
    val dst = layout.projectDirectory.dir("releases")
    val expected = (supportedAbis + "universal").mapTo(mutableSetOf()) { abi ->
        "app-$abi-release.$ext"
    }
    dependsOn("assembleRelease")
    doFirst {
        check(isSignsValid) {
            "Release signing configuration is missing or incomplete; refusing to collect unsigned APKs"
        }
        val actual = src.get().asFile.listFiles { file -> file.isFile && file.extension == ext }
            .orEmpty().mapTo(mutableSetOf()) { it.name }
        check(actual == expected) { "Expected exactly ${expected.sorted()}, but found ${actual.sorted()}" }
    }
    from(src)
    into(dst)
    include("*.$ext")
    rename { name ->
        val abi = name.replace(Regex("^app-(.+?)-release(\\.$ext)$"), "$1")
        val prefix = "${rootProject.name}-v${versions.appVersionName}-$abi"
        "$prefix-${utils.digestCRC32(src.get().file(name).asFile)}.$ext"
    }
}
