package com.vcm.sensorkit.data.storage

import android.content.Context

class AndroidPatternStorage (
    private val context: Context
) : PatternStorage {

     override suspend fun save(json: String) {

        context.openFileOutput("patterns.json", Context.MODE_PRIVATE)
            .bufferedWriter()
            .use { it.write(json) }
    }

     override suspend fun load(): String? {

        return try {
            context.openFileInput("patterns.json")
                .bufferedReader()
                .readText()
        } catch (e: Exception) {
            println( "Error loading patterns: ${e.message}")
            null
        }
    }
}
