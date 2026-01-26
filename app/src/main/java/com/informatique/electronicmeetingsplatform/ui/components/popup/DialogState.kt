package com.informatique.electronicmeetingsplatform.ui.components.popup

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Types of dialogs available
 */
enum class DialogType {
    SUCCESS,
    ERROR,
    WARNING,
    INFO,
    CONFIRMATION,
    DESTRUCTIVE // For delete/dangerous actions
}

/**
 * Data class representing a dialog
 */
sealed class DialogData {
    abstract val id: Long
    abstract val dismissible: Boolean

    /**
     * Alert dialog - simple informational dialog with single action
     */
    data class Success(
        val message: String,
        val type: DialogType = DialogType.SUCCESS,
        val confirmText: String = "موافق",
        val onConfirm: () -> Unit = {},
        override val dismissible: Boolean = true,
        override val id: Long = System.currentTimeMillis()
    ) : DialogData()

    /**
     * Alert dialog - simple informational dialog with single action
     */
    data class Alert(
        val message: String,
        val type: DialogType = DialogType.INFO,
        val title: String? = null,
        val confirmText: String = "موافق",
        val onConfirm: () -> Unit = {},
        override val dismissible: Boolean = true,
        override val id: Long = System.currentTimeMillis()
    ) : DialogData()

    /**
     * Confirmation dialog - requires user to confirm or cancel
     */
    data class Confirmation(
        val message: String,
        val type: DialogType = DialogType.WARNING,
        val title: String? = null,
        val confirmText: String = "تأكيد",
        val cancelText: String = "إلغاء",
        val onConfirm: () -> Unit,
        val onCancel: (() -> Unit)? = null,
        override val dismissible: Boolean = true,
        override val id: Long = System.currentTimeMillis()
    ) : DialogData()

    /**
     * Destructive confirmation - for dangerous actions like delete
     */
    data class Destructive(
        val message: String,
        val title: String? = null,
        val itemName: String? = null, // Optional item name to display
        val confirmText: String = "حذف",
        val cancelText: String = "إلغاء",
        val onConfirm: () -> Unit,
        val onCancel: (() -> Unit)? = null,
        val requiresDoubleConfirm: Boolean = false, // Requires typing or extra confirmation
        override val dismissible: Boolean = true,
        override val id: Long = System.currentTimeMillis()
    ) : DialogData()

    /**
     * Loading dialog - shows progress
     */
    data class Loading(
        val message: String = "جاري التحميل...",
        val progress: Float? = null, // null for indeterminate
        override val dismissible: Boolean = false,
        override val id: Long = System.currentTimeMillis()
    ) : DialogData()

    /**
     * Custom dialog with multiple actions
     */
    data class Custom(
        val message: String,
        val title: String? = null,
        val icon: ImageVector? = null,
        val iconTint: Color? = null,
        val actions: List<DialogAction>,
        override val dismissible: Boolean = true,
        override val id: Long = System.currentTimeMillis()
    ) : DialogData()
}

/**
 * Represents an action button in a dialog
 */
data class DialogAction(
    val label: String,
    val onClick: () -> Unit,
    val isPrimary: Boolean = false,
    val isDestructive: Boolean = false
)

/**
 * UI configuration for each dialog type
 */
data class DialogTypeConfig(
    val icon: ImageVector,
    val iconColor: Color,
    val iconBackgroundColor: Color,
    val confirmButtonColor: Color
)

/**
 * Returns the configuration for each dialog type
 */
@Composable
fun DialogType.getConfig(): DialogTypeConfig {
    return when (this) {
        DialogType.SUCCESS -> DialogTypeConfig(
            icon = Icons.Filled.CheckCircle,
            iconColor = Color(0xFF2E7D32), // Dark green
            iconBackgroundColor = Color(0xFFE8F5E9), // Light green background
            confirmButtonColor = Color(0xFF2E7D32)
        )
        DialogType.ERROR -> DialogTypeConfig(
            icon = Icons.Filled.Error,
            iconColor = Color(0xFFC62828), // Dark red
            iconBackgroundColor = Color(0xFFFFEBEE), // Light red background
            confirmButtonColor = Color(0xFFC62828)
        )
        DialogType.WARNING -> DialogTypeConfig(
            icon = Icons.Filled.Warning,
            iconColor = Color(0xFFF57C00), // Dark orange
            iconBackgroundColor = Color(0xFFFFF3E0), // Light orange background
            confirmButtonColor = Color(0xFFF57C00)
        )
        DialogType.INFO -> DialogTypeConfig(
            icon = Icons.Filled.Info,
            iconColor = Color(0xFF1976D2), // Dark blue
            iconBackgroundColor = Color(0xFFE3F2FD), // Light blue background
            confirmButtonColor = Color(0xFF1976D2)
        )
        DialogType.CONFIRMATION -> DialogTypeConfig(
            icon = Icons.Filled.Info,
            iconColor = Color(0xFF1976D2),
            iconBackgroundColor = Color(0xFFE3F2FD),
            confirmButtonColor = Color(0xFF1976D2)
        )
        DialogType.DESTRUCTIVE -> DialogTypeConfig(
            icon = Icons.Filled.Delete,
            iconColor = Color(0xFFD32F2F), // Bright red
            iconBackgroundColor = Color(0xFFFFCDD2), // Light red background
            confirmButtonColor = Color(0xFFD32F2F)
        )
    }
}
