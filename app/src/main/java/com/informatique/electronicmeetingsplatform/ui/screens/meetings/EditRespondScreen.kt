package com.informatique.electronicmeetingsplatform.ui.screens.meetings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.informatique.electronicmeetingsplatform.ui.theme.LocalExtraColors
import com.informatique.electronicmeetingsplatform.ui.viewModel.MeetingsViewModel
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.informatique.electronicmeetingsplatform.ui.screens.MeetingCard
import com.informatique.electronicmeetingsplatform.ui.theme.AppTheme

data class ResponseOption(
    val id: Int,
    val text: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditRespondScreen(viewModel: MeetingsViewModel, navController: NavController, meetingId: String) {

    val extraColors = LocalExtraColors.current

    var confirmAttendance by remember { mutableStateOf(true) }
    var apologize by remember { mutableStateOf(false) }
    var selectedApologyReason by remember { mutableStateOf<Int?>(1) }

    val apologyReasons = listOf(
        ResponseOption(1, "?????"),
        ResponseOption(2, "???? ??????"),
        ResponseOption(3, "?? ????"),
        ResponseOption(4, "????")
    )

    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(extraColors.background)
                .padding(horizontal = 16.dp)
        ) {

            // Title bar
            Row(
                modifier = Modifier.padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ){
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Back",
                        tint = extraColors.maroonColor
                    )
                }

                Text(
                    text = "تعديل الرد",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = extraColors.blueColor
                )

            }

            Spacer(modifier = Modifier.height(8.dp))

            MeetingCard {
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = "الاجتماع",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = extraColors.textGray,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Text(
                        text = "اختبار هاتف 3",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = extraColors.blueColor,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Confirm Attendance Button
            ResponseButton(
                text = "تأكيد الحضور",
                isSelected = confirmAttendance,
                onClick = {
                    confirmAttendance = !confirmAttendance
                    if (confirmAttendance) apologize = false
                },
                backgroundColor = if (confirmAttendance) extraColors.success else Color.White,
                textColor = if (confirmAttendance) Color.White else extraColors.blueColor,
                showCheckmark = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Apologize Button
            ResponseButton(
                text = "اعتذار",
                isSelected = apologize,
                onClick = {
                    apologize = !apologize
                    if (apologize) confirmAttendance = false
                },
                backgroundColor = if (apologize) extraColors.maroonColor else Color.White,
                textColor = if (apologize) Color.White else extraColors.blueColor,
                showCloseIcon = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Apology Reasons Section
            AnimatedVisibility(
                visible = confirmAttendance,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Box(
                    modifier = Modifier.padding(top = 16.dp)
                        .background(
                            color = extraColors.success.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .border(1.dp, extraColors.success, RoundedCornerShape(12.dp)),
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(25.dp)
                                .background(extraColors.success, RoundedCornerShape(50))
                                .padding(4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Column {
                            Text(
                                text = "تأكيد الحضور",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                color = extraColors.blueColor,
                                modifier = Modifier.fillMaxWidth()
                            )

                            Text(
                                text = "سيتم إرسال اشعار للمنظم بتأكيد حضورك",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = extraColors.textGray,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }

            // Apology Reasons Section
            AnimatedVisibility(
                visible = apologize,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column(
                    modifier = Modifier.padding(top = 16.dp)
                ) {
                    // Apology Reason Label
                    Text(
                        text = "سبب الاعتذار",
                        fontSize = 16.sp,
                        color = extraColors.blueColor,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 8.dp)
                    )

                    // Apology Reason Options
                    apologyReasons.forEach { option ->
                        ApologyReasonOption(
                            text = option.text,
                            isSelected = selectedApologyReason == option.id,
                            onClick = {
                                selectedApologyReason = if (selectedApologyReason == option.id) {
                                    null
                                } else {
                                    option.id
                                }
                            }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.height(24.dp))

            // Bottom Action Button
            Button(
                onClick = { /* Handle confirmation */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp)
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (confirmAttendance) extraColors.success else extraColors.maroonColor
                )
            ) {
                Text(
                    text = if (confirmAttendance) "تأكيد الحضور" else "تأكيد الاعتذار",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
fun ResponseButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    backgroundColor: Color,
    textColor: Color,
    showCheckmark: Boolean = false,
    showCloseIcon: Boolean = false
) {

    val extraColors = LocalExtraColors.current

    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = if (isSelected) 8.dp else 0.dp)
            .height(64.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = backgroundColor
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = if (isSelected) 2.dp else 0.dp
        )
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Right Icon
            if (showCloseIcon) {
                Box(
                    modifier = Modifier
                        .size(25.dp)
                        .background(if (isSelected) Color.White
                        else extraColors.maroonColor, RoundedCornerShape(50))
                        .padding(4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = null,
                        tint = if (isSelected) extraColors.maroonColor else Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            else if (showCheckmark) {
                Box(
                    modifier = Modifier
                        .size(25.dp)
                        .background(if (isSelected) Color.White
                        else extraColors.success, RoundedCornerShape(50))
                        .padding(4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = if (isSelected) extraColors.success else Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // Text
            Text(
                modifier = Modifier.weight(1f),
                text = text,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = textColor
            )

            // Left Icon
            if (isSelected) {
                Box(
                    modifier = Modifier
                        .size(25.dp)
                        .background(Color.White, RoundedCornerShape(50))
                        .padding(4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = backgroundColor,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun ApologyReasonOption(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {

    val extraColors = LocalExtraColors.current

    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .height(56.dp)
            .then(
                if (isSelected) {
                    Modifier.border(
                        width = 2.dp,
                        color = if (isSelected) extraColors.maroonColor else Color.LightGray,
                        shape = RoundedCornerShape(12.dp)
                    )
                } else Modifier
            ),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) extraColors.maroonColor.copy(alpha = 0.2f) else Color.White
        ),
        elevation = ButtonDefaults.buttonElevation(0.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                text = text,
                fontSize = 16.sp,
                color = if (isSelected) extraColors.blueColor else Color.Black,
                fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal
            )

            Spacer(modifier = Modifier.weight(1f))

            // Radio button
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .border(
                        width = 2.dp,
                        color = if (isSelected) extraColors.maroonColor else Color.LightGray,
                        shape = RoundedCornerShape(50)
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (isSelected) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .background(extraColors.maroonColor, RoundedCornerShape(50))
                    )
                }
            }

        }
    }
}

@Preview(showBackground = true)
@Composable
fun EditRespondPreview(){
    AppTheme {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            EditRespondScreen(
                viewModel = hiltViewModel(),
                navController = rememberNavController(),
                meetingId = "56"
            )
        }
    }
}