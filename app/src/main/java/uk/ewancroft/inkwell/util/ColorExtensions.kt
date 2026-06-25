package com.example.inkwell.util

import androidx.compose.ui.graphics.Color
import uk.ewancroft.inkwell.data.model.atproto.ColorValue
import uk.ewancroft.inkwell.data.model.atproto.RgbColor

fun ColorValue.toColor(): Color = Color(r, g, b, a ?: 255)

fun RgbColor.toColor(): Color = Color(r, g, b)
