package com.informatique.electronicmeetingsplatform.ui.screens


import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBackIos
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

// أنواع الطلبات
enum class RequestType(val arabicName: String) {
    CLOUD_SERVICE("خدمة بالبالية عن"),
    OFFICIAL_TASK("طلب الموافقة على مهمة رسمية"),
    TASK_ALLOWANCE("طلب بدل مهمة رسمية"),
    VACATION("طلب إجازة"),
    CERTIFICATE("طلب شهادة"),
    DATA_MODIFICATION("طلب تعديل البيانات الشخصية")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddRequestScreen(
    navController: NavController,
    onDismiss: (() -> Unit)? = null,
    onNavigateToOfficialTaskRequest: (() -> Unit)? = null
) {
    var selectedRequestType by remember { mutableStateOf<RequestType?>(null) }

    // Handle system back button
    BackHandler {
        onDismiss?.invoke() ?: navController.popBackStack()
    }

    Scaffold(
        containerColor = LightGrayBg,
        topBar = {
            TopAppBar(
                title = {
                    Column(
                        horizontalAlignment = Alignment.Start,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            "إنشاء طلب جديد",
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Bold,
                            color = DarkBlueText
                        )
                        Text(
                            "اختر نوع الطلب الذي تريد إنشاءه",
                            fontSize = 12.sp,
                            color = GrayText
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            onDismiss?.invoke() ?: navController.popBackStack()
                        }
                    ) {
                        Icon(
                            Icons.Rounded.ArrowBackIos,
                            contentDescription = "رجوع",
                            tint = DarkRedTab
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = LightGrayBg
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Request Types List
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(0.dp)
                ) {
                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    items(RequestType.entries) { requestType ->
                        RequestTypeItem(
                            requestType = requestType,
                            isSelected = selectedRequestType == requestType,
                            onClick = { selectedRequestType = requestType }
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(100.dp))
                    }
                }

                // Bottom Button - يظهر فقط لما يختار
                AnimatedVisibility(
                    visible = selectedRequestType != null,
                    enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                    exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(LightGrayBg)
                            .padding(horizontal = 20.dp)
                            .padding(bottom = 20.dp, top = 8.dp)
                    ) {
                        Button(
                            onClick = {
                                // التوجه إلى الصفحة المناسبة حسب نوع الطلب
                                when (selectedRequestType) {
                                    RequestType.OFFICIAL_TASK -> {
                                        onNavigateToOfficialTaskRequest?.invoke()
                                    }
                                    else -> {
                                        // يمكن إضافة صفحات أخرى لاحقاً
                                    }
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = DarkRedTab
                            )
                        ) {
                            Text(
                                "متابعة",
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

@Composable
fun RequestTypeItem(
    requestType: RequestType,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = if (isSelected) {
            androidx.compose.foundation.BorderStroke(2.dp, DarkRedTab)
        } else {
            null
        }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 18.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                requestType.arabicName,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                color = if (isSelected) { DarkRedTab} else{ DarkBlueText},
                textAlign = TextAlign.Start,
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 16.dp)
            )
            // Radio Button
            Box(
                modifier = Modifier.size(26.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    // الدائرة الخارجية
                    drawCircle(
                        color = if (isSelected) DarkRedTab else Color(0xFFE0E0E0),
                        style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2.5.dp.toPx())
                    )

                    // الدائرة الداخلية لما يكون محدد
                    if (isSelected) {
                        drawCircle(
                            color = DarkRedTab,
                            radius = size.minDimension / 3.5f
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AddRequestScreenPreview() {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        AddRequestScreen(navController = rememberNavController())
    }
}