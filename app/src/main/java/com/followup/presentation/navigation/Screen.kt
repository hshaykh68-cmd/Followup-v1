package com.followup.presentation.navigation

sealed class Screen(val route: String) {
    data object Onboarding : Screen("onboarding")
    data object NotificationPermission : Screen("notification_permission")
    data object Home : Screen("home")
    data object Stats : Screen("stats")
    data object Settings : Screen("settings")
}
