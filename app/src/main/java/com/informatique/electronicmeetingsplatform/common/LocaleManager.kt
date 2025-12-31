package com.informatique.electronicmeetingsplatform.common

import android.content.Context
import android.content.res.Configuration
import java.util.Locale

object LocaleManager {
    fun applyLocale(context: Context, lang: String): Context {
        val locale = Locale(lang)
        Locale.setDefault(locale)
        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)
        config.setLayoutDirection(locale)
        return context.createConfigurationContext(config)
    }
}

