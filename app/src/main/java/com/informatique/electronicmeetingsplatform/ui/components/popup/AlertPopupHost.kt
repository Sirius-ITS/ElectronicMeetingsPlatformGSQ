package com.informatique.electronicmeetingsplatform.ui.components.popup

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
            animationSpec = tween(durationMillis = 250)
        ) + fadeOut()
    ) {
        Card(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .shadow(
                    elevation = 8.dp,
                    shape = RoundedCornerShape(16.dp)
                ),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = config.containerColor
            )
        ) {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                // Progress bar
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(3.dp),
                    color = config.contentColor.copy(alpha = 0.5f),
                    trackColor = Color.Transparent,
                )

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

                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .scale(scale)
                            .background(
                                color = config.iconColor.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(12.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = config.icon,
                            contentDescription = null,
                            tint = config.iconColor,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    // Content
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        // Title
                        alertData.title?.let { title ->
                            Text(
                                text = title,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = config.contentColor,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        // Message
                        Text(
                            text = alertData.message,
                            style = MaterialTheme.typography.bodyMedium,
                            color = config.contentColor.copy(alpha = 0.9f),
                            maxLines = 3,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    // Action button or dismiss button
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
                                contentColor = config.contentColor
                            )
                        ) {
                            Text(
                                text = alertData.actionLabel,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp
                            )
                        }
                    } else if (alertData.dismissible) {
                        Spacer(modifier = Modifier.width(4.dp))
                        IconButton(
                            onClick = {
                                scope.launch {
                                    isVisible = false
                                    delay(300)
                                    onDismiss()
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Dismiss",
                                tint = config.contentColor.copy(alpha = 0.7f),
                                modifier = Modifier.size(20.dp)
                            )
                        }
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
