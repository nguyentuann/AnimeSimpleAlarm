package com.app.base.utils

import android.graphics.Color

fun randomColor(): Int {
    val hsv = floatArrayOf(
        (0..360).random().toFloat(), // Hue
        0.4f + Math.random().toFloat() * 0.3f, // Saturation
        0.8f + Math.random().toFloat() * 0.2f  // Value (độ sáng)
    )
    return Color.HSVToColor(hsv)
}
