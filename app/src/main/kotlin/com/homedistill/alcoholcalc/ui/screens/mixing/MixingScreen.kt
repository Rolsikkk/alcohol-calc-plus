package com.homedistill.alcoholcalc.ui.screens.mixing

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
import com.homedistill.alcoholcalc.ui.components.ResetButton
import com.homedistill.alcoholcalc.ui.theme.AppFieldColors

@Composable
fun MixingScreen(onBack: () -> Unit, viewModel: MixingViewModel = viewModel()) {
    val s1 by viewModel.solution1.collectAsStateWithLifecycle()
    val s2 by viewModel.solution2.collectAsStateWithLifecycle()
    val s3 by viewModel.solution3.collectAsStateWithLifecycle()
    val result by viewModel.result.collectAsStateWithLifecycle()

    val unitMl = stringResource(R.string.unit_ml)
    val unitG = stringResource(R.string.unit_g)
    val unitPercent = stringResource(R.string.unit_percent)

    val fields = listOf(s1, s2, s3)
    val solutionLabels = listOf(
        stringResource(R.string.mixing_solution_1),
        stringResource(R.string.mixing_solution_2),
        stringResource(R.string.mixing_solution_3),
    )

    CalculatorScaffold(title = stringResource(R.string.tab_mixing), onBack = onBack) {
        fields.forEachIndexed { index, fieldState ->
            FieldCard(title = solutionLabels[index]) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    GridCell(
                        value = fieldState.volumeText,
                        unitLabel = unitMl,
                        onValueChange = { viewModel.onVolumeChange(index, it) },
                        color = AppFieldColors.Volume,
                    )
                    GridCell(
                        value = fieldState.massText,
                        unitLabel = unitG,
                        onValueChange = { viewModel.onMassChange(index, it) },
                        color = AppFieldColors.Neutral,
                    )
                    GridCell(
                        value = fieldState.abvText,
                        unitLabel = unitPercent,
                        onValueChange = { viewModel.onAbvChange(index, it) },
                        color = AppFieldColors.Percent,
                    )
                }
            }
        }

        val total = result.total
        FieldCard(title = stringResource(R.string.mixing_result)) {
            Row(modifier = Modifier.fillMaxWidth()) {
                GridCell(
                    value = total?.let { formatDecimal(it.resultVolumeMl, 0) } ?: DASH,
                    unitLabel = unitMl,
                    onValueChange = null,
                    color = AppFieldColors.Volume,
                )
                GridCell(
                    value = total?.let { formatDecimal(it.totalMassG, 0) } ?: DASH,
                    unitLabel = unitG,
                    onValueChange = null,
                    color = AppFieldColors.Neutral,
                )
                GridCell(
                    value = total?.let { formatDecimal(it.resultAbvPct, 1) } ?: DASH,
                    unitLabel = unitPercent,
                    onValueChange = null,
                    color = AppFieldColors.Percent,
                )
            }
        }

        ResetButton(label = stringResource(R.string.action_reset), onClick = viewModel::reset)
    }
}
