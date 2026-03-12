package com.vcm.sensorkit.tabs

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.SensorManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState
import com.vcm.sensorkit.LocationRepositoryImpl
import com.vcm.sensorkit.SensorRepositoryImpl
import com.vcm.sensorkit.VibrationEffectRepositoryImpl
import com.vcm.sensorkit.viewmodels.TrailViewModel

@Composable
fun HapticTrail() {

    val context = LocalContext.current
    val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager

    var hasLocationPermission by remember {
        mutableStateOf(
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED)}


    val permissionLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { granted ->

            if (granted) {
                println("Permiso concedido")


            } else {
                println("Permiso denegado")
            }
        }


    LaunchedEffect(Unit) {
        if (!hasLocationPermission) {
            permissionLauncher.launch(
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        }
    }

    if (!hasLocationPermission) {

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
            LocationRepositoryImpl(fusedClient)
        }

        val sensorRepository = remember { SensorRepositoryImpl(sensorManager) }

        val vibrationRepository = remember { VibrationEffectRepositoryImpl(context) }

        val viewModel = remember {
            TrailViewModel(
                locationRepository,
                sensorRepository
            )
        }

        LaunchedEffect(Unit) {
            viewModel.startTracking()

            viewModel.hapticCommand.collect { command ->
                vibrationRepository.vibrate(command)
            }

        }

        val trail by viewModel.trail.collectAsState()
        val myLocation = LatLng(-12.0464, -77.0428)
        val cameraPositionState = rememberCameraPositionState {
            position = CameraPosition.fromLatLngZoom(myLocation, 15f)
        }

        LaunchedEffect(Unit) {
            viewModel.startTracking()
        }

        val path = trail.coordinates.map {
            LatLng(it.latitude, it.longitude)
        }

        val mapProperties = MapProperties(
            isMyLocationEnabled = hasLocationPermission
        )

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

}

