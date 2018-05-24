package com.ascii.warmpackage

interface WarmPackageView {
    fun initUIStatus()
    fun updateCurrentTemperature(currentTemp: Double)
    fun updateUIStatus()
}