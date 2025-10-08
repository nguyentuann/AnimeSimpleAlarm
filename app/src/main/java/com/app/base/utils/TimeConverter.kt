package com.app.base.utils

import android.content.Context
import java.util.Calendar
import com.app.base.R

object TimeConverter {
    fun convertTimeToString(hour: Int, minute: Int): String {
        val amPm = if (hour < 12) "AM" else "PM"
        val hour12 = when {
            hour > 12 -> hour - 12
            else -> hour
        }
        return "%02d:%02d %s".format(hour12, minute, amPm)
    }

    fun convertListDateToString(context: Context,days: List<Int>?): String {

        if (days == null || days.isEmpty()) return context.getString(R.string.once)
        if (days.size == 7) return context.getString(R.string.everyday)
        val dayNames = listOf(
            context.getString(R.string.sun),
            context.getString(R.string.mon),
            context.getString(R.string.tue),
            context.getString(R.string.wed),
            context.getString(R.string.thu),
            context.getString(R.string.fri),
            context.getString(R.string.sat),
        )
        val selectedDayNames = days.map { e -> dayNames[(e - 1) % 7] }
        return selectedDayNames.joinToString(", ")

    }

    // todo lấy ra thứ tiếp theo có báo thức
    fun nameDateOfWeek(context: Context,calendar: Calendar): String {
        val dateOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
        return when (dateOfWeek) {
            1 -> context.getString(R.string.sunday)
            2 -> context.getString(R.string.monday)
            3 ->context.getString(R.string.tuesday)
            4 -> context.getString(R.string.wednesday)
            5 -> context.getString(R.string.thursday)
            6 -> context.getString(R.string.friday)
            7 -> context.getString(R.string.saturday)
            else -> "Unknown"
        }
    }

    fun dayMonthYearFormat(day: Int, month: Int, year: Int): String {
        return "$day/${month + 1}/$year"
    }

    fun dayMonthYearFormatFromCalendar(millis: Long): String {
        val cal = Calendar.getInstance().apply {
            timeInMillis = millis
        }
        return dayMonthYearFormat(
            cal.get(Calendar.DAY_OF_MONTH),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.YEAR)
        )
    }

}