package com.informatique.electronicmeetingsplatform.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.informatique.electronicmeetingsplatform.ui.theme.AppTheme
import com.informatique.electronicmeetingsplatform.ui.theme.LocalExtraColors
import java.time.LocalDate
import java.time.LocalTime
import java.time.YearMonth
import java.util.Locale

@Composable
fun DateTimePicker(
    selectedDate: LocalDate?,
    selectedTime: LocalTime,
    minDate: LocalDate?,
    minTime: LocalTime?,
    onDateSelected: (LocalDate) -> Unit,
    onTimeChanged: (LocalTime) -> Unit,
    onDismiss: () -> Unit
) {

    val extraColors = LocalExtraColors.current

    // Initialize with selected date or current month
    var currentMonth by remember(selectedDate) {
        mutableStateOf(
            if (selectedDate != null) {
                YearMonth.of(selectedDate.year, selectedDate.month)
            } else {
                YearMonth.now()
            }
        )
    }
    var showTimePicker by remember { mutableStateOf(false) }
    var showMonthPicker by remember { mutableStateOf(false) }
    var showYearPicker by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .width(360.dp)
            .background(
                color = extraColors.background,
                shape = RoundedCornerShape(12.dp)
            )
    ) {
        // Header with month navigation
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { currentMonth = currentMonth.minusMonths(1) },
                        enabled = minDate == null || currentMonth.isAfter(YearMonth.from(minDate))
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                            contentDescription = "الشهر السابق",
                            tint = Color.Black
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {

                        // Clickable Month
                        Text(
                            text = getArabicMonth(currentMonth.monthValue),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black,
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .clickable {
                                    showMonthPicker = !showMonthPicker
                                    showYearPicker = false
                                }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        // Clickable Year
                        Text(
                            text = currentMonth.year.toString(),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black,
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .clickable {
                                    showYearPicker = !showYearPicker
                                    showMonthPicker = false
                                }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        )

                    }

                    IconButton(onClick = { currentMonth = currentMonth.plusMonths(1) }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = "الشهر التالي",
                            tint = Color.Black
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Week days header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    listOf("الأحد", "الإثنين", "الثلاثاء", "الأربعاء", "الخميس", "الجمعة", "السبت").forEach { day ->
                        Text(
                            text = day,
                            fontSize = 12.sp,
                            color = Color.Gray,
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }

        // Content Area - shows either calendar, month picker, or year picker
        when {
            showMonthPicker -> {
                MonthPickerContent(
                    currentMonth = currentMonth.monthValue,
                    onMonthSelected = { month ->
                        currentMonth = YearMonth.of(currentMonth.year, month)
                        showMonthPicker = false
                    }
                )
            }
            showYearPicker -> {
                YearPickerContent(
                    currentYear = currentMonth.year,
                    minYear = minDate?.year ?: 2020,
                    onYearSelected = { year ->
                        currentMonth = YearMonth.of(year, currentMonth.monthValue)
                        showYearPicker = false
                    }
                )
            }
            else -> {
                // Calendar grid
                CalendarGrid(
                    yearMonth = currentMonth,
                    selectedDate = selectedDate,
                    minDate = minDate,
                    onDateSelected = onDateSelected
                )
            }
        }

        // Time picker button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(Color.Gray.copy(alpha = 0.1f))
                .clickable { showTimePicker = !showTimePicker }
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "الوقت",
                fontSize = 16.sp,
                color = Color.Black,
                fontWeight = FontWeight.SemiBold
            )

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = formatArabicTime(selectedTime),
                    fontSize = 15.sp,
                    color = extraColors.blueColor,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = if (showTimePicker) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = if (showTimePicker) "إخفاء" else "عرض",
                    tint = extraColors.blueColor
                )
            }
        }

        // Inline Time Picker with animation
        AnimatedVisibility(
            visible = showTimePicker,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            InlineTimePicker(
                selectedTime = selectedTime,
                minTime = minTime,
                onTimeChanged = onTimeChanged
            )
        }

        // Confirm button
        TextButton(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = extraColors.blueColor.copy(alpha = 0.15f)
            ),
            onClick = { onDismiss() }
        ){
            Text(
                text = "تم",
                fontSize = 17.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Gray
            )
        }
    }
}

