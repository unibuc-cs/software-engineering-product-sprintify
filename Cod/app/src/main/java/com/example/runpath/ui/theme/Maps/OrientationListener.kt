package com.example.runpath.ui.theme.Maps

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager

// OrientationListener is a singleton class

object OrientationListener : SensorEventListener {
    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null
    private var magnetometer: Sensor? = null
    private var isListening = false

    private var gravity = FloatArray(3)
    private var geomagnetic = FloatArray(3)
    private val orientation = FloatArray(3)
    private val rotationMatrix = FloatArray(9)

    private var azimuth: Float = 0f

    fun initialize(context: Context) {
        if (!::sensorManager.isInitialized) {
            sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
            magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
        }
    }

    fun startListening() {
        if (!isListening) {
            accelerometer?.let { sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_GAME) }
            magnetometer?.let { sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_GAME) }
            isListening = true
        }
    }
    fun stop() {
        if (isListening) {
            sensorManager.unregisterListener(this)
            isListening = false
            gravity = FloatArray(3)
            geomagnetic = FloatArray(3)
        }
    }
    fun stopListening() {
        sensorManager.unregisterListener(this, accelerometer)
        sensorManager.unregisterListener(this, magnetometer)
    }

    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
            gravity = event.values
        }
        if (event.sensor.type == Sensor.TYPE_MAGNETIC_FIELD) {
            geomagnetic = event.values
        }
        if (gravity.isNotEmpty() && geomagnetic.isNotEmpty()) {
            val R = FloatArray(9)
            val I = FloatArray(9)
            val success = SensorManager.getRotationMatrix(R, I, gravity, geomagnetic)
            if (success) {
                val orientation = FloatArray(3)
                SensorManager.getOrientation(R, orientation)
                azimuth = Math.toDegrees(orientation[0].toDouble()).toFloat()
                azimuth = (azimuth + 360) % 360
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    fun getAzimuth(): Float {
        return azimuth
    }

    fun lerp(start: Float, end: Float, fraction: Float): Float {
        return (1 - fraction) * start + fraction * end
    }

    fun adjustAngle(start: Float, end: Float, fraction: Float): Float {
        var delta = (end - start + 360) % 360
        if(delta > 180) {
            delta -= 360
        }
        return (start + fraction * delta + 360) % 360
    }

    fun lowPassFilter(input: Float, output: Float, alpha: Float = 0.1f): Float {
        return output + alpha * (input - output)
    }
}