package pro.yagupov.stepcounterwithwidget.ui

import android.appwidget.AppWidgetManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import pro.yagupov.stepcounterwithwidget.R
import pro.yagupov.stepcounterwithwidget.repository.StepRepository
import pro.yagupov.stepcounterwithwidget.repository.StepRepositoryImpl
import pro.yagupov.stepcounterwithwidget.service.StepCounterService

class MainActivity : AppCompatActivity() {

    var repository: StepRepository? = null

    private val stepReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            update()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        repository = StepRepositoryImpl(this)
    }

    override fun onStart() {
        super.onStart()

        registerReceiver(stepReceiver, IntentFilter(AppWidgetManager.ACTION_APPWIDGET_UPDATE))
        startService(Intent(this, StepCounterService::class.java))
    }

    override fun onResume() {
        super.onResume()
        update()
    }

    private fun update() {
        steps_today.text = String.format("%d", repository?.getTodaySteps())
    }

    override fun onStop() {
        super.onStop()
        try {
            unregisterReceiver(stepReceiver)
        } catch (e: Exception) {

        }
    }
}
