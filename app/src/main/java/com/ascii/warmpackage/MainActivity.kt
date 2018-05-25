package com.ascii.warmpackage

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.Color
import android.os.Bundle
import android.os.IBinder
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.SeekBar
import com.ascii.warmpackage.model.WarmService
import com.ascii.warmpackage.presenter.MainPresenter
import com.ascii.warmpackage.presenter.WarmPackagePresenter

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*

class MainActivity : AppCompatActivity(), WarmPackageView {

    private val MAX_TEMPERATURE:Int = 50

    private var warmService: WarmService? = null
    private var presenter: WarmPackagePresenter? =null
    private var parentView: View? = null
    private var initTargetTemp:Int = 0
    private var snackbar:Snackbar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        presenter = MainPresenter(this)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        seekbarTemperature.setOnSeekBarChangeListener(seekBarChangeListener)
        parentView = findViewById(R.id.parent_view)
        fab.setOnClickListener { view ->
            ( presenter?.startWarm() )
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (presenter?.getIsRunning() == false) {
            presenter?.closeService()
        }
    }

    override fun onResume() {
        super.onResume()
        bindService(Intent(this, WarmService::class.java), serviceConnectionListener, Context.BIND_AUTO_CREATE)
    }

    override fun onPause() {
        super.onPause()
        unbindService(serviceConnectionListener)
    }

    override fun initUIStatus() {
        var targetTemperature:Int = presenter?.getTargetTemperature()?.toInt() ?: 0
        textviewTemperature.setText("" + targetTemperature)
        updateUIStatus()
    }

    // private ServiceConnection serviceConnectionListener = new ServiceConnection() 的 Kotlin 寫法
    private val serviceConnectionListener = object: ServiceConnection {
        override fun onServiceDisconnected(className: ComponentName?) {
            presenter?.detachModel()
            warmService = null
        }

        override fun onServiceConnected(className: ComponentName?, service: IBinder?) {
            val binder = service as WarmService.WarmBinder
            warmService = binder.getService()
            // if (persenter != null) { ... } 的簡潔寫法
            // 若不需回傳值，或只有一個指令要操作，直接用 warmService?.foo() 即可
            // 若需要回傳值，就要改用 ?.let { it } 語法來寫，可保平安之外又不失簡潔
            warmService?.let {
                presenter?.attachModel(it)
                presenter?.initial()
            }
            if (warmService == null) {
                updateUIStatus()
            }
        }
    }

    private var seekBarChangeListener = object: SeekBar.OnSeekBarChangeListener {
        override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
            if (fromUser) {
                var temperature = initTargetTemp + progress
                textviewTemperature.setText("" + temperature)
                presenter?.setTargetTemperature(temperature.toDouble())
            }
        }

        override fun onStartTrackingTouch(seekBar: SeekBar?) {
        }

        override fun onStopTrackingTouch(seekBar: SeekBar?) {
        }
    }

    override fun updateCurrentTemperature(currentTemp: Double) {
        if (initTargetTemp == 0) {
            initTargetTemp = Math.min(currentTemp.toInt() + 5, MAX_TEMPERATURE)
            var tempMax = MAX_TEMPERATURE - initTargetTemp
            seekbarTemperature.setMax(tempMax)
            textviewTemperature.setText("" + initTargetTemp)

            // 因為 presenter 可能是 null 所以不能只寫 if (presenter?.getIsRunning())
            if (presenter?.getIsRunning() == true) {
                var targetTemperature:Int = presenter?.getTargetTemperature()?.toInt() ?: 0
                var progress:Int = targetTemperature - initTargetTemp
                seekbarTemperature.setProgress(progress)
                textviewTemperature.setText("" + targetTemperature)
            } else {
                presenter?.setTargetTemperature(initTargetTemp.toDouble())
            }
        }
        textviewCurrentTemperature.text = currentTemp.toString()
    }

    override fun updateUIStatus() {
        var uiStatus: Boolean = presenter?.getIsRunning() ?: false

        // 這是 Kotlin 的 Switch case
        when (uiStatus) {
            true -> {
                // Kotlin 有 Property (屬性) 的概念
                // 所以像這種 Java 風格的 get/set 在 Kotlin 中會希望你改用 Property 來操作
                fab.setEnabled(false)
                seekbarTemperature.setEnabled(false)
                createSnackBar()
            }
            false -> {
                // isEnabled 是一個 Property
                // 作用等價於 Java 的 setEnabled(true)
                fab.isEnabled = true
                seekbarTemperature.isEnabled = true
                closeSnackBar()
            }
        }
    }

    // Unit 就是 void 的意思，可省略不寫
    // Nothing
    private fun createSnackBar(): Unit {
        // object: View.OnClickListener 是 new View.OnClickListener 最直接的翻譯
        snackbar = Snackbar.make(parentView!!, R.string.running, Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.action_exit, object : View.OnClickListener {
                    override fun onClick(v: View?) {
                        presenter?.stopWarm()
                    }
                })
        snackbar?.let {
            it.setActionTextColor(Color.WHITE)
            it.show()
        }
    }

    private fun closeSnackBar() {
        snackbar?.dismiss()
    }
}
