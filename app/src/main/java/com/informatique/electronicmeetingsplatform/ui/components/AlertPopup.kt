package com.informatique.electronicmeetingsplatform.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Modern alert popup that appears at the bottom of the screen
 * with smooth animations and interactive features
 */
@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AlertPopup(
    alertData: AlertPopupData,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val config = alertData.type.getConfig()
    val scope = rememberCoroutineScope()
    var isVisible by remember { mutableStateOf(false) }
    var progress by remember { mutableFloatStateOf(0f) }

    // Entrance animation
    LaunchedEffect(Unit) {
        isVisible = true

        // Progress bar animation
        val startTime = System.currentTimeMillis()
        while (progress < 1f) {
            delay(16) // ~60fps
            val elapsed = System.currentTimeMillis() - startTime
            progress = (elapsed.toFloat() / alertData.duration).coerceIn(0f, 1f)
        }

        // Auto dismiss after duration
        isVisible = false
        delay(300) // Wait for exit animation
        onDismiss()
    }

    AnimatedVisibility(
        visible = isVisible,
        enter = slideInVertically(
            initialOffsetY = { it },
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        ) + fadeIn(),
        exit = slideOutVertically(
            targetOffsetY = { it },
            animationSpec = tween(durationMillis = 300)
        ) + fadeOut(),
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(
                        elevation = 8.dp,
                        shape = RoundedCornerShape(16.dp),
                        ambientColor = Color.Black.copy(alpha = 0.3f),
                        spotColor = Color.Black.copy(alpha = 0.3f)
                    ),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = config.containerColor
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    // Main content
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Icon with pulse animation
                        val infiniteTransition = rememberInfiniteTransition(label = "pulse")
                        val scale by infiniteTransition.animateFloat(
                            initialValue = 1f,
                            targetValue = 1.1f,
                            animationSpec = infiniteRepeatable(
                                animation = tween(1000, easing = FastOutSlowInEasing),
                                repeatMode = RepeatMode.Reverse
                            ),
                            label = "scale"
                        )

                        Icon(
                            imageVector = config.icon,
                            contentDescription = null,
                            tint = config.iconColor,
                            modifier = Modifier
                                .size(32.dp)
                                .scale(scale)
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        // Text content
                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            alertData.title?.let { title ->
                                Text(
                                    text = title,
                                    color = config.contentColor,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                            }

                            Text(
                                text = alertData.message,
                                color = config.contentColor.copy(alpha = 0.9f),
                                fontSize = 14.sp,
                                lineHeight = 18.sp,
                                maxLines = 3,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        // Action button or close button
                        if (alertData.actionLabel != null && alertData.onAction != null) {
                            Spacer(modifier = Modifier.width(8.dp))
                            TextButton(
                                onClick = {
                                    alertData.onAction.invoke()
                                    scope.launch {
                                        isVisible = false
                                        delay(300)
                                        onDismiss()
                                    }
                                },
                                colors = ButtonDefaults.textButtonColors(
                                    contentColor = config.iconColor
                                )
                            ) {
                                Text(
                                    text = alertData.actionLabel,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            }
                        } else if (alertData.dismissible) {
                            Spacer(modifier = Modifier.width(8.dp))
                            IconButton(
                                onClick = {
                                    scope.launch {
                                        isVisible = false
                                        delay(300)
                                        onDismiss()
                                    }
                                },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Close,
                                    contentDescription = "Dismiss",
                                    tint = config.contentColor.copy(alpha = 0.7f),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }

                    // Progress bar
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(3.dp)
                            .background(config.containerColor.copy(alpha = 0.3f))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .fillMaxWidth(1f - progress)
                                .background(
                                    Brush.horizontalGradient(
                                        colors = listOf(
                                            config.iconColor,
                                            config.iconColor.copy(alpha = 0.7f)
                                        )
                                    )
                                )
                        )
                    }
                }
            }
        }
    }
}

/**
 * Alert popup host that manages the display of alerts
 * Place this at the root of your screen/app
 */
@Composable
fun AlertPopupHost(
    alertPopupManager: AlertPopupManager,
    modifier: Modifier = Modifier
) {
    val currentAlert = remember { mutableStateOf<AlertPopupData?>(null) }
    val alertQueue = remember { mutableStateListOf<AlertPopupData>() }

    // Collect alerts from the manager
    LaunchedEffect(Unit) {
        alertPopupManager.alertFlow.collect { alert ->
            alertQueue.add(alert)
        }
    }

    // Show next alert when current one is dismissed
    LaunchedEffect(currentAlert.value, alertQueue.size) {
        if (currentAlert.value == null && alertQueue.isNotEmpty()) {
            currentAlert.value = alertQueue.removeFirst()
        }
    }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomCenter
    ) {
        currentAlert.value?.let { alert ->
            AlertPopup(
                alertData = alert,
                onDismiss = {
                    currentAlert.value = null
                }
            )
        }
    }
}

/**
 * Simplified alert popup host using remember
 * For screens where you don't need DI
 */
@Composable
fun rememberAlertPopupState(): AlertPopupState {
    return remember { AlertPopupState() }
}

/**
 * State holder for alert popups that doesn't require DI
 */
class AlertPopupState {
    private var currentAlert by mutableStateOf<AlertPopupData?>(null)
    private val alertQueue = mutableStateListOf<AlertPopupData>()

    fun showSuccess(
        message: String,
        title: String? = "Success",
        actionLabel: String? = null,
        onAction: (() -> Unit)? = null,
        duration: Long = 4000L
    ) {
        addAlert(
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

    fun showError(
        message: String,
        title: String? = "Error",
        actionLabel: String? = null,
        onAction: (() -> Unit)? = null,
        duration: Long = 5000L
    ) {
        addAlert(
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

    fun showWarning(
        message: String,
        title: String? = "Warning",
        actionLabel: String? = null,
        onAction: (() -> Unit)? = null,
        duration: Long = 4500L
    ) {
        addAlert(
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

    fun showInfo(
        message: String,
        title: String? = "Info",
        actionLabel: String? = null,
        onAction: (() -> Unit)? = null,
        duration: Long = 3500L
    ) {
        addAlert(
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

    fun showAlert(alertData: AlertPopupData) {
        addAlert(alertData)
    }

    private fun addAlert(alert: AlertPopupData) {
        alertQueue.add(alert)
        if (currentAlert == null) {
            showNextAlert()
        }
    }

    private fun showNextAlert() {
        if (alertQueue.isNotEmpty()) {
            currentAlert = alertQueue.removeFirst()
        }
    }

    @Composable
    fun AlertHost(modifier: Modifier = Modifier) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.BottomCenter
        ) {
            currentAlert?.let { alert ->
                AlertPopup(
                    alertData = alert,
                    onDismiss = {
                        currentAlert = null
                        showNextAlert()
                    }
                )
            }
        }
    }
}

