package com.homedistill.alcoholcalc.ui.screens.timer

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.homedistill.alcoholcalc.R
import com.homedistill.alcoholcalc.core.calculators.formatDecimal
import com.homedistill.alcoholcalc.core.calculators.formatMmSs
import com.homedistill.alcoholcalc.ui.components.CalculatorScaffold
import com.homedistill.alcoholcalc.ui.components.DASH
import com.homedistill.alcoholcalc.ui.components.FieldCard
import com.homedistill.alcoholcalc.ui.components.LabeledValueRow
import com.homedistill.alcoholcalc.ui.theme.AppFieldColors

@Composable
fun TimerScreen(onBack: () -> Unit, viewModel: TimerViewModel = viewModel()) {
    val volume by viewModel.volumeText.collectAsStateWithLifecycle()
    val time by viewModel.timeText.collectAsStateWithLifecycle()
    val rate by viewModel.rate.collectAsStateWithLifecycle()
    val isRunning by viewModel.isRunning.collectAsStateWithLifecycle()
    val elapsedSeconds by viewModel.elapsedSeconds.collectAsStateWithLifecycle()

    CalculatorScaffold(title = stringResource(R.string.tab_timer), onBack = onBack) {
        FieldCard {
            LabeledValueRow(
                label = stringResource(R.string.timer_volume),
                value = volume,
                onValueChange = viewModel::onVolumeChange,
                color = AppFieldColors.Neutral,
            )
            LabeledValueRow(
                label = stringResource(R.string.timer_time),
                value = time,
                onValueChange = viewModel::onTimeChange,
                color = AppFieldColors.Neutral,
            )
            LabeledValueRow(
                label = stringResource(R.string.timer_ml_per_hour),
                value = rate?.let { formatDecimal(it.mlPerHour, 0) } ?: DASH,
                onValueChange = null,
                color = AppFieldColors.Muted,
            )
            LabeledValueRow(
                label = stringResource(R.string.timer_ml_per_min),
                value = rate?.let { formatDecimal(it.mlPerMinute, 1) } ?: DASH,
                onValueChange = null,
                color = AppFieldColors.Muted,
            )
        }

        Text(
            text = formatMmSs(elapsedSeconds),
            style = MaterialTheme.typography.titleLarge.copy(fontSize = 40.sp),
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )

        if (isRunning) {
            OutlinedButton(onClick = viewModel::stopStopwatch, modifier = Modifier.fillMaxWidth()) {
                Text(stringResource(R.string.timer_stop))
            }
        } else {
            Button(onClick = viewModel::startStopwatch, modifier = Modifier.fillMaxWidth()) {
                Text(stringResource(R.string.timer_start))
            }
        }

        Spacer(modifier = Modifier.height(48.dp))

        OutlinedButton(onClick = viewModel::resetStopwatch, modifier = Modifier.fillMaxWidth()) {
            Text(stringResource(R.string.timer_reset))
        }
    }
}
