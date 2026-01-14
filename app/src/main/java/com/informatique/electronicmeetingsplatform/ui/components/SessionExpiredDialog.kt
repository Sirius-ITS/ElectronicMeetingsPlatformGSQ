package com.informatique.electronicmeetingsplatform.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Session expired alert dialog
 * Shows when the user's session has expired
 */
@Composable
fun SessionExpiredDialog(
    onRenew: () -> Unit
) {
    AlertDialog(
        onDismissRequest = { /* Prevent dismissal by clicking outside */ },
        icon = {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = "Session Expired",
                tint = MaterialTheme.colorScheme.error
            )
        },
        title = {
            Text(
                text = "Session Expired",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Text(
                text = "Your session has expired. Please log in again to continue.",
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
        },
        confirmButton = {
            TextButton(
                onClick = onRenew
            ) {
                Text(
                    text = "Renew Session",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        },
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = (6f).dp
    )
}

