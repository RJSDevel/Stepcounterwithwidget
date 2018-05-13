package pro.yagupov.stepcounterwithwidget.service.manager

import android.hardware.Sensor
import android.hardware.SensorEventListener
import android.hardware.SensorManager

abstract class StepCounterManager(internal val sensorManager: SensorManager, internal val listener: SensorEventListener) : SensorEventListener {

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

    }

    open fun stop() {
        sensorManager.unregisterListener(this)
    }
}