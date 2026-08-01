package com.homedistill.alcoholcalc.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.ui.graphics.vector.ImageVector
import com.homedistill.alcoholcalc.R
import com.homedistill.alcoholcalc.data.CalculatorTabIds

/** One entry in the home-screen list. [id] doubles as the DataStore key and the nav route. */
enum class CalculatorTab(val id: String, val titleRes: Int, val icon: ImageVector) {
    DILUTION(CalculatorTabIds.DILUTION, R.string.tab_dilution, Icons.Filled.WaterDrop),
    RECTIFICATION(CalculatorTabIds.RECTIFICATION, R.string.tab_rectification, Icons.Filled.Science),
    MIXING(CalculatorTabIds.MIXING, R.string.tab_mixing, Icons.Filled.Layers),
    TIMER(CalculatorTabIds.TIMER, R.string.tab_timer, Icons.Filled.Timer),
    THERMOMETER(CalculatorTabIds.THERMOMETER, R.string.tab_thermometer, Icons.Filled.Thermostat),
    HYDROMETER(CalculatorTabIds.HYDROMETER, R.string.tab_hydrometer, Icons.Filled.Straighten),
    PRESSURE(CalculatorTabIds.PRESSURE, R.string.tab_pressure, Icons.Filled.Speed),
    HEATER(CalculatorTabIds.HEATER, R.string.tab_heater, Icons.Filled.Bolt),
    ;

    val route: String get() = id

    companion object {
        fun fromId(id: String): CalculatorTab? = entries.find { it.id == id }
    }
}
