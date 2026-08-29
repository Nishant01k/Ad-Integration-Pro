package org.viw.adintegrationpro.core

import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.VfsUtil
import com.intellij.openapi.vfs.VirtualFile

object FileModifier {

    // ============================================================
    // GRADLE DEPENDENCY
    // Supports:
    // app/build.gradle
    // app/build.gradle.kts
    // ============================================================

    fun addDependency(
        project: Project,
        provider: String
    ) {

        val gradleFile =
            findProjectFile(
                project,
                "app/build.gradle.kts"
            )
                ?: findProjectFile(
                    project,
                    "app/build.gradle"
                )
                ?: return

        val dependencyCoordinate =
            when (provider) {

                "Google AdMob" ->
                    "com.google.android.gms:play-services-ads:23.3.0"

                "Unity Ads" ->
                    "com.unity3d.ads:unity-ads:4.12.0"

                "Facebook Audience" ->
                    "com.facebook.android:audience-network-sdk:6.+"

                "IronSource" ->
                    "com.ironsource.sdk:mediationsdk:8.3.0"

                else -> return
            }

        val dependencyLine =
            if (gradleFile.name.endsWith(".kts")) {

                // Kotlin DSL
                """implementation("$dependencyCoordinate")"""

            } else {

                // Groovy DSL
                """implementation '$dependencyCoordinate'"""
            }

        WriteCommandAction.runWriteCommandAction(project) {

            var content =
                VfsUtil.loadText(gradleFile)

            // Do not add dependency twice
            if (content.contains(dependencyCoordinate)) {
                return@runWriteCommandAction
            }

            val dependenciesRegex =
                Regex("""dependencies\s*\{""")

            val match =
                dependenciesRegex.find(content)

            content =
                if (match != null) {

                    val insertPosition =
                        match.range.last + 1

                    content.substring(
                        0,
                        insertPosition
                    ) +
                            "\n    $dependencyLine" +
                            content.substring(
                                insertPosition
                            )

                } else {

                    // Very unusual project with no dependencies block
                    content.trimEnd() +
                            """



                            dependencies {
                                $dependencyLine
                            }
                            """.trimIndent()
                }

            VfsUtil.saveText(
                gradleFile,
                content
            )
        }
    }


    // ============================================================
    // ANDROID MANIFEST
    // ============================================================

    fun updateManifest(
        project: Project,
        provider: String
    ) {

        val manifestFile =
            findProjectFile(
                project,
                "app/src/main/AndroidManifest.xml"
            )
                ?: return

        WriteCommandAction.runWriteCommandAction(project) {

            var content =
                VfsUtil.loadText(
                    manifestFile
                )


            // ====================================================
            // INTERNET PERMISSION
            // Needed by all ad providers
            // ====================================================

            content =
                ensurePermission(
                    content,
                    "android.permission.INTERNET"
                )


            // ====================================================
            // ACCESS NETWORK STATE
            // ====================================================

            content =
                ensurePermission(
                    content,
                    "android.permission.ACCESS_NETWORK_STATE"
                )


            // ====================================================
            // PROVIDER-SPECIFIC CONFIG
            // ====================================================

            when (provider) {

                // ------------------------------------------------
                // GOOGLE ADMOB
                // ------------------------------------------------

                "Google AdMob" -> {

                    if (
                        !content.contains(
                            "com.google.android.gms.ads.APPLICATION_ID"
                        )
                    ) {

                        val metadata =
                            """
                            <meta-data
                                android:name="com.google.android.gms.ads.APPLICATION_ID"
                                android:value="ca-app-pub-xxxxxxxxxxxxxxxx~yyyyyyyyyy" />
                            """.trimIndent()

                        content =
                            ensureInsideApplication(
                                content,
                                metadata
                            )
                    }
                }


                // ------------------------------------------------
                // UNITY ADS
                // ------------------------------------------------

                "Unity Ads" -> {

                    // Internet/network permissions
                    // are already inserted above.

                }


                // ------------------------------------------------
                // FACEBOOK / META AUDIENCE NETWORK
                // ------------------------------------------------

                "Facebook Audience" -> {

                    if (
                        !content.contains(
                            "com.facebook.ads.AudienceNetworkActivity"
                        )
                    ) {

                        val activity =
                            """
                            <activity
                                android:name="com.facebook.ads.AudienceNetworkActivity"
                                android:configChanges="keyboardHidden|orientation" />
                            """.trimIndent()

                        content =
                            ensureInsideApplication(
                                content,
                                activity
                            )
                    }
                }


                // ------------------------------------------------
                // IRONSOURCE
                // ------------------------------------------------

                "IronSource" -> {

                    content =
                        ensurePermission(
                            content,
                            "com.google.android.gms.permission.AD_ID"
                        )
                }
            }


            // ====================================================
            // SAVE
            // ====================================================

            VfsUtil.saveText(
                manifestFile,
                content
            )
        }
    }


    // ============================================================
    // FIND FILE WITHOUT project.baseDir
    //
    // This also removes the deprecated baseDir warning.
    // ============================================================

    private fun findProjectFile(
        project: Project,
        relativePath: String
    ): VirtualFile? {

        val basePath =
            project.basePath
                ?: return null

        val fullPath =
            "$basePath/$relativePath"
                .replace("\\", "/")

        return LocalFileSystem
            .getInstance()
            .refreshAndFindFileByPath(
                fullPath
            )
    }


    // ============================================================
    // ADD PERMISSION SAFELY
    // ============================================================

    private fun ensurePermission(
        content: String,
        permission: String
    ): String {

        // Permission already exists
        if (
            content.contains(
                """android:name="$permission""""
            )
        ) {
            return content
        }

        val permissionLine =
            """    <uses-permission android:name="$permission" />"""

        // Find <application>
        val applicationMatch =
            Regex("""<application\b""")
                .find(content)

        if (applicationMatch != null) {

            // Insert permission BEFORE <application>
            val lineStart =
                content.lastIndexOf(
                    '\n',
                    applicationMatch.range.first
                )
                    .let {
                        if (it == -1) 0
                        else it + 1
                    }

            return content.substring(
                0,
                lineStart
            ) +
                    permissionLine +
                    "\n" +
                    content.substring(
                        lineStart
                    )
        }

        // Fallback:
        // if application tag cannot be found,
        // put permission before </manifest>
        return content.replace(
            "</manifest>",
            "$permissionLine\n</manifest>"
        )
    }


    // ============================================================
    // INSERT XML INSIDE EXISTING <application>
    //
    // IMPORTANT:
    // This prevents creation of a second <application>.
    // ============================================================

    private fun ensureInsideApplication(
        content: String,
        xml: String
    ): String {

        val applicationRegex =
            Regex(
                """<application\b[^>]*>"""
            )

        val match =
            applicationRegex.find(content)
                ?: return content

        val lineStart =
            content.lastIndexOf(
                '\n',
                match.range.first
            )
                .let {
                    if (it == -1) 0
                    else it + 1
                }

        val applicationIndent =
            content.substring(
                lineStart,
                match.range.first
            )

        val childIndent =
            applicationIndent + "    "

        val indentedXml =
            xml
                .lines()
                .joinToString("\n") {
                    childIndent + it
                }

        val insertPosition =
            match.range.last + 1

        return content.substring(
            0,
            insertPosition
        ) +
                "\n" +
                indentedXml +
                content.substring(
                    insertPosition
                )
    }
}