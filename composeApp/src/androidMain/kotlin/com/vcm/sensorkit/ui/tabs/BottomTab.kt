package com.vcm.sensorkit.ui.tabs

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomTab(val route: String, val title: String, val icon: ImageVector) {
    object First : BottomTab("first", "MotionCompass", Icons.Default.Home)
    object Second : BottomTab("second", "HapicTrail", Icons.Default.ChatBubbleOutline)
    object Third : BottomTab("third", "HapicStudio", Icons.Default.Person)
}