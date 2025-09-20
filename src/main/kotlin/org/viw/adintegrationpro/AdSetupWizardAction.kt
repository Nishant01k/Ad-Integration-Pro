package org.viw.adintegrationpro

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.ui.DialogBuilder
import java.awt.Font
import javax.swing.*

class AdSetupWizardAction : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return   // always check project is not null

        while (true) {
            // --- STEP 1: Select Provider ---
            val providerPanel = JPanel()
            val providerLabel = JLabel("Select Ad Provider:")
            val providerCombo = JComboBox(arrayOf("Google AdMob", "Unity Ads", "Facebook Audience", "IronSource"))
            providerPanel.add(providerLabel)
            providerPanel.add(providerCombo)

            val step1 = DialogBuilder()
            step1.setTitle("Ad Integration Assistant")
            step1.setCenterPanel(providerPanel)
            step1.addOkAction()
            step1.addCancelAction()

            if (step1.show() != 0) {
                // User cancelled Step 1 -> exit wizard
                break
            }

            val selectedProvider = providerCombo.selectedItem as String

            // --- STEP 2: Select Ad Formats ---
            while (true) {
                val formatPanel = JPanel()
                formatPanel.layout = BoxLayout(formatPanel, BoxLayout.Y_AXIS)

                // Show the selected provider at the top
                val providerInfoLabel = JLabel("Selected Provider: $selectedProvider")
                providerInfoLabel.font = providerInfoLabel.font.deriveFont(Font.BOLD)
                providerInfoLabel.border = BorderFactory.createEmptyBorder(0, 0, 10, 0)

                val banner = JCheckBox("Banner Ad")
                val interstitial = JCheckBox("Interstitial Ad")
                val rewarded = JCheckBox("Rewarded Ad")
                val nativeAd = JCheckBox("Native Ad")

                formatPanel.add(providerInfoLabel)
                formatPanel.add(JLabel("Select Ad Formats:"))
                formatPanel.add(banner)
                formatPanel.add(interstitial)
                formatPanel.add(rewarded)
                formatPanel.add(nativeAd)

                val step2 = DialogBuilder()
                step2.setTitle("Ad Integration Assistant")
                step2.setCenterPanel(formatPanel)
                step2.addOkAction()
                step2.addCancelAction()

                val result = step2.show()
                if (result == 0) {
                    // OK clicked
                    if (!banner.isSelected && !interstitial.isSelected && !rewarded.isSelected && !nativeAd.isSelected) {
                        JOptionPane.showMessageDialog(
                            null,
                            "Please select at least one ad format!",
                            "Error",
                            JOptionPane.ERROR_MESSAGE
                        )
                    } else {
                        val selectedFormats = mutableListOf<String>()
                        if (banner.isSelected) selectedFormats.add("Banner")
                        if (interstitial.isSelected) selectedFormats.add("Interstitial")
                        if (rewarded.isSelected) selectedFormats.add("Rewarded")
                        if (nativeAd.isSelected) selectedFormats.add("Native")

                        // ✅ Insert Gradle + Manifest changes
                        FileModifier.addDependency(project, selectedProvider)
                        FileModifier.updateManifest(project, selectedProvider)

                        // ✅ Generate Helper Classes
                        HelperFileGenerator.generateHelpers(project)

                        // Debug log
                        println("Provider: $selectedProvider")
                        println("Formats: $selectedFormats")

                        JOptionPane.showMessageDialog(
                            null,
                            "Ad integration setup completed for $selectedProvider with formats: $selectedFormats\n" +
                                    "Helper classes (AdHelper.java & AdHelper.kt) have been generated.",
                            "Success",
                            JOptionPane.INFORMATION_MESSAGE
                        )

                        return  // exit wizard after successful integration
                    }
                } else {
                    // Cancel pressed in Step 2 -> go back to Step 1
                    break
                }
            }
        }
    }
}
