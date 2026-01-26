package com.informatique.electronicmeetingsplatform.ui.components.popup

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

/**
 * Modern dialog host that manages dialog display
 * Place this at the root of your screen/app
 */
@Composable
fun DialogHost(
    dialogManager: DialogManager,
    modifier: Modifier = Modifier
) {
    val currentDialog by dialogManager.currentDialog.collectAsState()

    currentDialog?.let { dialog ->
        when (dialog) {
            is DialogData.Alert -> ModernAlertDialog(
                data = dialog,
                onDismiss = { dialogManager.dismissDialog() }
            )
            is DialogData.Confirmation -> ModernConfirmationDialog(
                data = dialog,
                onDismiss = { dialogManager.dismissDialog() }
            )
            is DialogData.Destructive -> ModernDestructiveDialog(
                data = dialog,
                onDismiss = { dialogManager.dismissDialog() }
            )
            is DialogData.Loading -> ModernLoadingDialog(
                data = dialog,
                onDismiss = { if (dialog.dismissible) dialogManager.dismissDialog() }
            )
            is DialogData.Custom -> ModernCustomDialog(
                data = dialog,
                onDismiss = { dialogManager.dismissDialog() }
            )
        }
    }
}

/**
 * Modern alert dialog with animated icon
 */
@Composable
fun ModernAlertDialog(
    data: DialogData.Alert,
    onDismiss: () -> Unit
) {
    val config = data.type.getConfig()
    var showContent by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        showContent = true
    }

    Dialog(
        onDismissRequest = { if (data.dismissible) onDismiss() },
        properties = DialogProperties(
            dismissOnBackPress = data.dismissible,
            dismissOnClickOutside = data.dismissible,
            usePlatformDefaultWidth = false
        )
    ) {
        AnimatedVisibility(
            visible = showContent,
            enter = scaleIn(
                initialScale = 0.8f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            ) + fadeIn(),
            exit = scaleOut(targetScale = 0.8f) + fadeOut()
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .padding(16.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Animated icon with pulse effect
                    AnimatedDialogIcon(
                        icon = config.icon,
                        iconColor = config.iconColor,
                        backgroundColor = config.iconBackgroundColor
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Title
                    data.title?.let { title ->
                        Text(
                            text = title,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    // Message
                    Text(
                        text = data.message,
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                        lineHeight = 24.sp
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Confirm button
                    Button(
                        onClick = {
                            data.onConfirm()
                            onDismiss()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = config.confirmButtonColor
                        )
                    ) {
                        Text(
                            text = data.confirmText,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}

/**
 * Modern confirmation dialog with confirm/cancel actions
 */
@Composable
fun ModernConfirmationDialog(
    data: DialogData.Confirmation,
    onDismiss: () -> Unit
) {
    val config = data.type.getConfig()
    var showContent by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        showContent = true
    }

    Dialog(
        onDismissRequest = {
            if (data.dismissible) {
                data.onCancel?.invoke()
                onDismiss()
            }
        },
        properties = DialogProperties(
            dismissOnBackPress = data.dismissible,
            dismissOnClickOutside = data.dismissible,
            usePlatformDefaultWidth = false
        )
    ) {
        AnimatedVisibility(
            visible = showContent,
            enter = scaleIn(
                initialScale = 0.8f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            ) + fadeIn(),
            exit = scaleOut(targetScale = 0.8f) + fadeOut()
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .padding(16.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Animated icon
                    AnimatedDialogIcon(
                        icon = config.icon,
                        iconColor = config.iconColor,
                        backgroundColor = config.iconBackgroundColor
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Title
                    data.title?.let { title ->
                        Text(
                            text = title,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    // Message
                    Text(
                        text = data.message,
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                        lineHeight = 24.sp
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Action buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Cancel button
                        OutlinedButton(
                            onClick = {
                                data.onCancel?.invoke()
                                onDismiss()
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = MaterialTheme.colorScheme.onSurface
                            )
                        ) {
                            Text(
                                text = data.cancelText,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        // Confirm button
                        Button(
                            onClick = {
                                data.onConfirm()
                                onDismiss()
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = config.confirmButtonColor
                            )
                        ) {
                            Text(
                                text = data.confirmText,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Modern destructive dialog for dangerous actions
 */
@Composable
fun ModernDestructiveDialog(
    data: DialogData.Destructive,
    onDismiss: () -> Unit
) {
    val config = DialogType.DESTRUCTIVE.getConfig()
    var showContent by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        showContent = true
    }

    Dialog(
        onDismissRequest = {
            if (data.dismissible) {
                data.onCancel?.invoke()
                onDismiss()
            }
        },
        properties = DialogProperties(
            dismissOnBackPress = data.dismissible,
            dismissOnClickOutside = data.dismissible,
            usePlatformDefaultWidth = false
        )
    ) {
        AnimatedVisibility(
            visible = showContent,
            enter = scaleIn(
                initialScale = 0.8f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            ) + fadeIn(),
            exit = scaleOut(targetScale = 0.8f) + fadeOut()
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .padding(16.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Animated icon with warning shake
                    AnimatedDialogIcon(
                        icon = config.icon,
                        iconColor = config.iconColor,
                        backgroundColor = config.iconBackgroundColor,
                        shake = true
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Title
                    data.title?.let { title ->
                        Text(
                            text = title,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    // Item name in a highlight box
                    data.itemName?.let { itemName ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)
                            )
                        ) {
                            Text(
                                text = itemName,
                                modifier = Modifier.padding(12.dp),
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.SemiBold,
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    // Message
                    Text(
                        text = data.message,
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                        lineHeight = 24.sp
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Action buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Cancel button
                        Button(
                            onClick = {
                                data.onCancel?.invoke()
                                onDismiss()
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                contentColor = MaterialTheme.colorScheme.onSurface
                            )
                        ) {
                            Text(
                                text = data.cancelText,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        // Delete button
                        Button(
                            onClick = {
                                data.onConfirm()
                                onDismiss()
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = config.confirmButtonColor,
                                contentColor = Color.White
                            )
                        ) {
                            Text(
                                text = data.confirmText,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Modern loading dialog with progress indicator
 */
@Composable
fun ModernLoadingDialog(
    data: DialogData.Loading,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = { if (data.dismissible) onDismiss() },
        properties = DialogProperties(
            dismissOnBackPress = data.dismissible,
            dismissOnClickOutside = data.dismissible,
            usePlatformDefaultWidth = false
        )
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .padding(16.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (data.progress != null) {
                    // Determinate progress
                    CircularProgressIndicator(
                        progress = { data.progress },
                        modifier = Modifier.size(56.dp),
                        strokeWidth = 4.dp,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant,
                    )
                } else {
                    // Indeterminate progress
                    CircularProgressIndicator(
                        modifier = Modifier.size(56.dp),
                        strokeWidth = 4.dp,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant,
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = data.message,
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                )
            }
        }
    }
}

/**
 * Modern custom dialog with multiple actions
 */
@Composable
fun ModernCustomDialog(
    data: DialogData.Custom,
    onDismiss: () -> Unit
) {
    var showContent by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        showContent = true
    }

    Dialog(
        onDismissRequest = { if (data.dismissible) onDismiss() },
        properties = DialogProperties(
            dismissOnBackPress = data.dismissible,
            dismissOnClickOutside = data.dismissible,
            usePlatformDefaultWidth = false
        )
    ) {
        AnimatedVisibility(
            visible = showContent,
            enter = scaleIn(
                initialScale = 0.8f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            ) + fadeIn(),
            exit = scaleOut(targetScale = 0.8f) + fadeOut()
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .padding(16.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Optional icon
                    data.icon?.let { icon ->
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .clip(CircleShape)
                                .background(
                                    data.iconTint?.copy(alpha = 0.1f)
                                        ?: MaterialTheme.colorScheme.primaryContainer
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = icon,
                                contentDescription = null,
                                modifier = Modifier.size(40.dp),
                                tint = data.iconTint ?: MaterialTheme.colorScheme.primary
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    // Title
                    data.title?.let { title ->
                        Text(
                            text = title,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    // Message
                    Text(
                        text = data.message,
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                        lineHeight = 24.sp
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Action buttons
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        data.actions.forEach { action ->
                            if (action.isPrimary || action.isDestructive) {
                                Button(
                                    onClick = {
                                        action.onClick()
                                        onDismiss()
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(50.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (action.isDestructive)
                                            MaterialTheme.colorScheme.error
                                        else
                                            MaterialTheme.colorScheme.primary
                                    )
                                ) {
                                    Text(
                                        text = action.label,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            } else {
                                OutlinedButton(
                                    onClick = {
                                        action.onClick()
                                        onDismiss()
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(50.dp),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text(
                                        text = action.label,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Animated icon component with pulse effect
 */
@Composable
private fun AnimatedDialogIcon(
    icon: ImageVector,
    iconColor: Color,
    backgroundColor: Color,
    shake: Boolean = false
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    val rotation by infiniteTransition.animateFloat(
        initialValue = if (shake) -5f else 0f,
        targetValue = if (shake) 5f else 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "rotation"
    )

    Box(
        modifier = Modifier
            .size(80.dp)
            .scale(scale)
            .clip(CircleShape)
            .background(backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(40.dp),
            tint = iconColor
        )
    }
}

/**
 * Simplified dialog host using remember
 * For screens where you don't need DI
 */
@Composable
fun rememberDialogState(): DialogState {
    return remember { DialogState() }
}

/**
 * State holder for dialogs that doesn't require DI
 */
class DialogState {
    private var currentDialog by mutableStateOf<DialogData?>(null)

    fun showAlert(
        message: String,
        type: DialogType = DialogType.INFO,
        title: String? = null,
        confirmText: String = "موافق",
        onConfirm: () -> Unit = {}
    ) {
        currentDialog = DialogData.Alert(
            message = message,
            type = type,
            title = title,
            confirmText = confirmText,
            onConfirm = onConfirm
        )
    }

    fun showConfirmation(
        message: String,
        title: String? = null,
        type: DialogType = DialogType.WARNING,
        confirmText: String = "تأكيد",
        cancelText: String = "إلغاء",
        onConfirm: () -> Unit,
        onCancel: (() -> Unit)? = null
    ) {
        currentDialog = DialogData.Confirmation(
            message = message,
            type = type,
            title = title,
            confirmText = confirmText,
            cancelText = cancelText,
            onConfirm = onConfirm,
            onCancel = onCancel
        )
    }

    fun showDestructive(
        message: String,
        title: String? = null,
        itemName: String? = null,
        confirmText: String = "حذف",
        cancelText: String = "إلغاء",
        onConfirm: () -> Unit,
        onCancel: (() -> Unit)? = null
    ) {
        currentDialog = DialogData.Destructive(
            message = message,
            title = title,
            itemName = itemName,
            confirmText = confirmText,
            cancelText = cancelText,
            onConfirm = onConfirm,
            onCancel = onCancel
        )
    }

    fun showLoading(
        message: String = "جاري التحميل...",
        progress: Float? = null
    ) {
        currentDialog = DialogData.Loading(
            message = message,
            progress = progress
        )
    }

    fun dismiss() {
        currentDialog = null
    }

    @Composable
    fun DialogHost(modifier: Modifier = Modifier) {
        currentDialog?.let { dialog ->
            when (dialog) {
                is DialogData.Alert -> ModernAlertDialog(
                    data = dialog,
                    onDismiss = { dismiss() }
                )
                is DialogData.Confirmation -> ModernConfirmationDialog(
                    data = dialog,
                    onDismiss = { dismiss() }
                )
                is DialogData.Destructive -> ModernDestructiveDialog(
                    data = dialog,
                    onDismiss = { dismiss() }
                )
                is DialogData.Loading -> ModernLoadingDialog(
                    data = dialog,
                    onDismiss = { dismiss() }
                )
                is DialogData.Custom -> ModernCustomDialog(
                    data = dialog,
                    onDismiss = { dismiss() }
                )
            }
        }
    }
}
