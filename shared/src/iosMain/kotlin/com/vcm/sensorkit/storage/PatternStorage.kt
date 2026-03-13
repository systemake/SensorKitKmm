package com.vcm.sensorkit.storage

import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.*
@OptIn(ExperimentalForeignApi::class)
actual class PatternStorage {

    actual suspend fun save(json: String) {

        val path = NSSearchPathForDirectoriesInDomains(
            NSDocumentDirectory,
            NSUserDomainMask,
            true
        ).first() as String

        val filePath = "$path/patterns.json"
        val nsString = json as NSString

        nsString.writeToFile(
            filePath,
            atomically = true,
            encoding = NSUTF8StringEncoding,
            error = null
        )
    }


    actual suspend fun load(): String? {

        val path = NSSearchPathForDirectoriesInDomains(
            NSDocumentDirectory,
            NSUserDomainMask,
            true
        ).first() as String

        val filePath = "$path/patterns.json"

        return NSString.stringWithContentsOfFile(
            filePath,
            NSUTF8StringEncoding,
            null
        ) as String?
    }
}