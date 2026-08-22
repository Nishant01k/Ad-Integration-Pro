package org.viw.adintegrationpro.features

enum class AdProvider(val displayName: String) {
    ADMOB("Google AdMob"),
    UNITY("Unity Ads"),
    FACEBOOK("Facebook Audience"),
    IRONSOURCE("IronSource");

    override fun toString(): String = displayName
}
