package pro.yagupov.stepcounterwithwidget.repository.db

import android.arch.persistence.room.TypeConverter
import java.util.*

class CalendarConvector {

    @TypeConverter
    fun toTimestamp(date: Calendar?): Long? {
        return date?.timeInMillis
    }

    @TypeConverter
    fun toCalendar(timestamp: Long?): Calendar? {
        val date = Calendar.getInstance()

        return if (timestamp == null) null else {
            date.timeInMillis = timestamp
            date
        }
    }
}