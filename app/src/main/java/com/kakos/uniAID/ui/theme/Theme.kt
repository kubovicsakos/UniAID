package com.kakos.uniAID.ui.theme

import android.os.Build
import android.util.Log
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.kakos.uniAID.ui.theme.color.blueDarkColorScheme
import com.kakos.uniAID.ui.theme.color.blueLightColorScheme
import com.kakos.uniAID.ui.theme.color.greenDarkColorScheme
import com.kakos.uniAID.ui.theme.color.greenLightColorScheme
import com.kakos.uniAID.ui.theme.color.greyDarkColorScheme
import com.kakos.uniAID.ui.theme.color.greyLightColorScheme
import com.kakos.uniAID.ui.theme.color.purpleDarkColorScheme
import com.kakos.uniAID.ui.theme.color.purpleLightColorScheme
import com.kakos.uniAID.ui.theme.color.redDarkColorScheme
import com.kakos.uniAID.ui.theme.color.redLightColorScheme
import com.kakos.uniAID.ui.theme.color.yellowDarkColorScheme
import com.kakos.uniAID.ui.theme.color.yellowLightColorScheme


val inactive_text_color = Color(0xFFBEBEBE)
val dark_note_color = Color(0xFF212121)
val light_note_color = Color(0xFFE5E5E5)

/**
 * Composable that applies the app's theme.
 *
 * Configures Material Design theme based on user preferences
 * and system feature_settings.
 *
 * @param themeMode Light/Dark/Auto theme setting.
 * @param colorScheme Color scheme selection.
 * @param dynamicColor Whether to use Android 12+ dynamic colors.
 * @param content Content to be themed.
 */
@Composable
fun UniAidTheme(
    themeMode: String = "Auto",
    colorScheme: String = "Auto",
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    Log.d("Theme", "UniAidTheme called with $themeMode-$colorScheme")

    val context = LocalContext.current
    val isSystemInDarkTheme = isSystemInDarkTheme()

    val darkTheme = when (themeMode) {
        "Light" -> false
        "Dark" -> true
        else -> isSystemInDarkTheme // "Auto"
    }

    val colors = when (colorScheme) {
        "Blue" -> if (darkTheme) blueDarkColorScheme else blueLightColorScheme
        "Green" -> if (darkTheme) greenDarkColorScheme else greenLightColorScheme
        "Grey" -> if (darkTheme) greyDarkColorScheme else greyLightColorScheme
        "Purple" -> if (darkTheme) purpleDarkColorScheme else purpleLightColorScheme
        "Red" -> if (darkTheme) redDarkColorScheme else redLightColorScheme
        "Yellow" -> if (darkTheme) yellowDarkColorScheme else yellowLightColorScheme
        else -> if (dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        } else {
            if (darkTheme) blueDarkColorScheme else blueLightColorScheme
        }
    }

    MaterialTheme(
        colorScheme = colors,
        typography = Typography,
        content = content
    )
}