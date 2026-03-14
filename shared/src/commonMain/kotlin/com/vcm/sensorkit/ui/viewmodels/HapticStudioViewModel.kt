package com.vcm.sensorkit.ui.viewmodels
import com.vcm.sensorkit.domain.models.HapticPattern
import com.vcm.sensorkit.domain.repository.HapticPlayerRepository
import com.vcm.sensorkit.data.storage.PatternStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class HapticStudioViewModel(
    private val hapticPlayer: HapticPlayerRepository,
    private val storage: PatternStorage
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    private val _patterns = MutableStateFlow<List<HapticPattern>>(emptyList())
    val patterns: StateFlow<List<HapticPattern>> = _patterns

    private val _selectedPattern = MutableStateFlow<HapticPattern?>(null)
    val selectedPattern: StateFlow<HapticPattern?> = _selectedPattern

    private val _log = MutableSharedFlow<String>()
    val log: SharedFlow<String> = _log


    fun addPattern(pattern: HapticPattern) {
        _patterns.value = _patterns.value + pattern
    }

    fun selectPattern(pattern: HapticPattern) {
        _selectedPattern.value = pattern
    }


    fun playSelectedPattern() {
        val pattern = _selectedPattern.value ?: return
        hapticPlayer.play(pattern)
        scope.launch {
            _log.emit("Vibration: ${pattern.name}")
        }
    }


    fun save() {

        scope.launch {

            val json = Json.encodeToString(_patterns.value)

            storage.save(json)

            _log.emit("Saved")
        }
    }

    fun load() {

        scope.launch {

            val json = storage.load() ?: return@launch
            val imported = Json.decodeFromString<List<HapticPattern>>(json)
            _patterns.value = imported

            _log.emit("Loaded")
        }
    }
}
