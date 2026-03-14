package com.vcm.sensorkit.models

import kotlinx.serialization.Serializable
import kotlin.experimental.ExperimentalObjCName
import kotlin.native.ObjCName
@OptIn(ExperimentalObjCName::class)
@Serializable
enum class HapticType {

    @ObjCName("Transient") TRANSIENT,
    @ObjCName("Continuous") CONTINUOUS
}