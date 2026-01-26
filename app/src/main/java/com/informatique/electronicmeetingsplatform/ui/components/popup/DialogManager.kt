package com.informatique.electronicmeetingsplatform.ui.components.popup

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Singleton manager for handling dialogs throughout the app
 * Inject this into ViewModels or use it directly to show dialogs
 */
@Singleton
class DialogManager @Inject constructor() {

    private val _currentDialog = MutableStateFlow<DialogData?>(null)
    val currentDialog: StateFlow<DialogData?> = _currentDialog.asStateFlow()

    /**
     * Show a simple alert dialog with one action
     */
    fun showAlert(
        message: String,
        type: DialogType = DialogType.INFO,
        title: String? = null,
        confirmText: String = "موافق",
        onConfirm: () -> Unit = {}
    ) {
        _currentDialog.value = DialogData.Alert(
            message = message,
            type = type,
            title = title,
            confirmText = confirmText,
            onConfirm = onConfirm
        )
    }

    /**
     * Show success alert
     */
    fun showSuccess(
        message: String,
        title: String? = "نجح",
        confirmText: String = "موافق",
        onConfirm: () -> Unit = {}
    ) {
        showAlert(
            message = message,
            type = DialogType.SUCCESS,
            title = title,
            confirmText = confirmText,
            onConfirm = onConfirm
        )
    }

    /**
     * Show error alert
     */
    fun showError(
        message: String,
        title: String? = "خطأ",
        confirmText: String = "موافق",
        onConfirm: () -> Unit = {}
    ) {
        showAlert(
            message = message,
            type = DialogType.ERROR,
            title = title,
            confirmText = confirmText,
            onConfirm = onConfirm
        )
    }

    /**
     * Show info alert
     */
    fun showInfo(
        message: String,
        title: String? = "معلومة",
        confirmText: String = "موافق",
        onConfirm: () -> Unit = {}
    ) {
        showAlert(
            message = message,
            type = DialogType.INFO,
            title = title,
            confirmText = confirmText,
            onConfirm = onConfirm
        )
    }

    /**
     * Show confirmation dialog with confirm/cancel actions
     */
    fun showSuccess(
        message: String,
        confirmText: String = "موافق",
        onConfirm: () -> Unit
    ) {
        _currentDialog.value = DialogData.Success(
            message = message,
            confirmText = confirmText,
            onConfirm = onConfirm
        )
    }

    /**
     * Show confirmation dialog with confirm/cancel actions
     */
    fun showConfirmation(
        message: String,
        title: String? = null,
        type: DialogType = DialogType.WARNING,
        confirmText: String = "تأكيد",
        cancelText: String = "إلغاء",
        onConfirm: () -> Unit,
        onCancel: (() -> Unit)? = null
    ) {
        _currentDialog.value = DialogData.Confirmation(
            message = message,
            type = type,
            title = title,
            confirmText = confirmText,
            cancelText = cancelText,
            onConfirm = onConfirm,
            onCancel = onCancel
        )
    }

    /**
     * Show warning confirmation dialog
     */
    fun showWarningConfirmation(
        message: String,
        title: String? = "تحذير",
        confirmText: String = "متابعة",
        cancelText: String = "إلغاء",
        onConfirm: () -> Unit,
        onCancel: (() -> Unit)? = null
    ) {
        showConfirmation(
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
     * Show destructive confirmation dialog for delete operations
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
        _currentDialog.value = DialogData.Destructive(
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
     * Show loading dialog
     */
    fun showLoading(
        message: String = "جاري التحميل...",
        progress: Float? = null
    ) {
        _currentDialog.value = DialogData.Loading(
            message = message,
            progress = progress
        )
    }

    /**
     * Show custom dialog with multiple actions
     */
    fun showCustomDialog(
        message: String,
        title: String? = null,
        icon: ImageVector? = null,
        iconTint: Color? = null,
        actions: List<DialogAction>,
        dismissible: Boolean = true
    ) {
        _currentDialog.value = DialogData.Custom(
            message = message,
            title = title,
            icon = icon,
            iconTint = iconTint,
            actions = actions,
            dismissible = dismissible
        )
    }

    /**
     * Dismiss the current dialog
     */
    fun dismissDialog() {
        _currentDialog.value = null
    }
}
