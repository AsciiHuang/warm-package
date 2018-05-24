package com.ascii.warmpackage.presenter

import com.ascii.warmpackage.WarmPackageView
import com.ascii.warmpackage.model.WarmPackageModel

class MainPresenter(view: WarmPackageView): WarmPackagePresenter {

    private var view: WarmPackageView?= null
    private var model: WarmPackageModel?= null

    init {
        this.view = view
    }

    override fun attachModel(model: WarmPackageModel) {
        this.model = model
    }

    override fun detachModel() {
        model = null
    }

    override fun getIsRunning(): Boolean {
        return model?.getIsRunning() ?: false
    }

    override fun startWarm() {
        model?.startWarm()
        view?.updateUIStatus()
    }

    override fun initial() {
        model?.setTemperatureSensorUpdateListener(temperatureUpdateListener)
        view?.initUIStatus()
    }

    override fun stopWarm() {
        model?.stopWarm()
        view?.updateUIStatus()
    }

    override fun setTargetTemperature(temperature: Double) {
        model?.setTargetTemperature(temperature)
    }

    override fun getTargetTemperature(): Double? {
        return model?.getTargetTemperature()
    }

    override fun closeService() {
        model?.closeService()
    }

    private var temperatureUpdateListener = object: WarmPackageModel.TemperatureSensorUpdate {
        override fun notifyTargetArrival() {
            view.updateUIStatus()
        }

        override fun update(temperature: Double) {
            view.updateCurrentTemperature(temperature)
        }
    }
}