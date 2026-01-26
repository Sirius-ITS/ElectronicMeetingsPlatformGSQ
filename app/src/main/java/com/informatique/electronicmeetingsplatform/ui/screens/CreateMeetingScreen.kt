package com.informatique.electronicmeetingsplatform.ui.screens

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.informatique.electronicmeetingsplatform.data.model.meeting.attachments.AttachmentResponse
import com.informatique.electronicmeetingsplatform.data.model.meeting.create.Attendee
import com.informatique.electronicmeetingsplatform.data.model.meeting.priorities.Data as priorityData
import com.informatique.electronicmeetingsplatform.data.model.meeting.type.Data as typeData
import com.informatique.electronicmeetingsplatform.ui.components.DateTimePicker
import com.informatique.electronicmeetingsplatform.ui.components.InviteesBottomSheet
import com.informatique.electronicmeetingsplatform.ui.theme.AppTheme
import com.informatique.electronicmeetingsplatform.ui.theme.LocalExtraColors
import com.informatique.electronicmeetingsplatform.ui.viewModel.AttachmentState
import com.informatique.electronicmeetingsplatform.ui.viewModel.CreateMeetingState
import com.informatique.electronicmeetingsplatform.ui.viewModel.CreateMeetingViewModel
import com.informatique.electronicmeetingsplatform.ui.viewModel.DeleteAttachmentState
import com.informatique.electronicmeetingsplatform.ui.viewModel.InviteeState
import com.informatique.electronicmeetingsplatform.ui.viewModel.PriorityState
import com.informatique.electronicmeetingsplatform.ui.viewModel.TypeState
import com.informatique.electronicmeetingsplatform.util.emailValidator
import java.io.File
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import kotlin.collections.plus

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateMeetingScreen(navController: NavController) {

    val extraColors = LocalExtraColors.current

    val viewModel = hiltViewModel<CreateMeetingViewModel>()
    val meetingTypeState = viewModel.typeState.collectAsStateWithLifecycle()
    val uploadAttachmentState = viewModel.attachmentState.collectAsStateWithLifecycle()
    val deleteAttachmentState = viewModel.deleteAttachmentState.collectAsStateWithLifecycle()
    val createMeetingState = viewModel.createMeetingState.collectAsStateWithLifecycle()

    val meetingTitle by viewModel.topic.collectAsStateWithLifecycle()
    val meetingDuration by viewModel.meetingDuration.collectAsStateWithLifecycle()
    val location by viewModel.location.collectAsStateWithLifecycle()
    val selectedMeetingType by viewModel.meetingTypeId.collectAsStateWithLifecycle()
    val priorityLevel by viewModel.meetingPriorityId.collectAsStateWithLifecycle()
    val isSaveEnabled by viewModel.isSaveEnabled.collectAsStateWithLifecycle()

    var startDate by remember { mutableStateOf<LocalDate>(LocalDate.now()) }
    var endDate by remember { mutableStateOf(startDate) }
    var startTime by remember { mutableStateOf(LocalTime.now()) }
    var endTime by remember { mutableStateOf(LocalTime.now().plusHours(1)) }
    var isRecurring by remember { mutableStateOf(false) }
    var recurringType by remember { mutableStateOf(Recurring.DAILY) }
    var externalAttendees by remember { mutableStateOf<List<Attendee>?>(emptyList()) }
    var uploadedAttachments by remember { mutableStateOf<List<AttachmentResponse>>(emptyList()) }
    var priorityLevelColor by remember { mutableStateOf<Color?>(null) }
    var notes by remember { mutableStateOf("") }
    var showMeetingTypeDropdown by remember { mutableStateOf(false) }
    var showDateTimePicker by remember { mutableStateOf(false) }
    var isStartTime by remember { mutableStateOf(true) }

    var storagePermissionGranted by remember { mutableStateOf(false) }

    // determine storage permission based on Android version
    val storagePermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        Manifest.permission.READ_MEDIA_IMAGES
    } else {
        Manifest.permission.READ_EXTERNAL_STORAGE
    }

    // launcher to pick up an image / file
    val imageLauncher = rememberLauncherForActivityResult(
        // contract = ActivityResultContracts.GetContent() // for images only
        contract = ActivityResultContracts.GetMultipleContents() // for multi-type files
    ) { uris ->
        // selectedImage = uri
        // selectedAttachments = selectedAttachments + uri
        uris.forEach { uri ->
            viewModel.meetingAttachments(
                fileName = File(uri.path!!).name, file = uri)
        }
    }

    // Launcher for storage permission
    val storagePermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        storagePermissionGranted = isGranted
        if (isGranted) {
            imageLauncher.launch("*/*")
        }
    }

    // Observe the uploadAttachmentState
    LaunchedEffect(uploadAttachmentState.value) {
        if (uploadAttachmentState.value is AttachmentState.Success) {
            val newAttachment = (uploadAttachmentState.value as AttachmentState.Success).data
            uploadedAttachments = uploadedAttachments + newAttachment
        }
    }

    // Observe the deleteAttachmentState
    LaunchedEffect(deleteAttachmentState.value) {
        if (deleteAttachmentState.value is DeleteAttachmentState.Success) {
            val fileName = (deleteAttachmentState.value as DeleteAttachmentState.Success).fileName
            val isDeleted = (deleteAttachmentState.value as DeleteAttachmentState.Success).isDeleted

            if (isDeleted)
                uploadedAttachments = uploadedAttachments.filter { it.fileName != fileName }
        }
    }

    // Sync date/time changes to ViewModel
    LaunchedEffect(startDate, startTime) {
        val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'")
        val timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss")
        viewModel.updateDateFrom(LocalDateTime.of(startDate, LocalTime.MIDNIGHT).format(dateFormatter))
        viewModel.updateTimeFrom(startTime.format(timeFormatter))
    }

    LaunchedEffect(endDate, endTime) {
        val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'")
        val timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss")
        viewModel.updateDateTo(LocalDateTime.of(endDate, LocalTime.MIDNIGHT).format(dateFormatter))
        viewModel.updateTimeTo(endTime.format(timeFormatter))
    }


    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(extraColors.background)
                .padding(horizontal = 16.dp)
        ) {
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
                        text = "اجتماع جديد",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = extraColors.blueColor
                    )

                    Text(
                        text = "قم بتنظيم اجتماع ودعوة الحضور",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                // Meeting Title Card
                item {
                    MeetingCard {
                        Column {
                            Text(
                                text = "عنوان الاجتماع",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = extraColors.blueColor
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            OutlinedTextField(
                                value = meetingTitle,
                                onValueChange = { viewModel.updateTopic(it) },
                                placeholder = {
                                    Text(
                                        "ادخل عنوان الاجتماع",
                                        fontSize = 14.sp
                                    )
                                },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    unfocusedContainerColor = extraColors.background,
                                    focusedContainerColor = extraColors.background,
                                    focusedBorderColor = Color.Transparent,
                                    unfocusedBorderColor = Color.Transparent
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Meeting Type Card
                item {
                    MeetingCard {
                        Column {
                            Text(
                                text = "نوع الاجتماع",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp,
                                color = extraColors.blueColor
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Box {
                                OutlinedTextField(
                                    value = selectedMeetingType?.name ?: "",
                                    onValueChange = {},
                                    readOnly = true,
                                    enabled = false,
                                    placeholder = {
                                        Column {
                                            Text(
                                                "اختر نوع الاجتماع",
                                                fontSize = 14.sp
                                            )
                                            Text(
                                                "اضغط للإختيار",
                                                fontSize = 12.sp
                                            )
                                        }
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            showMeetingTypeDropdown = !showMeetingTypeDropdown
                                        },
                                    shape = RoundedCornerShape(12.dp),
                                    trailingIcon = {
                                        if (meetingTypeState.value is TypeState.Loading) {
                                            CircularProgressIndicator(
                                                color = extraColors.maroonColor
                                            )
                                        } else if (meetingTypeState.value is TypeState.Success) {
                                            Icon(
                                                imageVector = if (showMeetingTypeDropdown)
                                                    Icons.Default.KeyboardArrowUp
                                                else
                                                    Icons.Default.KeyboardArrowDown,
                                                contentDescription = null
                                            )
                                        }
                                    },
                                    colors = OutlinedTextFieldDefaults.colors(
                                        disabledContainerColor = extraColors.background,
                                        unfocusedContainerColor = extraColors.background,
                                        focusedContainerColor = extraColors.background,
                                        disabledBorderColor = extraColors.background,
                                        unfocusedBorderColor = Color.Transparent,
                                        focusedBorderColor = extraColors.blueColor
                                    )
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            AnimatedVisibility(
                                visible = showMeetingTypeDropdown,
                                enter = expandVertically(animationSpec = tween(300)) + fadeIn(),
                                exit = shrinkVertically(animationSpec = tween(300)) + fadeOut()
                            ) {
                                MeetingCard(
                                    elevation = 2,
                                ) {
                                    Column(
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        if (meetingTypeState.value is TypeState.Success) {
                                            val types =
                                                (meetingTypeState.value as TypeState.Success).data
                                            types.forEachIndexed { index, type ->
                                                OfficialMeetingToggle(
                                                    type = type,
                                                    selectedType = selectedMeetingType,
                                                    onToggle = { viewModel.updateMeetingTypeId(it) }
                                                )

                                                if (index < types.lastIndex) {
                                                    HorizontalDivider(
                                                        modifier = Modifier.padding(vertical = 4.dp),
                                                        thickness = 1.dp,
                                                        color = extraColors.background
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Time Period Card
                item {
                    MeetingCard {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.DateRange,
                                    contentDescription = null,
                                    tint = extraColors.maroonColor
                                )
                                Text(
                                    text = "الفترة الزمنية",
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 14.sp,
                                    color = extraColors.blueColor
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                DateTimeCard(
                                    modifier = Modifier
                                        .weight(1f)
                                        .then(
                                            if (isStartTime && showDateTimePicker) {
                                                Modifier.border(
                                                    width = 1.dp,
                                                    color = extraColors.maroonColor,
                                                    shape = RoundedCornerShape(12.dp)
                                                )
                                            } else {
                                                Modifier.border(
                                                    width = 0.dp,
                                                    color = Color.Transparent
                                                )
                                            }
                                        ),
                                    label = "يبدأ",
                                    date = startDate,
                                    time = startTime,
                                    onClick = {
                                        isStartTime = true
                                        showDateTimePicker = true
                                    }
                                )

                                DateTimeCard(
                                    modifier = Modifier
                                        .weight(1f)
                                        .then(
                                            if (!isStartTime && showDateTimePicker) {
                                                Modifier.border(
                                                    width = 1.dp,
                                                    color = extraColors.maroonColor,
                                                    shape = RoundedCornerShape(12.dp)
                                                )
                                            } else {
                                                Modifier.border(
                                                    width = 0.dp,
                                                    color = Color.Transparent
                                                )
                                            }
                                        ),
                                    label = "ينتهي",
                                    date = if (!isStartTime && startDate.isAfter(endDate)) startDate else endDate,
                                    time = endTime,
                                    onClick = {
                                        isStartTime = false
                                        showDateTimePicker = true
                                    }
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            AnimatedVisibility(
                                visible = showDateTimePicker,
                                enter = expandVertically(animationSpec = tween(300)) + fadeIn(),
                                exit = shrinkVertically(animationSpec = tween(300)) + fadeOut()
                            ) {
                                DateTimePicker(
                                    selectedDate = if (!isStartTime && startDate.isAfter(endDate)) startDate
                                    else if (isStartTime) startDate else endDate,
                                    selectedTime = if (isStartTime) startTime else endTime,
                                    minDate = if (!isStartTime) startDate else null,
                                    minTime = if (!isStartTime && endDate == startDate) startTime else null,
                                    onDateSelected = {
                                        if (isStartTime) {
                                            startDate = it
                                            // Auto-adjust end date if needed
                                            if (endDate.isBefore(it)) {
                                                endDate = it
                                            }
                                        } else {
                                            endDate = it
                                        }
                                    },
                                    onTimeChanged = {
                                        if (isStartTime) {
                                            startTime = it// Auto-adjust end time if same day
                                            if (endDate == startDate && endTime.isBefore(it)) {
                                                endTime = it.plusHours(1)
                                            }
                                        } else {
                                            endTime = it
                                        }
                                    },
                                    onDismiss = { showDateTimePicker = false }
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            DurationDisplay(duration = meetingDuration)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Recurring Meeting Card
                item {
                    MeetingCard {
                        RecurringMeetingToggle(
                            isEnabled = isRecurring,
                            onToggle = { isRecurring = it },
                            selectedRecurringType = recurringType,
                            onRecurringSelected = { recurringType = it }
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Invitees Card
                item {
                    MeetingCard {
                        InviteesSection(
                            viewModel = viewModel,
                            onConfirm = { invitees -> viewModel.updateAttendees(invitees) }
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Priority Level Card
                item {
                    MeetingCard {
                        PrioritySelector(
                            viewModel = viewModel,
                            selectedPriority = priorityLevel,
                            selectedColor = priorityLevelColor,
                            onPrioritySelected = { priority, color ->
                                viewModel.updateMeetingPriorityId(priority)
                                priorityLevelColor = color
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }

                // External Invitees Card
                item {
                    MeetingCard {
                        ExternalInviteeSection(
                            onExternalInviteeAdd = { externalAttendees = it },
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Attachments Card
                item {
                    MeetingCard {
                        FileManagerSection(
                            viewModel = viewModel,
                            selectedAttachments = uploadedAttachments,
                            onAddNewFile = {
                                if (storagePermissionGranted) {
                                    imageLauncher.launch("*/*")
                                } else {
                                    storagePermissionLauncher.launch(storagePermission)
                                }
                            },
                            onRemoveFile = { fileName ->
                                viewModel.onDeleteAttachment(fileName)
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Additional Details Card
                item {
                    MeetingCard {
                        AdditionalDetailsSection(
                            location = location,
                            onLocationChange = { viewModel.updateLocation(it) },
                            notes = notes,
                            onNotesChange = { notes = it }
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }

            }

            // Bottom Buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    enabled = isSaveEnabled,
                    onClick = {
                        viewModel.createMeeting(
                            attachmentPaths = uploadedAttachments.map { it.fileName },
                            externalAttendees = externalAttendees,
                            isRepeated = isRecurring,
                            repeatRule = recurringType.name,
                            notes = notes
                        )
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isSaveEnabled)
                            extraColors.maroonColor else Color.Gray,
                    )
                ) {
                    when (createMeetingState.value) {
                        is CreateMeetingState.Loading -> {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = Color.White
                            )
                        }

                        is CreateMeetingState.Success -> {
                            Icon(
                                imageVector = Icons.Default.Done,
                                contentDescription = "Save",
                                tint = Color.White
                            )
                        }
                        else -> {
                            Text(
                                text = "حفظ",
                                color = Color.White,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }
                    }
                }

                OutlinedButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = extraColors.maroonColor.copy(alpha = 0.2f)
                    )
                ) {
                    Text(
                        text = "الغاء",
                        color = extraColors.maroonColor,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun MeetingCard(elevation: Int = 0, content: @Composable () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = elevation.dp)
    ) {
        Box(modifier = Modifier.padding(16.dp)) {
            content()
        }
    }
}

@Composable
fun OfficialMeetingToggle(
    type: typeData,
    selectedType: typeData?,
    onToggle: (typeData) -> Unit
) {

    val extraColors = LocalExtraColors.current

    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        RadioButton(
            selected = selectedType == type,
            onClick = {
                onToggle(type)
            },
            colors = RadioButtonDefaults.colors(
                selectedColor = extraColors.maroonColor,
                unselectedColor = extraColors.textGray
            )
        )

        Text(
            modifier = Modifier.weight(1f),
            text = type.name,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = extraColors.blueColor
        )

        if (selectedType == type){
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = extraColors.maroonColor
            )
        }
    }
}

@Composable
fun DateTimeCard(
    modifier: Modifier = Modifier,
    label: String,
    date: LocalDate,
    time: LocalTime,
    onClick: () -> Unit
) {

    val extraColors = LocalExtraColors.current

    Surface(
        modifier = modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        color = extraColors.background
    ) {

        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .background(
                        extraColors.maroonColor.copy(alpha = 0.2f)
                    ),
                contentAlignment = Alignment.Center
            ){
                Icon(
                    imageVector = Icons.Default.DateRange,
                    contentDescription = null,
                    tint = extraColors.maroonColor,
                    modifier = Modifier.size(24.dp)
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = label, fontSize = 14.sp, color = Color.DarkGray)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = date.format(
                        DateTimeFormatter.ofPattern("d يناير yyyy")),
                    fontWeight = FontWeight.SemiBold,
                    color = extraColors.blueColor
                )
                Text(
                    text = time.format(DateTimeFormatter.ofPattern("hh:mm a")),
                    fontSize = 12.sp,
                    color = Color.DarkGray
                )
            }
        }
    }
}

@Composable
fun DurationDisplay(duration: Triple<Int, Int, Int>) {

    val extraColors = LocalExtraColors.current
    val (days, hours, minutes) = duration

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = extraColors.background
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "مدة الاجتماع",
                fontSize = 14.sp,
                color = Color.DarkGray
            )

            Text(
                text = buildString {
                    if (days > 0) append("$days يوم ")
                    if (hours > 0) append("$hours ساعة ")
                    if (minutes > 0) append("$minutes دقيقة")
                }.trim(),
                fontSize = 14.sp,
                color = Color.DarkGray
            )
        }
    }
}

@Composable
fun RecurringMeetingToggle(
    isEnabled: Boolean,
    onToggle: (Boolean) -> Unit,
    selectedRecurringType: Recurring,
    onRecurringSelected: (Recurring) -> Unit
) {

    val extraColors = LocalExtraColors.current

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Column(horizontalAlignment = Alignment.Start) {
                Text(
                    text = "اجتماع متكرر",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = extraColors.blueColor
                )
                Text(
                    text = if (isEnabled) "سيتم تكرار هذا الإجتماع" else "اجتماع لمرة واحدة",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }

            Switch(
                checked = isEnabled,
                onCheckedChange = onToggle,
                colors = SwitchDefaults.colors(
                    uncheckedThumbColor = Color.White,
                    uncheckedTrackColor = Color.LightGray,
                    checkedThumbColor = Color.White,
                    checkedTrackColor = extraColors.maroonColor,
                    uncheckedBorderColor = Color.Transparent
                )
            )
        }

        if (isEnabled)
            Spacer(modifier = Modifier.height(16.dp))

        AnimatedVisibility(
            visible = isEnabled,
            enter = expandVertically(animationSpec = tween(300)) + fadeIn(),
            exit = shrinkVertically(animationSpec = tween(300)) + fadeOut()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                RecurringButton(
                    modifier = Modifier.weight(1f),
                    text = "يوميا",
                    isSelected = selectedRecurringType == Recurring.DAILY,
                    onClick = { onRecurringSelected(Recurring.DAILY) },
                    icon = Icons.Default.WbSunny
                )
                RecurringButton(
                    modifier = Modifier.weight(1f),
                    text = "اسبوعيا",
                    isSelected = selectedRecurringType == Recurring.WEEKLY,
                    onClick = { onRecurringSelected(Recurring.WEEKLY) },
                    icon = Icons.Default.CalendarMonth
                )
                RecurringButton(
                    modifier = Modifier.weight(1f),
                    text = "شهريا",
                    isSelected = selectedRecurringType == Recurring.MONTHLY,
                    onClick = { onRecurringSelected(Recurring.MONTHLY) },
                    icon = Icons.Default.DateRange
                )
            }
        }
    }
}

enum class Recurring {
    DAILY, WEEKLY, MONTHLY
}

@Composable
fun RecurringButton(
    modifier: Modifier,
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    icon: ImageVector
) {

    val extraColors = LocalExtraColors.current

    val backgroundColor = if (isSelected) extraColors.maroonColor.copy(alpha = 0.2f)
        else extraColors.background
    val borderColor = if (isSelected) extraColors.maroonColor else Color.Transparent
    val contentColor = if (isSelected) extraColors.maroonColor else Color.Gray

    Surface(
        onClick = onClick,
        border = BorderStroke(1.dp, borderColor),
        shape = RoundedCornerShape(12.dp),
        color = backgroundColor,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                tint = contentColor,
                contentDescription = text
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = text,
                fontSize = 12.sp,
                color = contentColor
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InviteesSection(
    viewModel: CreateMeetingViewModel,
    onConfirm: (List<Attendee>) -> Unit
) {

    val extraColors = LocalExtraColors.current

    val meetingInviteeState = viewModel.inviteeState.collectAsStateWithLifecycle()

    val sheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false) }

    var selectedInvitees by remember { mutableStateOf<List<Attendee>>(emptyList()) }

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.clickable{
                    viewModel.meetingInvitees()
                    showBottomSheet = true
                },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (meetingInviteeState.value is InviteeState.Loading){
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = extraColors.maroonColor
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.AddCircle,
                        contentDescription = "Add",
                        tint = extraColors.maroonColor,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Text(
                    text = "إضافة",
                    fontWeight = FontWeight.Medium,
                    color = extraColors.maroonColor
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.People,
                    contentDescription = "People",
                    tint = extraColors.maroonColor,
                    modifier = Modifier.size(20.dp)
                )

                Text(
                    text = "المدعوون (${selectedInvitees.size})",
                    color = Color.Gray,
                    fontSize = 14.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (selectedInvitees.isEmpty()){
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = extraColors.background
            ) {
                Column(
                    modifier = Modifier.padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = Color.LightGray
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "لم يتم إضافة مدعوين بعد",
                        color = Color.Gray,
                        fontSize = 14.sp
                    )
                }
            }
        } else {
            // Invitees List
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                selectedInvitees.forEach { invitee ->
                    SelectedInviteeCard(
                        invitee = invitee,
                        onDelete = { person ->
                            selectedInvitees = selectedInvitees.toMutableList().apply {
                                removeIf { it.id == person.id }
                            }
                        }
                    )
                }
            }
        }

        if (showBottomSheet && meetingInviteeState.value is InviteeState.Success) {
            InviteesBottomSheet(
                invitees = (meetingInviteeState.value as InviteeState.Success).data,
                sheetState = sheetState,
                onDismiss = { showBottomSheet = false },
                onConfirm = {
                    onConfirm(it)
                    selectedInvitees = it
                    showBottomSheet = false
                }
            )
        }

    }
}

@Composable
fun SelectedInviteeCard(
    invitee: Attendee,
    onDelete: (Attendee) -> Unit
) {

    val extraColors = LocalExtraColors.current

    Surface(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = extraColors.background
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            // Profile Image with Badge
            Box(
                modifier = Modifier.size(56.dp)
            ) {
                // Profile circle
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(extraColors.maroonColor.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    if (invitee.personalPhotoPath == null) {
                        Text(
                            text = invitee.fullName?.firstOrNull()?.uppercase() ?: "?",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    } else {
                        AsyncImage(
                            model = invitee.personalPhotoPath,
                            contentDescription = "Profile Picture",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Fit
                        )
                    }
                }

//                Box(
//                    modifier = Modifier
//                        .size(24.dp)
//                        .align(Alignment.TopStart)
//                        .offset(x = (-4).dp, y = (-4).dp)
//                        .clip(CircleShape)
//                        .background(extraColors.success)
//                        .border(2.dp, Color.White, CircleShape),
//                    contentAlignment = Alignment.Center
//                ) {
//                    Icon(
//                        imageVector = Icons.Default.Check,
//                        contentDescription = null,
//                        tint = Color.White,
//                        modifier = Modifier.size(14.dp)
//                    )
//                }

            }

            // Text Content
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = invitee.fullName ?: "",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black,
                    lineHeight = 16.sp
                )
                Text(
                    text = invitee.jobDescription ?: "-",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    lineHeight = 18.sp
                )
            }


            Box(
                modifier = Modifier
                    .size(25.dp)
                    .clip(CircleShape)
                    .background(extraColors.error, shape = CircleShape)
                    .clickable {
                        onDelete(invitee)
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Remove,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
fun PrioritySelector(
    viewModel: CreateMeetingViewModel,
    selectedPriority: priorityData?,
    selectedColor: Color?,
    onPrioritySelected: (priorityData, Color) -> Unit
) {

    val extraColors = LocalExtraColors.current

    val meetingPriorityState = viewModel.priorityState.collectAsStateWithLifecycle()

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                text = "مستوى الأهمية",
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                color = extraColors.blueColor
            )

            Surface(
                shape = RoundedCornerShape(20.dp),
                color = selectedColor?.copy(alpha = 0.2f) ?: extraColors.blueColor
            ) {
                Text(
                    text = selectedPriority?.name ?: "",
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    fontSize = 12.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (meetingPriorityState.value is PriorityState.Loading){
            CircularProgressIndicator(
                color = extraColors.blueColor
            )
        } else if (meetingPriorityState.value is PriorityState.Success){
            val priorities = (meetingPriorityState.value as PriorityState.Success).data

            LaunchedEffect(priorities) {
                if (selectedPriority == null && priorities.isNotEmpty()) {
                    onPrioritySelected(priorities[0], extraColors.blueColor)
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = Color.LightGray.copy(alpha = 0.3f),
                        shape = RoundedCornerShape(12.dp)
                    )
            ) {
                priorities.forEachIndexed { index, priority ->
                    PriorityButton(
                        modifier = Modifier
                            .weight(1f)
                            .height(70.dp),
                        priority = priority,
                        isSelected = selectedPriority?.id == priority.id,
                        onClick = { color -> onPrioritySelected(priority, color) },
                        icon = "!".repeat(priority.order),
                        roundedCornerShape = when (index) {
                            0 -> RoundedCornerShape(topStart = 12.dp, bottomStart = 12.dp)
                            priorities.lastIndex -> RoundedCornerShape(topEnd = 12.dp, bottomEnd = 12.dp)
                            else -> RoundedCornerShape(0.dp)
                        }
                    )
                }
            }
        }


        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                text = "عادي",
                fontWeight = FontWeight.SemiBold,
                fontSize = 10.sp,
                color = extraColors.textGray
            )

            Text(
                text = "سري للغاية",
                fontWeight = FontWeight.SemiBold,
                fontSize = 10.sp,
                color = extraColors.textGray
            )
        }
    }
}

@Composable
fun PriorityButton(
    modifier: Modifier,
    priority: priorityData,
    isSelected: Boolean,
    onClick: (Color) -> Unit,
    icon: String,
    roundedCornerShape: RoundedCornerShape
) {
    val extraColors = LocalExtraColors.current

    val colors = listOf(
        extraColors.blueColor, // Green
        extraColors.lightDrab, // Amber
        extraColors.maroonColor, // Orange
        extraColors.error  // Red
    )

    val colorIndex = (priority.order - 1).coerceIn(0, 3)
    val backgroundColor = if (isSelected) colors[colorIndex] else Color.Transparent
    val contentColor = if (isSelected) Color.White else colors[colorIndex]

    Surface(
        onClick = { onClick(colors[colorIndex]) },
        shape = roundedCornerShape,
        color = backgroundColor,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = icon,
                fontSize = if (isSelected) 16.sp else 24.sp,
                color = contentColor,
                fontWeight = FontWeight.Bold
            )

            if (isSelected) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = priority.name,
                    fontSize = 12.sp,
                    color = contentColor
                )
            }
        }
    }
}

@Composable
fun ExternalInviteeSection(
    onExternalInviteeAdd: (List<Attendee>) -> Unit
) {

    val extraColors = LocalExtraColors.current

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }

    var selectedExternalInvitees by remember { mutableStateOf<List<Attendee>>(emptyList()) }

    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Email,
                contentDescription = "External Invitees",
                tint = extraColors.maroonColor,
                modifier = Modifier.size(20.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ){
                Text(
                    text = "المدعوون الخارجيون",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = extraColors.blueColor
                )

                Text(
                    modifier = Modifier.weight(1f),
                    text = "(${selectedExternalInvitees.size})",
                    color = Color.Gray,
                    fontSize = 14.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "الاسم (اختياري)",
            fontSize = 14.sp,
            color = extraColors.blueColor
        )

        Spacer(modifier = Modifier.height(6.dp))

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            placeholder = { Text("أدخل الاسم") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = extraColors.background,
                focusedContainerColor = extraColors.background,
                unfocusedBorderColor = Color.Transparent,
                focusedBorderColor = Color.Transparent
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "البريد الإلكتروني",
            fontSize = 14.sp,
            color = extraColors.blueColor
        )

        Spacer(modifier = Modifier.height(6.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            placeholder = { Text("أدخل البريد الإلكتروني") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = extraColors.background,
                focusedContainerColor = extraColors.background,
                unfocusedBorderColor = Color.Transparent,
                focusedBorderColor = Color.Transparent
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "رقم الهاتف (اختياري)",
            fontSize = 14.sp,
            color = extraColors.blueColor
        )

        Spacer(modifier = Modifier.height(6.dp))

        OutlinedTextField(
            value = phoneNumber,
            onValueChange = { phoneNumber = it },
            placeholder = { Text("أدخل رقم الهاتف") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = extraColors.background,
                focusedContainerColor = extraColors.background,
                unfocusedBorderColor = Color.Transparent,
                focusedBorderColor = Color.Transparent
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        if (email.emailValidator()) {
            Button(
                onClick = {
                    selectedExternalInvitees = selectedExternalInvitees + Attendee(
                        fullName = name,
                        email = email
                    )

                    name = ""
                    email = ""
                    phoneNumber = ""

                    onExternalInviteeAdd(selectedExternalInvitees)
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = extraColors.maroonColor,
                )
            ) {
                Text(
                    text = "إضافة مدعو خارجي",
                    color = Color.White,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ){
            selectedExternalInvitees.forEach { invitee ->
                SelectedInviteeCard(
                    invitee = invitee,
                    onDelete = {
                        selectedExternalInvitees = selectedExternalInvitees.toMutableList().apply {
                            removeIf { it.id == invitee.id }
                        }

                        onExternalInviteeAdd(selectedExternalInvitees)
                    }
                )
            }
        }
    }
}

@Composable
fun FileManagerSection(
    viewModel: CreateMeetingViewModel,
    selectedAttachments: List<AttachmentResponse>,
    onAddNewFile: () -> Unit,
    onRemoveFile: (String) -> Unit
) {

    val extraColors = LocalExtraColors.current

    val uploadAttachmentState = viewModel.attachmentState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(0.35f),
                verticalAlignment = Alignment.CenterVertically
            ){
                Text(
                    modifier = Modifier.weight(1f),
                    text = "المرفقات",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = extraColors.blueColor
                )

                Text(
                    modifier = Modifier.weight(1f),
                    text = "(${selectedAttachments.size})",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = extraColors.textGray
                )
            }

            // Add File Button
            Surface(
                onClick = { onAddNewFile() },
                shape = RoundedCornerShape(24.dp),
                color = extraColors.maroonColor.copy(alpha = 0.2f)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (uploadAttachmentState.value is AttachmentState.Loading){
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            color = extraColors.maroonColor
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .size(18.dp)
                                .clip(CircleShape)
                                .background(extraColors.maroonColor),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Add",
                                tint = Color.White,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }

                    Text(
                        text = "إضافة ملف",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = extraColors.maroonColor
                    )
                }
            }
        }

        if (selectedAttachments.isEmpty()){
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = extraColors.background
            ) {
                Column(
                    modifier = Modifier.padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Attachment,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = Color.LightGray
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "لم يتم إضافة مرفقات بعد",
                        color = Color.Gray,
                        fontSize = 14.sp
                    )
                }
            }
        } else {
            // File List
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                selectedAttachments.forEach { data ->
                    FileCard(data = data, onRemoveFile = onRemoveFile)
                }
            }
        }
    }
}

@Composable
fun FileCard(
    data: AttachmentResponse,
    onRemoveFile: (String) -> Unit
) {

    val extraColors = LocalExtraColors.current

    Surface(
        shape = RoundedCornerShape(20.dp),
        color = extraColors.background,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // File Info
            Column(
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = data.originalFileName,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black,
                    maxLines = 1
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = data.getFormattedFileSize(),
                        fontSize = 12.sp,
                        color = extraColors.textGray
                    )
                    Text(
                        text = "•",
                        fontSize = 12.sp,
                        color = extraColors.textGray
                    )
                    Text(
                        text = data.metadata.mimeMainType,
                        fontSize = 12.sp,
                        color = extraColors.textGray
                    )
                }
            }

            // Action Buttons
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // View Button
                Surface(
                    shape = CircleShape,
                    color = extraColors.blueColor.copy(alpha = 0.2f),
                    modifier = Modifier.size(40.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Icon(
                            imageVector = Icons.Default.RemoveRedEye,
                            contentDescription = "View",
                            tint = extraColors.blueColor,
                            modifier = Modifier.size(25.dp)
                        )
                    }
                }

                // Delete Button
                Surface(
                    onClick = { onRemoveFile(data.fileName) },
                    shape = CircleShape,
                    color = extraColors.error.copy(alpha = 0.2f),
                    modifier = Modifier.size(40.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = extraColors.error,
                            modifier = Modifier.size(25.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AdditionalDetailsSection(
    location: String,
    onLocationChange: (String) -> Unit,
    notes: String,
    onNotesChange: (String) -> Unit
) {

    val extraColors = LocalExtraColors.current

    Column {
        Text(
            text = "تفاصيل إضافية",
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp,
            color = extraColors.blueColor
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = location,
            onValueChange = onLocationChange,
            placeholder = { Text("الموقع") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = extraColors.background,
                focusedContainerColor = extraColors.background,
                unfocusedBorderColor = Color.Transparent,
                focusedBorderColor = Color.Transparent
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = notes,
            onValueChange = onNotesChange,
            placeholder = { Text("ملاحظات") },
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = extraColors.background,
                focusedContainerColor = extraColors.background,
                unfocusedBorderColor = Color.Transparent,
                focusedBorderColor = Color.Transparent
            ),
            maxLines = 5
        )
    }
}


@Preview(showBackground = true)
@Composable
fun CreateMeetingPreview(){
    AppTheme {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            //CreateMeetingScreen(navController = rememberNavController())
            // FileManagerSection()
        }
    }
}