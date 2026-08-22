package org.viw.adintegrationpro.core

import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VfsUtil

object FileModifier {

    fun addDependency(project: Project, provider: String) {
        val gradleFile = project.baseDir.findFileByRelativePath("app/build.gradle") ?: return

        val dependency = when (provider) {
            "Google AdMob" -> "implementation 'com.google.android.gms:play-services-ads:23.3.0'"
            "Unity Ads" -> "implementation 'com.unity3d.ads:unity-ads:4.12.0'"
            "Facebook Audience" -> "implementation 'com.facebook.android:audience-network-sdk:6.+'"
            "IronSource" -> "implementation 'com.ironsource.sdk:mediationsdk:8.3.0'"
            else -> return
        }

        WriteCommandAction.runWriteCommandAction(project) {
            val content = VfsUtil.loadText(gradleFile)
            if (!content.contains(dependency)) {
                val updated = content.replace("dependencies {", "dependencies {\n    $dependency")
                VfsUtil.saveText(gradleFile, updated)
            }
        }
    }

    fun updateManifest(project: Project, provider: String) {
        val manifestFile = project.baseDir.findFileByRelativePath("app/src/main/AndroidManifest.xml") ?: return

        WriteCommandAction.runWriteCommandAction(project) {
            var content = VfsUtil.loadText(manifestFile)

            when (provider) {
                "Google AdMob" -> {
                    if (!content.contains("com.google.android.gms.ads.APPLICATION_ID")) {
                        val admobConfig = """
                            <uses-permission android:name="android.permission.INTERNET"/>
                            <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>
                            
                            <application>
                                <meta-data
                                    android:name="com.google.android.gms.ads.APPLICATION_ID"
                                    android:value="ca-app-pub-xxxxxxxxxxxxxxxx~yyyyyyyyyy"/>
                            </application>
                        """.trimIndent()

                        content = content.replace("</manifest>", "$admobConfig\n</manifest>")
                    }
                }

                "Unity Ads" -> {
                    if (!content.contains("unity.ads")) {
                        val unityConfig = """
                            <uses-permission android:name="android.permission.INTERNET"/>
                            <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>
                        """.trimIndent()

                        content = content.replace("</manifest>", "$unityConfig\n</manifest>")
                    }
                }

                "Facebook Audience" -> {
                    if (!content.contains("com.facebook.ads.AudienceNetworkActivity")) {
                        val fbConfig = """
                            <uses-permission android:name="android.permission.INTERNET"/>
                            <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>

                            <application>
                                <activity android:name="com.facebook.ads.AudienceNetworkActivity"
                                    android:configChanges="keyboardHidden|orientation"/>
                            </application>
                        """.trimIndent()

                        content = content.replace("</manifest>", "$fbConfig\n</manifest>")
                    }
                }

                "IronSource" -> {
                    if (!content.contains("com.ironsource")) {
                        val ironConfig = """
                            <uses-permission android:name="android.permission.INTERNET"/>
                            <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>
                            <uses-permission android:name="com.google.android.gms.permission.AD_ID"/>
                        """.trimIndent()

                        content = content.replace("</manifest>", "$ironConfig\n</manifest>")
                    }
                }
            }

            VfsUtil.saveText(manifestFile, content)
        }
    }
}