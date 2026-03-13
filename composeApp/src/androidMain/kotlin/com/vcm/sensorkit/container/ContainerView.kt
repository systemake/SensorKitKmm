package com.vcm.sensorkit.container

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.vcm.sensorkit.tabs.HapticStudio
import com.vcm.sensorkit.tabs.HapticTrail
import com.vcm.sensorkit.tabs.MotionCompass

sealed class BottomTab(val route: String, val title: String, val icon: ImageVector) {
    object First : BottomTab("first", "MotionCompass", Icons.Default.Home)
    object Second : BottomTab("second", "HapicTrail", Icons.Default.ChatBubbleOutline)
    object Third : BottomTab("third", "HapicStudio", Icons.Default.Person)
}

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun ContainerView() {
    val bottomNavController = rememberNavController()

    val tabs = listOf(
        BottomTab.First,
        BottomTab.Second,
        BottomTab.Third
    )

    Scaffold(
        bottomBar = {
            NavigationBar {
                val currentRoute =
                    bottomNavController.currentBackStackEntryAsState().value?.destination?.route

                tabs.forEach { tab ->
                    NavigationBarItem(
                        selected = currentRoute == tab.route,
                        onClick = {
                            bottomNavController.navigate(tab.route) {
                                popUpTo(bottomNavController.graph.startDestinationId)
                                launchSingleTop = true
                            }
                        },
                        icon = { Icon(tab.icon, contentDescription = tab.title) },
                        label = { Text(tab.title) }
                    )
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = bottomNavController,
            startDestination = BottomTab.First.route,
            modifier = Modifier.padding(padding)
        ) {
            composable(BottomTab.First.route) { MotionCompass() }
            composable(BottomTab.Second.route) { HapticTrail() }
            composable(BottomTab.Third.route) { HapticStudio() }
        }
    }

}