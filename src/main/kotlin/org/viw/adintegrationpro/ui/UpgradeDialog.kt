package org.viw.adintegrationpro.ui

import com.intellij.ide.BrowserUtil
import com.intellij.openapi.ui.DialogWrapper
import java.awt.BorderLayout
import java.awt.Dimension
import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.JPanel

class UpgradeDialog : DialogWrapper(true) {

    companion object {

        /*
         * PRODUCTION MARKETPLACE PAGE
         *
         * Keep this URL when releasing the real plugin.
         */
        private const val MARKETPLACE_URL =
            "https://plugins.jetbrains.com/plugin/28580-ad-integration-pro/edit/pricing"

        /*
         * DEMO / SANDBOX PURCHASE URL
         *
         * While testing payment in JetBrains Marketplace Demo,
         * temporarily use this instead:
         *
         * https://master.demo.marketplace.intellij.net/purchase-link/PADINTEGRATIONP
         *
         * IMPORTANT:
         * Change back to MARKETPLACE_URL before publishing
         * the production plugin.
         */
    }

    init {
        title = "Ad Integration Pro — Pro Feature"

        setOKButtonText("Buy Pro")

        init()
    }

    override fun createCenterPanel(): JComponent {

        val panel = JPanel(
            BorderLayout(
                10,
                15
            )
        )

        panel.preferredSize =
            Dimension(
                500,
                220
            )

        val message =
            JLabel(
                """
               <html>

    <h2>Ad Integration Pro — Pro Feature</h2>

    <p>
    This feature requires an active
    <b>Ad Integration Pro</b> license.
    </p>

    <p>
    Purchase Pro securely through
    <b>JetBrains Marketplace</b>.
    </p>

    <br>

    <b>Before purchasing:</b>

    <ol>
        <li>
            Sign in to or create your
            <b>JetBrains Account</b>.
        </li>

        <li>
            Purchase Ad Integration Pro using that account.
        </li>

        <li>
            Return to Android Studio / IntelliJ IDEA.
        </li>

        <li>
            Make sure the IDE is signed in to the
            <b>same JetBrains Account</b>.
        </li>

        <li>
            Open the Pro feature again.
        </li>
    </ol>

    <p>
    JetBrains will automatically provide the license
    to the IDE. You do not need to enter a separate
    license key.
    </p>

    </html>
                """.trimIndent()
            )

        panel.add(
            message,
            BorderLayout.CENTER
        )

        return panel
    }

    override fun doOKAction() {

        super.doOKAction()

        BrowserUtil.browse(
            MARKETPLACE_URL
        )
    }

    override fun createActions():
            Array<javax.swing.Action> {

        return arrayOf(
            okAction,
            cancelAction
        )
    }
}