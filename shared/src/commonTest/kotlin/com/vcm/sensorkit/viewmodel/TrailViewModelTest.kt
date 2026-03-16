package com.vcm.sensorkit.viewmodel

import com.vcm.sensorkit.domain.models.HapticCommand
import com.vcm.sensorkit.domain.models.SensorEvent
import com.vcm.sensorkit.repository.MockLocationRepository
import com.vcm.sensorkit.repository.MockSensorRepository
import com.vcm.sensorkit.ui.viewmodels.TrailViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class TrailViewModelTest {

    private val sensorMock = MockSensorRepository()
    private val locationMock = MockLocationRepository()
    private lateinit var viewModel: TrailViewModel
    private val testDispatcher = StandardTestDispatcher()

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = TrailViewModel(locationMock, sensorMock, testDispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `test haptic command emission on sensor event`() = runTest {
        val results = mutableListOf<HapticCommand>()

        backgroundScope.launch {
            viewModel.hapticCommand.collect { results.add(it) }
        }

        viewModel.startTracking()


        val t1 = 1_000_000_000L
        val t2 = 1_500_000_000L

        sensorMock.emit(SensorEvent(1f, 0f, 0f, t1))
        runCurrent()
        sensorMock.emit(SensorEvent(1f, 0f, 0f, t2))


        advanceUntilIdle()

        assertTrue(results.any { it is HapticCommand.Cadence }, "No se recibió Cadence")
    }

    @Test
    fun `test stop command after inactivity delay`() = runTest {
        val results = mutableListOf<HapticCommand>()

        backgroundScope.launch {
            viewModel.hapticCommand.collect { results.add(it) }
        }

        viewModel.startTracking()

        sensorMock.emit(SensorEvent(1f, 0f, 0f, 1_000_000_000L))
        runCurrent()

        advanceTimeBy(3500)
        runCurrent()

        assertTrue(results.any { it is HapticCommand.Stop }, "No se recibió Stop")
    }
}