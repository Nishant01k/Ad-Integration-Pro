package org.viw.adintegrationpro.features

enum class AdFormat(val displayName: String) {
    BANNER("Banner Ad"),
    INTERSTITIAL("Interstitial Ad"),
    REWARDED("Rewarded Ad"),
    NATIVE("Native Ad");

    override fun toString(): String = displayName
}
