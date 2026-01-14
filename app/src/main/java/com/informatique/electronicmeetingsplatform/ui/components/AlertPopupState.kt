package com.informatique.electronicmeetingsplatform.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Types of alert popups with predefined styles
 */
enum class AlertType {
    SUCCESS,
    ERROR,
    WARNING,
    INFO
}

/**
 * Data class representing an alert popup
 */
data class AlertPopupData(
    val message: String,
    val type: AlertType = AlertType.INFO,
    val title: String? = null,
    val actionLabel: String? = null,
    val onAction: (() -> Unit)? = null,
    val duration: Long = 4000L, // Duration in milliseconds
    val dismissible: Boolean = true,
    val id: Long = System.currentTimeMillis()
)

/**
 * UI configuration for each alert type
 */
data class AlertTypeConfig(
    val icon: ImageVector,
    val containerColor: Color,
    val contentColor: Color,
    val iconColor: Color
)

/**
 * Returns the configuration for each alert type
 */
@Composable
fun AlertType.getConfig(): AlertTypeConfig {
    return when (this) {
        AlertType.SUCCESS -> AlertTypeConfig(
            icon = Icons.Filled.CheckCircle,
            containerColor = Color(0xFF2E7D32), // Dark green
            contentColor = Color.White,
            iconColor = Color(0xFF81C784) // Light green
        )
        AlertType.ERROR -> AlertTypeConfig(
            icon = Icons.Filled.Error,
            containerColor = Color(0xFFC62828), // Dark red
            contentColor = Color.White,
            iconColor = Color(0xFFEF5350) // Light red
        )
        AlertType.WARNING -> AlertTypeConfig(
            icon = Icons.Filled.Warning,
            containerColor = Color(0xFFF57C00), // Dark orange
            contentColor = Color.White,
            iconColor = Color(0xFFFFB74D) // Light orange
        )
        AlertType.INFO -> AlertTypeConfig(
            icon = Icons.Filled.Info,
            containerColor = Color(0xFF1976D2), // Dark blue
            contentColor = Color.White,
            iconColor = Color(0xFF64B5F6) // Light blue
        )
    }
}

