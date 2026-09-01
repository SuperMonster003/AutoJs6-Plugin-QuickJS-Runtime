import java.util.Properties

plugins {
    id("org.autojs.build.utils")
    id("org.autojs.build.versions")
    id("org.autojs.build.signs")
    id("org.autojs.build.jvm-convention")
    id("com.android.application")
}

val globalApplicationId = "io.github.supermonster003.autojs6.plugin.quickjs.runtime"
var isSignsValid = false

android {
    namespace = globalApplicationId
    compileSdk = versions.sdkVersionCompile

    defaultConfig {
        applicationId = globalApplicationId
        minSdk = versions.sdkVersionMin
        targetSdk = versions.sdkVersionTarget
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        versionCode = versions.appVersionCode
        versionName = versions.appVersionName

        resValue("string", "app_name", "QuickJS Runtime")
        resValue("string", "plugin_author", "SuperMonster003")
        resValue("string", "plugin_id", "quickjs-runtime")
        resValue("string", "plugin_engine", "quickjs")
        resValue("string", "plugin_variant", "quickjs-2026-06-04")
        resValue("string", "plugin_version_date", utils.getDateString("MMM d, yyyy", "GMT+08:00"))
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
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            releaseSigning?.let { signingConfig = it }
        }
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

tasks.register("appendDigestToReleasedFiles") {
    group = "distribution"
    description = "Reserved release collection entry point until native ABI packaging is added."
    dependsOn("assembleRelease")
    doLast {
        check(isSignsValid) {
            "Release signing configuration is missing or incomplete; refusing to collect unsigned APKs"
        }
        error("Native ABI release collection is not available in the repository bootstrap commit")
    }
}
