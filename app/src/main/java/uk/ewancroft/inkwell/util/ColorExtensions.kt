/**
 * Colour conversion extensions from AT Protocol colour types to Compose Color.
 *
 * Note: the package declaration here is incorrect (com.example.inkwell.util
 * instead of uk.ewancroft.inkwell.util). This is a known artifact from the
 * initial template — fix if it causes build issues, but the import paths
 * in Theme.kt use the fully qualified name so it resolves correctly.
 */
package com.example.inkwell.util

import androidx.compose.ui.graphics.Color
import uk.ewancroft.inkwell.data.model.atproto.ColorValue
import uk.ewancroft.inkwell.data.model.atproto.RgbColor

/** Converts a Leaflet ColorValue to a Compose Color. */
fun ColorValue.toColor(): Color = Color(r, g, b, a ?: 255)

/** Converts a standard.site RgbColor to a Compose Color. */
fun RgbColor.toColor(): Color = Color(r, g, b)
