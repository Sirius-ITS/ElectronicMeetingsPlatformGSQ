package com.informatique.electronicmeetingsplatform.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.informatique.electronicmeetingsplatform.ui.theme.LocalExtraColors
import com.informatique.electronicmeetingsplatform.ui.viewModel.MeetingsViewModel
import com.informatique.electronicmeetingsplatform.navigation.NavRoutes
import com.kizitonwose.calendar.compose.CalendarState
import com.kizitonwose.calendar.compose.HorizontalCalendar
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.core.*
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth

private val BackgroundGray = Color(0xFFF5F5F5)
private val CardBackground = Color.White
private val TextPrimary = Color(0xFF1A1A1A)
private val TextSecondary = Color(0xFF999999)
private val RedBadge = Color(0xFFE74C3C)
private val GreenBadge = Color(0xFF27AE60)
private val BlueBadge = Color(0xFF3498DB)
private val LightPurple = Color(0xFFF3E5F5)

@Composable
fun CalendarScreen(
    navController: NavController,
    viewModel: MeetingsViewModel? = null
) {
    val meetingsViewModel = viewModel ?: hiltViewModel()
    val context = androidx.compose.ui.platform.LocalContext.current

    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var currentMonth by remember { mutableStateOf(YearMonth.now()) }
    var selectedTab by remember { mutableStateOf(CalendarTab.GREGORIAN) }
    var expandedMeetingId by remember { mutableStateOf<String?>(null) }
    var isMeetingExpanded by remember { mutableStateOf(false) }
    var showCalendar by remember { mutableStateOf(true) }

    // Fetch meetings from device calendar based on current month
    val calendarMeetings = remember(currentMonth) {
        val startOfMonth = currentMonth.atDay(1)
        val endOfMonth = currentMonth.atEndOfMonth()
        meetingsViewModel.getEventsFromDeviceCalendar(startOfMonth, endOfMonth)
    }

    // Convert API Meeting to Calendar Screen Meeting
    val meetings = remember(calendarMeetings) {
        calendarMeetings.map { apiMeeting ->
            val eventDate = try {
                java.time.LocalDateTime.parse(apiMeeting.startDateTime).toLocalDate()
            } catch (_: Exception) {
                LocalDate.now()
            }

            // Ensure unique string id for LazyColumn keys: prefer server id if available
            val baseId = apiMeeting.id
            val uniqueId = if (baseId > 0) {
                // server-linked meeting
                baseId.toString()
            } else {
                // unlinked calendar event: combine calendar id and start time hash to make a stable unique key
                "cal_${baseId}_${apiMeeting.startDateTime.hashCode()}"
            }

            Meeting(
                id = uniqueId,
                title = apiMeeting.topic,
                date = eventDate,
                time = "${apiMeeting.startTime} - ${apiMeeting.endTime}",
                location = apiMeeting.location,
                description = apiMeeting.notes,
                priority = apiMeeting.priorityName,
                isOrganizer = apiMeeting.isOrganizer ?: false,
                attendanceStatus = mapOf(
                    AttendanceStatus.CONFIRMED to apiMeeting.acceptedCount,
                    AttendanceStatus.DECLINED to apiMeeting.refusedCount
                ),
                attendees = apiMeeting.attendees.mapNotNull { it.fullName }
            )
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundGray)
    ) {
        Column(
            modifier = Modifier. fillMaxSize()
        ) {
            // Top Header
            CalendarHeader()

            // Content
            LazyColumn(
                modifier = Modifier
                    . fillMaxSize()
                    .weight(1f),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Calendar Toggle Tabs
                item {
                    AnimatedVisibility(
                        visible = showCalendar,
                        enter = fadeIn() + expandVertically(),
                        exit = fadeOut() + shrinkVertically()
                    ) {
                        CalendarTabSelector(
                            selectedTab = selectedTab,
                            onTabSelected = { selectedTab = it }
                        )
                    }
                }

                // Calendar Widget with Real Calendar Library
                item {
                    AnimatedVisibility(
                        visible = showCalendar,
                        enter = fadeIn() + expandVertically(),
                        exit = fadeOut() + shrinkVertically()
                    ) {
                        when (selectedTab) {
                            CalendarTab.GREGORIAN -> {
                                GregorianCalendarWidget(
                                    selectedDate = selectedDate,
                                    onDateSelected = { selectedDate = it },
                                    meetings = meetings,
                                    onMonthChanged = { newMonth -> currentMonth = newMonth }
                                )
                            }
                            CalendarTab.HIJRI -> {
                                HijriCalendarWidget(
                                    selectedDate = selectedDate,
                                    onDateSelected = { selectedDate = it },
                                    meetings = meetings,
                                    onMonthChanged = { newMonth -> currentMonth = newMonth }
                                )
                            }
                        }
                    }
                }

                // Meeting Sections
                val meetingsForDate = meetings.filter { it.date == selectedDate }
                val organizedMeetings = meetingsForDate.filter { it.isOrganizer }
                val invitedMeetings = meetingsForDate.filter { !it.isOrganizer }

                if (meetingsForDate.isEmpty()) {
                    item {
                        EmptyMeetingsCard()
                    }
                } else {
                    if (organizedMeetings.isNotEmpty()) {
                        item {
                            MeetingSectionHeader(
                                title = "اجتماعاتي",
                                subtitle = "أنت المنظم",
                                icon = Icons.Default.Person
                            )
                        }

//                        item {
//                            WeeklyMeetingCard(
//                                isExpanded = isMeetingExpanded,
//                                onExpandChange = { isMeetingExpanded = it }
//                            )
//                        }

                        items(
                            items = organizedMeetings,
                            key = { it.id }
                        ) { meeting ->
                            MeetingItem(
                                meeting = meeting,
                                isExpanded = expandedMeetingId == meeting.id,
                                onExpandToggle = {
                                    expandedMeetingId = if (expandedMeetingId == meeting.id) null else meeting.id
                                },
                                onMeetingClick = { meetingId ->
                                    // Navigate only for server-linked meetings (numeric positive id)
                                    val numericId = meetingId.toIntOrNull()
                                    if (numericId != null && numericId > 0) {
                                        navController.navigate(NavRoutes.MeetingDetailRoute.createRoute(meetingId))
                                    } else {
                                        android.widget.Toast.makeText(
                                            context,
                                            "هذا الاجتماع مضاف من تقويم خارجي ولا توجد تفاصيل مرتبطة.",
                                            android.widget.Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                            )
                        }
                    }

                    if (invitedMeetings.isNotEmpty()) {
                        item {
                            MeetingSectionHeader(
                                title = "دعيت إليها",
                                subtitle = "اجتماعات مدعو إليها",
                                icon = Icons.Default.PersonAddAlt
                            )
                        }

                        items(
                            items = invitedMeetings,
                            key = { it.id }
                        ) { meeting ->
                            MeetingItem(
                                meeting = meeting,
                                isExpanded = expandedMeetingId == meeting.id,
                                onExpandToggle = {
                                    expandedMeetingId = if (expandedMeetingId == meeting.id) null else meeting.id
                                },
                                onMeetingClick = { meetingId ->
                                    val numericId = meetingId.toIntOrNull()
                                    if (numericId != null && numericId > 0) {
                                        navController.navigate(NavRoutes.MeetingDetailRoute.createRoute(meetingId))
                                    } else {
                                        android.widget.Toast.makeText(
                                            context,
                                            "هذا الاجتماع مضاف من تقويم خارجي ولا توجد تفاصيل مرتبطة.",
                                            android.widget.Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                            )
                        }
                    }
                }

                // Bottom spacing for navigation bar
                item {
                    Spacer(modifier = Modifier. height(80.dp))
                }
            }
        }

    }
}

@Composable
fun CalendarHeader() {
    val extraColors = LocalExtraColors.current

    Surface(
        modifier = Modifier. fillMaxWidth(),
        color = BackgroundGray,
        shadowElevation = 0.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ){
                Text(
                    text = "المواعيد",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = extraColors.maroonColor
                )
                Text(
                    text = "إدارة جميع المواعيد",
                    fontSize = 14.sp,
                    color = TextSecondary
                )
            }

            IconButton(
                onClick = { /* Add new meeting */ },
                modifier = Modifier.size(40.dp),
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = extraColors.maroonColor,
                )
            ) {
                Icon(
                    Icons.Default.Add,
                    tint = Color.White,
                    contentDescription = "Add"
                )
            }
        }
    }
}

enum class CalendarTab {
    GREGORIAN, HIJRI
}

@Composable
fun CalendarTabSelector(
    selectedTab: CalendarTab,
    onTabSelected:  (CalendarTab) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        TabButton(
            text = "ميلادي",
            isSelected = selectedTab == CalendarTab.GREGORIAN,
            onClick = { onTabSelected(CalendarTab.GREGORIAN) },
            modifier = Modifier.weight(1f)
        )
        TabButton(
            text = "هجري",
            isSelected = selectedTab == CalendarTab.HIJRI,
            onClick = { onTabSelected(CalendarTab.HIJRI) },
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun TabButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {

    val extraColors = LocalExtraColors.current

    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected) extraColors.maroonColor else Color.White,
        animationSpec = tween(300)
    )
    val textColor by animateColorAsState(
        targetValue = if (isSelected) Color.White else extraColors.blueColor,
        animationSpec = tween(300)
    )

    Box(
        modifier = modifier
            .fillMaxHeight()
            .padding(4.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(),
                onClick = onClick
            ),
        contentAlignment = Alignment. Center
    ) {
        Text(
            text = text,
            fontSize = 16.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = textColor
        )
    }
}

@Composable
fun GregorianCalendarWidget(
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit,
    meetings: List<Meeting>,
    onMonthChanged: (YearMonth) -> Unit = {}
) {
    val currentMonth = remember { YearMonth.now() }
    val startMonth = remember { currentMonth.minusMonths(100) }
    val endMonth = remember { currentMonth.plusMonths(100) }
    val daysOfWeek = remember { daysOfWeekFromLocale() }

    val state = rememberCalendarState(
        startMonth = startMonth,
        endMonth = endMonth,
        firstVisibleMonth = currentMonth,
        firstDayOfWeek = daysOfWeek. first()
    )

    val coroutineScope = rememberCoroutineScope()
    val visibleMonth = rememberFirstCompletelyVisibleMonth(state)

    // Notify parent when month changes
    LaunchedEffect(visibleMonth.yearMonth) {
        onMonthChanged(visibleMonth.yearMonth)
    }

    Surface(
        modifier = Modifier. fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = CardBackground,
        shadowElevation = 2.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // Month Header with Navigation
            CalendarMonthHeader(
                currentMonth = visibleMonth.yearMonth,
                onPreviousMonth = {
                    coroutineScope. launch {
                        state.animateScrollToMonth(visibleMonth.yearMonth.minusMonths(1))
                    }
                },
                onNextMonth = {
                    coroutineScope.launch {
                        state.animateScrollToMonth(visibleMonth.yearMonth.plusMonths(1))
                    }
                }
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Days of Week Header
            DaysOfWeekHeader(daysOfWeek = daysOfWeek)

            Spacer(modifier = Modifier.height(12.dp))

            // Calendar Grid using Kizitonwose Calendar
            HorizontalCalendar(
                state = state,
                dayContent = { day ->
                    val meetingsOnDay = meetings.filter { it.date == day.date }

                    CalendarDay(
                        day = day,
                        isSelected = selectedDate == day.date,
                        isToday = day.date == LocalDate.now(),
                        hasMeetings = meetingsOnDay.isNotEmpty(),
                        meetingCount = meetingsOnDay.size,
                        onClick = {
                            if (day.position == DayPosition.MonthDate) {
                                onDateSelected(day.date)
                            }
                        }
                    )
                },
                monthHeader = { },
                monthBody = { _, content ->
                    Box {
                        content()
                    }
                }
            )
        }
    }
}

@Composable
fun HijriCalendarWidget(
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit,
    meetings: List<Meeting>,
    onMonthChanged: (YearMonth) -> Unit = {}
) {
    // For Hijri calendar, we'll still use the Gregorian dates internally
    // but display Hijri dates using UmmalquraCalendar
    val currentMonth = remember { YearMonth. now() }
    val startMonth = remember { currentMonth.minusMonths(100) }
    val endMonth = remember { currentMonth.plusMonths(100) }
    val daysOfWeek = remember { daysOfWeekFromLocale() }

    val state = rememberCalendarState(
        startMonth = startMonth,
        endMonth = endMonth,
        firstVisibleMonth = currentMonth,
        firstDayOfWeek = daysOfWeek.first()
    )

    val coroutineScope = rememberCoroutineScope()
    val visibleMonth = rememberFirstCompletelyVisibleMonth(state)

    // Notify parent when month changes
    LaunchedEffect(visibleMonth.yearMonth) {
        onMonthChanged(visibleMonth.yearMonth)
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = CardBackground,
        shadowElevation = 2.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // Month Header with Hijri date
            HijriCalendarMonthHeader(
                currentMonth = visibleMonth.yearMonth,
                onPreviousMonth = {
                    coroutineScope.launch {
                        state.animateScrollToMonth(visibleMonth.yearMonth.minusMonths(1))
                    }
                },
                onNextMonth = {
                    coroutineScope.launch {
                        state.animateScrollToMonth(visibleMonth.yearMonth.plusMonths(1))
                    }
                }
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Days of Week Header
            DaysOfWeekHeader(daysOfWeek = daysOfWeek)

            Spacer(modifier = Modifier.height(12.dp))

            // Calendar Grid
            HorizontalCalendar(
                state = state,
                dayContent = { day ->
                    val meetingsOnDay = meetings.filter { it.date == day.date }

                    CalendarDay(
                        day = day,
                        isSelected = selectedDate == day.date,
                        isToday = day.date == LocalDate.now(),
                        hasMeetings = meetingsOnDay.isNotEmpty(),
                        meetingCount = meetingsOnDay.size,
                        onClick = {
                            if (day.position == DayPosition.MonthDate) {
                                onDateSelected(day.date)
                            }
                        }
                    )
                },
                monthHeader = { },
                monthBody = { _, content ->
                    Box {
                        content()
                    }
                }
            )
        }
    }
}

@Composable
fun CalendarMonthHeader(
    currentMonth: YearMonth,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit
) {

    val extraColors = LocalExtraColors.current

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {

        Column(
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = "${getArabicMonth(currentMonth.monthValue)} ${currentMonth.year}",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = extraColors.blueColor
            )
            Text(
                text = getHijriDate(currentMonth),
                fontSize = 13.sp,
                color = TextSecondary
            )
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            IconButton(
                onClick = onPreviousMonth,
                modifier = Modifier
                    .size(40.dp)
                    .background(BackgroundGray, CircleShape)
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                    contentDescription = "Previous",
                    tint = extraColors.maroonColor
                )
            }

            IconButton(
                onClick = onNextMonth,
                modifier = Modifier
                    .size(40.dp)
                    .background(BackgroundGray, CircleShape)
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = "Next",
                    tint = extraColors.maroonColor
                )
            }
        }

    }
}

