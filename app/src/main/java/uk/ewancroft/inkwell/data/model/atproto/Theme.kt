package uk.ewancroft.inkwell.data.model.atproto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// --- Theme ---

@Serializable
data class PublicationTheme(
    @SerialName("\$type") val type: String = "pub.leaflet.publication#theme",
    val backgroundColor: ColorValue? = null,
    val pageBackground: ColorValue? = null,
    val primary: ColorValue? = null,
    val accentBackground: ColorValue? = null,
    val accentText: ColorValue? = null,
    val pageWidth: Int? = null,
    val showPageBackground: Boolean? = null,
    val headingFont: String? = null,
    val bodyFont: String? = null
)

@Serializable
data class BasicTheme(
    @SerialName("\$type") val type: String = "site.standard.theme.basic",
    val background: RgbColor,
    val foreground: RgbColor,
    val accent: RgbColor,
    val accentForeground: RgbColor
)

@Serializable
data class ColorValue(
    @SerialName("\$type") val type: String = "pub.leaflet.theme.color#rgb",
    val r: Int,
    val g: Int,
    val b: Int,
    val a: Int? = null
)

@Serializable
data class RgbColor(
    @SerialName("\$type") val type: String = "site.standard.theme.color#rgb",
    val r: Int,
    val g: Int,
    val b: Int
)

// --- Preferences ---

@Serializable
data class PublicationPreferences(
    val showInDiscover: Boolean? = null
)

@Serializable
data class DocumentPreferences(
    val showComments: Boolean? = null,
    val showMentions: Boolean? = null,
    val showRecommends: Boolean? = null,
    val showPrevNext: Boolean? = null,
    val showInDiscover: Boolean? = null
)