@Composable
fun InlineTimePicker(
    selectedTime: LocalTime,
    minTime: LocalTime?,
    onTimeChanged: (LocalTime) -> Unit
) {
    var selectedHour by remember(selectedTime) {
        mutableStateOf(if (selectedTime.hour % 12 == 0) 12 else selectedTime.hour % 12)
    }
    var selectedMinute by remember(selectedTime) { mutableStateOf(selectedTime.minute) }
    var isPM by remember(selectedTime) { mutableStateOf(selectedTime.hour >= 12) }

    // Helper function to convert 12-hour format to 24-hour format
    fun to24Hour(hour12: Int, pm: Boolean): Int {
        return if (pm) {
            if (hour12 == 12) 12 else hour12 + 12
        } else {
            if (hour12 == 12) 0 else hour12
        }
    }

    // Helper to update time with validation
    fun updateTime(hour12: Int, minute: Int, pm: Boolean) {
        val hour24 = to24Hour(hour12, pm)
        val newTime = LocalTime.of(hour24, minute)

        if (minTime != null && newTime.isBefore(minTime)) {
            // Adjust to minTime
            val minHour12 = if (minTime.hour % 12 == 0) 12 else minTime.hour % 12
            selectedHour = minHour12
            selectedMinute = minTime.minute
            isPM = minTime.hour >= 12
            onTimeChanged(minTime)
        } else {
            selectedHour = hour12
            selectedMinute = minute
            isPM = pm
            onTimeChanged(newTime)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Time display
        Text(
            text = "${String.format(Locale.ENGLISH, "%02d", selectedHour)}:${String.format(Locale.ENGLISH, "%02d", selectedMinute)} ${if (isPM) "م" else "ص"}",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF3B82F6)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Hour Picker
            CompactNumberPicker(
                value = selectedHour,
                range = 1..12,
                onValueChange = { updateTime(it, selectedMinute, isPM) }
            )

            Text(
                text = ":",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF3B82F6),
                modifier = Modifier.padding(horizontal = 12.dp)
            )

            // Minute Picker
            CompactNumberPicker(
                value = selectedMinute,
                range = 0..59,
                onValueChange = { updateTime(selectedHour, it, isPM) }
            )

            Spacer(modifier = Modifier.width(16.dp))

            // AM/PM Toggle
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            if (!isPM) Color(0xFF3B82F6) else Color(0xFFF0F4F8)
                        )
                        .clickable { updateTime(selectedHour, selectedMinute, false) }
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "ص",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (!isPM) Color.White else Color(0xFF64748B)
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            if (isPM) Color(0xFF3B82F6) else Color(0xFFF0F4F8)
                        )
                        .clickable { updateTime(selectedHour, selectedMinute, true) }
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "م",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isPM) Color.White else Color(0xFF64748B)
                    )
                }
            }
        }
    }
}

@Composable
fun CompactNumberPicker(
    value: Int,
    range: IntRange,
    onValueChange: (Int) -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        IconButton(
            onClick = {
                val newValue = if (value == range.last) range.first else value + 1
                onValueChange(newValue)
            },
            modifier = Modifier
                .size(36.dp)
                .background(Color(0xFFF0F4F8), CircleShape)
        ) {
            Icon(
                imageVector = Icons.Default.KeyboardArrowUp,
                contentDescription = "زيادة",
                modifier = Modifier.size(24.dp),
                tint = Color(0xFF3B82F6)
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Card(
            modifier = Modifier.size(60.dp, 50.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = String.format(Locale.ENGLISH, "%02d", value),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E293B)
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        IconButton(
            onClick = {
                val newValue = if (value == range.first) range.last else value - 1
                onValueChange(newValue)
            },
            modifier = Modifier
                .size(36.dp)
                .background(Color(0xFFF0F4F8), CircleShape)
        ) {
            Icon(
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = "تقليل",
                modifier = Modifier.size(24.dp),
                tint = Color(0xFF3B82F6)
            )
        }
    }
}


@Composable
fun CalendarGrid(
    yearMonth: YearMonth,
    selectedDate: LocalDate?,
    minDate: LocalDate?,
    onDateSelected: (LocalDate) -> Unit
) {

    val extraColors = LocalExtraColors.current

    val firstDayOfMonth = yearMonth.atDay(1)
    val daysInMonth = yearMonth.lengthOfMonth()
    val firstDayOfWeek = (firstDayOfMonth.dayOfWeek.value % 7)
    val today = LocalDate.now()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        var dayCounter = 1
        val totalWeeks = kotlin.math.ceil((firstDayOfWeek + daysInMonth) / 7.0).toInt()

        repeat(totalWeeks) { week ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                repeat(7) { dayOfWeek ->
                    val cellIndex = week * 7 + dayOfWeek

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                            .padding(4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        if (cellIndex >= firstDayOfWeek && dayCounter <= daysInMonth) {
                            val currentDate = yearMonth.atDay(dayCounter)
                            val isSelected = currentDate == selectedDate
                            val isDisabled = minDate != null && currentDate.isBefore(minDate)
                            val isToday = currentDate == today

                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(CircleShape)
                                    .then(
                                        if (isToday && isSelected && !isDisabled) {
                                            Modifier.background(color = extraColors.blueColor)
                                        } else if (isSelected) {
                                            Modifier.background(
                                                color = extraColors.textGray.copy(alpha = 0.3f)
                                            )
                                        } else {
                                            Modifier.background(Color.Transparent)
                                        }
                                    )
                                    .clickable(enabled = !isDisabled) {
                                        onDateSelected(currentDate)
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = dayCounter.toString(),
                                    fontSize = 16.sp,
                                    color = when {
                                        isSelected -> Color.White
                                        isDisabled -> Color.Gray
                                        isToday -> extraColors.blueColor
                                        else -> Color(0xFF1F2937)
                                    },
                                    fontWeight = when {
                                        isSelected || isToday -> FontWeight.Bold
                                        else -> FontWeight.Medium
                                    }
                                )
                            }
                            dayCounter++
                        }
                        // Empty space for days not in this month - no content rendered
                    }
                }
            }
        }
    }
}

