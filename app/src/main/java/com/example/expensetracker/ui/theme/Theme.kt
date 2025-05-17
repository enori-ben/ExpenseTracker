package com.example.expensetracker.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.material3.Typography
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.sp
import com.example.expensetracker.R
import androidx.compose.animation.core.tween

val DefaultAnimationSpec = tween<Float>(
    durationMillis = 500,
    easing = LinearOutSlowInEasing
)

val InterFont = FontFamily(Font(R.font.inter))
val ItemFont = FontFamily(Font(R.font.item))

val AppTypography = Typography(
    displaySmall = TextStyle(
        fontFamily = InterFont,
        fontSize = 40.sp // Larger font for total balance
    ),
    titleLarge = TextStyle(
        fontFamily = InterFont,
        fontSize = 20.sp // For "Income", "Expense" labels
    ),
    titleMedium = TextStyle(
        fontFamily = InterFont,
        fontSize = 16.sp // For amounts under Income/Expense
    ),
    bodyMedium = TextStyle(
        fontFamily = InterFont,
        fontSize = 14.sp // For period selector and transaction dates
    ),
    bodySmall = TextStyle(
        fontFamily = InterFont,
        fontSize = 12.sp // For smaller text like the day of the week
    )
)

val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFFFBB0C), // Orange from the image for FAB and accents
    secondary = Color(0xEB16292F), // Background color from the image

//    secondary = Color(0xFF363D44), // Background color from the image
    tertiary = Color(0xFF4CAF50), // Green for income
    surface = Color.Black, // Card background for period selector
    onSurface = Color.White, // Text on cards
    onBackground = Color.White // Text on main background
)

val LightColorScheme = lightColorScheme(
    primary = Color(0xFFFFBB0C),
    secondary = Color(0xEB16292F),
    tertiary = Color(0xFF4CAF50),
    surface = Color.Black,
    onSurface = Color.White,
    onBackground = Color.White
)

@Composable
fun ExpenseTrackerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicDarkColorScheme(context)
        }
        else -> DarkColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        content = content
    )
}