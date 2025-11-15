package com.app.base.data.model


data class SettingModel(
    val titleRes: Int,
    val iconRes: Int,
    val action: () -> Unit
)