package pro.yagupov.stepcounterwithwidget.service.manager

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager

class StepCounterManagerStepCounterSensor(sensorManager: SensorManager, listener: SensorEventListener) : StepCounterManager(sensorManager, listener) {

    init {
        sensorManager.registerListener(listener, sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER), SensorManager.SENSOR_DELAY_UI)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun stop() {
        sensorManager.unregisterListener(listener)
    }
}