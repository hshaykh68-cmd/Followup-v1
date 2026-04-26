package com.followup.presentation.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.followup.R
import com.followup.domain.repository.SettingsRepository
import com.followup.presentation.home.HomeScreen
import com.followup.presentation.onboarding.NotificationPermissionScreen
import com.followup.presentation.onboarding.OnboardingScreen
import com.followup.presentation.reminder.ReminderViewModel
import com.followup.presentation.settings.SettingsScreen
import com.followup.presentation.stats.StatsScreen
import com.followup.service.notification.NotificationPermissionHelper
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation(
    settingsRepository: SettingsRepository,
    notificationPermissionHelper: NotificationPermissionHelper
) {
    val navController = rememberNavController()
    val viewModel: ReminderViewModel = hiltViewModel()
    val scope = rememberCoroutineScope()

    // Check onboarding and permission states
    val onboardingCompleted by settingsRepository.isOnboardingCompleted()
        .collectAsState(initial = false)
    val notificationListenerPromptShown by settingsRepository.isNotificationListenerPromptShown()
        .collectAsState(initial = false)
    val isNotificationListenerEnabled = notificationPermissionHelper.isNotificationListenerEnabled()

    // Determine start destination based on state
    val startDestination = when {
        !onboardingCompleted -> Screen.Onboarding.route
        !notificationListenerPromptShown && !isNotificationListenerEnabled -> Screen.NotificationPermission.route
        else -> Screen.Home.route
    }

    val items = listOf(
        NavigationItem(
            route = Screen.Home.route,
            label = stringResource(R.string.nav_home),
            selectedIcon = Icons.Filled.Home,
            unselectedIcon = Icons.Outlined.Home
        ),
        NavigationItem(
            route = Screen.Stats.route,
            label = stringResource(R.string.nav_stats),
            selectedIcon = Icons.Filled.Star,
            unselectedIcon = Icons.Outlined.Star
        ),
        NavigationItem(
            route = Screen.Settings.route,
            label = stringResource(R.string.nav_settings),
            selectedIcon = Icons.Filled.Settings,
            unselectedIcon = Icons.Outlined.Settings
        )
    )

    Scaffold(
        topBar = {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route

            // Hide top bar on onboarding
            if (currentRoute == Screen.Onboarding.route) return@Scaffold

            val title = when (currentRoute) {
                Screen.Home.route -> stringResource(R.string.app_name)
                Screen.Stats.route -> stringResource(R.string.nav_stats)
                Screen.Settings.route -> stringResource(R.string.nav_settings)
                else -> ""
            }

            TopAppBar(
                title = {
                    Text(
                        text = title,
                        style = if (currentRoute == Screen.Home.route) {
                            // App name gets the strongest treatment
                            MaterialTheme.typography.headlineLarge
                        } else {
                            // Other screens use clean headline
                            MaterialTheme.typography.headlineMedium
                        }
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        bottomBar = {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route

            // Hide bottom bar on onboarding
            if (currentRoute == Screen.Onboarding.route) return@Scaffold

            NavigationBar(
                tonalElevation = 3.dp
            ) {
                val currentDestination = navBackStackEntry?.destination

                items.forEach { item ->
                    val selected = currentDestination?.hierarchy?.any {
                        it.route == item.route
                    } == true

                    NavigationBarItem(
                        icon = {
                            Icon(
                                imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                                contentDescription = item.label
                            )
                        },
                        label = { Text(item.label) },
                        selected = selected,
                        onClick = {
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            enterTransition = {
                fadeIn(animationSpec = tween(300))
            },
            exitTransition = {
                fadeOut(animationSpec = tween(300))
            }
        ) {
            composable(Screen.Onboarding.route) {
                OnboardingScreen(
                    onComplete = {
                        scope.launch {
                            settingsRepository.setOnboardingCompleted(true)
                        }
                        // Navigate to permission screen instead of home
                        navController.navigate(Screen.NotificationPermission.route) {
                            popUpTo(Screen.Onboarding.route) {
                                inclusive = true
                            }
                        }
                    },
                    onSkip = {
                        scope.launch {
                            settingsRepository.setOnboardingCompleted(true)
                        }
                        // Navigate to permission screen instead of home
                        navController.navigate(Screen.NotificationPermission.route) {
                            popUpTo(Screen.Onboarding.route) {
                                inclusive = true
                            }
                        }
                    }
                )
            }

            composable(Screen.NotificationPermission.route) {
                NotificationPermissionScreen(
                    notificationPermissionHelper = notificationPermissionHelper,
                    onPermissionGranted = {
                        scope.launch {
                            settingsRepository.setNotificationListenerPromptShown(true)
                            settingsRepository.setNotificationListenerEnabled(true)
                        }
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.NotificationPermission.route) {
                                inclusive = true
                            }
                        }
                    },
                    onSkip = {
                        scope.launch {
                            settingsRepository.setNotificationListenerPromptShown(true)
                        }
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.NotificationPermission.route) {
                                inclusive = true
                            }
                        }
                    }
                )
            }

            composable(Screen.Home.route) {
                HomeScreen(viewModel = viewModel)
            }

            composable(Screen.Stats.route) {
                StatsScreen(viewModel = viewModel)
            }

            composable(Screen.Settings.route) {
                SettingsScreen()
            }
        }
    }
}

private data class NavigationItem(
    val route: String,
    val label: String,
    val selectedIcon: androidx.compose.ui.graphics.vector.ImageVector,
    val unselectedIcon: androidx.compose.ui.graphics.vector.ImageVector
)
