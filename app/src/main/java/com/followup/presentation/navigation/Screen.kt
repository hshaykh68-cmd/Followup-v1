package com.followup.presentation.navigation

sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object Stats : Screen("stats")
    data object Settings : Screen("settings")
}
