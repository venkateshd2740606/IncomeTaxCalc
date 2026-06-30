package com.incometaxcalc.presentation.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.incometaxcalc.presentation.ui.screens.*

sealed class TaxRoute(val route: String, val label: String) {
    data object Calculator : TaxRoute("calculator", "Calculator")
    data object Compare : TaxRoute("compare", "Compare")
    data object Tips : TaxRoute("tips", "Tips")
}

@Composable
fun TaxNavHost() {
    val navController = rememberNavController()
    val items = listOf(TaxRoute.Calculator, TaxRoute.Compare, TaxRoute.Tips)
    val navBackStack by navController.currentBackStackEntryAsState()
    val current = navBackStack?.destination?.route

    Scaffold(
        bottomBar = {
            NavigationBar {
                items.forEach { route ->
                    val icon = when (route) {
                        TaxRoute.Calculator -> Icons.Default.Calculate
                        TaxRoute.Compare -> Icons.Default.CompareArrows
                        TaxRoute.Tips -> Icons.Default.TipsAndUpdates
                    }
                    NavigationBarItem(
                        selected = current == route.route,
                        onClick = {
                            navController.navigate(route.route) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(icon, route.label) },
                        label = { Text(route.label) }
                    )
                }
            }
        }
    ) { padding ->
        NavHost(
            navController,
            startDestination = TaxRoute.Calculator.route,
            modifier = Modifier.padding(padding)
        ) {
            composable(TaxRoute.Calculator.route) { CalculatorScreen() }
            composable(TaxRoute.Compare.route) { RegimeCompareScreen() }
            composable(TaxRoute.Tips.route) { TaxSavingTipsScreen() }
        }
    }
}
