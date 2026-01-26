package com.informatique.electronicmeetingsplatform.ui.screens.meetings

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import com.informatique.electronicmeetingsplatform.data.model.meeting.allMeeting.Attendee
import com.informatique.electronicmeetingsplatform.data.model.meeting.allMeeting.Meeting
import com.informatique.electronicmeetingsplatform.ui.components.MeetingCard
import com.informatique.electronicmeetingsplatform.ui.theme.AppTheme
import com.informatique.electronicmeetingsplatform.ui.theme.LocalExtraColors
import com.informatique.electronicmeetingsplatform.ui.viewModel.MeetingDetailState
import com.informatique.electronicmeetingsplatform.ui.viewModel.MeetingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MeetingDetailScreen(viewModel: MeetingsViewModel, navController: NavController, meetingId: String) {

    val context = LocalContext.current
    val extraColors = LocalExtraColors.current

    val meetingDetailState by viewModel.meetingDetailState.collectAsStateWithLifecycle()

    val pendingCalendarAdd = remember { mutableStateOf<Meeting?>(null) }

    val calendarPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val readGranted = permissions[Manifest.permission.READ_CALENDAR] == true
        val writeGranted = permissions[Manifest.permission.WRITE_CALENDAR] == true
        if (readGranted && writeGranted) {
            pendingCalendarAdd.value?.let { detail ->
                viewModel.addEventToCalendar(detail)
                pendingCalendarAdd.value = null
            }
        }
    }

    LaunchedEffect(meetingId) {
        if (!meetingId.isEmpty())
            viewModel.getMeetingById(meetingId = meetingId.toInt())
    }

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
                verticalAlignment = Alignment.CenterVertically
            ){
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back"
                    )
                }

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(vertical = 16.dp),
                    horizontalAlignment = Alignment.Start
                ){
                    Text(
                        text = "تفاصيل الاجتماع",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = extraColors.blueColor
                    )

                    Text(
                        text = "عرض تفاصيل الاجتماع والحضور",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }

            // Content
            if (meetingDetailState is MeetingDetailState.Loading){
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = extraColors.maroonColor
                    )
                }
            }
            else if (meetingDetailState is MeetingDetailState.Success){
                val detail = (meetingDetailState as MeetingDetailState.Success).data

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Meeting Type Selection Card
                    item {
                        MeetingTypeCard(detail)
                    }

                    // Meeting Info Card
                    item {
                        MeetingInfoCard(detail)
                    }

                    // Attendees Card
                    item {
                        AttendeesCard(meeting = detail)
                    }

                    // Notes Card
                    item {
                        NotesCard(detail)
                    }

                    // Attachments Card (shown in second image)
                    item {
                        AttachmentsCard(viewModel, detail)
                    }

                    item {
                        Spacer(Modifier.height(8.dp))
                    }
                }

                // Accept or Refuse buttons
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Add to calender btn
                    if (detail.isOrganizer == false && detail.myStatus == "Accepted") {
                        Button(
                            modifier = Modifier
                                .weight(1f)
                                .height(56.dp)
                                .padding(16.dp),
                            onClick = {
                                val hasReadPermission = ContextCompat.checkSelfPermission(
                                    context, Manifest.permission.READ_CALENDAR
                                ) == PackageManager.PERMISSION_GRANTED
                                val hasWritePermission = ContextCompat.checkSelfPermission(
                                    context, Manifest.permission.WRITE_CALENDAR
                                ) == PackageManager.PERMISSION_GRANTED

                                if (hasReadPermission && hasWritePermission) {
                                    viewModel.addEventToCalendar(detail)
                                } else {
                                    pendingCalendarAdd.value = detail
                                    calendarPermissionLauncher.launch(
                                        arrayOf(
                                            Manifest.permission.READ_CALENDAR,
                                            Manifest.permission.WRITE_CALENDAR
                                        )
                                    )
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = extraColors.maroonColor
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(
                                Icons.Default.DateRange,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text("إضافة للتقويم", fontSize = 16.sp, fontWeight = FontWeight.Medium)
                        }
                    }

                    if (detail.isOrganizer == false && detail.myStatus == "Pending") {
                        Button(
                            onClick = {
                                viewModel.respondMeeting(
                                    meetingId = meetingId.toInt(),
                                    response = "accept",
                                    reasonId = 0,
                                    otherReason = ""
                                )
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(56.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = extraColors.success,
                            )
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ){
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = Color.White
                                )
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    text = "تأكيد الحضور",
                                    color = Color.White,
                                    modifier = Modifier.padding(vertical = 8.dp)
                                )
                            }
                        }
                    }

                    if (detail.isOrganizer == false && detail.myStatus == "Pending") {
                        Button(
                            onClick = {
                                viewModel.respondMeeting(
                                    meetingId = meetingId.toInt(),
                                    response = "apology",
                                    reasonId = 0,
                                    otherReason = ""
                                )
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(56.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = extraColors.maroonColor
                            )
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = null,
                                    tint = Color.White
                                )
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    text = "اعتذار",
                                    color = Color.White,
                                    modifier = Modifier.padding(vertical = 8.dp)
                                )
                            }
                        }
                    }

                    if (detail.isOrganizer == false && (detail.myStatus == "Refused"
                                || (detail.myStatus == "Accepted" && detail.canApologyAfterAccept == true))) {
                        Button(
                            onClick = {
                                viewModel.respondMeeting(
                                    meetingId = meetingId.toInt(),
                                    response = "apology",
                                    reasonId = 0,
                                    otherReason = ""
                                )
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(56.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = extraColors.maroonColor
                            )
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = null,
                                    tint = Color.White
                                )
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    text = "تعديل الرد",
                                    color = Color.White,
                                    modifier = Modifier.padding(vertical = 8.dp)
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
fun MeetingTypeCard(meeting: Meeting) {

    val extraColors = LocalExtraColors.current

    val colors = listOf(
        extraColors.blueColor, // Green
        extraColors.lightDrab, // Amber
        extraColors.maroonColor, // Orange
        extraColors.error  // Red
    )

    val colorIndex = (meeting.priorityId - 1).coerceIn(0, 3)
    val contentColor = colors[colorIndex]

    MeetingCard {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                meeting.topic,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = extraColors.blueColor
            )

            Spacer(Modifier.height(12.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                FilterChip(
                    selected = false,
                    onClick = { },
                    label = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("!".repeat(meeting.priorityId), fontSize = 14.sp)

                            Spacer(Modifier.width(4.dp))

                            Text(meeting.priorityName, fontSize = 14.sp)
                        }
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        containerColor = contentColor.copy(alpha = 0.2f),
                        labelColor = contentColor
                    ),
                    border = null
                )

                Spacer(Modifier.weight(1f))
            }
        }
    }
}

