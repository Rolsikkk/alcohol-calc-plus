package com.homedistill.alcoholcalc.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.homedistill.alcoholcalc.data.CalculatorTabIds
import com.homedistill.alcoholcalc.data.UserPreferencesRepository
import com.homedistill.alcoholcalc.ui.navigation.CalculatorTab
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class HomeViewModel(repository: UserPreferencesRepository) : ViewModel() {

    val visibleTabs: StateFlow<List<CalculatorTab>> = repository.settingsFlow
        .map { settings ->
            CalculatorTab.entries.filter { it.id in settings.visibleTabs || it.id in CalculatorTabIds.LOCKED }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), CalculatorTab.entries.toList())
}
