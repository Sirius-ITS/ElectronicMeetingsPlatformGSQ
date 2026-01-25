package com.informatique.electronicmeetingsplatform.ui.screens.meetings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.informatique.electronicmeetingsplatform.data.model.meeting.allMeeting.Meeting
import com.informatique.electronicmeetingsplatform.navigation.NavRoutes
import com.informatique.electronicmeetingsplatform.ui.components.WeeklyMeetingCard
import com.informatique.electronicmeetingsplatform.ui.theme.AppTheme
import com.informatique.electronicmeetingsplatform.ui.theme.LocalExtraColors
import com.informatique.electronicmeetingsplatform.ui.viewModel.AllMeetingState
import com.informatique.electronicmeetingsplatform.ui.viewModel.MeetingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllMeetingScreen(navController: NavController) {

    val extraColors = LocalExtraColors.current

    val viewModel = hiltViewModel<MeetingsViewModel>()

    val allMeetingUiState by viewModel.allMeetingState.collectAsState()

    var selectedTab by remember { mutableIntStateOf(0) }

    val meetingsCount = when (val state = allMeetingUiState) {
        is AllMeetingState.Success -> {
            val data = state.data
            when (selectedTab) {
                0 -> listOfNotNull(data.nextOfficialMeeting).size + data.invited.size + data.organized.size
                1 -> data.organized.size
                else -> data.invited.size
            }
        }
        else -> 0
    }

    var isMeetingExpanded by remember { mutableStateOf(false) }


    Column (
        modifier = Modifier.fillMaxSize()
            .background(extraColors.background),
    ) {

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
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
            ) {
                Text(
                    text = "إجمالي الاجتماعات",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = extraColors.blueColor
                )

                Text(
                    text = "$meetingsCount اجتماع",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        }

        if (allMeetingUiState is AllMeetingState.Loading){
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = extraColors.maroonColor
                )
            }
        }
        else if (allMeetingUiState is AllMeetingState.Success) {
            val allMeetingsData = (allMeetingUiState as AllMeetingState.Success).data
            val allMeetings = when (selectedTab) {
                0 -> {
                    listOfNotNull(allMeetingsData.nextOfficialMeeting) + allMeetingsData.invited + allMeetingsData.organized
                }
                1 -> {
                    allMeetingsData.organized
                }
                else -> {
                    allMeetingsData.invited
                }
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                // Filter tabs
                item {
                    FilterTabs(selectedTab = selectedTab, onTabSelected = { selectedTab = it })
                }

                // List of meetings
                itemsIndexed(allMeetings) { index, meeting ->
                    if (index == 0 && selectedTab == 0){
                        WeeklyMeetingCard(
                            mediaUrl = viewModel.getMediaUrl(),
                            officialMeeting = meeting,
                            isExpanded = isMeetingExpanded,
                            onExpandChange = { isMeetingExpanded = it }
                        )
                    } else {
                        MeetingCard(
                            meeting = meeting,
                            onMeetingClicked = {
                                viewModel.selectMeeting(it)
                                navController.navigate(
                                    NavRoutes.MeetingDetailRoute.createRoute(it.id.toString())
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun FilterTabs(selectedTab: Int, onTabSelected: (Int) -> Unit) {

    val extraColors = LocalExtraColors.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // الكل (All)
        FilterChip(
            selected = selectedTab == 0,
            onClick = { onTabSelected(0) },
            label = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Apps,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("الكل", fontSize = 14.sp, fontWeight = FontWeight.Medium)
                }
            },
            modifier = Modifier
                .weight(1f)
                .height(48.dp),
            colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = extraColors.maroonColor,
                selectedLabelColor = Color.White,
                containerColor = extraColors.blueColor.copy(alpha = 0.05f),
                labelColor = extraColors.blueColor
            ),
            shape = RoundedCornerShape(12.dp)
        )

        // اجتماعاتي (My Meetings)
        FilterChip(
            selected = selectedTab == 1,
            onClick = { onTabSelected(1) },
            label = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Group,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("اجتماعاتي", fontSize = 14.sp, fontWeight = FontWeight.Medium)
                }
            },
            modifier = Modifier
                .weight(1f)
                .height(48.dp),
            colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = extraColors.maroonColor,
                selectedLabelColor = Color.White,
                containerColor = extraColors.blueColor.copy(alpha = 0.05f),
                labelColor = extraColors.blueColor
            ),
            shape = RoundedCornerShape(12.dp)
        )

        // دُعيت إليها (Invited)
        FilterChip(
            selected = selectedTab == 2,
            onClick = { onTabSelected(2) },
            label = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("دُعيت إليها", fontSize = 14.sp, fontWeight = FontWeight.Medium)
                }
            },
            modifier = Modifier
                .weight(1f)
                .height(48.dp),
            colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = extraColors.maroonColor,
                selectedLabelColor = Color.White,
                containerColor = extraColors.blueColor.copy(alpha = 0.05f),
                labelColor = extraColors.blueColor
            ),
            shape = RoundedCornerShape(12.dp)
        )
    }
}

@Composable
fun MeetingCard(meeting: Meeting, onMeetingClicked: (Meeting) -> Unit) {

    val extraColors = LocalExtraColors.current

    val colors = listOf(
        extraColors.blueColor, // Green
        extraColors.lightDrab, // Amber
        extraColors.maroonColor, // Orange
        extraColors.error  // Red
    )

    val colorIndex = (meeting.priorityId - 1).coerceIn(0, 3)
    val contentColor = colors[colorIndex]

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
            .shadow(2.dp, shape = RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        onClick = { onMeetingClicked(meeting) }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(end = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Colored left border
            Box(
                modifier = Modifier
                    .width(6.dp)
                    .fillMaxHeight()
                    .background(contentColor)
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Title and chevron
                Text(
                    text = meeting.topic,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = extraColors.blueColor
                )

                // Time
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Schedule,
                        contentDescription = null,
                        tint = Color.Gray,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "${meeting.startTime} - ${meeting.endTime}",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }

                // Date
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.CalendarToday,
                        contentDescription = null,
                        tint = Color.Gray,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (meeting.startDate == meeting.endDate){
                            meeting.startDate
                        } else {
                            "${meeting.startDate} - ${meeting.endDate}"
                        },
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }

                // Status badge
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start
                ) {
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = contentColor.copy(alpha = 0.2f)
                    ) {
                        Text(
                            text = "${"!".repeat(meeting.priorityId)} ${meeting.priorityName}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = contentColor,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = extraColors.textGray,
                modifier = Modifier.size(30.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomePreview(){
    AppTheme {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            AllMeetingScreen(navController = rememberNavController())
        }
    }
}