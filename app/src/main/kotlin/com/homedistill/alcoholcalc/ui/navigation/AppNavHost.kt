package com.homedistill.alcoholcalc.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.homedistill.alcoholcalc.ui.screens.dilution.DilutionScreen
import com.homedistill.alcoholcalc.ui.screens.heater.HeaterScreen
import com.homedistill.alcoholcalc.ui.screens.home.HomeScreen
import com.homedistill.alcoholcalc.ui.screens.hydrometer.HydrometerScreen
import com.homedistill.alcoholcalc.ui.screens.mixing.MixingScreen
import com.homedistill.alcoholcalc.ui.screens.pressure.PressureScreen
import com.homedistill.alcoholcalc.ui.screens.rectification.RectificationScreen
import com.homedistill.alcoholcalc.ui.screens.settings.SettingsScreen
import com.homedistill.alcoholcalc.ui.screens.thermometer.ThermometerScreen
import com.homedistill.alcoholcalc.ui.screens.timer.TimerScreen

private const val ROUTE_HOME = "home"
private const val ROUTE_SETTINGS = "settings"

@Composable
fun AppNavHost(onLanguageChanged: () -> Unit) {
    val navController: NavHostController = rememberNavController()

    NavHost(navController = navController, startDestination = ROUTE_HOME) {
        composable(ROUTE_HOME) {
            HomeScreen(
                onOpenTab = { tab -> navController.navigate(tab.route) },
                onOpenSettings = { navController.navigate(ROUTE_SETTINGS) },
            )
        }
        composable(ROUTE_SETTINGS) {
            SettingsScreen(
                onBack = navController::popBackStack,
                onLanguageChanged = onLanguageChanged,
            )
        }
        composable(CalculatorTab.DILUTION.route) {
            DilutionScreen(onBack = navController::popBackStack)
        }
        composable(CalculatorTab.RECTIFICATION.route) {
            RectificationScreen(onBack = navController::popBackStack)
        }
        composable(CalculatorTab.MIXING.route) {
            MixingScreen(onBack = navController::popBackStack)
        }
        composable(CalculatorTab.TIMER.route) {
            TimerScreen(onBack = navController::popBackStack)
        }
        composable(CalculatorTab.THERMOMETER.route) {
            ThermometerScreen(onBack = navController::popBackStack)
        }
        composable(CalculatorTab.HYDROMETER.route) {
            HydrometerScreen(onBack = navController::popBackStack)
        }
        composable(CalculatorTab.PRESSURE.route) {
            PressureScreen(onBack = navController::popBackStack)
        }
        composable(CalculatorTab.HEATER.route) {
            HeaterScreen(onBack = navController::popBackStack)
        }
    }
}