@Composable
fun HijriCalendarMonthHeader(
    currentMonth: YearMonth,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit
) {

    val extraColors = LocalExtraColors.current

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {

        Column(
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = getHijriMonthName(currentMonth),
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = extraColors.blueColor
            )
            Text(
                text = "${getArabicMonth(currentMonth.monthValue)} ${currentMonth.year}",
                fontSize = 13.sp,
                color = TextSecondary
            )
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            IconButton(
                onClick = onPreviousMonth,
                modifier = Modifier
                    .size(40.dp)
                    . background(BackgroundGray, CircleShape)
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                    contentDescription = "Previous",
                    tint = extraColors.maroonColor
                )
            }
            IconButton(
                onClick = onNextMonth,
                modifier = Modifier
                    . size(40.dp)
                    .background(BackgroundGray, CircleShape)
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = "Next",
                    tint = extraColors.maroonColor
                )
            }
        }

    }
}

@Composable
fun DaysOfWeekHeader(daysOfWeek: List<DayOfWeek>) {
    val arabicDays = mapOf(
        DayOfWeek.SATURDAY to "السبت",
        DayOfWeek.SUNDAY to "الأحد",
        DayOfWeek.MONDAY to "الاثنين",
        DayOfWeek.TUESDAY to "الثلاثاء",
        DayOfWeek.WEDNESDAY to "الأربعاء",
        DayOfWeek.THURSDAY to "الخميس",
        DayOfWeek.FRIDAY to "الجمعة"
    )

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        daysOfWeek.forEach { day ->
            Text(
                text = arabicDays[day] ?: "",
                fontSize = 12.sp,
                color = TextSecondary,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun CalendarDay(
    day: CalendarDay,
    isSelected:  Boolean,
    isToday:  Boolean,
    hasMeetings: Boolean,
    meetingCount: Int,
    onClick: () -> Unit
) {

    val extraColors = LocalExtraColors.current

    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.05f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
    )

    val isCurrentMonth = day.position == DayPosition.MonthDate

    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .padding(2.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            },
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(12.dp))
                .background(
                    when {
                        isSelected -> extraColors.maroonColor
                        isToday -> Color.Transparent
                        else -> Color.Transparent
                    }
                )
                .then(
                    if (isToday && !isSelected) {
                        Modifier.border(
                            width = 2.dp,
                            color = extraColors.maroonColor,
                            shape = RoundedCornerShape(12.dp)
                        )
                    } else Modifier
                )
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = ripple(bounded = true),
                    onClick = onClick
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = day.date.dayOfMonth.toString(),
                    fontSize = 16.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    color = when {
                        isSelected -> Color.White
                        !isCurrentMonth -> TextSecondary.copy(alpha = 0.3f)
                        else -> TextPrimary
                    }
                )

                if (hasMeetings && isCurrentMonth) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(2.dp),
                        modifier = Modifier.padding(top = 4.dp)
                    ) {
                        repeat(minOf(meetingCount, 3)) {
                            Box(
                                modifier = Modifier
                                    .size(4.dp)
                                    .background(
                                        if (isSelected) Color.White else extraColors.maroonColor,
                                        CircleShape
                                    )
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun rememberFirstCompletelyVisibleMonth(state: CalendarState): CalendarMonth {
    val visibleMonth = remember(state) { mutableStateOf(state.firstVisibleMonth) }
    LaunchedEffect(state) {
        snapshotFlow { state.layoutInfo.visibleMonthsInfo.firstOrNull() }
            .collect { month ->
                month?.let { visibleMonth.value = it.month }
            }
    }
    return visibleMonth. value
}

@Composable
fun MeetingSectionHeader(
    title: String,
    subtitle: String,
    icon: ImageVector
) {

    val extraColors = LocalExtraColors.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = extraColors.maroonColor,
            modifier = Modifier.size(24.dp)
        )

        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = title,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = extraColors.blueColor
            )
            Text(
                text = subtitle,
                fontSize = 13.sp,
                color = TextSecondary
            )
        }

    }
}

