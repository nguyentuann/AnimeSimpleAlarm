package com.app.base.data.model

import android.graphics.drawable.Icon

data class SettingModel(
    val title: String,
    val icon: Icon,
    val action: () -> Unit
)