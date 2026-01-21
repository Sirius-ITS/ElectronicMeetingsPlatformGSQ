package com.informatique.electronicmeetingsplatform.ui

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.hilt.navigation.compose.hiltViewModel
import com.informatique.electronicmeetingsplatform.common.util.LocalAppLocale
import com.informatique.electronicmeetingsplatform.data.session.SessionManager
import com.informatique.electronicmeetingsplatform.navigation.NavHost
import com.informatique.electronicmeetingsplatform.ui.base.BaseActivity
import com.informatique.electronicmeetingsplatform.ui.components.popup.AlertPopupHost
import com.informatique.electronicmeetingsplatform.ui.components.popup.AlertPopupManager
import com.informatique.electronicmeetingsplatform.ui.components.popup.DialogHost
import com.informatique.electronicmeetingsplatform.ui.components.popup.SessionExpiredDialog
import com.informatique.electronicmeetingsplatform.ui.screens.SplashScreen
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
    lateinit var sessionManager: SessionManager

    @Inject
    lateinit var alertPopupManager: AlertPopupManager

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

            // Monitor session expiration globally
            val isSessionExpired by sessionManager.sessionExpired.collectAsState()
            var showSessionDialog by remember { mutableStateOf(false) }
            var navHostReference by remember { mutableStateOf<androidx.navigation.NavHostController?>(null) }

            // Show dialog when session expires
            LaunchedEffect(isSessionExpired) {
                if (isSessionExpired) {
                    showSessionDialog = true
                }
            }

            CompositionLocalProvider(
                LocalLayoutDirection provides
                        if (lang == "ar") LayoutDirection.Rtl else LayoutDirection.Ltr,
                LocalAppLocale provides currentLocale
            ) {

                AppTheme(themeOption = themeOption) {
                    var showSplash by remember { mutableStateOf(true) }

                    Surface(
                        modifier = Modifier.fillMaxSize()
                            .statusBarsPadding()
                            .navigationBarsPadding(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        Box(modifier = Modifier.fillMaxSize()) {
                            if (showSplash) {
                                SplashScreen(
                                    onSplashComplete = {
                                        showSplash = false
                                    }
                                )
                            } else {
                                NavHost(
                                    themeViewModel = themeViewModel,
                                    onNavControllerReady = { navController ->
                                        navHostReference = navController
                                    }
                                )
                            }

                            // Show session expired dialog overlay
                            if (showSessionDialog) {
                                SessionExpiredDialog(
                                    onRenew = {
                                        showSessionDialog = false
                                        sessionManager.resetSessionExpired()
                                        // Navigate to login screen
                                        navHostReference?.navigate("login") {
                                            popUpTo(0) { inclusive = true }
                                        }
                                    }
                                )
                            }

                            // Global dialog host for confirmation dialogs
                            DialogHost(dialogManager = alertPopupManager.getDialogManager())

                            // Global alert popup host for toast notifications
                            AlertPopupHost(alertPopupManager = alertPopupManager)
                        }
                    }
                }

            }
        }
    }
}