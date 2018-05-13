package pro.yagupov.stepcounterwithwidget.repository


import android.arch.persistence.room.Room
import android.content.Context
import pro.yagupov.stepcounterwithwidget.repository.db.Period
import pro.yagupov.stepcounterwithwidget.repository.db.StepDatabase


class StepRepositoryImpl(context: Context) : StepRepository {

    private val database: StepDatabase = Room
            .databaseBuilder(context, StepDatabase::class.java, "step.db")
            .allowMainThreadQueries()
            .build()

    override fun getCurrentPeriod(): Period? {
        return database.stepDao().getCurrentPeriod()
    }

    override fun updatePeriod(period: Period?) {
        if (period == null) {
            database.stepDao().insertNewPeriod(Period())
        } else{
            database.stepDao().updatePeriod(period)
        }
    }

    override fun getTodaySteps(): Int {
        return database.stepDao().getTodayStepCount()
    }
}