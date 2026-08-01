package com.homedistill.alcoholcalc.ui.screens.thermometer

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.homedistill.alcoholcalc.R
import com.homedistill.alcoholcalc.core.calculators.formatDecimal
import com.homedistill.alcoholcalc.ui.components.CalculatorScaffold
import com.homedistill.alcoholcalc.ui.components.DASH
import com.homedistill.alcoholcalc.ui.components.FieldCard
import com.homedistill.alcoholcalc.ui.components.HintText
import com.homedistill.alcoholcalc.ui.components.LabeledValueRow
import com.homedistill.alcoholcalc.ui.theme.AppFieldColors

@Composable
fun ThermometerScreen(onBack: () -> Unit, viewModel: ThermometerViewModel = viewModel()) {
    val temp by viewModel.tempText.collectAsStateWithLifecycle()
    val apparent by viewModel.apparentText.collectAsStateWithLifecycle()
    val real by viewModel.real.collectAsStateWithLifecycle()

    CalculatorScaffold(title = stringResource(R.string.tab_thermometer), onBack = onBack) {
        FieldCard {
            LabeledValueRow(
                label = stringResource(R.string.thermometer_temp),
                value = temp,
                onValueChange = viewModel::onTempChange,
                color = AppFieldColors.Temperature,
            )
            LabeledValueRow(
                label = stringResource(R.string.thermometer_apparent),
                value = apparent,
                onValueChange = viewModel::onApparentChange,
                color = AppFieldColors.Percent,
            )
            LabeledValueRow(
                label = stringResource(R.string.thermometer_real),
                value = real?.let { formatDecimal(it, 1) } ?: DASH,
                onValueChange = null,
                color = AppFieldColors.Percent,
            )
        }
        HintText(stringResource(R.string.thermometer_hint))
    }
}
