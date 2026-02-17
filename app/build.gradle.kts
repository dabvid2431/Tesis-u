plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("org.jetbrains.kotlin.kapt")
    id("jacoco")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.tuempresa.stockapp"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.tuempresa.stockapp"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    // Default BASE_URL for development (emulator). For physical device testing use your PC IP (updated below).
    buildConfigField("String", "BASE_URL", "\"http://192.168.18.15:3000/api/\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        viewBinding = true
        // Enable generation of BuildConfig fields so BuildConfig.BASE_URL is available
        buildConfig = true
    }
    
    packaging {
        resources {
            excludes += setOf(
                "META-INF/NOTICE",
                "META-INF/LICENSE",
                "META-INF/DEPENDENCIES",
                "META-INF/INDEX.LIST",
                "META-INF/io.netty.versions.properties",
                "META-INF/*.kotlin_module",
                "META-INF/versions/9/module-info.class"
            )
        }
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)


    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.material)
    
    // Navigation Component
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.7")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.7")
    
    // RecyclerView
    implementation("androidx.recyclerview:recyclerview:1.3.1")
    
    // Retrofit para API
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.9.3")
    
    // Lifecycle
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.7.0")

    // Offline-first sync
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    add("kapt", "androidx.room:room-compiler:2.6.1")
    implementation("androidx.work:work-runtime-ktx:2.9.1")
    
    // Security - Encrypted SharedPreferences
    implementation("androidx.security:security-crypto:1.1.0-alpha06")
    
    // PDF Generation
    implementation("com.itextpdf:itext7-core:7.2.5")
    
    // CSV es suficiente - Excel (Apache POI) removido por incompatibilidad con Android API 24
    
    // Firebase for Cloud Backup
    implementation(platform("com.google.firebase:firebase-bom:32.7.1"))
    implementation("com.google.firebase:firebase-storage-ktx")
    implementation("com.google.firebase:firebase-auth-ktx")
    
    // Testing
    testImplementation(libs.junit)
    // Core testing for LiveData (InstantTaskExecutorRule)
    testImplementation("androidx.arch.core:core-testing:2.2.0")
    testImplementation("org.mockito:mockito-core:4.11.0")
    testImplementation("com.squareup.okhttp3:okhttp:4.9.3")
    testImplementation("com.squareup.retrofit2:retrofit-mock:2.9.0")

// JaCoCo report helper (already added jacoco toolVersion above)

    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}

// Task to generate JaCoCo report for unit tests (debug variant)
// Use the Gradle Jacoco report task type from 'org.gradle.testing.jacoco.tasks.JacocoReport'
val jacocoTestReport by tasks.registering(org.gradle.testing.jacoco.tasks.JacocoReport::class) {
    dependsOn("testDebugUnitTest")

    val debugTree = fileTree("${project.buildDir}/tmp/kotlin-classes/debug") {
        exclude("**/R.class", "**/R\$*.class", "**/*\$ViewInjector*", "**/*\$ViewBinder*")
    }
    val mainSrc = files("src/main/java", "src/main/kotlin")

    classDirectories.setFrom(files(debugTree))
    sourceDirectories.setFrom(mainSrc)

    executionData.setFrom(files(
        "${project.buildDir}/jacoco/testDebugUnitTest.exec",
        "${project.buildDir}/outputs/unit_test_code_coverage/debugUnitTest/testDebugUnitTest.exec",
        "${project.buildDir}/tmp/jacoco/testDebugUnitTest.exec"
    ))

    reports {
        xml.required.set(true)
        html.required.set(true)
        csv.required.set(false)
        html.outputLocation.set(file("${project.buildDir}/reports/jacoco"))
    }
}

// Configure JaCoCo extension for unit tests
tasks.withType(Test::class).configureEach {
    extensions.configure(org.gradle.testing.jacoco.plugins.JacocoTaskExtension::class) {
        isIncludeNoLocationClasses = true
        // Optionally exclude internal JDK classes
        excludes = listOf("jdk.internal.*")
    }
}
