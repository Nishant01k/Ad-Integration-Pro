package org.viw.adintegrationpro.ui

import com.intellij.ide.BrowserUtil
import com.intellij.openapi.ui.DialogWrapper
import java.awt.BorderLayout
import java.awt.Dimension
import javax.swing.JButton
import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.JPanel

class UpgradeDialog : DialogWrapper(true) {

    companion object {
        private const val MARKETPLACE_URL =
            "https://plugins.jetbrains.com/plugin/28580-ad-integration-pro"
    }

    init {
        title = "Ad Integration Pro — Pro Feature"
        setOKButtonText("Start Trial / Buy Pro")
        init()
    }

    override fun createCenterPanel(): JComponent {
        val panel = JPanel(BorderLayout(10, 15))
        panel.preferredSize = Dimension(500, 220)

        val message = JLabel(
            """
            <html>
            <h2>Ad Integration Pro — Pro Feature</h2>
            <p>This feature is available in the Pro version.</p>
            <p>
            Start your <b>30-day trial</b> or purchase a Pro license
            through JetBrains Marketplace.
            </p>
            <br>
            <p>
            You can also manage your plugin license from
            <b>Help → Register</b>.
            </p>
            </html>
            """.trimIndent()
        )

        panel.add(message, BorderLayout.CENTER)

        return panel
    }

    override fun doOKAction() {
        BrowserUtil.browse(MARKETPLACE_URL)
        super.doOKAction()
    }

    override fun createActions(): Array<javax.swing.Action> {
        return arrayOf(okAction)
    }
}