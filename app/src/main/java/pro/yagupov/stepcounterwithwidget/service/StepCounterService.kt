package pro.yagupov.stepcounterwithwidget.service


import android.app.AlarmManager
import android.app.PendingIntent
import android.app.Service
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager.FEATURE_SENSOR_ACCELEROMETER
import android.content.pm.PackageManager.FEATURE_SENSOR_STEP_DETECTOR
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.IBinder
import android.support.v4.app.NotificationCompat
import android.support.v4.app.TaskStackBuilder
import android.util.Log
import pro.yagupov.stepcounterwithwidget.R
import pro.yagupov.stepcounterwithwidget.repository.StepRepository
import pro.yagupov.stepcounterwithwidget.repository.StepRepositoryImpl
import pro.yagupov.stepcounterwithwidget.service.manager.StepCounterManager
import pro.yagupov.stepcounterwithwidget.service.manager.StepCounterManagerAccelerometer
import pro.yagupov.stepcounterwithwidget.service.manager.StepCounterManagerStepCounterSensor
import pro.yagupov.stepcounterwithwidget.ui.MainActivity
import java.util.concurrent.atomic.AtomicInteger


class StepCounterService : Service(), SensorEventListener {

    companion object {
        private const val TAG = "StepCounterService"
        private const val SERVICES_ID = 12345
    }

    private lateinit var counterManager: StepCounterManager

    private val unknownSteps = AtomicInteger()
    private val steps = AtomicInteger()

    private lateinit var repository: StepRepository


    override fun onBind(intent: Intent?): IBinder {
        TODO("not implemented")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return Service.START_STICKY
    }

    override fun onCreate() {
        super.onCreate()

        Log.d(TAG, "Start Service")

        repository = StepRepositoryImpl(baseContext)

        try {
            val sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager

            counterManager = when {
                packageManager.hasSystemFeature(FEATURE_SENSOR_STEP_DETECTOR) -> StepCounterManagerStepCounterSensor(sensorManager, this)
                packageManager.hasSystemFeature(FEATURE_SENSOR_ACCELEROMETER) -> StepCounterManagerAccelerometer(sensorManager, this)
                else -> throw Exception(String.format("Device don't have a step counter sensor - FEATURE_SENSOR_STEP_DETECTOR = %b, FEATURE_SENSOR_ACCELEROMETER = %b",
                        packageManager.hasSystemFeature(FEATURE_SENSOR_STEP_DETECTOR), packageManager.hasSystemFeature(FEATURE_SENSOR_ACCELEROMETER)))
            }

            val builder = NotificationCompat.Builder(this, "")
                    .setContentTitle("")
                    .setContentText("")
                    .setSmallIcon(R.mipmap.ic_launcher)

            val stepCounterIntent = Intent(applicationContext, MainActivity::class.java)
            val stackBuilder = TaskStackBuilder.create(applicationContext)
            stackBuilder.addNextIntent(stepCounterIntent)
            val resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
            builder.setContentIntent(resultPendingIntent)
            startForeground(SERVICES_ID, builder.build())
        } catch (e: Exception) {
            stopSelf()
            return
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        Log.e(TAG, "TASK DESTROYED")

        counterManager.stop()
    }

    override fun onTaskRemoved(rootIntent: Intent) {
        Log.e(TAG, "TASK REMOVED")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            val restartIntent = Intent(this, javaClass)
            val rpi = PendingIntent.getService(this, 0, restartIntent, PendingIntent.FLAG_ONE_SHOT)
            val am = getSystemService(Context.ALARM_SERVICE) as AlarmManager
            am.setExact(AlarmManager.RTC, System.currentTimeMillis() + 500, rpi)
            Log.e(TAG, "TRY RESTART")
        }
    }

    override fun onSensorChanged(event: SensorEvent) {
        val rawSteps = event.values[0].toInt()
        Log.d(TAG, "Sensor raw data - $rawSteps")

        val period = repository.getCurrentPeriod()

        if (period == null) {
            if (unknownSteps.get() == 0)
                unknownSteps.set(rawSteps)
            else
                unknownSteps.set(0)
            steps.set(0)
        } else {
            if (rawSteps > period.steps && unknownSteps.get() == 0) {
                unknownSteps.set(rawSteps - period.steps)
            }

            if (rawSteps < period.steps) {
                steps.set(rawSteps + period.steps - unknownSteps.get())
                unknownSteps.set(rawSteps)
            } else {
                steps.set(rawSteps - unknownSteps.get())
            }

            period.steps = steps.get()
        }

        Log.d(TAG, "Sensor data unknown steps - " + unknownSteps.get())
        Log.d(TAG, "Sensor data steps amount - " + steps.get())
        Log.d(TAG, "Period steps - " + period?.steps)

        repository.updatePeriod(period)

        val steps = repository.getTodaySteps()
        Log.d(TAG, "Today steps - $steps")
        sendBroadcast(Intent(AppWidgetManager.ACTION_APPWIDGET_UPDATE))
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

    }
}