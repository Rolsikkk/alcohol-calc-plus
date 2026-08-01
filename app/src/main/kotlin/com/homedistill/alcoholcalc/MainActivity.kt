package com.homedistill.alcoholcalc

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.homedistill.alcoholcalc.ui.navigation.AppNavHost
import com.homedistill.alcoholcalc.ui.theme.AlcoholCalcTheme
import com.homedistill.alcoholcalc.ui.update.UpdateGate

/**
 * Extends AppCompatActivity (not just ComponentActivity) so per-app language changes via
 * AppCompatDelegate.setApplicationLocales() recreate the activity automatically, with no
 * custom attachBaseContext/blocking DataStore read needed at cold start.
 */
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AlcoholCalcTheme {
                AppNavHost()
                UpdateGate()
            }
        }
    }
}
