package com.vcm.sensorkit.utils

import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

object FlowUtils {

    fun <T> collectStateFlow(
        flow: StateFlow<T>,
        callback: (T) -> Unit
    ): Job {
        return MainScope().launch {
            flow.collect {
                callback(it)
            }
        }
    }

    fun <T> collectSharedFlow(
        flow: SharedFlow<T>,
        callback: (T) -> Unit
    ): Job {
        return MainScope().launch {
            flow.collect {
                callback(it)
            }
        }
    }
}