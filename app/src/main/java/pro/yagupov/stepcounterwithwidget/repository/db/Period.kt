package pro.yagupov.stepcounterwithwidget.repository.db

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import android.arch.persistence.room.TypeConverters
import java.util.*

@Entity
@TypeConverters(CalendarConvector::class)
data class Period(
        @PrimaryKey(autoGenerate = true)
        var id: Long = 0,
        var period: Calendar = Calendar.getInstance(),
        var steps: Int = 0
)