package com.vcm.sensorkit.storage

import android.content.Context

actual class PatternStorage(
    private val context: Context
) {

    actual suspend fun save(json: String) {

        context.openFileOutput("patterns.json", Context.MODE_PRIVATE)
            .bufferedWriter()
            .use { it.write(json) }
    }

    actual suspend fun load(): String? {

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