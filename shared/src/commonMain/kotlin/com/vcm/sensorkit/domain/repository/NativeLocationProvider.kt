package com.vcm.sensorkit.domain.repository

import com.vcm.sensorkit.domain.models.LocationEvent

interface NativeLocationProvider {
    fun setListener(onLocation: (LocationEvent) -> Unit)
}