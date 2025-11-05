package com.app.base.utils

import com.app.base.R

object AppConstants {

    const val TIMER_CHANNEL_ID = "timer_channel"
    const val TIMER_NAME = "Timer Channel"

    const val STOPWATCH_CHANNEL_ID = "stopwatch_channel"
    const val STOPWATCH_NAME = "Stopwatch Channel"

    const val ALARM_CHANNEL_ID = "alarm_channel"
    const val ALARM_NAME = "Alarm Channel"

    val characters = mapOf<String, Int>(
        "Naruto" to R.drawable.img_naruto,
        "Luffy" to R.drawable.img_luffy,
        "Sasuke" to R.drawable.img_sasuke,
        "Goku" to R.drawable.img_goku,
        "Zoro" to R.drawable.img_zoro,
        "Conan" to R.drawable.img_conan,
        "Pikachu" to R.drawable.img_pikachu,
        "Gojo" to R.drawable.img_gojo
    )

    val sounds = mapOf(
        R.string.default_tone to R.raw.base,
        R.string.clock_tone to R.raw.clockalarm,
        R.string.ring_tone to R.raw.ringtone,
        R.string.school_tone to R.raw.schoolbell,
        R.string.trumpet_tone to R.raw.terompetole
    )

    val dates = mapOf(
        1 to R.string.sunday,
        2 to R.string.monday,
        3 to R.string.tuesday,
        4 to R.string.wednesday,
        5 to R.string.thursday,
        6 to R.string.friday,
        7 to R.string.saturday
    )

    fun getNameCharacterById(id: Int): String {
        return characters.entries.firstOrNull { it.value == id }?.key ?: "Naruto"
    }

    fun getCharacterIdByName(name: String): Int {
        // Nếu có nhu cầu ngược lại — từ tên sang id nhân vật
        return characters[name] ?: R.drawable.img_naruto
    }

    fun getAllCharacters(): List<Int> {
        return characters.values.toList()
    }

    fun getNameSoundById(id: Int): Int {
        return sounds.entries.firstOrNull { it.value == id }?.key ?: R.string.default_tone
    }

    fun getAllSoundIds(): List<Int> {
        return sounds.values.toList()
    }

    fun getAllDates(): List<Int> {
        return dates.values.toList()
    }
}
