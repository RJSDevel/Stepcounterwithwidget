package pro.yagupov.stepcounterwithwidget.repository.db

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import android.arch.persistence.room.Update

@Dao
interface StepDao {

    @Insert
    fun insertNewPeriod(period: Period)

    @Update
    fun updatePeriod(period: Period)

    @Query("SELECT * FROM period WHERE period + 3600000 > (SELECT strftime('%s', 'now') * 1000) ORDER BY period LIMIT 1")
    fun getCurrentPeriod(): Period?

    @Query("SELECT sum(steps) FROM period WHERE period BETWEEN (SELECT strftime('%s', 'now', 'start of day') * 1000) AND (SELECT strftime('%s', 'now') * 1000)")
    fun getTodayStepCount(): Int

}