@Composable
fun MonthPickerContent(
    currentMonth: Int,
    onMonthSelected: (Int) -> Unit
) {
    // Month Grid
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(20.dp)
    ) {
        val months = listOf(
            1 to "يناير", 2 to "فبراير", 3 to "مارس", 4 to "أبريل",
            5 to "مايو", 6 to "يونيو", 7 to "يوليو", 8 to "أغسطس",
            9 to "سبتمبر", 10 to "أكتوبر", 11 to "نوفمبر", 12 to "ديسمبر"
        )

        months.chunked(3).forEach { rowMonths ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                rowMonths.forEach { (monthNum, monthName) ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                if (monthNum == currentMonth) {
                                    Brush.horizontalGradient(
                                        colors = listOf(Color(0xFF3B82F6), Color(0xFF2563EB))
                                    )
                                } else {
                                    Brush.horizontalGradient(
                                        colors = listOf(Color(0xFFF0F4F8), Color(0xFFF0F4F8))
                                    )
                                }
                            )
                            .clickable {
                                onMonthSelected(monthNum)
                            }
                            .padding(vertical = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = monthName,
                            fontSize = 15.sp,
                            fontWeight = if (monthNum == currentMonth) FontWeight.Bold else FontWeight.Medium,
                            color = if (monthNum == currentMonth) Color.White else Color(0xFF374151)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
fun YearPickerContent(
    currentYear: Int,
    minYear: Int,
    onYearSelected: (Int) -> Unit
) {
    var displayYear by remember { mutableStateOf(currentYear) }

    // Year Picker
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        IconButton(
            onClick = { displayYear++ },
            modifier = Modifier
                .size(56.dp)
                .background(Color(0xFFF0F4F8), CircleShape)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                contentDescription = "السنة التالية",
                modifier = Modifier.size(32.dp),
                tint = Color(0xFF3B82F6)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = displayYear.toString(),
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E293B)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        IconButton(
            onClick = { if (displayYear > minYear) displayYear-- },
            modifier = Modifier
                .size(56.dp)
                .background(Color(0xFFF0F4F8), CircleShape)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = "السنة السابقة",
                modifier = Modifier.size(32.dp),
                tint = Color(0xFF3B82F6)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Confirm Button
        Button(
            onClick = { onYearSelected(displayYear) },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF3B82F6)
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                "تأكيد",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }
    }
}

fun getArabicMonth(month: Int): String {
    return when (month) {
        1 -> "يناير"
        2 -> "فبراير"
        3 -> "مارس"
        4 -> "أبريل"
        5 -> "مايو"
        6 -> "يونيو"
        7 -> "يوليو"
        8 -> "أغسطس"
        9 -> "سبتمبر"
        10 -> "أكتوبر"
        11 -> "نوفمبر"
        12 -> "ديسمبر"
        else -> ""
    }
}

fun formatArabicDate(date: LocalDate): String {
    val day = date.dayOfMonth
    val month = getArabicMonth(date.monthValue)
    val year = date.year
    return "$day $month $year"
}

fun formatArabicTime(time: LocalTime): String {
    val hour = if (time.hour % 12 == 0) 12 else time.hour % 12
    val minute = time.minute
    val period = if (time.hour >= 12) "م" else "ص"
    return "${String.format(Locale.ENGLISH, "%02d", minute)}:$hour $period"
    //return "$period ${String.format("%02d", minute)}:$hour"
}


@Preview(showBackground = true)
@Composable
fun DateTimePickerPreview(){
    AppTheme {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            DateTimePicker(
                selectedDate = LocalDate.now(),
                selectedTime = LocalTime.now(),
                minDate = null,
                minTime = null,
                onDateSelected = {},
                onTimeChanged = {},
                onDismiss = {}
            )
        }
    }
}