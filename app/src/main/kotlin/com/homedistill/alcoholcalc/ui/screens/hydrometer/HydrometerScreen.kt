package com.homedistill.alcoholcalc.ui.screens.hydrometer

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
import com.homedistill.alcoholcalc.ui.components.LabeledValueRow
import com.homedistill.alcoholcalc.ui.theme.AppFieldColors

@Composable
fun HydrometerScreen(onBack: () -> Unit, viewModel: HydrometerViewModel = viewModel()) {
    val temp by viewModel.tempText.collectAsStateWithLifecycle()
    val sg by viewModel.sgText.collectAsStateWithLifecycle()
    val result by viewModel.result.collectAsStateWithLifecycle()

    CalculatorScaffold(title = stringResource(R.string.tab_hydrometer), onBack = onBack) {
        FieldCard {
            LabeledValueRow(
                label = stringResource(R.string.hydrometer_temp),
                value = temp,
                onValueChange = viewModel::onTempChange,
                color = AppFieldColors.Temperature,
            )
            LabeledValueRow(
                label = stringResource(R.string.hydrometer_sg),
                value = sg,
                onValueChange = viewModel::onSgChange,
                color = AppFieldColors.Neutral,
            )
            LabeledValueRow(
                label = stringResource(R.string.hydrometer_brix),
                value = result?.let { formatDecimal(it.brix, 1) } ?: DASH,
                onValueChange = null,
                color = AppFieldColors.Muted,
            )
        }
        FieldCard {
            LabeledValueRow(
                label = stringResource(R.string.hydrometer_sg20),
                value = result?.let { formatDecimal(it.sg20, 0) } ?: DASH,
                onValueChange = null,
                color = AppFieldColors.Muted,
            )
            LabeledValueRow(
                label = stringResource(R.string.hydrometer_brix20),
                value = result?.let { formatDecimal(it.brix20, 1) } ?: DASH,
                onValueChange = null,
                color = AppFieldColors.Muted,
            )
        }
    }
}
