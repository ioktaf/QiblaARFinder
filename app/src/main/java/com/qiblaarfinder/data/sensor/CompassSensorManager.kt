package com.qiblaarfinder.data.sensor

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import com.qiblaarfinder.domain.util.CompassMath
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class CompassSensorManager(context: Context) {
    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager

    fun headingFlow(): Flow<Float> = callbackFlow {
        val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        val magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)

        if (accelerometer == null || magnetometer == null) {
            close(IllegalStateException("Perangkat tidak memiliki sensor kompas yang dibutuhkan."))
            return@callbackFlow
        }

        var gravityValues: FloatArray? = null
        var magneticValues: FloatArray? = null
        val rotationMatrix = FloatArray(9)
        val orientationAngles = FloatArray(3)

        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                when (event.sensor.type) {
                    Sensor.TYPE_ACCELEROMETER -> gravityValues = lowPass(event.values, gravityValues)
                    Sensor.TYPE_MAGNETIC_FIELD -> magneticValues = lowPass(event.values, magneticValues)
                }

                val gravity = gravityValues ?: return
                val magnetic = magneticValues ?: return

                if (SensorManager.getRotationMatrix(rotationMatrix, null, gravity, magnetic)) {
                    SensorManager.getOrientation(rotationMatrix, orientationAngles)
                    val azimuth = Math.toDegrees(orientationAngles[0].toDouble())
                    trySend(CompassMath.normalize360(azimuth).toFloat())
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit
        }

        sensorManager.registerListener(listener, accelerometer, SensorManager.SENSOR_DELAY_GAME)
        sensorManager.registerListener(listener, magnetometer, SensorManager.SENSOR_DELAY_GAME)

        awaitClose {
            sensorManager.unregisterListener(listener)
        }
    }

    private fun lowPass(input: FloatArray, output: FloatArray?): FloatArray {
        if (output == null) return input.copyOf()
        val alpha = 0.18f
        return FloatArray(input.size) { index ->
            output[index] + alpha * (input[index] - output[index])
        }
    }
}

