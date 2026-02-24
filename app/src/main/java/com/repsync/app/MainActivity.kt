package com.repsync.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.repsync.app.navigation.BottomNavBar
import com.repsync.app.navigation.BottomNavTab
import com.repsync.app.navigation.RepSyncNavHost
import com.repsync.app.navigation.Screen
import com.repsync.app.ui.components.ActiveWorkoutBanner
import com.repsync.app.ui.theme.RepSyncTheme
import com.repsync.app.ui.viewmodel.ActiveWorkoutManager

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT),
        )
        setContent {
            RepSyncTheme {
                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                // Activity-scoped ViewModel — persists across all navigation
                val activeWorkoutManager: ActiveWorkoutManager = viewModel(
                    viewModelStoreOwner = this@MainActivity,
                )

                val bannerInfo by activeWorkoutManager.bannerInfo.collectAsState()

                val isOnActiveWorkoutScreen = currentRoute?.startsWith("active_workout") == true
                    || currentRoute == Screen.QuickWorkout.route

                val showBottomNav = currentRoute == Screen.Home.route
                    || currentRoute == Screen.Profile.route

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = MaterialTheme.colorScheme.background,
                    bottomBar = {
                        if (showBottomNav) {
                            BottomNavBar(
                                currentRoute = currentRoute,
                                onTabSelected = { tab ->
                                    if (currentRoute == tab.route) return@BottomNavBar

                                    // From any screen, pop back to Home and navigate to the tab
                                    navController.navigate(tab.route) {
                                        popUpTo(Screen.Home.route) {
                                            inclusive = (tab == BottomNavTab.Home)
                                        }
                                        launchSingleTop = true
                                    }
                                },
                            )
                        }
                    },
                ) { innerPadding ->
                    Column(
                        modifier = Modifier
                            .padding(innerPadding)
                            .consumeWindowInsets(innerPadding),
                    ) {
                        // Active workout banner — visible when workout is active
                        // and user is NOT on the active workout screen itself
                        val info = bannerInfo
                        if (info != null && !isOnActiveWorkoutScreen) {
                            ActiveWorkoutBanner(
                                bannerInfo = info,
                                onTap = {
                                    val state = activeWorkoutManager.activeWorkoutState.value
                                        ?: return@ActiveWorkoutBanner
                                    if (state.isQuickWorkout) {
                                        navController.navigate(Screen.QuickWorkout.route) {
                                            launchSingleTop = true
                                        }
                                    } else {
                                        state.templateId?.let { id ->
                                            navController.navigate(
                                                Screen.ActiveWorkout.createRoute(id)
                                            ) {
                                                launchSingleTop = true
                                            }
                                        }
                                    }
                                },
                            )
                        }

                        RepSyncNavHost(
                            navController = navController,
                            activeWorkoutManager = activeWorkoutManager,
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
            }
        }
    }
}
