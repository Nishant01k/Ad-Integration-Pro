package org.viw.adintegrationpro.ui

import org.viw.adintegrationpro.features.AdProvider
import javax.swing.*

object ProviderSelectionStep {
    fun showDialog(): AdProvider? {
        val combo = JComboBox(AdProvider.values())
        val panel = JPanel()
        panel.add(JLabel("Select Ad Provider:"))
        panel.add(combo)

        val result = JOptionPane.showConfirmDialog(
            null, panel, "Ad Integration Assistant",
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE
        )

        return if (result == JOptionPane.OK_OPTION) {
            combo.selectedItem as AdProvider
        } else null
    }
}