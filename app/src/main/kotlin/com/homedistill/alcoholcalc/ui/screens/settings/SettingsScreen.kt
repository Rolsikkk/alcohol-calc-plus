package com.homedistill.alcoholcalc.ui.screens.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.homedistill.alcoholcalc.R
import com.homedistill.alcoholcalc.data.CalculatorTabIds
import com.homedistill.alcoholcalc.data.Language
import com.homedistill.alcoholcalc.data.UserPreferencesRepository
import com.homedistill.alcoholcalc.ui.components.FieldCard
import com.homedistill.alcoholcalc.ui.components.HintText
import com.homedistill.alcoholcalc.ui.components.CalculatorScaffold
import com.homedistill.alcoholcalc.ui.navigation.CalculatorTab

@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    onLanguageChanged: () -> Unit,
    viewModel: SettingsViewModel = run {
        val context = LocalContext.current.applicationContext
        viewModel(
            factory = viewModelFactory {
                initializer { SettingsViewModel(UserPreferencesRepository(context)) }
            },
        )
    },
) {
    val settings by viewModel.settings.collectAsStateWithLifecycle()

    CalculatorScaffold(title = stringResource(R.string.tab_settings), onBack = onBack) {
        FieldCard(title = stringResource(R.string.settings_visible_tabs)) {
            CalculatorTab.entries.forEach { tab ->
                val locked = tab.id in CalculatorTabIds.LOCKED
                val checked = locked || tab.id in settings.visibleTabs
                TabCheckboxRow(
                    label = stringResource(tab.titleRes),
                    checked = checked,
                    enabled = !locked,
                    onCheckedChange = { visible -> viewModel.setTabVisible(tab.id, visible) },
                )
            }
            TabCheckboxRow(
                label = stringResource(R.string.tab_settings),
                checked = true,
                enabled = false,
                onCheckedChange = {},
            )
        }

        HorizontalDivider(modifier = Modifier.fillMaxWidth())

        FieldCard(title = stringResource(R.string.settings_language)) {
            LanguageOption(
                label = stringResource(R.string.settings_language_ru),
                selected = settings.language == Language.RU,
                onSelect = { viewModel.setLanguage(Language.RU, onComplete = onLanguageChanged) },
            )
            LanguageOption(
                label = stringResource(R.string.settings_language_en),
                selected = settings.language == Language.EN,
                onSelect = { viewModel.setLanguage(Language.EN, onComplete = onLanguageChanged) },
            )
        }

        HintText(stringResource(R.string.settings_hint))
    }
}

@Composable
private fun TabCheckboxRow(
    label: String,
    checked: Boolean,
    enabled: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .selectable(
                selected = checked,
                enabled = enabled,
                onClick = { onCheckedChange(!checked) },
            ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Checkbox(checked = checked, onCheckedChange = onCheckedChange, enabled = enabled)
        Text(
            text = label,
            color = if (enabled) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun LanguageOption(label: String, selected: Boolean, onSelect: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .selectable(selected = selected, onClick = onSelect),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        RadioButton(selected = selected, onClick = onSelect)
        Text(label)
    }
}
