package com.homedistill.alcoholcalc.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.homedistill.alcoholcalc.data.AppSettings
import com.homedistill.alcoholcalc.data.UserPreferencesRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(private val repository: UserPreferencesRepository) : ViewModel() {

    val settings: StateFlow<AppSettings> = repository.settingsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AppSettings())

    fun setTabVisible(tabId: String, visible: Boolean) {
        viewModelScope.launch { repository.setTabVisible(tabId, visible) }
    }

    /**
     * Persists the language choice and only then invokes [onComplete]. The write must be
     * confirmed before the caller recreates the Activity, otherwise attachBaseContext's
     * synchronous read can race the async write and pick up the stale language.
     */
    fun setLanguage(language: String, onComplete: () -> Unit) {
        viewModelScope.launch {
            repository.setLanguage(language)
            onComplete()
        }
    }
}
