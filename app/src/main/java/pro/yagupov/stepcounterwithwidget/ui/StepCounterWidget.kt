package pro.yagupov.stepcounterwithwidget.ui

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import pro.yagupov.stepcounterwithwidget.R
import pro.yagupov.stepcounterwithwidget.repository.StepRepository
import pro.yagupov.stepcounterwithwidget.repository.StepRepositoryImpl
import android.content.ComponentName
import pro.yagupov.stepcounterwithwidget.service.StepCounterService
import android.app.PendingIntent




class StepCounterWidget : AppWidgetProvider() {

    private var stepRepository: StepRepository? = null


    override fun onUpdate(context: Context?, appWidgetManager: AppWidgetManager?, appWidgetIds: IntArray?) {
        context?.startService(Intent(context, StepCounterService::class.java))
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        super.onReceive(context, intent)

        if (stepRepository == null) stepRepository = StepRepositoryImpl(context!!)

        val appWidgetIds = AppWidgetManager.getInstance(context).getAppWidgetIds(ComponentName(context, StepCounterWidget::class.java))

        appWidgetIds?.forEach {
            val remoteViews = RemoteViews(context?.packageName, R.layout.view_widget)
            remoteViews.setTextViewText(R.id.steps, String.format("%d", stepRepository!!.getTodaySteps()))

            val pendingIntent = PendingIntent.getActivity(context, 0,
                    Intent(context, MainActivity::class.java),
                    PendingIntent.FLAG_UPDATE_CURRENT)
            remoteViews.setOnClickPendingIntent(R.id.update, pendingIntent)

            AppWidgetManager.getInstance(context).updateAppWidget(it, remoteViews)
        }
    }
}