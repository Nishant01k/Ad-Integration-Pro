package org.viw.adintegrationpro.core

import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VfsUtil
import com.intellij.openapi.vfs.VirtualFile
import java.io.File
import java.io.IOException

object HelperFileGenerator {

    fun generateHelper(project: Project, provider: String, formats: List<String>) {
        val isKotlin = isKotlinProject(project)

        WriteCommandAction.runWriteCommandAction(project) {
            try {
                val basePath = project.basePath ?: return@runWriteCommandAction
                val baseDir = VfsUtil.findFile(File(basePath).toPath(), true) ?: return@runWriteCommandAction

                // Generate in a fixed package
                val packageDir = VfsUtil.createDirectoryIfMissing(
                    baseDir,
                    "app/src/main/java/org/viw/adintegrationpro"
                ) ?: return@runWriteCommandAction

                val fileName = if (isKotlin) "AdHelper.kt" else "AdHelper.java"
                val content = if (isKotlin) buildKotlinHelper(provider, formats) else buildJavaHelper(provider, formats)

                createOrUpdateFile(packageDir, fileName, content)

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun buildJavaHelper(provider: String, formats: List<String>): String {
        val initCode = when (provider) {
            "Google AdMob" -> "MobileAds.initialize(context);"
            "Unity Ads" -> "UnityAds.initialize((Activity) context, \"YOUR_UNITY_GAME_ID\", false);"
            "Facebook Audience" -> "AudienceNetworkAds.initialize(context);"
            "IronSource" -> "IronSource.init((Activity) context, \"YOUR_IRONSOURCE_APP_KEY\");"
            else -> "// No provider selected"
        }

        return """
            package org.viw.adintegrationpro;

            import android.app.Activity;
            import android.content.Context;
            import android.widget.Toast;

            public class AdHelper {
                private Context context;

                public AdHelper(Context context) {
                    this.context = context;
                }

                public void initAds() {
                    $initCode
                    Toast.makeText(context, "$provider initialized", Toast.LENGTH_SHORT).show();
                }

                ${if (formats.contains("Banner")) bannerJava() else ""}
                ${if (formats.contains("Interstitial")) interstitialJava() else ""}
                ${if (formats.contains("Rewarded")) rewardedJava() else ""}
                ${if (formats.contains("Native")) nativeJava() else ""}
            }
        """.trimIndent()
    }

    private fun buildKotlinHelper(provider: String, formats: List<String>): String {
        val initCode = when (provider) {
            "Google AdMob" -> "MobileAds.initialize(context)"
            "Unity Ads" -> "UnityAds.initialize(context as Activity, \"YOUR_UNITY_GAME_ID\", false)"
            "Facebook Audience" -> "AudienceNetworkAds.initialize(context)"
            "IronSource" -> "IronSource.init(context as Activity, \"YOUR_IRONSOURCE_APP_KEY\")"
            else -> "// No provider selected"
        }

        return """
            package org.viw.adintegrationpro

            import android.app.Activity
            import android.content.Context
            import android.widget.Toast

            class AdHelper(private val context: Context) {

                fun initAds() {
                    $initCode
                    Toast.makeText(context, "$provider initialized", Toast.LENGTH_SHORT).show()
                }

                ${if (formats.contains("Banner")) bannerKotlin() else ""}
                ${if (formats.contains("Interstitial")) interstitialKotlin() else ""}
                ${if (formats.contains("Rewarded")) rewardedKotlin() else ""}
                ${if (formats.contains("Native")) nativeKotlin() else ""}
            }
        """.trimIndent()
    }

    // --------------------
    // Stub format methods
    // --------------------
    private fun bannerJava() = """
        public void showBanner(Activity activity) {
            Toast.makeText(activity, "Banner Ad Shown", Toast.LENGTH_SHORT).show();
        }
    """

    private fun interstitialJava() = """
        public void showInterstitial(Activity activity) {
            Toast.makeText(activity, "Interstitial Ad Shown", Toast.LENGTH_SHORT).show();
        }
    """

    private fun rewardedJava() = """
        public void showRewarded(Activity activity) {
            Toast.makeText(activity, "Rewarded Ad Shown", Toast.LENGTH_SHORT).show();
        }
    """

    private fun nativeJava() = """
        public void showNative(Activity activity) {
            Toast.makeText(activity, "Native Ad Shown", Toast.LENGTH_SHORT).show();
        }
    """

    private fun bannerKotlin() = """
        fun showBanner(activity: Activity) {
            Toast.makeText(activity, "Banner Ad Shown", Toast.LENGTH_SHORT).show()
        }
    """

    private fun interstitialKotlin() = """
        fun showInterstitial(activity: Activity) {
            Toast.makeText(activity, "Interstitial Ad Shown", Toast.LENGTH_SHORT).show()
        }
    """

    private fun rewardedKotlin() = """
        fun showRewarded(activity: Activity) {
            Toast.makeText(activity, "Rewarded Ad Shown", Toast.LENGTH_SHORT).show()
        }
    """

    private fun nativeKotlin() = """
        fun showNative(activity: Activity) {
            Toast.makeText(activity, "Native Ad Shown", Toast.LENGTH_SHORT).show()
        }
    """

    private fun createOrUpdateFile(dir: VirtualFile, fileName: String, content: String) {
        try {
            val existingFile = dir.findChild(fileName)
            if (existingFile != null) {
                existingFile.setBinaryContent(content.toByteArray())
            } else {
                val newFile = dir.createChildData(this, fileName)
                newFile.setBinaryContent(content.toByteArray())
            }
        } catch (ioe: IOException) {
            ioe.printStackTrace()
        }
    }

    private fun isKotlinProject(project: Project): Boolean {
        val basePath = project.basePath ?: return false
        val srcDir = File(basePath, "app/src/main/java")
        if (!srcDir.exists()) return false

        return srcDir.walkTopDown().any { it.extension == "kt" }
    }
}