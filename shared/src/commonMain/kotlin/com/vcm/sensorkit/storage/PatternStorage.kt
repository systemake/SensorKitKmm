package com.vcm.sensorkit.storage

expect class PatternStorage {

    suspend fun save(json: String)

    suspend fun load(): String?

}