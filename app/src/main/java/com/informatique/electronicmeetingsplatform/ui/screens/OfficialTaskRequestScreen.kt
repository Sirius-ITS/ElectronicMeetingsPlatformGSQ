
package com.informatique.electronicmeetingsplatform.ui.screens

import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.ArrowBackIos
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OfficialTaskRequestScreen(
    navController: NavController,
    onDismiss: (() -> Unit)? = null
) {
    val context = LocalContext.current
    // Get today's date in Arabic format
    val todayFormatted = remember {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("EEEE, dd MMMM yyyy", Locale("ar"))
        dateFormat.format(calendar.time)
    }

    var taskDuration by remember { mutableStateOf("1 يوم") }
    var selectedCountry by remember { mutableStateOf("") }
    var selectedTaskType by remember { mutableStateOf("") }
    var selectedPurpose by remember { mutableStateOf("") }
    var startDate by remember { mutableStateOf(todayFormatted) }
    var endDate by remember { mutableStateOf(todayFormatted) }
    var additionalNotes by remember { mutableStateOf("") }
    var attachmentUri by remember { mutableStateOf("") }

    var showCountryBottomSheet by remember { mutableStateOf(false) }
    var showTaskTypeBottomSheet by remember { mutableStateOf(false) }
    var showPurposeBottomSheet by remember { mutableStateOf(false) }
    var showStartDatePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }

    val countrySheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
    val taskTypeSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
    val purposeSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)

    // File picker launcher
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            attachmentUri = it.toString()
        }
    }

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
                            "طلب مهمة رسمية",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = DarkBlueText
                        )
                        Text(
                            "يرجى تعبئة جميع التفاصيل",
                            fontSize = 13.sp,
                            color = GrayText
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { onDismiss?.invoke() ?: navController.popBackStack() }
                    ) {
                        Icon(
                            Icons.Rounded.ArrowBackIos,
                            contentDescription = "رجوع",
                            tint = DarkRedTab,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = LightGrayBg
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { Spacer(modifier = Modifier.height(8.dp)) }

            // تفاصيل المهمة Card
            item {
                SectionCard(
                    title = "تفاصيل المهمة",
                    icon = Icons.Default.Description
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        DropdownField(
                            label = "الدولة",
                            value = selectedCountry,
                            placeholder = "اختر الدولة",
                            onClick = { showCountryBottomSheet = true }
                        )

                        DropdownField(
                            label = "نوع المهمة",
                            value = selectedTaskType,
                            placeholder = "اختر نوع المهمة",
                            onClick = { showTaskTypeBottomSheet = true }
                        )

                        DropdownField(
                            label = "الغرض من المهمة",
                            value = selectedPurpose,
                            placeholder = "اختر الغرض من المهمة",
                            onClick = { showPurposeBottomSheet = true }
                        )
                    }
                }
            }

            // الفترة الزمنية Card
            item {
                SectionCard(
                    title = "الفترة الزمنية",
                    icon = Icons.Default.CalendarMonth
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        DatePickerField(
                            label = "تاريخ البدء",
                            value = startDate,
                            placeholder = "اختر تاريخ البدء",
                            onClick = { showStartDatePicker = true }
                        )

                        DatePickerField(
                            label = "تاريخ الانتهاء",
                            value = endDate,
                            placeholder = "اختر تاريخ الانتهاء",
                            onClick = { showEndDatePicker = true }
                        )

                        DurationDisplay(duration = taskDuration)
                    }
                }
            }

            // ملاحظات إضافية Card
            item {
                SectionCard(
                    title = "ملاحظات إضافية",
                    icon = Icons.Default.Message
                ) {
                    NotesTextField(
                        value = additionalNotes,
                        onValueChange = { if (it.length <= 500) additionalNotes = it }
                    )
                }
            }

            // المرفقات Card
            item {
                SectionCard(
                    title = "المرفقات",
                    icon = Icons.Default.AttachFile
                ) {
                    AttachmentSection(
                        attachmentUri = attachmentUri,
                        onPickFile = { filePickerLauncher.launch("*/*") },
                        onRemoveFile = { attachmentUri = "" }
                    )
                }
            }

            // Submit Button
            item {
                Button(
                    onClick = {
                        onDismiss?.invoke() ?: navController.popBackStack()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = DarkRedTab
                    )
                ) {
                    Text(
                        "إرسال الطلب",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }

    // Date Pickers
    if (showStartDatePicker) {
        CustomDatePickerDialog(
            onDateSelected = { selectedDate ->
                val calendar = Calendar.getInstance()
                calendar.timeInMillis = selectedDate
                val formatter = SimpleDateFormat("EEEE, dd MMMM yyyy", Locale("ar"))
                startDate = formatter.format(calendar.time)
                showStartDatePicker = false
            },
            onDismiss = { showStartDatePicker = false },
            allowPastDates = false,
            initialDate = startDate
        )
    }

    if (showEndDatePicker) {
        CustomDatePickerDialog(
            onDateSelected = { selectedDate ->
                val calendar = Calendar.getInstance()
                calendar.timeInMillis = selectedDate
                val formatter = SimpleDateFormat("EEEE, dd MMMM yyyy", Locale("ar"))
                endDate = formatter.format(calendar.time)
                showEndDatePicker = false
            },
            onDismiss = { showEndDatePicker = false },
            allowPastDates = false,
            initialDate = endDate
        )
    }

    // Bottom Sheets
    if (showCountryBottomSheet) {
        DropdownBottomSheet(
            title = "اختر الدولة",
            items = listOf("قطر", "الإمارات", "السعودية", "البحرين", "الكويت", "عمان"),
            selectedItem = selectedCountry,
            onItemSelected = { selectedCountry = it },
            sheetState = countrySheetState,
            onDismiss = { showCountryBottomSheet = false }
        )
    }

    if (showTaskTypeBottomSheet) {
        DropdownBottomSheet(
            title = "اختر نوع المهمة",
            items = listOf("مؤتمر", "اجتماع", "تدريب", "زيارة رسمية", "ورشة عمل"),
            selectedItem = selectedTaskType,
            onItemSelected = { selectedTaskType = it },
            sheetState = taskTypeSheetState,
            onDismiss = { showTaskTypeBottomSheet = false }
        )
    }

    if (showPurposeBottomSheet) {
        DropdownBottomSheet(
            title = "اختر الغرض من المهمة",
            items = listOf("حضور مؤتمر", "اجتماع وزاري", "دورة تدريبية", "زيارة ميدانية"),
            selectedItem = selectedPurpose,
            onItemSelected = { selectedPurpose = it },
            sheetState = purposeSheetState,
            onDismiss = { showPurposeBottomSheet = false }
        )
    }
}

@Composable
fun SectionCard(
    title: String,
    icon: ImageVector,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = DarkRedTab,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = DarkBlueText
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            content()
        }
    }
}

