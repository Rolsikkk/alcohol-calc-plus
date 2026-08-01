package com.homedistill.alcoholcalc

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.homedistill.alcoholcalc.data.LocaleHelper
import com.homedistill.alcoholcalc.data.UserPreferencesRepository
import com.homedistill.alcoholcalc.ui.navigation.AppNavHost
import com.homedistill.alcoholcalc.ui.theme.AlcoholCalcTheme
import com.homedistill.alcoholcalc.ui.update.UpdateGate
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class MainActivity : ComponentActivity() {

    override fun attachBaseContext(newBase: Context) {
        val language = runBlocking { UserPreferencesRepository(newBase).settingsFlow.first().language }
        super.attachBaseContext(LocaleHelper.wrap(newBase, language))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AlcoholCalcTheme {
                AppNavHost(onLanguageChanged = { recreate() })
                UpdateGate()
            }
        }
    }
}
