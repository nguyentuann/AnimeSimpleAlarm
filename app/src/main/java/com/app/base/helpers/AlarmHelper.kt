package com.app.base.helpers
import java.util.Calendar

object AlarmHelper {
    //todo Lấy thời gian tiếp theo từ giờ phút và ngày trong tuần
    fun getNextDayOfWeek(hour: Int, minute: Int, daysOfWeek: List<Int>): Calendar {
        val now = Calendar.getInstance()
        val next = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        var addDays = 0
        while (true) {
            val dayOfWeek = (now.get(Calendar.DAY_OF_WEEK) + addDays) % 7
            val calendarDay = if (dayOfWeek == 0) 7 else dayOfWeek
            if (daysOfWeek.contains(calendarDay)) {
                next.add(Calendar.DAY_OF_YEAR, addDays)
                if (next.timeInMillis <= now.timeInMillis) {
                    addDays++
                    continue
                }
                break
            }
            addDays++
        }
        return next
    }

    // todo chuyển đổi từ DB sang List
    fun fromBitToList(daysOfWeek: Int): List<Int> {
        val list = mutableListOf<Int>()
        for (i in 1..7) {
            if (daysOfWeek and (1 shl i) != 0) {
                list.add(i)
            }
        }
        return list
    }

    // todo chuyển đổi từ List sang DB
    fun fromListToBit(daysOfWeek: List<Int>): Int {
        var result = 0
        for (day in daysOfWeek) {
            result = result or (1 shl day)
        }
        return result
    }

    // todo trả về Calendar từ ngày + dùng để setup ngày được chon
    fun getCalendarFromDate(year: Int, month: Int, day: Int): Long {
        return Calendar.getInstance().apply {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, month)
            set(Calendar.DAY_OF_MONTH, day)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
    }

    // todo lấy ra ngày mai
    fun getTomorrowDate(): Long {
        return Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
    }
}