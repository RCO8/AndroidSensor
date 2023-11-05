package kr.co.leelab.sensortest

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorManager
import android.hardware.SensorEventListener
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.CompoundButton
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity(), SensorEventListener {

    //퍼미션 (권한요청)
    val MY_PERMISSION_SENSOR = 100

    /*센서동작할 속성들*/
    //가속도 센서
    lateinit var accelerSwitch : Switch //활성 여부
    //각 센서가 움직임을 감지받는 축
    lateinit var sensorX : TextView
    lateinit var sensorY : TextView
    lateinit var sensorZ : TextView

    //자이로 센서
    lateinit var gyroscopeSwitch : Switch
    lateinit var gyroscopeX : TextView
    lateinit var gyroscopeY : TextView
    lateinit var gyroscopeZ : TextView

    //마그네톰 센서
    lateinit var magnetomSwitch : Switch
    lateinit var magnetomX : TextView
    lateinit var magnetomY : TextView
    lateinit var magnetomZ : TextView

    //각 센서 매니저 생성
    private val acceletorSensor by lazy {
        getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }
    private val gyroscopeSensor by lazy {
        getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }
    private val magnetomSensor by lazy {
        getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }

    private var activityPermission : Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //가속도 센서
        accelerSwitch = findViewById(R.id.accelerSwitch)
        sensorX = findViewById(R.id.sensorX)
        sensorY = findViewById(R.id.sensorY)
        sensorZ = findViewById(R.id.sensorZ)
        
        //자이로 센서
        gyroscopeSwitch = findViewById(R.id.gyroscopeSwitch)
        gyroscopeX = findViewById(R.id.gyroscopeX)
        gyroscopeY = findViewById(R.id.gyroscopeY)
        gyroscopeZ = findViewById(R.id.gyroscopeZ)

        //중력 센서
        magnetomSwitch = findViewById(R.id.magnetomSwitch)
        magnetomX = findViewById(R.id.magnetomX)
        magnetomY = findViewById(R.id.magnetomY)
        magnetomZ = findViewById(R.id.magnetomZ)

        // 퍼미션 확인해주는 데이터
        val sensorPermission = ContextCompat.checkSelfPermission(this,
            Manifest.permission.BODY_SENSORS)
        val sensorBackgroundPermission = ContextCompat.checkSelfPermission(this,
            Manifest.permission.BODY_SENSORS_BACKGROUND)
        if(sensorPermission == PackageManager.PERMISSION_GRANTED || //센서 퍼미션이 권한이 확인되면
            sensorBackgroundPermission == PackageManager.PERMISSION_GRANTED) {
            activityPermission = true
            //라디오 버튼 식으로 하나만 활성화되게 하기 (다꺼도 상관 없음)
            accelerSwitch.setOnCheckedChangeListener { p0, isChecked ->
                if (isChecked) {
                    gyroscopeSwitch.isChecked = false
                    magnetomSwitch.isChecked = false
                }
            }
            gyroscopeSwitch.setOnCheckedChangeListener { p0, isChecked ->
                if (isChecked) {
                    accelerSwitch.isChecked = false
                    magnetomSwitch.isChecked = false
                }
            }
            magnetomSwitch.setOnCheckedChangeListener { p0, isChecked ->
                if (isChecked) {
                    accelerSwitch.isChecked = false
                    gyroscopeSwitch.isChecked = false
                }
            }
        }
        else    // 안되있으면 설정 요청
        {
            activityPermission = false
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.BODY_SENSORS),MY_PERMISSION_SENSOR)
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.BODY_SENSORS_BACKGROUND),MY_PERMISSION_SENSOR)
        }


    }
    override fun onResume(){    //센서 등록 메서드
        super.onResume()

        if(accelerSwitch.isChecked) {   // 가속도 센서 선언
            acceletorSensor.registerListener(
                this,
                acceletorSensor.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL
            )
        }
        else if(gyroscopeSwitch.isChecked) {    //자이로 센서 선언
            gyroscopeSensor.registerListener(
                this,
                gyroscopeSensor.getDefaultSensor(Sensor.TYPE_GYROSCOPE),
                SensorManager.SENSOR_DELAY_NORMAL
            )
        }
        else if(magnetomSwitch.isChecked) {     //중력 센서 선언
            magnetomSensor.registerListener(
                this,
                magnetomSensor.getDefaultSensor(Sensor.TYPE_GRAVITY),
                SensorManager.SENSOR_DELAY_NORMAL
            )
        }
    }
    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {    // 센서 정밀도 변경시
    //이 메서드는 onSensorChanged를 사용하기 위해 필요한 함수이다 그리고 메인 클래스에서 SensorEventListener를 상속받아야 한다 (interface 상속)
    }
    override fun onSensorChanged(event: SensorEvent?) {               // 센서 값 변경시

        if(activityPermission) {
            event?.let {
                if (accelerSwitch.isChecked) {   //가속도 센서 받아오는 값
                    sensorX.text = event!!.values[0].toString()
                    sensorY.text = event.values[1].toString()
                    sensorZ.text = event.values[2].toString()
                } else if (gyroscopeSwitch.isChecked) {    //자이로 센서 받아오는 값
                    gyroscopeX.text = event.values[0].toString()
                    gyroscopeY.text = event.values[1].toString()
                    gyroscopeZ.text = event.values[2].toString()
                } else if (magnetomSwitch.isChecked) {     //중력 센서 받아오는 값
                    magnetomX.text = event.values[0].toString()
                    magnetomY.text = event.values[1].toString()
                    magnetomZ.text = event.values[2].toString()
                }
                //Log.d("MainActivity", " x:${event.values[0]}, y:${event.values[1]}, z:${event.values[2]} ")
                // [0] x축값, [1] y축값, [2] z축값
            }
        }
    }
    // ⑤ 리스너 해제
    override fun onPause() {
        super.onPause()
        acceletorSensor.unregisterListener(this)
        gyroscopeSensor.unregisterListener(this)
        magnetomSensor.unregisterListener(this)
    }

    //퍼미션 동작
    override fun onRequestPermissionsResult(    //퍼미션 권한이 해제되면 경고창 표시
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if(requestCode == MY_PERMISSION_SENSOR)
            if (grantResults.size > 0) {
                for (grant in grantResults) {
                    if (grant != PackageManager.PERMISSION_GRANTED)
                    {
                        //System.exit(0)
                        Toast.makeText(this,"퍼미션 권한 해제",Toast.LENGTH_SHORT).show()
                    }
                }
            }
    }
}