package com.homedistill.alcoholcalc.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "settings")

class UserPreferencesRepository(private val context: Context) {

    private object Keys {
        val VISIBLE_TABS = stringSetPreferencesKey("visible_tabs")
    }

    val settingsFlow: Flow<AppSettings> = context.dataStore.data.map { prefs ->
        AppSettings(visibleTabs = prefs[Keys.VISIBLE_TABS] ?: CalculatorTabIds.ALL)
    }

    suspend fun setTabVisible(tabId: String, visible: Boolean) {
        if (tabId in CalculatorTabIds.LOCKED && !visible) return
        context.dataStore.edit { prefs ->
            val current = prefs[Keys.VISIBLE_TABS] ?: CalculatorTabIds.ALL
            prefs[Keys.VISIBLE_TABS] = if (visible) current + tabId else current - tabId
        }
    }
}
