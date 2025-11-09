package com.app.base.data.model

import android.graphics.drawable.Icon

data class SettingModel(
    val title: String,
    val iconRes: Int,
    val action: () -> Unit
)