@Composable
fun DropdownField(
    label: String,
    value: String,
    placeholder: String,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Color.Gray.copy(alpha = 0.5f), shape = RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFFFF)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                horizontalAlignment = Alignment.Start,
                modifier = Modifier.padding(horizontal = 4.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    label,
                    fontSize = 12.sp,
                    color = GrayText,
                    textAlign = TextAlign.Start
                )

                Text(
                    value.ifEmpty { placeholder },
                    fontSize = 15.sp,
                    color = DarkBlueText,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Start
                )
            }
            Icon(
                Icons.Default.KeyboardArrowDown,
                contentDescription = null,
                tint = DarkRedTab,
                modifier = Modifier.size(25.dp)
            )
        }
    }
}

@Composable
fun DatePickerField(
    label: String,
    value: String,
    placeholder: String,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Color.Gray.copy(alpha = 0.5f), shape = RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFFFF)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                horizontalAlignment = Alignment.Start,
                modifier = Modifier.padding(horizontal = 4.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    label,
                    fontSize = 12.sp,
                    color = GrayText,
                    textAlign = TextAlign.Start
                )

                Text(
                    value.ifEmpty { placeholder },
                    fontSize = 15.sp,
                    color = DarkBlueText,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Start
                )
            }
            Icon(
                Icons.Default.DateRange,
                contentDescription = null,
                tint = DarkRedTab,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
fun DurationDisplay(duration: String) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .background(
                        Color(0xFFEEE1E4),
                        RoundedCornerShape(20.dp)
                    )
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        duration,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = DarkRedTab
                    )
                    Icon(
                        Icons.Default.AccessTimeFilled,
                        contentDescription = null,
                        tint = DarkRedTab,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
            Text(
                "مدة المهمة",
                fontSize = 13.sp,
                color = GrayText,
                textAlign = TextAlign.End,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun NotesTextField(
    value: String,
    onValueChange: (String) -> Unit
) {
    Column {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .border(1.dp, Color(0xFFD2CDD3), shape = RoundedCornerShape(14.dp)),
            placeholder = {
                Text(
                    "اكتب أي ملاحظات أو تفاصيل إضافية هنا...",
                    fontSize = 13.sp,
                    color = Color(0xFFBDBDBD),
                    textAlign = TextAlign.Start,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = Color(0xFFF3F2F7),
                focusedContainerColor = Color(0xFFF3F2F7),
                unfocusedBorderColor = Color.Transparent,
                focusedBorderColor = DarkRedTab.copy(alpha = 0.5f)
            ),
            textStyle = LocalTextStyle.current.copy(
                textAlign = TextAlign.Start,
                fontSize = 14.sp
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            "${value.length}/500",
            fontSize = 12.sp,
            color = GrayText,
            textAlign = TextAlign.Start,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun AttachmentSection(
    attachmentUri: String,
    onPickFile: () -> Unit,
    onRemoveFile: () -> Unit
) {
    val context = LocalContext.current
    val fileName = remember(attachmentUri) {
        if (attachmentUri.isNotEmpty()) {
            getFileName(context, Uri.parse(attachmentUri)) ?: "ملف مرفق"
        } else ""
    }

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        if (attachmentUri.isNotEmpty()) {
            // File selected - show file card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFF3F2F7)
                ),
                border = androidx.compose.foundation.BorderStroke(
                    width = 1.dp,
                    color = Color(0xFFC5B8C0)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.CheckCircle,
                            contentDescription = null,
                            tint = DarkRedTab,
                            modifier = Modifier.size(24.dp)
                        )

                        Icon(
                            imageVector = Icons.Default.Description,
                            contentDescription = null,
                            tint = DarkRedTab,
                            modifier = Modifier.size(24.dp)
                        )

                        Text(
                            text = fileName,
                            fontSize = 14.sp,
                            color = DarkBlueText,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    IconButton(onClick = onRemoveFile) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "حذف",
                            tint = DarkRedTab,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        } else {
            // No file - show upload area
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onPickFile)
                    .background(Color(0xFFF3F2F7), RoundedCornerShape(12.dp))
                    .dashedBorder(
                        width = 2.dp,
                        color = Color(0xFFC5B8C0),
                        cornerRadius = 12.dp,
                        dashWidth = 12.dp,
                        gapWidth = 8.dp
                    )
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier.size(64.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .background(Color(0xFFE4DDE4), CircleShape)
                        )
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .background(Color(0xFF7D3B54), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Add,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }

                    Text(
                        "إضافة مرفق",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = DarkBlueText
                    )

                    Text(
                        "صورة أو مستند PDF",
                        fontSize = 13.sp,
                        color = Color(0xFF9E9E9E)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                Icons.Default.Info,
                contentDescription = null,
                tint = Color(0xFF7D3B54),
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                "يمكنك إرفاق المستندات الداعمة للطلب (اختياري)",
                fontSize = 12.sp,
                color = Color(0xFF9E9E9E)
            )
        }
    }
}

fun Modifier.dashedBorder(
    width: Dp,
    color: Color,
    cornerRadius: Dp,
    dashWidth: Dp = 10.dp,
    gapWidth: Dp = 5.dp
) = this.drawWithContent {
    drawContent()

    val stroke = Stroke(
        width = width.toPx(),
        pathEffect = PathEffect.dashPathEffect(
            floatArrayOf(dashWidth.toPx(), gapWidth.toPx()),
            0f
        )
    )

    val radius = cornerRadius.toPx()

    drawRoundRect(
        color = color,
        style = stroke,
        cornerRadius = CornerRadius(radius, radius),
        topLeft = Offset(width.toPx() / 2, width.toPx() / 2),
        size = Size(
            size.width - width.toPx(),
            size.height - width.toPx()
        )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomDatePickerDialog(
    onDateSelected: (Long) -> Unit,
    onDismiss: () -> Unit,
    allowPastDates: Boolean = true,
    initialDate: String? = null
) {
    val today = System.currentTimeMillis()

    val initialDateMillis = remember(initialDate) {
        if (initialDate != null && initialDate.isNotEmpty()) {
            try {
                // Try to parse Arabic date format "الخميس, 15 يناير 2026"
                val formatter = SimpleDateFormat("EEEE, dd MMMM yyyy", Locale("ar"))
                formatter.parse(initialDate)?.time
            } catch (e: Exception) {
                null
            }
        } else {
            null
        }
    }

    val selectableDates = object : SelectableDates {
        override fun isSelectableDate(utcTimeMillis: Long): Boolean {
            return if (allowPastDates) {
                utcTimeMillis <= today
            } else {
                utcTimeMillis >= today
            }
        }
    }

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = initialDateMillis ?: today,
        selectableDates = selectableDates
    )

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    datePickerState.selectedDateMillis?.let { selectedDate ->
                        onDateSelected(selectedDate)
                    }
                }
            ) {
                Text("موافق")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("إلغاء")
            }
        }
    ) {
        DatePicker(state = datePickerState)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownBottomSheet(
    title: String,
    items: List<String>,
    selectedItem: String,
    onItemSelected: (String) -> Unit,
    sheetState: SheetState,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        containerColor = CardWhite,
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 12.dp)
                    .width(40.dp)
                    .height(4.dp)
                    .background(
                        Color.Gray.copy(alpha = 0.3f),
                        RoundedCornerShape(2.dp)
                    )
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 24.dp)
        ) {
            Text(
                title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(20.dp))

            items.forEach { item ->
                DropdownItem(
                    text = item,
                    isSelected = item == selectedItem,
                    onClick = {
                        onItemSelected(item)
                        onDismiss()
                    }
                )

                if (item != items.last()) {
                    HorizontalDivider(
                        color = Color(0xFFF0F0F0),
                        thickness = 1.dp
                    )
                }
            }
        }
    }
}

@Composable
fun DropdownItem(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text,
            fontSize = 16.sp,
            fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal,
            color = if (isSelected) DarkRedTab else Color.Black
        )
        if (isSelected) {
            Icon(
                Icons.Default.CheckCircle,
                contentDescription = null,
                tint = DarkRedTab,
                modifier = Modifier.size(24.dp)
            )
        } else {
            Spacer(modifier = Modifier.size(24.dp))
        }
    }
}

private fun getFileName(context: android.content.Context, uri: Uri): String? {
    return try {
        context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            val nameIndex = cursor.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
            cursor.moveToFirst()
            cursor.getString(nameIndex)
        }
    } catch (e: Exception) {
        uri.lastPathSegment
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun OfficialTaskRequestScreenPreview() {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        OfficialTaskRequestScreen(
            navController = rememberNavController(),
            onDismiss = null
        )
    }
}