package com.vcm.sensorkit.storage

interface  PatternStorage {
    suspend fun save(json: String)
    suspend fun load(): String?

}

