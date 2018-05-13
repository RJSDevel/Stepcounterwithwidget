package pro.yagupov.stepcounterwithwidget.service.manager

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.hardware.SensorManager.SENSOR_DELAY_UI


internal class StepCounterManagerAccelerometer(sensorManager: SensorManager, listener: SensorEventListener) : StepCounterManager(sensorManager, listener) {

    companion object {
        private const val ACCELEROMETER_RING_SIZE = 8 // n ^ 2

        private const val STEP_THRESHOLD_RANGE_MIN = 90
        private const val STEP_THRESHOLD_RANGE_MAX = 130

        private const val STEP_DELAY_NS = 35 * 10000000

        private var IM_POSITIVE_THRESHOLD = 2.0
        private var IM_NEGATIVE_THRESHOLD = -2.0
    }

    private var lastStepTimeNs: Long = 0

    private var index = 0
    private var deltas: Array<Complex?>

    private val mLowPassFilter: LowPassFilter = LowPassFilter()

    private var steps = 0


    init {
        this.deltas = arrayOfNulls<Complex?>(ACCELEROMETER_RING_SIZE)
        this.mLowPassFilter
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SENSOR_DELAY_UI)
    }

    override fun onSensorChanged(event: SensorEvent) {
        val filtered = mLowPassFilter.lowPassFilter(event.values)
        val delta = Math.sqrt((filtered[0] * filtered[0] + filtered[1] * filtered[1] + filtered[2] * filtered[2]).toDouble())

        deltas[index++ % ACCELEROMETER_RING_SIZE] = Complex(delta, 0.0)

        if (index % ACCELEROMETER_RING_SIZE == 0) {

            val result = Complex.fft(deltas)

            val isStepPattern = STEP_THRESHOLD_RANGE_MIN < result[0]!!.re() && result[0]!!.re() < STEP_THRESHOLD_RANGE_MAX

            if (isStepPattern) {
                for (complex in result) {

                    var isStep = false
                    if (complex!!.im() != 0.0) {
                        if (complex.im() > IM_POSITIVE_THRESHOLD) {
                            IM_POSITIVE_THRESHOLD = IM_POSITIVE_THRESHOLD / complex.im() / 2
                            if (complex.im() > IM_POSITIVE_THRESHOLD) isStep = true
                        } else if (complex.im() < IM_NEGATIVE_THRESHOLD) {
                            IM_NEGATIVE_THRESHOLD = IM_NEGATIVE_THRESHOLD / complex.im() / 2
                            if (complex.im() > IM_NEGATIVE_THRESHOLD) isStep = true
                        }
                    }

                    isStep = isStep and (event.timestamp - lastStepTimeNs > STEP_DELAY_NS)

                    if (isStep) {
                        event.values[0] = (++steps).toFloat()
                        listener.onSensorChanged(event)
                        lastStepTimeNs = event.timestamp
                        break
                    }
                }
            }

        }
    }

    private class LowPassFilter {
        companion object {
            private const val timeConstant = 0.230f
        }

        private var alpha = 0.0f
        private var timestamp = System.nanoTime().toFloat()
        private val timestampOld = System.nanoTime().toFloat()
        private val output = FloatArray(3)

        private var count = 0

        internal fun lowPassFilter(input: FloatArray): FloatArray {
            timestamp = System.nanoTime().toFloat()

            val dt = 1 / (count / ((timestamp - timestampOld) / 1000000000.0f))
            alpha = timeConstant / (timeConstant + dt)

            output[0] = output[0] + alpha * (input[0] - output[0])
            output[1] = output[1] + alpha * (input[1] - output[1])
            output[2] = output[2] + alpha * (input[2] - output[2])

            count++

            return output
        }
    }
}
