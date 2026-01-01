package com.informatique.electronicmeetingsplatform.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import com.informatique.electronicmeetingsplatform.R
import com.informatique.electronicmeetingsplatform.ui.theme.AppFontFamily

private val MaroonColor = Color(0xFF7D1F3F)
private val BlueColor = Color(0xFF0D4261)
private val TextGray = Color(0xFFABABAB)

@Composable
fun SplashScreen(
    onSplashComplete: () -> Unit
) {
    // Animation states
    var startAnimation by remember { mutableStateOf(false) }

    // Logo animation
    val logoScale by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0.3f,
        animationSpec = spring(
            dampingRatio = Spring. DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "logoScale"
    )

    val logoAlpha by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(durationMillis = 800),
        label = "logoAlpha"
    )

    // Text animation with delay
    val textAlpha by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(
            durationMillis = 600,
            delayMillis = 400
        ),
        label = "textAlpha"
    )

    // Loading dots animation
    val infiniteTransition = rememberInfiniteTransition(label = "loadingDots")
    val dotScale1 by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(600),
            repeatMode = RepeatMode.Reverse,
            initialStartOffset = StartOffset(0)
        ),
        label = "dot1"
    )

    val dotScale2 by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(600),
            repeatMode = RepeatMode.Reverse,
            initialStartOffset = StartOffset(200)
        ),
        label = "dot2"
    )

    val dotScale3 by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(600),
            repeatMode = RepeatMode.Reverse,
            initialStartOffset = StartOffset(400)
        ),
        label = "dot3"
    )

    // Trigger animations on composition
    LaunchedEffect(Unit) {
        startAnimation = true
        delay(3000) // Splash duration
        onSplashComplete()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFF8F8F8),
                        Color(0xFFF0F0F0)
                    )
                )
            )
            .systemBarsPadding()
    ) {
        // Subtle diagonal decoration line (top-left)
        Box(
            modifier = Modifier
                .fillMaxWidth(0.3f)
                .fillMaxHeight(0.2f)
                .align(Alignment.TopStart)
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color. White.copy(alpha = 0.7f),
                            Color. Transparent
                        )
                    )
                )
        )

        // Main content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Logo with animation
            Image(
                painter = painterResource(id = R.drawable.ic_logo),
                contentDescription = "Qatar Council Emblem",
                colorFilter = ColorFilter.tint(MaroonColor),
                modifier = Modifier
                    .size(90.dp)
                    .scale(logoScale)
                    .alpha(logoAlpha),
                contentScale = ContentScale.Fit
            )

            Spacer(modifier = Modifier. height(10.dp))

            Column(
                verticalArrangement = Arrangement.spacedBy((-4).dp)
            ) {
                TightLineHeightText(
                    text = "الأمانــــة العامـــــة لمجلــــس الـــوزراء",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaroonColor,
                    letterSpacing = 0.5.sp,
                    modifier = Modifier
                        .alpha(textAlpha)
                        .fillMaxWidth(),
                )

                TightLineHeightText(
                    text = "Council of Ministers Secretariat General",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaroonColor,
                    letterSpacing = 0.3.sp,
                    modifier = Modifier
                        .alpha(textAlpha)
                        .fillMaxWidth(),
                )

                TightLineHeightText(
                    text = "دولــــة قطـــر • State of Qatar",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaroonColor,
                    letterSpacing = 0.2.sp,
                    modifier = Modifier
                        .alpha(textAlpha)
                        .fillMaxWidth(),
                )
            }

            Spacer(modifier = Modifier. height(28.dp))

            // App name in Arabic
            Text(
                text = "منصة الاجتماعات الإلكترونية",
                fontFamily = AppFontFamily,
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium,
                color = BlueColor,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .alpha(textAlpha)
                    .fillMaxWidth(),
                letterSpacing = 0.5.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Subtitle in Arabic
            Text(
                text = "الأمانة العامة لمجلس الوزراء",
                fontFamily = AppFontFamily,
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                color = TextGray,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .alpha(textAlpha)
                    .fillMaxWidth(),
                letterSpacing = 0.3.sp
            )
        }

        // Loading indicator dots at bottom
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 100.dp)
                .alpha(textAlpha),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            LoadingDot(scale = dotScale3, color = MaroonColor)
            LoadingDot(scale = dotScale2, color = MaroonColor.copy(alpha = 0.6f))
            LoadingDot(scale = dotScale1, color = MaroonColor. copy(alpha = 0.4f))
        }
    }
}

@Composable
fun TightLineHeightText(
    text: String,
    fontSize: TextUnit,
    fontWeight: FontWeight,
    color: Color,
    letterSpacing: TextUnit = 0.sp,
    modifier: Modifier = Modifier
) {
    Text(
        text = text,
        fontFamily = AppFontFamily,
        fontSize = fontSize,
        fontWeight = fontWeight,
        color = color,
        textAlign = TextAlign.Center,
        letterSpacing = letterSpacing,
        lineHeight = fontSize * 1.1f,
        modifier = modifier
    )
}

@Composable
private fun LoadingDot(
    scale: Float,
    color:  Color
) {
    Box(
        modifier = Modifier
            .size(10.dp)
            .scale(scale)
            .background(
                color = color,
                shape = androidx.compose.foundation.shape.CircleShape
            )
    )
}

// Extension for system bars padding
@Composable
private fun Modifier.systemBarsPadding(): Modifier {
    return this.statusBarsPadding()
}