package com.ascii.warmpackage

interface WarmPackageModel {
    fun startWarm()
    fun stopWarm()
    fun getIsRunning(): Boolean
    fun setTemperatureSensorUpdateListener(listener: TemperatureSensorUpdate)
    fun setTargetTemperature(temperature: Double)
    fun getTargetTemperature(): Double
    fun closeService()

    interface TemperatureSensorUpdate {
        fun update(temperature: Double)
        fun notifyTargetArrival()
    }
}