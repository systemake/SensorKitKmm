package com.vcm.sensorkit

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform