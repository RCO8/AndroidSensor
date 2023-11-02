package kr.co.leelab.sensortest

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorManager
import android.hardware.SensorEventListener
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView

class MainActivity : AppCompatActivity(), SensorEventListener {

    lateinit var sensorX : TextView
    lateinit var sensorY : TextView
    lateinit var sensorZ : TextView
    var sensorText = arrayOfNulls<String>(3)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sensorX = findViewById(R.id.sensorX)
        sensorY = findViewById(R.id.sensorY)
        sensorZ = findViewById(R.id.sensorZ)
    }

    private val acceletorSensor by lazy {
        getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }

    override fun onResume(){
        super.onResume()

        acceletorSensor.registerListener(this,
            acceletorSensor.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
        SensorManager.SENSOR_DELAY_NORMAL)
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {    // 센서 정밀도 변경시

    }

    override fun onSensorChanged(event: SensorEvent?) {               // 센서 값 변경시

        event?.let {
            sensorX.text = event!!.values[0].toString()
            sensorY.text = event.values[1].toString()
            sensorZ.text = event.values[2].toString()
            //Log.d("MainActivity", " x:${event.values[0]}, y:${event.values[1]}, z:${event.values[2]} ")

            // [0] x축값, [1] y축값, [2] z축값
        }
    }


    // ⑤ 리스너 해제
    override fun onPause() {
        super.onPause()
        acceletorSensor.unregisterListener(this)

    }
}