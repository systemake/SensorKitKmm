package com.vcm.sensorkit.tabs

import android.Manifest
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState
import com.vcm.sensorkit.LocationProviderRepositoryImpl
import com.vcm.sensorkit.SensorRepositoryImpl
import com.vcm.sensorkit.VibrationEffectRepositoryImpl
import com.vcm.sensorkit.models.HapticCommand
import com.vcm.sensorkit.viewmodels.TrailViewModel

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun HapticTrail() {

    val context = LocalContext.current
    val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager

    var hasLocationPermission by remember { mutableStateOf(false) }
    var hasActivityPermission by remember { mutableStateOf(false) }

    val permissionLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->

            hasLocationPermission =
                permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false

            hasActivityPermission =
                permissions[Manifest.permission.ACTIVITY_RECOGNITION] ?: false
        }

    LaunchedEffect(Unit) {
        if (!hasLocationPermission) {
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACTIVITY_RECOGNITION
                )
            )
        }
    }

    if (!hasLocationPermission || !hasActivityPermission) {

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("Waiting for location permission...")
        }

        return
    }

    val fusedClient = remember {
        LocationServices.getFusedLocationProviderClient(context)
    }

    val locationRepository = remember {
        LocationProviderRepositoryImpl(fusedClient)
    }
    val sensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR)
    val sensorRepository =
        remember { SensorRepositoryImpl(sensorManager, Sensor.TYPE_STEP_DETECTOR) }
    println("STEP SENSOR: $sensor")
    val vibrationRepository = remember { VibrationEffectRepositoryImpl(context) }
    var currentCommand by remember { mutableStateOf<HapticCommand?>(null) }


    val viewModel = remember {
        TrailViewModel(
            locationRepository,
            sensorRepository
        )
    }

    LaunchedEffect(Unit) {
        viewModel.startTracking()

        viewModel.hapticCommand.collect { command ->
            currentCommand = command
            vibrationRepository.vibrate(command)
        }

    }

    val trail by viewModel.trail.collectAsState()
    val myLocation = trail.locationEvents.lastOrNull()?.let {
        LatLng(it.latitude, it.longitude)
    }

    val cameraPositionState = rememberCameraPositionState()
    var firstLocationCentered by remember { mutableStateOf(false) }

    LaunchedEffect(myLocation) {
        if (!firstLocationCentered && myLocation != null) {
            cameraPositionState.move(
                CameraUpdateFactory.newLatLngZoom(myLocation, 18f)
            )

            firstLocationCentered = true
        }
    }

    LaunchedEffect(Unit) {
        viewModel.startTracking()
    }

    val path = trail.locationEvents.map {
        LatLng(it.latitude, it.longitude)
    }

    val mapProperties = MapProperties(
        isMyLocationEnabled = hasLocationPermission
    )

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = mapProperties
        ) {
            Polyline(
                points = path,
                width = 8f
            )
        }

        when (currentCommand) {

            is HapticCommand.Cadence -> {
                Button(
                    onClick = { println("Walking..") },
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(8.dp)
                ) {
                    Text("Walking")
                }


            }
            is HapticCommand.Stop -> {
                Button(
                    onClick = { println("Stopped") },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(8.dp)
                ) {
                    Text("Stopped")
                }
            }
            else -> {}
        }
    }



}

