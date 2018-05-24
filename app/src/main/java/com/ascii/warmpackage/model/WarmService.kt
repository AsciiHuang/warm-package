package com.ascii.warmpackage.model

import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.app.PendingIntent
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.support.v4.app.NotificationCompat
import android.util.Log
import java.math.BigInteger
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import com.android.volley.toolbox.Volley
import com.android.volley.RequestQueue
import com.ascii.warmpackage.Base64
import com.ascii.warmpackage.MainActivity
import com.ascii.warmpackage.R
import com.ascii.warmpackage.WarmApp
import com.ascii.warmpackage.api.API
import com.ascii.warmpackage.api.InvoiceAPI
import com.ascii.warmpackage.api.InvoiceResult

class WarmService : Service(), WarmPackageModel {

    private val WARM_NOTIFICATION: Int = 0
    private val warmBinder = WarmBinder()
    private var isRunning = false

    // Kotlin 所有的型別名字都是大寫開頭，沒有 double 與 Double 之分
    private var currentTemperature: Double = 0.0
    private var targetTemperature: Double = 0.0
    private var temperatureSensor: Sensor? = null
    private var temperatureUpdateListener: WarmPackageModel.TemperatureSensorUpdate? = null
    private var threadList: ArrayList<WarmRunnable> = ArrayList()
    private var requestQueue: RequestQueue? = null

    override fun onCreate() {
        super.onCreate()
        stopWarm()
        setUpSensor()
        cancelNotification()
        requestQueue = Volley.newRequestQueue(this)
    }

    override fun onBind(p0: Intent?): IBinder {
        return warmBinder
    }

    override fun onDestroy() {
        super.onDestroy()
        stopWarm()
        cancelNotification()
        var sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        sensorManager.unregisterListener(sensorEventListener)
        temperatureUpdateListener = null
    }

    inner class WarmBinder : Binder() {
        fun getService(): WarmService {
            return this@WarmService
        }
    }

    private fun setUpSensor() {
        // Kotlin 的轉型是用 as
        // 如果想避免 CaseException 也可以改用 as? 在無法轉型時回傳 null
        var sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager

        // 理論上這樣拿得到溫度 Sensor
        temperatureSensor = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE)

        if (temperatureSensor == null) {
            // 實際上很多機器要這樣才拿得到 (然後 htc 是兩種方式都拿不到)
            // 這裡示範了 ?: 運算式
            // 若不為空就得到左側的東西
            // 若為空就得到右側的東西
            // 而 emptyList() 是 Kotlin 的語法糖
            var allSensor:List<Sensor> = sensorManager.getSensorList(Sensor.TYPE_ALL) ?: emptyList()

            // 正常是寫 for (sensor:Sensor in allSensor)
            // 但 Kotlin 可自動辨示所以型態可以省略
            for (sensor in allSensor) {
                Log.e("Sensor", sensor.toString())
                if (sensor.name.toLowerCase().indexOf("temp") >= 0) {
                    temperatureSensor = sensor
                    break
                }
            }
        }

        // if (temperatureSensor != null) { ... } 的簡潔寫法
        temperatureSensor?.let {
            sensorManager.registerListener(sensorEventListener, temperatureSensor, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    override fun startWarm() {
        isRunning = true
        threadList.clear()
        createWarmThread()
        createAndUpdateNotification(true)
        demoAPICall()
    }

    private fun createWarmThread() {
        // for (int i=1; i<=30; ++i) 的意思
        for (i in 1..30) {
            try {
                var runnable = WarmRunnable()
                threadList.add(runnable)
                Thread(runnable).start()
            } catch (e: Exception) {
                break
            }
        }
    }

    private fun demoAPICall() {
        requestQueue?.let {
            InvoiceAPI(it).success(object: API.APISuccessListener<InvoiceResult> {
                override fun onSuccess(result: InvoiceResult) {
                    Log.e("InvoiceResult API", result.toString())
                }
            }).fail(object: API.APIFailListener {
                override fun onFail() {
                    Log.e("InvoiceResult API", "Fail")
                }
            }).start()
        }
    }

    override fun stopWarm() {
        for (runnable in threadList) {
            runnable.stop = true
        }
        cancelNotification()
        threadList.clear()
        isRunning = false
    }

    override fun getIsRunning(): Boolean {
        return isRunning
    }

    override fun setTemperatureSensorUpdateListener(listener: WarmPackageModel.TemperatureSensorUpdate) {
        temperatureUpdateListener = listener
    }

    override fun setTargetTemperature(temperature: Double) {
        targetTemperature = temperature
    }

    // private SensorEventListener sensorEventListener = new SensorEventListener() 的意思
    private var sensorEventListener = object: SensorEventListener {
        override fun onSensorChanged(event: SensorEvent) {
            var temperature: Double = event.values[0] + 0.05
            var temp: Int = (temperature * 10).toInt()
            temperature = temp / 10.0
            if (currentTemperature != temperature) {
                currentTemperature = temperature
                temperatureUpdateListener?.update(currentTemperature)
                if (currentTemperature >= targetTemperature) {
                    stopWarm()
                    temperatureUpdateListener?.notifyTargetArrival()
                }
                if (isRunning) {
                    createAndUpdateNotification(false)
                }
            }
        }

        override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
        }
    }

    override fun getTargetTemperature(): Double {
        return targetTemperature
    }

    override fun closeService() {
        stopSelf()
    }

    private fun createAndUpdateNotification(first: Boolean) {
        val contentText:String = String.format("$currentTemperature / ${getTargetTemperature()}")
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0)

        // 在 Kotlin 中沒有 static 的用法
        // 要用 companion object + const 的方式來取代
        // 請至 WarmApp 中參考 DEFAULT_CHANNEL_ID 的宣告方式
        val mBuilder = NotificationCompat.Builder(this, WarmApp.DEFAULT_CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(getString(R.string.running))
                .setContentText(contentText)
                .setOngoing(true)
                .setContentIntent(pendingIntent)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        var notification = mBuilder.build()
        notificationManager.notify(WARM_NOTIFICATION, notification)
        if (first) {
            startForeground(WARM_NOTIFICATION, notification)
        }
    }

    private fun cancelNotification() {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(WARM_NOTIFICATION)
    }

    fun getMd5Hash(input: String): String? {
        try {
            val md = MessageDigest.getInstance("MD5")
            val messageDigest = md.digest(input.toByteArray())
            val number = BigInteger(1, messageDigest)
            var md5 = number.toString(16)
            while (md5.length < 32) {
                md5 = "0$md5"
            }
            return md5
        } catch (e: NoSuchAlgorithmException) {
            return null
        }
    }

    inner class WarmRunnable: Runnable {
        var stop: Boolean = false
        override fun run() {
            Log.e("Warm", "Thread Started")
            while (!stop) {
                var time = System.currentTimeMillis()
                var timeMD5Base64 = getMd5Hash(Base64.encodeString(System.currentTimeMillis().toString()))
                if (time % 5000 == 0L) {
                    Log.e("MD5", timeMD5Base64)
                }
            }
            Log.e("Warm", "Thread Stoped")
        }
    }
}