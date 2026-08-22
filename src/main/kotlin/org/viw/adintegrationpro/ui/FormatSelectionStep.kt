package org.viw.adintegrationpro.ui

import org.viw.adintegrationpro.features.AdFormat
import javax.swing.*

object FormatSelectionStep {
    fun showDialog(providerName: String): List<AdFormat>? {
        val panel = JPanel()
        panel.layout = BoxLayout(panel, BoxLayout.Y_AXIS)

        panel.add(JLabel("Selected Provider: $providerName"))
        panel.add(JLabel("Select Ad Formats:"))

        val checkboxes = AdFormat.values().map { JCheckBox(it.displayName) }
        checkboxes.forEach { panel.add(it) }

        val result = JOptionPane.showConfirmDialog(
            null, panel, "Ad Integration Assistant",
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE
        )

        return if (result == JOptionPane.OK_OPTION) {
            checkboxes.filter { it.isSelected }
                .map { adBox -> AdFormat.values().first { it.displayName == adBox.text } }
        } else null
    }
}