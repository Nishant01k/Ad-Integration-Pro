package org.viw.adintegrationpro.actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.ui.DialogBuilder
import org.viw.adintegrationpro.core.FileModifier
import org.viw.adintegrationpro.core.HelperFileGenerator
import org.viw.adintegrationpro.core.LicenseManager
import org.viw.adintegrationpro.ui.UpgradeDialog
import java.awt.Font
import javax.swing.*

class AdSetupWizardAction : AnAction() {

    override fun actionPerformed(e: AnActionEvent) {

        val project = e.project ?: return

        while (true) {

            // ==========================================
            // STEP 1 — PROVIDER
            // ==========================================

            val providerPanel = JPanel()

            providerPanel.add(
                JLabel("Select Ad Provider:")
            )

            val providers = arrayOf(
                "Google AdMob",
                "Unity Ads (Pro)",
                "Facebook Audience (Pro)",
                "IronSource (Pro)"
            )

            val providerCombo = JComboBox(providers)

            providerPanel.add(providerCombo)

            val step1 = DialogBuilder()

            step1.setTitle("Ad Integration Assistant")
            step1.setCenterPanel(providerPanel)

            step1.addOkAction()
            step1.addCancelAction()

            if (step1.show() != 0) {
                return
            }

            val selectedProviderRaw =
                providerCombo.selectedItem as String

            val isProProvider =
                selectedProviderRaw != "Google AdMob"

            // ==========================================
            // CHECK PROVIDER LICENSE
            // ==========================================

            if (isProProvider && !LicenseManager.isProVersion()) {

                UpgradeDialog().show()

                continue
            }

            val selectedProvider =
                selectedProviderRaw
                    .removeSuffix(" (Pro)")


            // ==========================================
            // STEP 2 — AD FORMAT
            // ==========================================

            val formatPanel = JPanel()

            formatPanel.layout =
                BoxLayout(
                    formatPanel,
                    BoxLayout.Y_AXIS
                )

            val providerInfo =
                JLabel(
                    "Selected Provider: $selectedProvider"
                )

            providerInfo.font =
                providerInfo.font.deriveFont(
                    Font.BOLD
                )

            formatPanel.add(providerInfo)

            formatPanel.add(
                JLabel("Select Ad Formats:")
            )

            val banner =
                JCheckBox("Banner Ad — Free")

            val interstitial =
                JCheckBox("Interstitial Ad — Free")

            val rewarded =
                JCheckBox("Rewarded Ad — Pro")

            val nativeAd =
                JCheckBox("Native Ad — Pro")

            formatPanel.add(banner)
            formatPanel.add(interstitial)
            formatPanel.add(rewarded)
            formatPanel.add(nativeAd)

            val step2 = DialogBuilder()

            step2.setTitle(
                "Ad Integration Assistant"
            )

            step2.setCenterPanel(
                formatPanel
            )

            step2.addOkAction()
            step2.addCancelAction()

            if (step2.show() != 0) {
                return
            }

            // ==========================================
            // FORMAT SELECTION
            // ==========================================

            val selectedFormats =
                mutableListOf<String>()

            if (banner.isSelected) {
                selectedFormats.add("Banner")
            }

            if (interstitial.isSelected) {
                selectedFormats.add("Interstitial")
            }

            val wantsProFormat =
                rewarded.isSelected ||
                        nativeAd.isSelected

            // ==========================================
            // CHECK PRO FORMAT
            // ==========================================

            if (
                wantsProFormat &&
                !LicenseManager.isProVersion()
            ) {

                UpgradeDialog().show()

                continue
            }

            if (rewarded.isSelected) {
                selectedFormats.add("Rewarded")
            }

            if (nativeAd.isSelected) {
                selectedFormats.add("Native")
            }

            // ==========================================
            // VALIDATION
            // ==========================================

            if (selectedFormats.isEmpty()) {

                JOptionPane.showMessageDialog(
                    null,
                    "Please select at least one ad format!",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
                )

                continue
            }

            // ==========================================
            // APPLY MODIFICATIONS
            // ==========================================

            FileModifier.addDependency(
                project,
                selectedProvider
            )

            FileModifier.updateManifest(
                project,
                selectedProvider
            )

            HelperFileGenerator.generateHelper(
                project,
                selectedProvider,
                selectedFormats
            )

            // ==========================================
            // SUCCESS
            // ==========================================

            JOptionPane.showMessageDialog(
                null,
                """
                Ad integration completed!

                Provider:
                $selectedProvider

                Formats:
                $selectedFormats
                """.trimIndent(),
                "Success",
                JOptionPane.INFORMATION_MESSAGE
            )

            return
        }
    }
}