package com.vcm.sensorkit.data.storage

interface  PatternStorage {
    suspend fun save(json: String)
    suspend fun load(): String?

}