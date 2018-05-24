package com.ascii.warmpackage.presenter

import com.ascii.warmpackage.model.WarmPackageModel

interface WarmPackagePresenter {
    fun initial()
    fun attachModel(model: WarmPackageModel)
    fun detachModel()
    fun startWarm()
    fun stopWarm()
    fun getIsRunning(): Boolean
    fun setTargetTemperature(temperature: Double)
    fun getTargetTemperature(): Double?
    fun closeService()
}