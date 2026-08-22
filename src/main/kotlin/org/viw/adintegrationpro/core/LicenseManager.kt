package org.viw.adintegrationpro.core

import org.viw.adintegrationpro.license.CheckLicense

object LicenseManager {

    const val PRODUCT_CODE = "PADINTEGRATIONP"

    fun isProVersion(): Boolean {
        return CheckLicense.isLicensed() == true
    }
}