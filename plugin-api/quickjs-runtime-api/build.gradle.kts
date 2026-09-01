plugins {
    id("org.autojs.build.versions")
    id("org.autojs.build.jvm-convention")
    id("com.android.library")
}

android {
    namespace = "org.autojs.plugin.quickjs.runtime.api"
    compileSdk = versions.sdkVersionCompile

    defaultConfig {
        minSdk = versions.sdkVersionMin
        consumerProguardFiles("consumer-rules.pro")
    }

    lint {
        targetSdk = versions.sdkVersionTarget
        abortOnError = true
    }

    buildFeatures {
        aidl = true
        buildConfig = false
    }
}

dependencies {
    compileOnly(files("$rootDir/libs/common-plugin-api.aar"))
    testImplementation(libs.junit)
}
