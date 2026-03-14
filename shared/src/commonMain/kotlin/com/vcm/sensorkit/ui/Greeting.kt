package com.vcm.sensorkit.ui

import com.vcm.sensorkit.getPlatform

class Greeting {
    private val platform = getPlatform()

    fun greet(): String {
        return "Hello, ${platform.name}!"
    }
}