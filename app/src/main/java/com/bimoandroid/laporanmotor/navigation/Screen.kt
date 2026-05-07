package com.bimoandroid.laporanmotor.navigation

sealed class Screen(val route: String) {

    data object Login : Screen("login")

    data object Dashboard : Screen("dashboard")

    data object AddReport : Screen("add_report")
}