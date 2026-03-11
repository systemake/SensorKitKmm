package com.vcm.sensorkit

import android.content.Context
import android.hardware.SensorManager
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vcm.sensorkit.models.HapticCommand
import com.vcm.sensorkit.viewmodels.CompassViewModel

@Composable
@Preview
fun App() {
    MaterialTheme {

        val context = LocalContext.current
        val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager

        val sensorRepository = remember { SensorRepositoryImpl(sensorManager) }

        val vibrationRepository = remember { VibrationEffectRepositoryImpl(context) }

        val viewModel = remember { CompassViewModel(sensorRepository) }

        val sensorState by viewModel.sensorState.collectAsState()

        val motionEvent by viewModel.motionEvent.collectAsState()

        LaunchedEffect(Unit) {
            viewModel.startSensors()
            println("calling..")
            viewModel.hapticCommand.collect { command ->

                when(command){

                    is HapticCommand.Cardinal -> {
                        vibrationRepository.vibrate()
                    }

                }
            }
        }

        DisposableEffect(Unit) {
            onDispose {
                viewModel.stop()
            }
        }


        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.primaryContainer)
                .safeContentPadding()
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {


            Text(
                text = "Accelerometer",
                fontSize = 18.sp,
                color = Color.Red,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)

            )

            Text("X: ${sensorState?.x}")
            Text("Y: ${sensorState?.y}")
            Text("Z: ${sensorState?.z}")

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Orientation",
                fontSize = 18.sp,
                color = Color.Red,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)

            )

            Spacer(modifier = Modifier.height(20.dp))

            Text("Roll: ${motionEvent?.roll}")
            Text("Pitch: ${motionEvent?.pitch}")
            Text("Yaw: ${motionEvent?.yaw}")

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Vibration",
                fontSize = 18.sp,
                color = Color.Red,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)

            )
            Text("vibrate angle: ${motionEvent?.heading}")

        }
    }
}