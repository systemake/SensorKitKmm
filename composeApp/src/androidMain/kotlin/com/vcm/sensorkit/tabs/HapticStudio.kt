package com.vcm.sensorkit.tabs

import android.content.Context
import android.hardware.SensorManager
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.vcm.sensorkit.HapticPlayerRepositoryImpl
import com.vcm.sensorkit.domain.models.HapticPattern
import com.vcm.sensorkit.domain.models.HapticType
import com.vcm.sensorkit.data.storage.AndroidPatternStorage
import com.vcm.sensorkit.ui.viewmodels.HapticStudioViewModel

@Composable
fun HapticStudio() {

    val context = LocalContext.current
    val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager

    val storage = remember {
        AndroidPatternStorage(context)
    }

    val hapticPlayerRepository = remember { HapticPlayerRepositoryImpl(context) }

    val viewModel = remember { HapticStudioViewModel(hapticPlayerRepository, storage) }
    val patterns by viewModel.patterns.collectAsState()
    val selected by viewModel.selectedPattern.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.log.collect { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }


    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {


        LazyColumn(modifier = Modifier.weight(1f)) {
            items(patterns) { pattern ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp)
                        .clickable { viewModel.selectPattern(pattern) },
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(pattern.name)
                    if (selected == pattern) {
                        Text("Selected")
                    }
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = { viewModel.playSelectedPattern() },
            enabled = selected != null,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Play")
        }

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = {
                viewModel.addPattern(
                    HapticPattern(
                        id = "p${patterns.size + 1}",
                        name = "Text  ${patterns.size + 1}",
                        intensity = 0.8f,
                        sharpness = 0.5f,
                        duration = 200,
                        attack = 0.1f,
                        decay = 0.1f,
                        type = HapticType.TRANSIENT
                    )
                )
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Add Text")
        }
        Button(onClick = {
             viewModel.save()
        } , modifier = Modifier.fillMaxWidth()) {
            Text("Export to JSON")
        }

        Button(onClick = {
            viewModel.load()
        },  modifier = Modifier.fillMaxWidth()) {
            Text("Import from JSON")
        }


    }


}

