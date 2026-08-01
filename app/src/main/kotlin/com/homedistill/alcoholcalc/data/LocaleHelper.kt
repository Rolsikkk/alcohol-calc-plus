package com.homedistill.alcoholcalc.data

import android.content.Context
import android.content.res.Configuration
import java.util.Locale

/** Wraps a [Context] with a configuration pinned to [languageCode], for use in attachBaseContext. */
object LocaleHelper {
    fun wrap(context: Context, languageCode: String): Context {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)
        return context.createConfigurationContext(config)
    }
}
