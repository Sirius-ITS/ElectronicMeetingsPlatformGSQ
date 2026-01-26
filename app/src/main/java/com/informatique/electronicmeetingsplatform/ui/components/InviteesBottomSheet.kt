package com.informatique.electronicmeetingsplatform.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.informatique.electronicmeetingsplatform.data.model.meeting.create.Attendee
import com.informatique.electronicmeetingsplatform.ui.theme.LocalExtraColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InviteesBottomSheet(
    mediaUrl: String,
    invitees: List<Attendee>,
    sheetState: SheetState,
    onDismiss: () -> Unit,
    onConfirm: (List<Attendee>) -> Unit
) {
    val extraColors = LocalExtraColors.current

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = extraColors.background,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        sheetState = sheetState ,
        dragHandle = null,
        modifier = Modifier
            .fillMaxHeight()
            .statusBarsPadding()
    ) {

        var selectedInvitees by remember { mutableStateOf(invitees.associate { it.id to false }) }
        var searchQuery by remember { mutableStateOf("") }

        val selectedCount = remember(selectedInvitees) {
            derivedStateOf { selectedInvitees.count { it.value } }
        }.value

        val allSelected = remember(selectedInvitees, invitees) {
            derivedStateOf { selectedCount == invitees.size }
        }.value

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
        ) {
            // Header
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Close button (on the right in RTL)
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color.Black,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    // Title
                    Text(
                        text = "إضافة مدعوين",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Black
                    )

                    // Confirm button
                    IconButton(
                        onClick = {
                            val selected = invitees.filter { selectedInvitees[it.id] == true }
                            onConfirm(selected)
                        },
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Confirm",
                            tint = Color.Black,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }

            // Search Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = {
                        Text(
                            "بحث عن مدعو",
                            color = Color.Gray,
                            textAlign = TextAlign.Start,
                            modifier = Modifier.fillMaxWidth()
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = Color.Gray
                        )
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = Color.LightGray.copy(alpha = 0.3f),
                        focusedContainerColor = Color.LightGray.copy(alpha = 0.3f),
                        unfocusedBorderColor = Color.Transparent,
                        focusedBorderColor = Color.Transparent
                    ),
                    shape = RoundedCornerShape(12.dp)
                )
            }

            // Select All
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        selectedInvitees = invitees.associate { it.id to !allSelected }
                    }
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Checkbox
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (allSelected) extraColors.maroonColor else extraColors.background)
                            .border(
                                width = if (allSelected) 0.dp else 2.dp,
                                color = if (allSelected) Color.Transparent else extraColors.maroonColor,
                                shape = RoundedCornerShape(6.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (allSelected) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Text(
                        text = if (allSelected) "إلغاء تحديد الكل" else "تحديد الكل",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = extraColors.maroonColor
                    )
                }

                Text(
                    text = "$selectedCount محدد",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }

            // Invitees List
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(invitees.filter {
                    searchQuery.isEmpty() ||
                            it.fullName!!.contains(searchQuery, ignoreCase = true) ||
                            (it.jobDescription?.contains(searchQuery, ignoreCase = true) == true)
                }) { invitee ->
                    InviteeCard(
                        mediaUrl = mediaUrl,
                        invitee = invitee,
                        isSelected = selectedInvitees[invitee.id] ?: false,
                        onToggle = { attendee ->
                            selectedInvitees = selectedInvitees.toMutableMap().apply {
                                this[attendee.id] = !(this[attendee.id] ?: false)
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun InviteeCard(
    mediaUrl: String,
    invitee: Attendee,
    isSelected: Boolean,
    onToggle: (Attendee) -> Unit
) {

    val extraColors = LocalExtraColors.current

    val backgroundColor = if (isSelected) extraColors.maroonColor.copy(alpha = 0.2f)
    else Color.White
    val borderColor = if (isSelected) extraColors.maroonColor else Color.Transparent
    val contentColor = if (isSelected) extraColors.maroonColor else extraColors.blueColor

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = { onToggle(invitee) }),
        shape = RoundedCornerShape(16.dp),
        color = backgroundColor,
        border = BorderStroke(2.dp, borderColor)
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
                modifier = Modifier.size(50.dp)
            ) {
                // Profile circle
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                        .background(Color.Gray.copy(alpha = 0.3f)),
                    contentAlignment = Alignment.Center
                ) {
                    if (invitee.personalPhotoPath == null) {
                        Text(
                            text = "👤",
                            fontSize = 24.sp,
                            color = Color.Gray
                        )
                    } else {
                        AsyncImage(
                            model = mediaUrl.plus(invitee.personalPhotoPath),
                            contentDescription = "Profile Picture",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Fit
                        )
                    }
                }

                // Green check badge
                if (isSelected){
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .align(Alignment.TopStart)
                            .offset(x = (-4).dp, y = (-4).dp)
                            .clip(CircleShape)
                            .background(extraColors.success)
                            .border(2.dp, extraColors.success, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }

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
                    color = contentColor,
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
                    .background(if (isSelected) extraColors.maroonColor else Color.White)
                    .border(
                        width = if (isSelected) 0.dp else 2.dp,
                        color = if (isSelected) Color.Transparent else Color.LightGray,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (isSelected) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}
