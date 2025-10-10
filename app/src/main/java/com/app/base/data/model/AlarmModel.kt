package com.app.base.data.model
data class AlarmModel(
    val id: String,
    var hour: Int,
    var minute: Int,
    var isOn: Boolean = true,
    var message: String? = null,
    var sound: Int? = null,
    var dateOfWeek: List<Int>? = null,
    var date: Long? = null,
    var character: Int? = null
)