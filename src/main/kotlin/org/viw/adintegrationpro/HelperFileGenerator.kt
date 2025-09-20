package org.viw.adintegrationpro

import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VfsUtil
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.command.WriteCommandAction
import java.io.IOException

object HelperFileGenerator {

    private val javaHelperContent = """
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
                // TODO: Initialize selected ad provider SDK here
                Toast.makeText(context, "Ads Initialized", Toast.LENGTH_SHORT).show();
            }

            public void showBanner(Activity activity) {
                // TODO: Load & show Banner Ad
                Toast.makeText(activity, "Banner Ad Shown", Toast.LENGTH_SHORT).show();
            }

            public void showInterstitial(Activity activity) {
                // TODO: Load & show Interstitial Ad
                Toast.makeText(activity, "Interstitial Ad Shown", Toast.LENGTH_SHORT).show();
            }

            public void showRewarded(Activity activity) {
                // TODO: Load & show Rewarded Ad
                Toast.makeText(activity, "Rewarded Ad Shown", Toast.LENGTH_SHORT).show();
            }

            public void showNative(Activity activity) {
                // TODO: Load & show Native Ad
                Toast.makeText(activity, "Native Ad Shown", Toast.LENGTH_SHORT).show();
            }
        }
    """.trimIndent()

    private val kotlinHelperContent = """
        package org.viw.adintegrationpro

        import android.app.Activity
        import android.content.Context
        import android.widget.Toast

        class AdHelper(private val context: Context) {

            fun initAds() {
                // TODO: Initialize selected ad provider SDK here
                Toast.makeText(context, "Ads Initialized", Toast.LENGTH_SHORT).show()
            }

            fun showBanner(activity: Activity) {
                // TODO: Load & show Banner Ad
                Toast.makeText(activity, "Banner Ad Shown", Toast.LENGTH_SHORT).show()
            }

            fun showInterstitial(activity: Activity) {
                // TODO: Load & show Interstitial Ad
                Toast.makeText(activity, "Interstitial Ad Shown", Toast.LENGTH_SHORT).show()
            }

            fun showRewarded(activity: Activity) {
                // TODO: Load & show Rewarded Ad
                Toast.makeText(activity, "Rewarded Ad Shown", Toast.LENGTH_SHORT).show()
            }

            fun showNative(activity: Activity) {
                // TODO: Load & show Native Ad
                Toast.makeText(activity, "Native Ad Shown", Toast.LENGTH_SHORT).show()
            }
        }
    """.trimIndent()

    fun generateHelpers(project: Project) {
        WriteCommandAction.runWriteCommandAction(project) {
            try {
                val baseDir: VirtualFile = project.baseDir
                val packageDir = VfsUtil.createDirectoryIfMissing(baseDir, "app/src/main/java/org/viw/adintegrationpro")

                createFile(packageDir, "AdHelper.java", javaHelperContent)
                createFile(packageDir, "AdHelper.kt", kotlinHelperContent)

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun createFile(dir: VirtualFile, fileName: String, content: String) {
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
}
