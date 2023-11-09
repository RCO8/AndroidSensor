package kr.co.leelab.sensortest

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorManager
import android.hardware.SensorEventListener
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.widget.Chronometer
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kr.co.leelab.sensortest.databinding.ActivityMainBinding
import org.w3c.dom.Text

class MainActivity : AppCompatActivity(), SensorEventListener {

    //퍼미션 (권한요청)
    val MY_PERMISSION_SENSOR = 100

    /*센서동작할 속성들*/
    //가속도 센서
    //각 센서가 움직임을 감지받는 축
    lateinit var sensorX : TextView
    lateinit var sensorY : TextView
    lateinit var sensorZ : TextView

    lateinit var sensorState : TextView
    private var activityPermission : Boolean = false

    //추가할 속성
    /*
    센서 동작에 따른 타이머
    타이머 일정 시간 초과시 상태 설정
     */
    lateinit var stateTimer : Chronometer

    // 알람을 울리기 위한 타이머  (우선 0으로 초기)
    private var secondAlarm : Int = 0
    private var minuteAlarm : Int = 0
    private var hourAlarm : Int = 0

    private var isTimer : Boolean = false   // 타이머 실행 상태

    lateinit var bingind : ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //가속도 센서
        sensorX = findViewById(R.id.sensorX)
        sensorY = findViewById(R.id.sensorY)
        sensorZ = findViewById(R.id.sensorZ)

        sensorState = findViewById(R.id.sensorState)


        // 퍼미션 확인해주는 데이터
        val sensorPermission = ContextCompat.checkSelfPermission(this,
            Manifest.permission.BODY_SENSORS)
        val sensorBackgroundPermission = ContextCompat.checkSelfPermission(this,
            Manifest.permission.BODY_SENSORS_BACKGROUND)
        if(sensorPermission == PackageManager.PERMISSION_GRANTED || //센서 퍼미션이 권한이 확인되면
            sensorBackgroundPermission == PackageManager.PERMISSION_GRANTED) {
            activityPermission = true
        }
        else    // 안되있으면 설정 요청
        {
            activityPermission = false
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.BODY_SENSORS),MY_PERMISSION_SENSOR)
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.BODY_SENSORS_BACKGROUND),MY_PERMISSION_SENSOR)
        }

        //타이머 속성
        stateTimer = findViewById(R.id.stateTimer)

        stateTimer.start()

        //알람 타이머 지정
        secondAlarm = 10
    }

    private val acceletorSensor by lazy {           // 지연된 초기화는 딱 한 번 실행됨

        getSystemService(Context.SENSOR_SERVICE) as SensorManager

    }

    override fun onResume(){    //센서 등록 메서드
        super.onResume()
        acceletorSensor.registerListener(
            this,
            acceletorSensor.getDefaultSensor(Sensor.TYPE_GYROSCOPE),    //가속도 혹은 자이로 중에 하나를 선택할텐데, 우선 설계하기 쉬운것부터 해보고
            SensorManager.SENSOR_DELAY_NORMAL
        )

    }
    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {    // 센서 정밀도 변경시
    //이 메서드는 onSensorChanged를 사용하기 위해 필요한 함수이다 그리고 메인 클래스에서 SensorEventListener를 상속받아야 한다 (interface 상속)
    }

    override fun onSensorChanged(event: SensorEvent?) {               // 센서 값 변경시
        //타이머 리셋 (동작시 상태 활성)
        if(activityPermission)
        {
            event.let {
                //가속도 센서 받아오는 값
                sensorX.text = event!!.values[0].toString()
                sensorY.text = event.values[1].toString()
                sensorZ.text = event.values[2].toString()

                if(event.values[0] == 0f && event.values[1] == 0f && event.values[2] == 0f) {
                    sensorState.text = "센서 미동작"//타이머 실행
                    isTimer = true
                    stateTimer.start()
                }
                else {
                    sensorState.text = "센서 동작" //타이머 리셋
                    isTimer = false
                    stateTimer.base = SystemClock.elapsedRealtime()
                    stateTimer.stop()
                }
                // [0] x축값, [1] y축값, [2] z축값
            }
            //타이머 적용
            Log.d("MainActivity", " x:${event!! .values[0]}, y:${event.values[1]}, z:${event.values[2]} ")

        }
    }
    // ⑤ 리스너 해제
    override fun onPause() {
        super.onPause()
        acceletorSensor.unregisterListener(this)
        sensorState.text="센서 미동작"
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