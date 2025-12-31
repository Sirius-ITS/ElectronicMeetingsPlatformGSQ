package com.informatique.electronicmeetingsplatform.ui

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.informatique.electronicmeetingsplatform.common.util.LocalAppLocale
import com.informatique.electronicmeetingsplatform.navigation.NavHost
import com.informatique.electronicmeetingsplatform.navigation.NavigationManagerImpl
import com.informatique.electronicmeetingsplatform.ui.base.BaseActivity
import com.informatique.electronicmeetingsplatform.ui.theme.AppTheme
import com.informatique.electronicmeetingsplatform.ui.theme.ThemeOption
import com.informatique.electronicmeetingsplatform.ui.viewModel.LanguageViewModel
import com.informatique.electronicmeetingsplatform.ui.viewModel.ThemeViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : BaseActivity() {

    @Inject
    lateinit var navigationManager: NavigationManagerImpl

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val configuration = resources.configuration

        // ناخد قيمة fontScale من الجهاز
        val originalScale = configuration.fontScale

        // نحدد الحدود اللي نسمح بيها
        val limitedScale = originalScale.coerceIn(0.85f, 1.15f)

        // لو كانت القيمة برا الرينج، نعدلها
        if (originalScale != limitedScale) {
            configuration.fontScale = limitedScale
            val newContext = createConfigurationContext(configuration)
            applyOverrideConfiguration(newContext.resources.configuration)
        }

        setContent {
            val languageViewModel: LanguageViewModel = hiltViewModel()
            val themeViewModel: ThemeViewModel = hiltViewModel()

            val lang by languageViewModel.languageFlow.collectAsState(initial = "ar")
            val currentLocale = Locale(lang)
            val themeOption by themeViewModel.theme.collectAsState(initial = ThemeOption.SYSTEM_DEFAULT)

            // State to control splash visibility
            var showSplash by remember { mutableStateOf(true) }

            CompositionLocalProvider(
                LocalLayoutDirection provides if (lang == "ar") LayoutDirection.Rtl else LayoutDirection.Ltr,
                LocalAppLocale provides currentLocale
            ) {

                AppTheme(themeOption = themeOption) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        // Main content (always rendered but hidden behind splash)
                        AnimatedVisibility(
                            visible = !showSplash,
                            enter = fadeIn(tween(400)),
                            exit = fadeOut(tween(400))
                        ) {
                            Surface(modifier = Modifier.fillMaxSize()) {
                                NavHost(
                                    themeViewModel = themeViewModel,
                                    navigationManager = navigationManager
                                )
                            }
                        }

                        // Splash screen overlay
                        AnimatedVisibility(
                            visible = showSplash,
                            exit = fadeOut(tween(400))
                        ) {
                            // ModernSplashScreen(onSplashFinished = { showSplash = false })
                        }
                    }
                }

            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    AppTheme {
        Greeting("Android")
    }
}