package uk.ewancroft.inkwell.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import uk.ewancroft.inkwell.data.model.BasicTheme
import uk.ewancroft.inkwell.data.model.PublicationTheme
import uk.ewancroft.inkwell.data.model.RgbColor

/**
 * Resolves a standard.site publication/document theme into Material 3 colors.
 *
 * Mirror's Inkwell iOS ReaderTheme: publication rich theme → basic theme → system defaults.
 * When both a publication theme and document theme are provided, the document theme wins.
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

@Composable
fun resolveTheme(
    publicationTheme: PublicationTheme? = null,
    basicTheme: BasicTheme? = null,
    documentTheme: PublicationTheme? = null
): ResolvedTheme {
    val isDark = isSystemInDarkTheme()

    // Resolve accent and foreground from richest available source
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

/** Inkwell-branded default colors — warm, paper-like. */
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

private fun uk.ewancroft.inkwell.data.model.ColorValue.toColor(): Color =
    Color(
        red = r / 255f,
        green = g / 255f,
        blue = b / 255f,
        alpha = (a ?: 100) / 100f
    )

private fun RgbColor.toColor(): Color =
    Color(red = r / 255f, green = g / 255f, blue = b / 255f)