@Composable
fun MeetingItem(
    modifier: Modifier = Modifier,
    meeting: Meeting,
    isExpanded: Boolean = false,
    onExpandToggle: () -> Unit = {},
    onMeetingClick: (String) -> Unit = {}
) {

    val extraColors = LocalExtraColors.current

    val rotation by animateFloatAsState(
        targetValue = if (isExpanded) 180f else 0f,
        animationSpec = tween(300)
    )

    // Determine color based on priority
    val priorityColor = when (meeting.priority.lowercase()) {
        "عالية", "ضرورية", "عاجلة", "urgent", "high" -> extraColors.maroonColor
        "متوسطة", "medium", "normal" -> extraColors.blueColor
        else -> extraColors.textGray
    }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .animateContentSize(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            )
            .clickable { onMeetingClick(meeting.id) },
        shape = RoundedCornerShape(20.dp),
        color = CardBackground,
        shadowElevation = 3.dp,
        tonalElevation = 0.dp
    ) {
        Row (
            modifier = modifier.fillMaxWidth()
                .height(150.dp),
        ){
            // Colored side indicator (right side for RTL)
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(10.dp)
                    .background(
                        color = priorityColor,
                        shape = RoundedCornerShape(topEnd = 20.dp, bottomEnd = 20.dp)
                    )
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 20.dp, end = 26.dp, top = 20.dp, bottom = 20.dp)
            ) {
                // Meeting Title
                Text(
                    text = meeting.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = extraColors.blueColor,
                    textAlign = TextAlign.Start,
                    modifier = Modifier.fillMaxWidth(),
                    lineHeight = 24.sp
                )

                Spacer(modifier = Modifier. height(12.dp))

                // Time Row
                Row(
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {

                    Icon(
                        Icons.Default.Schedule,
                        contentDescription = null,
                        tint = TextSecondary,
                        modifier = Modifier. size(16.dp)
                    )
                    Spacer(modifier = Modifier. width(6.dp))
                    Text(
                        text = meeting.time,
                        fontSize = 13.sp,
                        color = TextSecondary,
                        fontWeight = FontWeight.Normal
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Status Badges Row
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.Start),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Priority Badge
                    PriorityBadge(
                        text = meeting.priority,
                        textColor = priorityColor
                    )

                    // Attendance Badges (reversed order for RTL)
                    meeting.attendanceStatus.entries.sortedByDescending {
                        when(it.key) {
                            AttendanceStatus. CONFIRMED -> 3
                            AttendanceStatus.DECLINED -> 1
                        }
                    }.forEach { (status, count) ->
                        if (count > 0) {
                            AttendanceBadgeCompact(
                                count = count,
                                status = status
                            )
                        }
                    }
                }

                // Expanded Content (if needed)
                AnimatedVisibility(
                    visible = isExpanded,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    Column(
                        modifier = Modifier.padding(top = 16.dp)
                    ) {
                        HorizontalDivider(
                            color = Color.LightGray.copy(alpha = 0.3f),
                            thickness = 1.dp,
                            modifier = Modifier.padding(vertical = 12.dp)
                        )

                        // Additional meeting details
                        MeetingDetailRowCompact(
                            icon = Icons.Default.LocationOn,
                            text = meeting.location
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        MeetingDetailRowCompact(
                            icon = Icons.Default.Description,
                            text = meeting.description
                        )

                        if (meeting.attendees.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = "المشاركون",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = extraColors.maroonColor,
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Start
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            meeting.attendees.forEach { attendee ->
                                Text(
                                    text = "• $attendee",
                                    fontSize = 13.sp,
                                    color = TextPrimary.copy(alpha = 0.8f),
                                    modifier = Modifier.fillMaxWidth(),
                                    textAlign = TextAlign.Start
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PriorityBadge(
    text: String,
    modifier: Modifier = Modifier,
    textColor: Color
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = textColor.copy(alpha = 0.4f),
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement. Center,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
        ) {
            // Exclamation marks
            Text(
                text = "!!! ",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = textColor,
                letterSpacing = 0.5.sp
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = text,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = textColor
            )
        }
    }
}

@Composable
fun AttendanceBadgeCompact(
    count: Int,
    status: AttendanceStatus,
    modifier: Modifier = Modifier
) {
    val (icon, backgroundColor, iconColor) = when (status) {
        AttendanceStatus.CONFIRMED -> Triple(
            Icons.Default.Check,
            Color(0xFFE8F5E9),
            GreenBadge
        )
        AttendanceStatus. DECLINED -> Triple(
            Icons.Default.Close,
            Color(0xFFFFEBEE),
            RedBadge
        )
    }

    Surface(
        shape = CircleShape,
        color = backgroundColor,
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(18.dp)
                    .background(iconColor, CircleShape),
                contentAlignment = Alignment. Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier. size(12.dp)
                )
            }
            Spacer(modifier = Modifier. width(6.dp))
            Text(
                text = count.toString(),
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
        }
    }
}

@Composable
fun MeetingDetailRowCompact(
    icon: ImageVector,
    text:  String
) {
    Row(
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = text,
            fontSize = 13.sp,
            color = TextPrimary.copy(alpha = 0.8f),
            textAlign = TextAlign.End,
            modifier = Modifier.weight(1f)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = TextSecondary,
            modifier = Modifier.size(16.dp)
        )
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun MeetingCard(
    meeting: Meeting,
    isExpanded: Boolean,
    onExpandToggle: () -> Unit
) {

    val extraColors = LocalExtraColors.current

    val rotation by animateFloatAsState(
        targetValue = if (isExpanded) 180f else 0f,
        animationSpec = tween(300)
    )

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            ),
        shape = RoundedCornerShape(20.dp),
        color = extraColors.maroonColor,
        shadowElevation = 4.dp
    ) {
        Box {
            // Colored side indicator
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(6.dp)
                    .align(Alignment.CenterStart)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFFE74C3C),
                                Color(0xFFC0392B)
                            )
                        )
                    )
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {

                    // Meeting Title
                    Text(
                        text = meeting.title,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        textAlign = TextAlign.Start,
                        modifier = Modifier.weight(1f)
                    )

                    // Expand/Collapse Button
                    IconButton(
                        onClick = onExpandToggle,
                        modifier = Modifier
                            .size(32.dp)
                            .graphicsLayer { rotationZ = rotation }
                    ) {
                        Icon(
                            Icons.Default.KeyboardArrowDown,
                            contentDescription = if (isExpanded) "Collapse" else "Expand",
                            tint = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Time
                Row(
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {

                    Icon(
                        Icons.Default.Schedule,
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.9f),
                        modifier = Modifier.size(16.dp)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = meeting.time,
                        fontSize = 14.sp,
                        color = Color.White.copy(alpha = 0.9f)
                    )

                }

                Spacer(modifier = Modifier.height(12.dp))

                // Status Badges
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.Start),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Priority Badge
                    StatusBadge(
                        text = meeting.priority,
                        backgroundColor = Color.White.copy(alpha = 0.2f),
                        textColor = Color.White
                    )

                    // Attendance Count
                    meeting.attendanceStatus.forEach { (status, count) ->
                        AttendanceBadge(
                            count = count,
                            status = status
                        )
                    }
                }

                // Expanded Content
                AnimatedVisibility(
                    visible = isExpanded,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    Column(
                        modifier = Modifier.padding(top = 16.dp)
                    ) {
                        HorizontalDivider(
                            color = Color.White.copy(alpha = 0.2f),
                            thickness = 1.dp,
                            modifier = Modifier.padding(vertical = 12.dp)
                        )

                        // Additional meeting details
                        MeetingDetailRow(
                            icon = Icons.Default.LocationOn,
                            text = meeting.location
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        MeetingDetailRow(
                            icon = Icons.Default.Description,
                            text = meeting.description
                        )

                        if (meeting.attendees.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = "المشاركون",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.End
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            meeting.attendees.forEach { attendee ->
                                Text(
                                    text = "• $attendee",
                                    fontSize = 13.sp,
                                    color = Color.White.copy(alpha = 0.9f),
                                    modifier = Modifier. fillMaxWidth(),
                                    textAlign = TextAlign.End
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MeetingDetailRow(
    icon: ImageVector,
    text: String
) {
    Row(
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = text,
            fontSize = 13.sp,
            color = Color.White.copy(alpha = 0.9f),
            textAlign = TextAlign.Start,
            modifier = Modifier.weight(1f)
        )
        Spacer(modifier = Modifier. width(8.dp))
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color.White.copy(alpha = 0.7f),
            modifier = Modifier.size(16.dp)
        )
    }
}

@Composable
fun StatusBadge(
    text: String,
    backgroundColor: Color,
    textColor: Color
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = backgroundColor
    ) {
        Text(
            text = text,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = textColor,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        )
    }
}

@Composable
fun AttendanceBadge(
    count: Int,
    status: AttendanceStatus
) {
    val (icon, backgroundColor) = when (status) {
        AttendanceStatus.CONFIRMED -> Icons.Default.Check to GreenBadge
        AttendanceStatus. DECLINED -> Icons.Default.Close to RedBadge
    }

    Surface(
        shape = RoundedCornerShape(20.dp),
        color = backgroundColor. copy(alpha = 0.9f)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement. spacedBy(4.dp),
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(14.dp)
            )
            Text(
                text = count.toString(),
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}

@Composable
fun EmptyMeetingsCard() {

    val extraColors = LocalExtraColors.current

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 32.dp),
        shape = RoundedCornerShape(20.dp),
        color = CardBackground,
        shadowElevation = 2.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(40.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(BackgroundGray, CircleShape),
                contentAlignment = Alignment. Center
            ) {
                Icon(
                    Icons.Default.EventBusy,
                    contentDescription = null,
                    tint = extraColors.maroonColor,
                    modifier = Modifier.size(40.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "لا توجد اجتماعات",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "لا توجد اجتماعات مجدولة في هذا اليوم",
                fontSize = 14.sp,
                color = TextSecondary,
                textAlign = TextAlign. Center
            )
        }
    }
}

// Data Models
data class Meeting(
    val id: String,
    val title: String,
    val date: LocalDate,
    val time: String,
    val location: String,
    val description:  String,
    val priority: String,
    val isOrganizer: Boolean,
    val attendanceStatus: Map<AttendanceStatus, Int>,
    val attendees: List<String> = emptyList()
)

// Helper Functions
fun daysOfWeekFromLocale(): List<DayOfWeek> {
    return listOf(
        DayOfWeek.SUNDAY,
        DayOfWeek.MONDAY,
        DayOfWeek.TUESDAY,
        DayOfWeek. WEDNESDAY,
        DayOfWeek.THURSDAY,
        DayOfWeek.FRIDAY,
        DayOfWeek. SATURDAY
    )
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

fun getHijriDate(yearMonth: YearMonth): String {
    // Simple conversion - you can use UmmalquraCalendar for precise conversion
    return "رجب 1447 هـ"
}

fun getHijriMonthName(yearMonth: YearMonth): String {
    // You can implement proper Hijri conversion using UmmalquraCalendar
    val hijriMonths = listOf(
        "محرم", "صفر", "ربيع الأول", "ربيع الآخر", "جمادى الأولى", "جمادى الآخرة",
        "رجب", "شعبان", "رمضان", "شوال", "ذو القعدة", "ذو الحجة"
    )
    return "${hijriMonths[6]} 1447" // Example
}

fun getMockMeetings(): List<Meeting> {
    val today = LocalDate.now()
    return listOf(
        Meeting(
            id = "1",
            title = "التنسيق الأمني لبطولة كأس آسيا 2025",
            date = today.withDayOfMonth(5),
            time = "11:00 صباحاً",
            location = "مركز العمليات الأمنية",
            description = "اجتماع تنسيقي للترتيبات الأمنية",
            priority = "عالية",
            isOrganizer = true,
            attendanceStatus = mapOf(
                AttendanceStatus.CONFIRMED to 3,
                AttendanceStatus. DECLINED to 0
            ),
            attendees = listOf("خالد السعيد", "سارة أحمد")
        ),
        Meeting(
            id = "2",
            title = "مراجعة الميزانية العامة للدولة 2025",
            date = today.withDayOfMonth(5),
            time = "2:00 مساءً",
            location = "وزارة المالية",
            description = "مراجعة شاملة للميزانية السنوية",
            priority = "عالية",
            isOrganizer = false,
            attendanceStatus = mapOf(
                AttendanceStatus.CONFIRMED to 0,
                AttendanceStatus.DECLINED to 0
            )
        ),
        Meeting(
            id = "3",
            title = "متابعة مشاريع البنية التحتية للطرق",
            date = today. withDayOfMonth(5),
            time = "4:30 مساءً",
            location = "وزارة النقل",
            description = "متابعة تقدم المشاريع الجارية",
            priority = "متوسطة",
            isOrganizer = false,
            attendanceStatus = mapOf(
                AttendanceStatus.CONFIRMED to 0,
                AttendanceStatus. DECLINED to 0
            )
        ),
        Meeting(
            id = "4",
            title = "تطوير المناهج التعليمية للعام الدراسي القادم",
            date = today.withDayOfMonth(6),
            time = "10:00 صباحاً",
            location = "وزارة التعليم",
            description = "مناقشة خطة تطوير المناهج",
            priority = "عالية",
            isOrganizer = false,
            attendanceStatus = mapOf(
                AttendanceStatus.CONFIRMED to 0,
                AttendanceStatus.DECLINED to 0
            )
        ),
        Meeting(
            id = "5",
            title = "اجتماع طارئ",
            date = today.withDayOfMonth(9),
            time = "9:00 صباحاً",
            location = "مكتب رئيس الوزراء",
            description = "اجتماع عاجل",
            priority = "عالية",
            isOrganizer = true,
            attendanceStatus = mapOf(
                AttendanceStatus.CONFIRMED to 1,
                AttendanceStatus.DECLINED to 0
            )
        ),
        Meeting(
            id = "6",
            title = "مراجعة أسبوعية",
            date = today.withDayOfMonth(10),
            time = "3:00 مساءً",
            location = "قاعة الاجتماعات",
            description = "مراجعة أسبوعية للمشاريع",
            priority = "متوسطة",
            isOrganizer = true,
            attendanceStatus = mapOf(
                AttendanceStatus.CONFIRMED to 1,
                AttendanceStatus.DECLINED to 0
            )
        )
    )
}

// Preview function commented out due to missing dependencies
// @Preview(showBackground = true)
// @Composable
// fun CalenderPreview(){
//     AppTheme {
//         CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
//             CalendarScreen(navController = rememberNavController())
//         }
//     }
// }
