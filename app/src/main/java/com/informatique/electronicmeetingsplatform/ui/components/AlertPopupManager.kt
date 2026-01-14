package com.informatique.electronicmeetingsplatform.ui.components

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Singleton manager for handling alert popups throughout the app
 * Inject this into ViewModels or use it directly to show alerts
 */
@Singleton
class AlertPopupManager @Inject constructor() {

    private val _alertChannel = Channel<AlertPopupData>(Channel.BUFFERED)
    val alertFlow = _alertChannel.receiveAsFlow()

    /**
     * Show a success alert
     */
    suspend fun showSuccess(
        message: String,
        title: String? = "Success",
        actionLabel: String? = null,
        onAction: (() -> Unit)? = null,
        duration: Long = 4000L
    ) {
        _alertChannel.send(
            AlertPopupData(
                message = message,
                type = AlertType.SUCCESS,
                title = title,
                actionLabel = actionLabel,
                onAction = onAction,
                duration = duration
            )
        )
    }

    /**
     * Show an error alert
     */
    suspend fun showError(
        message: String,
        title: String? = "Error",
        actionLabel: String? = null,
        onAction: (() -> Unit)? = null,
        duration: Long = 5000L
    ) {
        _alertChannel.send(
            AlertPopupData(
                message = message,
                type = AlertType.ERROR,
                title = title,
                actionLabel = actionLabel,
                onAction = onAction,
                duration = duration
            )
        )
    }

    /**
     * Show a warning alert
     */
    suspend fun showWarning(
        message: String,
        title: String? = "Warning",
        actionLabel: String? = null,
        onAction: (() -> Unit)? = null,
        duration: Long = 4500L
    ) {
        _alertChannel.send(
            AlertPopupData(
                message = message,
                type = AlertType.WARNING,
                title = title,
                actionLabel = actionLabel,
                onAction = onAction,
                duration = duration
            )
        )
    }

    /**
     * Show an info alert
     */
    suspend fun showInfo(
        message: String,
        title: String? = "Info",
        actionLabel: String? = null,
        onAction: (() -> Unit)? = null,
        duration: Long = 3500L
    ) {
        _alertChannel.send(
            AlertPopupData(
                message = message,
                type = AlertType.INFO,
                title = title,
                actionLabel = actionLabel,
                onAction = onAction,
                duration = duration
            )
        )
    }

    /**
     * Show a custom alert with full control
     */
    suspend fun showAlert(alertData: AlertPopupData) {
        _alertChannel.send(alertData)
    }
}

