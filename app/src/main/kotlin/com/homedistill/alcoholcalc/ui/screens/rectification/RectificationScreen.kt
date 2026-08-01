package com.homedistill.alcoholcalc.ui.screens.rectification

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
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
import com.homedistill.alcoholcalc.ui.components.GridCell
import com.homedistill.alcoholcalc.ui.components.LabeledValueRow
import com.homedistill.alcoholcalc.ui.components.PlainResultLine
import com.homedistill.alcoholcalc.ui.components.ResetButton
import com.homedistill.alcoholcalc.ui.theme.AppFieldColors

@Composable
fun RectificationScreen(onBack: () -> Unit, viewModel: RectificationViewModel = viewModel()) {
    val v by viewModel.vText.collectAsStateWithLifecycle()
    val mass by viewModel.massText.collectAsStateWithLifecycle()
    val p by viewModel.pText.collectAsStateWithLifecycle()
    val heads by viewModel.headsText.collectAsStateWithLifecycle()
    val body by viewModel.bodyText.collectAsStateWithLifecycle()
    val tails by viewModel.tailsText.collectAsStateWithLifecycle()
    val result by viewModel.result.collectAsStateWithLifecycle()

    val unitMl = stringResource(R.string.unit_ml)
    val unitG = stringResource(R.string.unit_g)
    val unitPercent = stringResource(R.string.unit_percent)

    CalculatorScaffold(title = stringResource(R.string.tab_rectification), onBack = onBack) {
        FieldCard(title = stringResource(R.string.rectification_section_raw)) {
            Row(modifier = Modifier.fillMaxWidth()) {
                GridCell(value = v, unitLabel = unitMl, onValueChange = viewModel::onVChange, color = AppFieldColors.Volume)
                GridCell(value = mass, unitLabel = unitG, onValueChange = viewModel::onMassChange, color = AppFieldColors.Neutral)
                GridCell(value = p, unitLabel = unitPercent, onValueChange = viewModel::onPChange, color = AppFieldColors.Percent)
            }
        }

        FieldCard(title = stringResource(R.string.rectification_section_result)) {
            Row(modifier = Modifier.fillMaxWidth()) {
                PlainResultLine(
                    label = stringResource(R.string.rectification_heads),
                    value = result?.let { "${formatDecimal(it.headsMl, 0)} $unitMl" } ?: DASH,
                )
            }
            Row(modifier = Modifier.fillMaxWidth()) {
                PlainResultLine(
                    label = stringResource(R.string.rectification_body),
                    value = result?.let { "${formatDecimal(it.bodyMl, 0)} $unitMl" } ?: DASH,
                )
            }
            Row(modifier = Modifier.fillMaxWidth()) {
                PlainResultLine(
                    label = stringResource(R.string.rectification_tails),
                    value = result?.let { "${formatDecimal(it.tailsMl, 0)} $unitMl" } ?: DASH,
                )
            }
            Row(modifier = Modifier.fillMaxWidth()) {
                PlainResultLine(
                    label = stringResource(R.string.rectification_ac),
                    value = result?.let { "${formatDecimal(it.absoluteAlcoholMl, 0)} $unitMl" } ?: DASH,
                    color = AppFieldColors.Volume,
                )
            }
        }

        FieldCard(title = stringResource(R.string.rectification_section_settings)) {
            LabeledValueRow(
                label = stringResource(R.string.rectification_heads_pct),
                value = heads,
                onValueChange = viewModel::onHeadsChange,
                color = AppFieldColors.Neutral,
            )
            LabeledValueRow(
                label = stringResource(R.string.rectification_body_pct),
                value = body,
                onValueChange = viewModel::onBodyChange,
                color = AppFieldColors.Neutral,
            )
            LabeledValueRow(
                label = stringResource(R.string.rectification_tails_pct),
                value = tails,
                onValueChange = viewModel::onTailsChange,
                color = AppFieldColors.Neutral,
            )
        }

        ResetButton(label = stringResource(R.string.action_reset), onClick = viewModel::reset)
    }
}
