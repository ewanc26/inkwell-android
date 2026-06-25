/**
 * Theme resolution and Material 3 colour scheme for Inkwell.
 *
 * Cascades through three theme sources — Leaflet rich theme -> basicTheme ->
 * system defaults — matching the same logic in Inkwell iOS ReaderTheme.
 * Inkwell-branded defaults aim for warm, paper-like tones on light and
 * cool, high-contrast tones on dark.
 */
package uk.ewancroft.inkwell.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import uk.ewancroft.inkwell.data.model.atproto.BasicTheme
import uk.ewancroft.inkwell.data.model.atproto.PublicationTheme
import uk.ewancroft.inkwell.data.model.atproto.RgbColor

// ── Resolved Theme ───────────────────────────────────────────────────────

/**
 * A fully resolved colour palette ready for Material 3.
 *
 * Document-level theme values override publication-level ones when both exist,
 * mirroring the cascade in standard.site's rendering spec.
 */
data class ResolvedTheme(
    val background: Color,
    val foreground: Color,
    val accent: Color,
    val accentForeground: Color,
    val pageBackground: Color,
    val pageWidth: Int = 680,
    val showPageBackground: Boolean = false
)

// ── Theme Resolution ─────────────────────────────────────────────────────

/**
 * Resolves a publication/document theme cascade into a ResolvedTheme.
 *
 * Priority (highest first):
 *   1. Document theme (Leaflet rich)
 *   2. Publication theme (Leaflet rich)
 *   3. Basic theme (4-color palette)
 *   4. System default (dark/light)
 */
@Composable
fun resolveTheme(
    publicationTheme: PublicationTheme? = null,
    basicTheme: BasicTheme? = null,
    documentTheme: PublicationTheme? = null
): ResolvedTheme {
    val isDark = isSystemInDarkTheme()

    val accent = documentTheme?.accentBackground?.toColor()
        ?: publicationTheme?.accentBackground?.toColor()
        ?: basicTheme?.accent?.toColor()
        ?: if (isDark) Color(0xFF90CAF9) else Color(0xFF1565C0)

    val foreground = documentTheme?.primary?.toColor()
        ?: publicationTheme?.primary?.toColor()
        ?: basicTheme?.foreground?.toColor()
        ?: if (isDark) Color(0xFFE0E0E0) else Color(0xFF1A1A1A)

    val background = documentTheme?.backgroundColor?.toColor()
        ?: publicationTheme?.backgroundColor?.toColor()
        ?: basicTheme?.background?.toColor()
        ?: if (isDark) Color(0xFF121212) else Color(0xFFFAFAFA)

    val pageBg = documentTheme?.pageBackground?.toColor()
        ?: publicationTheme?.pageBackground?.toColor()
        ?: background

    val accentFg = documentTheme?.accentText?.toColor()
        ?: publicationTheme?.accentText?.toColor()
        ?: basicTheme?.accentForeground?.toColor()
        ?: Color.White

    val pageWidth = documentTheme?.pageWidth
        ?: publicationTheme?.pageWidth
        ?: 680

    val showPageBg = documentTheme?.showPageBackground
        ?: publicationTheme?.showPageBackground
        ?: false

    return ResolvedTheme(background, foreground, accent, accentFg, pageBg, pageWidth, showPageBg)
}

// ── Inkwell Theme ────────────────────────────────────────────────────────

/**
 * Material 3 colour scheme for the Inkwell app.
 *
 * When a resolved theme is available (from a publication/document), it maps
 * directly to Material 3's colour roles. Otherwise uses Inkwell's default
 * warm palette: paper-like on light, cool/readable on dark.
 */
@Composable
fun InkwellTheme(
    resolvedTheme: ResolvedTheme? = null,
    content: @Composable () -> Unit
) {
    val theme = resolvedTheme
    val isDark = isSystemInDarkTheme()

    val colorScheme = if (theme != null) {
        darkColorScheme(
            primary = theme.accent,
            onPrimary = theme.accentForeground,
            background = theme.background,
            onBackground = theme.foreground,
            surface = theme.pageBackground,
            onSurface = theme.foreground
        )
    } else if (isDark) {
        darkColorScheme(
            primary = Color(0xFF90CAF9),
            background = Color(0xFF121212),
            surface = Color(0xFF1E1E1E)
        )
    } else {
        lightColorScheme(
            primary = Color(0xFF1565C0),
            background = Color(0xFFFAFAFA),
            surface = Color(0xFFFFFFFF)
        )
    }

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}

// ── Colour Conversion Extensions ─────────────────────────────────────────

/**
 * Converts a Leaflet ColorValue (RGBA, alpha 0-100 percentage)
 * to a Compose Color (RGB 0-1 float, alpha 0-1 float).
 */
private fun ColorValue.toColor(): Color =
    Color(
        red = r / 255f,
        green = g / 255f,
        blue = b / 255f,
        alpha = (a ?: 100) / 100f
    )

/**
 * Converts a standard.site RgbColor (fully opaque) to a Compose Color.
 */
private fun RgbColor.toColor(): Color =
    Color(red = r / 255f, green = g / 255f, blue = b / 255f)
