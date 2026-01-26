package com.informatique.electronicmeetingsplatform.ui.components.popup

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Singleton manager for handling alert popups and dialogs throughout the app
 * Inject this into ViewModels or use it directly to show alerts and dialogs
 *
 * NOTE: This class now integrates with DialogManager for confirmation dialogs
 * - Use showSuccess/showError/showInfo for temporary notifications (toast-style)
 * - Use showConfirmation/showDeleteConfirmation for dialogs that require user confirmation
 */
@Singleton
class AlertPopupManager @Inject constructor(
    private val dialogManager: DialogManager
) {

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

    // ============================================================================
    // Confirmation Dialogs (Blocking UI, requires user confirmation)
    // ============================================================================

    fun showSuccess(
        message: String,
        confirmText: String,
        onConfirm: () -> Unit
    ) {
        dialogManager.showSuccess(
            message = message,
            confirmText = confirmText,
            onConfirm = onConfirm
        )
    }

    /**
     * Show a confirmation dialog that requires user action
     * This is a DIALOG (blocking) not a toast notification
     */
    fun showConfirmation(
        message: String,
        title: String? = "تأكيد",
        confirmText: String = "تأكيد",
        cancelText: String = "إلغاء",
        onConfirm: () -> Unit,
        onCancel: (() -> Unit)? = null
    ) {
        dialogManager.showConfirmation(
            message = message,
            title = title,
            type = DialogType.WARNING,
            confirmText = confirmText,
            cancelText = cancelText,
            onConfirm = onConfirm,
            onCancel = onCancel
        )
    }

    /**
     * Show a delete confirmation dialog (destructive action)
     * This is a DIALOG (blocking) not a toast notification
     */
    fun showDeleteConfirmation(
        message: String,
        title: String? = "تأكيد الحذف",
        itemName: String? = null,
        confirmText: String = "حذف",
        cancelText: String = "إلغاء",
        onConfirm: () -> Unit,
        onCancel: (() -> Unit)? = null
    ) {
        dialogManager.showDeleteConfirmation(
            message = message,
            title = title,
            itemName = itemName,
            confirmText = confirmText,
            cancelText = cancelText,
            onConfirm = onConfirm,
            onCancel = onCancel
        )
    }

    /**
     * Show a warning confirmation dialog
     */
    fun showWarningDialog(
        message: String,
        title: String? = "تحذير",
        confirmText: String = "متابعة",
        cancelText: String = "إلغاء",
        onConfirm: () -> Unit,
        onCancel: (() -> Unit)? = null
    ) {
        dialogManager.showWarningConfirmation(
            message = message,
            title = title,
            confirmText = confirmText,
            cancelText = cancelText,
            onConfirm = onConfirm,
            onCancel = onCancel
        )
    }

    /**
     * Access to the underlying DialogManager for advanced dialog operations
     */
    fun getDialogManager(): DialogManager = dialogManager
}
