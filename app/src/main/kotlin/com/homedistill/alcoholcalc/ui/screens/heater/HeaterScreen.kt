package com.homedistill.alcoholcalc.ui.screens.heater

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.homedistill.alcoholcalc.R
import com.homedistill.alcoholcalc.core.calculators.formatDecimal
import com.homedistill.alcoholcalc.ui.components.CalculatorScaffold
import com.homedistill.alcoholcalc.ui.components.DASH
import com.homedistill.alcoholcalc.ui.components.FieldCard
import com.homedistill.alcoholcalc.ui.components.LabeledValueRow
import com.homedistill.alcoholcalc.ui.theme.AppFieldColors

@Composable
fun HeaterScreen(onBack: () -> Unit, viewModel: HeaterViewModel = viewModel()) {
    val ratedVoltage by viewModel.ratedVoltageText.collectAsStateWithLifecycle()
    val ratedPower by viewModel.ratedPowerText.collectAsStateWithLifecycle()
    val realVoltage by viewModel.realVoltageText.collectAsStateWithLifecycle()
    val result by viewModel.result.collectAsStateWithLifecycle()

    CalculatorScaffold(title = stringResource(R.string.tab_heater), onBack = onBack) {
        FieldCard {
            LabeledValueRow(
                label = stringResource(R.string.heater_rated_voltage),
                value = ratedVoltage,
                onValueChange = viewModel::onRatedVoltageChange,
                color = AppFieldColors.Volume,
            )
            LabeledValueRow(
                label = stringResource(R.string.heater_rated_power),
                value = ratedPower,
                onValueChange = viewModel::onRatedPowerChange,
                color = AppFieldColors.Power,
            )
            LabeledValueRow(
                label = stringResource(R.string.heater_resistance),
                value = result?.let { formatDecimal(it.resistanceOhm, 1) } ?: DASH,
                onValueChange = null,
                color = AppFieldColors.Muted,
            )

            HorizontalDivider(modifier = Modifier.fillMaxWidth())

            LabeledValueRow(
                label = stringResource(R.string.heater_real_voltage),
                value = realVoltage,
                onValueChange = viewModel::onRealVoltageChange,
                color = AppFieldColors.Volume,
            )
            LabeledValueRow(
                label = stringResource(R.string.heater_real_power),
                value = result?.let { formatDecimal(it.realPowerW, 0) } ?: DASH,
                onValueChange = null,
                color = AppFieldColors.Power,
            )
            LabeledValueRow(
                label = stringResource(R.string.heater_real_current),
                value = result?.let { formatDecimal(it.realCurrentA, 1) } ?: DASH,
                onValueChange = null,
                color = AppFieldColors.Muted,
            )
        }
    }
}