@Composable
fun MeetingInfoCard(meeting: Meeting) {
    MeetingCard {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.Start
        ) {
            InfoRow("التاريخ", meeting.detailedStartDate)
            Spacer(Modifier.height(8.dp))
            InfoRow("الوقت", "${meeting.startTime} - ${meeting.endTime}")
            Spacer(Modifier.height(8.dp))
            InfoRow("الموقع", meeting.location)
        }
    }
}

@Composable
fun InfoRow(label: String, value: String) {

    val extraColors = LocalExtraColors.current

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            label,
            fontSize = 12.sp,
            color = extraColors.textGray
        )
        Spacer(Modifier.height(4.dp))
        Text(
            value,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = extraColors.blueColor
        )
    }
}

@Composable
fun AttendeesCard(meeting: Meeting) {

    val extraColors = LocalExtraColors.current

    MeetingCard {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        Icons.Default.People,
                        contentDescription = null,
                        tint = extraColors.maroonColor,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        "قائمة النصاب",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = extraColors.blueColor
                    )
                }

                Box(
                    modifier = Modifier
                        .size(35.dp)
                        .clip(CircleShape)
                        .background(extraColors.blueColor),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "${meeting.attendees.size}",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StatusBadge(
                    "${meeting.acceptedCount}",
                    extraColors.success,
                    Icons.Default.Check
                )
                StatusBadge(
                    "${meeting.pendingCount}",
                    extraColors.lightDrab,
                    Icons.Default.Info
                )
                StatusBadge(
                    "${meeting.refusedCount}",
                    extraColors.maroonColor,
                    Icons.Default.Close
                )
            }

            Spacer(Modifier.height(16.dp))

            meeting.attendees.forEach { attendee ->
                AttendeeItem(attendee = attendee)
                Spacer(Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun RowScope.StatusBadge(count: String, textColor: Color, icon: ImageVector) {
    Surface(
        modifier = Modifier.weight(1f),
        shape = RoundedCornerShape(12.dp),
        color = textColor.copy(alpha = 0.2f)
    ) {
        Row(
            modifier = Modifier.padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = textColor,
                modifier = Modifier.size(18.dp)
            )
            Spacer(Modifier.width(6.dp))
            Text(
                count,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = textColor
            )
        }
    }
}

@Composable
fun AttendeeItem(attendee: Attendee) {

    val extraColors = LocalExtraColors.current

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                extraColors.background,
                RoundedCornerShape(12.dp)
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(
                            when (attendee.status) {
                                "Accepted" -> extraColors.success.copy(alpha = 0.2f)
                                "Refused" -> extraColors.maroonColor.copy(alpha = 0.2f)
                                else -> extraColors.lightDrab.copy(alpha = 0.2f)
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = attendee.fullName?.split(" ")?.let {
                            parts -> val firstInitial = parts.getOrNull(0)?.firstOrNull()?.toString()
                            ?: ""
                            val lastInitial = parts.getOrNull(1)?.firstOrNull()?.toString()
                            ?: ""
                            "$firstInitial$lastInitial"
                        } ?: "",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = when (attendee.status) {
                            "Accepted" -> extraColors.success
                            "Refused" -> extraColors.maroonColor
                            else -> extraColors.lightDrab
                        }
                    )
                }

                Column(
                    horizontalAlignment = Alignment.Start
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            attendee.fullName ?: "",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = extraColors.blueColor
                        )

                        if (attendee.isOrganizer == true) {
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = extraColors.lightDrab
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        Icons.Default.Star,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(10.dp)
                                    )
                                    Text(
                                        "منظم",
                                        color = Color.White,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }

                    Text(
                        attendee.jobName ?: "",
                        fontSize = 10.sp,
                        color = extraColors.textGray
                    )
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            when (attendee.status) {
                                "Accepted" -> extraColors.success.copy(alpha = 0.2f)
                                "Refused" -> extraColors.maroonColor.copy(alpha = 0.2f)
                                else -> extraColors.lightDrab.copy(alpha = 0.2f)
                            }
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        when (attendee.status) {
                            "Accepted" -> "مؤكد"
                            "Refused" -> "معتذر"
                            else -> "قيد الانتظار"
                        },
                        color = when (attendee.status) {
                            "Accepted" -> extraColors.success
                            "Refused" -> extraColors.maroonColor
                            else -> extraColors.lightDrab
                        },
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
fun NotesCard(meeting: Meeting) {

    val extraColors = LocalExtraColors.current

    MeetingCard {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                "ملاحظات الاجتماع",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = extraColors.blueColor
            )

            Spacer(Modifier.height(12.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .background(
                        color = extraColors.background,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .padding(12.dp),
                contentAlignment = Alignment.TopStart
            ) {
                Text(
                    meeting.notes,
                    fontSize = 14.sp,
                    color = extraColors.blueColor
                )
            }
        }
    }
}

@Composable
fun AttachmentsCard(viewModel: MeetingsViewModel, meeting: Meeting) {

    val extraColors = LocalExtraColors.current

    MeetingCard {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "المرفقات",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = extraColors.blueColor
                )

                Box(
                    modifier = Modifier
                        .size(35.dp)
                        .clip(CircleShape)
                        .background(extraColors.blueColor),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "${meeting.attachments.size}",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            meeting.attachments.forEach { attachment ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = extraColors.background,
                            shape = RoundedCornerShape(12.dp)
                        )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 10.dp, horizontal = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Column(
                            modifier = Modifier.weight(0.7f),
                            horizontalAlignment = Alignment.Start
                        ) {
                            Text(
                                attachment.filePath,
                                fontSize = 12.sp,
                                color = extraColors.blueColor,
                                fontWeight = FontWeight.Medium,
                                maxLines = 2
                            )
                            Text(
                                viewModel.getAttachmentType(attachment.filePath),
                                fontSize = 10.sp,
                                color = extraColors.textGray
                            )
                        }

                        Row(
                            modifier = Modifier.weight(0.3f),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.End
                        ) {

                            Surface(
                                onClick = {},
                                shape = CircleShape,
                                color = extraColors.blueColor.copy(alpha = 0.2f),
                                modifier = Modifier.size(35.dp)
                            ) {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier.fillMaxSize()
                                ) {
                                    Icon(
                                        Icons.Default.RemoveRedEye,
                                        contentDescription = "عرض",
                                        tint = extraColors.blueColor,
                                        modifier = Modifier.size(15.dp)
                                    )
                                }
                            }

                            Spacer(Modifier.width(8.dp))

                            Surface(
                                onClick = {},
                                shape = CircleShape,
                                color = extraColors.maroonColor,
                                modifier = Modifier.size(35.dp)
                            ) {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier.fillMaxSize()
                                ) {
                                    Icon(
                                        Icons.Default.ArrowDownward,
                                        contentDescription = "تحميل",
                                        tint = Color.White,
                                        modifier = Modifier.size(15.dp)
                                    )
                                }
                            }
                        }

                    }
                }

                Spacer(Modifier.height(8.dp))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MeetingDetailPreview(){
    AppTheme {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            MeetingDetailScreen(
                viewModel = hiltViewModel(),
                navController = rememberNavController(),
                meetingId = "56"
            )
        }
    }
}