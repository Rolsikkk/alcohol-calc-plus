package com.homedistill.alcoholcalc.ui.screens.pressure

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
fun PressureScreen(onBack: () -> Unit, viewModel: PressureViewModel = viewModel()) {
    val pressure by viewModel.pressureText.collectAsStateWithLifecycle()
    val cubeAbv by viewModel.cubeAbvText.collectAsStateWithLifecycle()
    val result by viewModel.result.collectAsStateWithLifecycle()

    CalculatorScaffold(title = stringResource(R.string.tab_pressure), onBack = onBack) {
        FieldCard {
            LabeledValueRow(
                label = stringResource(R.string.pressure_p),
                value = pressure,
                onValueChange = viewModel::onPressureChange,
                color = AppFieldColors.Neutral,
            )
            LabeledValueRow(
                label = stringResource(R.string.pressure_cube_abv),
                value = cubeAbv,
                onValueChange = viewModel::onCubeAbvChange,
                color = AppFieldColors.Percent,
            )
            LabeledValueRow(
                label = stringResource(R.string.pressure_boil_temp),
                value = result?.let { formatDecimal(it.boilingTempC, 1) } ?: DASH,
                onValueChange = null,
                color = AppFieldColors.Temperature,
                highlighted = true,
            )
            LabeledValueRow(
                label = stringResource(R.string.pressure_vapor_abv),
                value = result?.let { formatDecimal(it.vaporAbvPct, 1) } ?: DASH,
                onValueChange = null,
                color = AppFieldColors.Percent,
            )
        }
        HintText(stringResource(R.string.pressure_hint))
    }
}
