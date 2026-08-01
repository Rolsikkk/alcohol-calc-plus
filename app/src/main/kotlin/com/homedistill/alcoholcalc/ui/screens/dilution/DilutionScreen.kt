package com.homedistill.alcoholcalc.ui.screens.dilution

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.homedistill.alcoholcalc.R
import com.homedistill.alcoholcalc.core.calculators.DilutionResult
import com.homedistill.alcoholcalc.core.calculators.formatDecimal
import com.homedistill.alcoholcalc.ui.components.CalculatorScaffold
import com.homedistill.alcoholcalc.ui.components.DASH
import com.homedistill.alcoholcalc.ui.components.FieldCard
import com.homedistill.alcoholcalc.ui.components.GridCell
import com.homedistill.alcoholcalc.ui.components.HintText
import com.homedistill.alcoholcalc.ui.components.PlainResultLine
import com.homedistill.alcoholcalc.ui.components.ResetButton
import com.homedistill.alcoholcalc.ui.components.StepperButtons
import com.homedistill.alcoholcalc.ui.theme.AppFieldColors

@Composable
fun DilutionScreen(onBack: () -> Unit, viewModel: DilutionViewModel = viewModel()) {
    val v1 by viewModel.v1Text.collectAsStateWithLifecycle()
    val g1 by viewModel.g1Text.collectAsStateWithLifecycle()
    val p1 by viewModel.p1Text.collectAsStateWithLifecycle()
    val target by viewModel.targetText.collectAsStateWithLifecycle()
    val result by viewModel.result.collectAsStateWithLifecycle()

    val unitMl = stringResource(R.string.unit_ml)
    val unitG = stringResource(R.string.unit_g)
    val unitPercent = stringResource(R.string.unit_percent)
    val success = result as? DilutionResult.Success

    CalculatorScaffold(title = stringResource(R.string.tab_dilution), onBack = onBack) {
        FieldCard(title = stringResource(R.string.dilution_section_spirit)) {
            Row(modifier = Modifier.fillMaxWidth()) {
                GridCell(value = v1, unitLabel = unitMl, onValueChange = viewModel::onV1Change, color = AppFieldColors.Volume)
                GridCell(value = g1, unitLabel = unitG, onValueChange = viewModel::onG1Change, color = AppFieldColors.Neutral)
                GridCell(
                    value = p1,
                    unitLabel = unitPercent,
                    onValueChange = viewModel::onP1Change,
                    color = AppFieldColors.Percent,
                    trailing = { StepperButtons(onDecrement = { viewModel.stepP1(-0.5) }, onIncrement = { viewModel.stepP1(0.5) }) },
                )
            }
        }

        FieldCard(title = stringResource(R.string.dilution_section_water)) {
            Row(modifier = Modifier.fillMaxWidth()) {
                GridCell(
                    value = success?.let { formatDecimal(it.waterVolumeMl, 0) } ?: DASH,
                    unitLabel = unitMl,
                    onValueChange = null,
                    color = AppFieldColors.Volume,
                )
                GridCell(
                    value = success?.let { formatDecimal(it.waterMassG, 0) } ?: DASH,
                    unitLabel = unitG,
                    onValueChange = null,
                    color = AppFieldColors.Neutral,
                )
            }
        }

        FieldCard(title = stringResource(R.string.dilution_section_result)) {
            Row(modifier = Modifier.fillMaxWidth()) {
                GridCell(
                    value = success?.let { formatDecimal(it.resultVolumeMl, 0) } ?: DASH,
                    unitLabel = unitMl,
                    onValueChange = null,
                    color = AppFieldColors.Volume,
                )
                GridCell(
                    value = success?.let { formatDecimal(it.resultMassG, 0) } ?: DASH,
                    unitLabel = unitG,
                    onValueChange = null,
                    color = AppFieldColors.Neutral,
                )
                GridCell(
                    value = target,
                    unitLabel = unitPercent,
                    onValueChange = viewModel::onTargetChange,
                    color = AppFieldColors.Percent,
                    trailing = { StepperButtons(onDecrement = { viewModel.stepTarget(-0.5) }, onIncrement = { viewModel.stepTarget(0.5) }) },
                )
            }
        }

        PlainResultLine(
            label = stringResource(R.string.dilution_ac),
            value = success?.let { formatDecimal(it.absoluteAlcoholMl, 0) } ?: DASH,
        )

        if (result is DilutionResult.Invalid) {
            HintText(stringResource(R.string.dilution_invalid_hint))
        }

        ResetButton(label = stringResource(R.string.action_reset), onClick = viewModel::reset)
    }
}
