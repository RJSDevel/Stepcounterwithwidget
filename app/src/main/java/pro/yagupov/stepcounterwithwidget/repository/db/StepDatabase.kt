package pro.yagupov.stepcounterwithwidget.repository.db

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase

@Database(entities = [(Period::class)], version = 1)
abstract class StepDatabase : RoomDatabase() {
    abstract fun stepDao(): StepDao
}