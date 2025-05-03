package com.kakos.uniAID.core.presentation

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration

/**
 * Represents the size of a window in terms of width and height.
 *
 * @property WindowSize.width The width of the window.
 * @property WindowSize.height The height of the window.
 */
data class WindowSize(
    val width: WindowType,
    val height: WindowType
)

/**
 * Enum class representing different types of window sizes.
 *
 * @property Compact Represents a compact window size.
 * @property Medium Represents a medium window size.
 * @property Expanded Represents an expanded window size.
 */
enum class WindowType {
    Compact, Medium, Expanded
}

/**
 * Represents the current size of the window, categorized into compact, medium, and expanded.
 *
 * @property WindowSize.width The width category of the window.
 * @property WindowSize.height The height category of the window.
 */
@Composable
fun rememberWindowSize(): WindowSize {
    val config = LocalConfiguration.current

    return WindowSize(
        width = when (config.screenWidthDp) {
            in 0..600 -> WindowType.Compact
            in 601..840 -> WindowType.Medium
            else -> WindowType.Expanded
        },
        height = when (config.screenHeightDp) {
            in 0..600 -> WindowType.Compact
            in 601..840 -> WindowType.Medium
            else -> WindowType.Expanded
        }
    )
}