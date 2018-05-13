package pro.yagupov.stepcounterwithwidget.repository

import pro.yagupov.stepcounterwithwidget.repository.db.Period

interface StepRepository {

    fun getCurrentPeriod(): Period?

    fun  updatePeriod(period: Period?)

    fun getTodaySteps(): Int

}