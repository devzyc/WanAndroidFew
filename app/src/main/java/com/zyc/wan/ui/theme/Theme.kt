package com.zyc.wan.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import com.zyc.wan.R

@Composable
fun WanTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable() () -> Unit) {
    val colors = if (darkTheme) {
        darkColors(
            primary = Purple200,
            primaryVariant = Pink700,
            secondary = Teal200,
        )
    } else {
        lightColors(
            primary = colorResource(R.color.primary_light),
            primaryVariant = Pink700,
            secondary = Teal200,
            onSecondary = Color.LightGray,
        )
    }

    MaterialTheme(
        colors = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}