plugins {
    id("java")
    id("org.jetbrains.kotlin.jvm") version "2.1.0"
    id("org.jetbrains.intellij.platform") version "2.7.1"
}

group = "org.viw"
version = "2026.1.2"

repositories {
    mavenCentral()
    google()
    intellijPlatform {
        defaultRepositories()
    }
}

// Configure IntelliJ Platform Gradle Plugin
// Read more: https://plugins.jetbrains.com/docs/intellij/tools-intellij-platform-gradle-plugin.html
dependencies {
    intellijPlatform {
        create("IC", "2025.1.4.1")
        testFramework(org.jetbrains.intellij.platform.gradle.TestFrameworkType.Platform)

      // Add necessary plugin dependencies for compilation here, example:
      // bundledPlugin("com.intellij.java")
    }
}

intellijPlatform {
    instrumentCode.set(false)

    pluginConfiguration {
        ideaVersion {
            sinceBuild = "251"
        }

        changeNotes = """
<h2>Ad Integration Pro 2026.1.2</h2>

    <p>
        This update introduces the new <b>Freemium experience</b>
        for Ad Integration Pro, with official JetBrains Marketplace
        licensing for Pro features.
    </p>

    <h3>✨ What's New</h3>
    <ul>
        <li>
            Added official <b>JetBrains Marketplace licensing</b>
            for Pro features.
        </li>
        <li>
            Added a streamlined <b>Buy Pro</b> activation flow
            through the JetBrains licensing system.
        </li>
        <li>
            Improved separation between <b>Free</b> and
            <b>Pro</b> features.
        </li>
    </ul>

    <h3>🆓 Free Features</h3>
    <ul>
        <li>Google AdMob integration</li>
        <li>Banner Ads</li>
        <li>Interstitial Ads</li>
        <li>Basic Gradle and Manifest configuration</li>
    </ul>

    <h3>💎 Pro Features</h3>
    <ul>
        <li>Unity Ads integration</li>
        <li>Meta / Facebook Audience Network integration</li>
        <li>IronSource integration</li>
        <li>Rewarded Ads</li>
        <li>Native Ads</li>
    </ul>

    <h3>🔧 Improvements</h3>
    <ul>
        <li>Improved Pro feature license verification.</li>
        <li>Improved purchase and license activation experience.</li>
        <li>Removed the old manual license-key activation flow.</li>
    </ul>

    <p>
        Thank you for using <b>Ad Integration Pro</b>!
    </p>
        """.trimIndent()
    }
}
tasks {
    // Set the JVM compatibility versions
    withType<JavaCompile> {
        sourceCompatibility = "21"
        targetCompatibility = "21"
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21)
    }
}
