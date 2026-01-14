package com.informatique.electronicmeetingsplatform.ui.screens.profile

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.informatique.electronicmeetingsplatform.R
import com.informatique.electronicmeetingsplatform.data.model.profile.Data
import com.informatique.electronicmeetingsplatform.ui.screens.TightLineHeightText
import com.informatique.electronicmeetingsplatform.ui.theme.AppTheme
import com.informatique.electronicmeetingsplatform.ui.theme.LocalExtraColors
import com.informatique.electronicmeetingsplatform.ui.viewModel.PersonImageState
import com.informatique.electronicmeetingsplatform.ui.viewModel.ProfileState
import com.informatique.electronicmeetingsplatform.ui.viewModel.ProfileViewModel

// Color Scheme
val QatarMaroon = Color(0xFF8B2951)
val QatarDarkBg = Color(0xFF1E2433)
val QatarCardBg = Color(0xFF2A3244)
val LightGray = Color(0xFFF5F5F5)
val DarkGray = Color(0xFF4A5568)

@Composable
fun ProfileScreen(navController: NavController) {

    val viewModel = hiltViewModel<ProfileViewModel>()

    val profileState by viewModel.profileState.collectAsStateWithLifecycle()

    var cardFlipped by remember { mutableStateOf(false) }
    var selectedTab by remember { mutableStateOf(ProfileTabType.PERSONAL) }
    val scrollState = rememberScrollState()

    val extraColors = LocalExtraColors.current


    if (profileState is ProfileState.Loading){
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(LightGray),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = extraColors.maroonColor)
        }
    }
    else if (profileState is ProfileState.Success){
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(LightGray)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
            ) {

                // Header with update info
                UpdateHeader()

                Spacer(modifier = Modifier.height(16.dp))

                // Flip Card
                FlipCard(
                    data = (profileState as ProfileState.Success).data,
                    viewModel = viewModel,
                    isFlipped = cardFlipped,
                    onFlipChange = { cardFlipped = it }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Three Action Buttons
                ActionButtonsRow(
                    selectedTab = selectedTab,
                    onTabSelected = { selectedTab = it }
                )

                Spacer(modifier = Modifier.height(24.dp))

                when (selectedTab) {
                    ProfileTabType.PERSONAL -> {
                        PersonalInformationSection(viewModel = viewModel)
                    }
                    ProfileTabType.SALARY -> {

                    }
                    else -> {

                    }
                }

            }
        }
    }

}

@Composable
fun UpdateHeader() {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .padding(top = 8.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            val extraColors = LocalExtraColors.current

            // Last update text
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = Color.Green,
                    modifier = Modifier.size(16.dp)
                )

                Spacer(modifier = Modifier.width(6.dp))

                Text(
                    text = "آخر تحديث: منذ 2 يوم",
                    fontSize = 13.sp,
                    color = extraColors.textGray
                )
            }

            // Refresh button
            Surface(
                modifier = Modifier
                    .wrapContentWidth()
                    .height(30.dp),
                shape = CircleShape,
                color = extraColors.maroonColor.copy(alpha = 0.15f)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {

                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Refresh",
                        tint = extraColors.maroonColor,
                        modifier = Modifier.size(20.dp)
                    )

                    Text(
                        text = "تحديث",
                        fontSize = 13.sp,
                        color = extraColors.maroonColor
                    )
                }
            }
        }

    }
}

@Composable
fun FlipCard(
    data: Data,
    viewModel: ProfileViewModel,
    isFlipped: Boolean,
    onFlipChange: (Boolean) -> Unit
) {
    val rotation by animateFloatAsState(
        targetValue = if (isFlipped) 180f else 0f,
        animationSpec = tween(600, easing = FastOutSlowInEasing)
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .height(370.dp)
    ) {
        Card(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    rotationY = rotation
                    cameraDistance = 12f * density
                }
                .clickable { onFlipChange(!isFlipped) },
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = QatarDarkBg),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            if (rotation <= 90f) {
                // Front Side
                JobCardFront(
                    data = data,
                    viewModel = viewModel,
                    moreInfo = { onFlipChange(!isFlipped) }
                )
            } else {
                // Back Side
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer { rotationY = 180f }
                ) {
                    JobCardBack(
                        data = data,
                        goBack = { onFlipChange(!isFlipped) }
                    )
                }
            }
        }
    }
}

@Composable
fun JobCardFront(
    data: Data,
    viewModel: ProfileViewModel,
    moreInfo: () -> Unit
) {

    val extraColors = LocalExtraColors.current

    val personImageState by viewModel.personImageState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(QatarDarkBg, QatarCardBg)
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Column(horizontalAlignment = Alignment.Start) {
                    Text(
                        text = "دولة قطر",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "البطاقة الوظيفية",
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 12.sp
                    )
                }

                // Qatar Logo placeholder
                Column(
                    modifier = Modifier
                        .size(70.dp)
                        .background(Color.White.copy(alpha = 0.1f), CircleShape),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Image(
                        painter = painterResource(id = R.drawable.ic_logo),
                        contentDescription = "Qatar Council Emblem",
                        colorFilter = ColorFilter.tint(Color.White),
                        modifier = Modifier.size(20.dp),
                        contentScale = ContentScale.Fit
                    )

                    Column(

                        verticalArrangement = Arrangement.spacedBy((-2).dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        TightLineHeightText(
                            text = "الأمانــــة العامـــــة لمجلــــس الـــوزراء",
                            fontSize = 5.sp,
                            fontWeight = FontWeight.Normal,
                            color = Color.White,
                            letterSpacing = 0.5.sp,
                            modifier = Modifier.fillMaxWidth()
                        )

                        TightLineHeightText(
                            text = "Council of Ministers Secretariat General",
                            fontSize = 4.sp,
                            fontWeight = FontWeight.Normal,
                            color = Color.White,
                            letterSpacing = 0.3.sp,
                            modifier = Modifier.fillMaxWidth()
                        )

                        TightLineHeightText(
                            text = "دولــــة قطـــر • State of Qatar",
                            fontSize = 2.sp,
                            fontWeight = FontWeight.Normal,
                            color = Color.White,
                            letterSpacing = 0.2.sp,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "State of Qatar",
                        color = Color.White,
                        fontSize = 14.sp
                    )
                    Text(
                        text = "Job Card",
                        color = Color.White.copy(alpha = 0.5f),
                        fontSize = 12.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Main info card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White.copy(alpha = 0.08f)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Profile photo placeholder
                    Box(
                        modifier = Modifier
                            .width(70.dp)
                            .height(100.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .border(2.dp, Color.White.copy(alpha = 0.5f))
                            .background(extraColors.maroonColor.copy(alpha = 0.7f)),
                        contentAlignment = Alignment.Center
                    ) {

                        if (personImageState is PersonImageState.Loading){
                            CircularProgressIndicator(
                                color = Color.White.copy(alpha = 0.5f)
                            )
                        }
                        else if (personImageState is PersonImageState.Success){
                            AsyncImage(
                                model = (personImageState as PersonImageState.Success).url,
                                contentDescription = "Profile Picture",
                                modifier = Modifier
                                    .fillMaxSize(),
                                contentScale = ContentScale.Fit
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.Start
                    ) {
                        InfoRow("الرقم الوظيفي:", data.person.id.toString(), null)

                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 8.dp),
                            thickness = 1.dp,
                            color = Color.White.copy(alpha = 0.2f)
                        )

                        InfoRow("هاتف:", data.person.phoneNumber, Icons.Default.Phone)

                        Spacer(modifier = Modifier.height(8.dp))

                        InfoRow("البريد:", data.person.email, Icons.Default.Mail)
                    }
                }
            }

        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Black.copy(alpha = 0.3f))
        ) {

            HorizontalDivider(thickness = 1.dp, color = extraColors.maroonColor)

            Spacer(modifier = Modifier.height(6.dp))

            // Name and title
            Text(
                text = data.person.fullName,
                color = Color.White,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
            Text(
                text = data.person.jobTitle,
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 11.sp,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(6.dp))
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Additional Info Toggle
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.End
        ) {
            Surface(
                modifier = Modifier
                    .wrapContentSize()
                    .clickable { moreInfo() },
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, extraColors.maroonColor),
                color = Color.White.copy(alpha = 0.1f),
            ) {
                Row(
                    modifier = Modifier.padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = null,
                        tint = extraColors.maroonColor,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "اضغط للمزيد",
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

    }
}

@Composable
fun JobCardBack(data: Data, goBack: () -> Unit) {

    val extraColors = LocalExtraColors.current
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(QatarDarkBg, QatarCardBg)
                )
            )
            .padding(16.dp)
    ) {
        // Header with icon
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    modifier = Modifier.size(20.dp),
                    imageVector = Icons.Default.Info,
                    contentDescription = "Additional Information",
                    tint = extraColors.maroonColor
                )

                Text(
                    text = "معلومات إضافية",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Subtitle
        Text(
            text = "معالي الشيخ ${data.person.fullName}",
            color = Color.White.copy(alpha = 0.6f),
            fontSize = 11.sp,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Start
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Scrollable Information items
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            HorizontalDivider(thickness = 1.dp, color = extraColors.maroonColor.copy(alpha = 0.3f))

            BackInfoItem(
                headerIcon = Icons.Default.Work,
                headerLabel = "جهة العمل",
                data = AdditionalInformation(
                    Icons.Default.Cases,
                    data.person.personDepartmentJobs[0].departmentName
                )
            )

            BackInfoItem(
                headerIcon = Icons.Default.LocationOn,
                headerLabel = "العنوان",
                data = listOf(
                    AdditionalInformation(Icons.Default.MyLocation, "835"),
                    AdditionalInformation(Icons.Default.LocationOn, "120"),
                    AdditionalInformation(Icons.Default.LocationCity, "قطر")
                )
            )

//            BackInfoItem(
//                icon = Icons.Default.Home,
//                label = "الشارع",
//                value = "835"
//            )
//
//            BackInfoItem(
//                icon = Icons.Default.Place,
//                label = "المنطقة",
//                value = "منطقة"
//            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Back button
        Surface(
            modifier = Modifier
                .align(Alignment.End)
                .clickable { goBack() },
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, extraColors.maroonColor),
            color = Color.White.copy(alpha = 0.1f)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = null,
                    tint = extraColors.maroonColor,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = "اضغط للعودة",
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

data class AdditionalInformation (
    val icon: ImageVector,
    val label: String
)

@Composable
fun BackInfoItem(
    headerIcon: ImageVector,
    headerLabel: String,
    data: AdditionalInformation
) {

    val extraColors = LocalExtraColors.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .background(extraColors.maroonColor, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    modifier = Modifier.size(12.dp),
                    imageVector = headerIcon,
                    contentDescription = headerLabel,
                    tint = Color.Black.copy(alpha = 0.5f)
                )
            }

            Text(
                text = headerLabel,
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 14.sp
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Color.White.copy(alpha = 0.05f),
                    RoundedCornerShape(12.dp)
                )
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .background(extraColors.maroonColor, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    modifier = Modifier.size(12.dp),
                    imageVector = data.icon,
                    contentDescription = data.label,
                    tint = Color.Black.copy(alpha = 0.5f)
                )
            }

            Text(
                text = data.label,
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 14.sp
            )
        }

    }
}

@Composable
fun BackInfoItem(
    headerIcon: ImageVector,
    headerLabel: String,
    data: List<AdditionalInformation>
) {

    val extraColors = LocalExtraColors.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .background(extraColors.maroonColor, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    modifier = Modifier.size(12.dp),
                    imageVector = headerIcon,
                    contentDescription = headerLabel,
                    tint = Color.Black.copy(alpha = 0.5f)
                )
            }

            Text(
                text = headerLabel,
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 14.sp
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Column (
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Color.White.copy(alpha = 0.05f),
                    RoundedCornerShape(12.dp)
                )
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            data.forEach {
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .background(extraColors.maroonColor, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        modifier = Modifier.size(12.dp),
                        imageVector = it.icon,
                        contentDescription = it.label,
                        tint = Color.Black.copy(alpha = 0.5f)
                    )
                }

                Text(
                    text = it.label,
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 14.sp
                )
            }
        }

    }
}

@Composable
fun InfoRow(label: String, value: String, icon: ImageVector?) {

    val extraColor = LocalExtraColors.current

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            color = Color.White.copy(alpha = 0.6f),
            fontSize = 12.sp,
            maxLines = 1
        )

        Text(
            text = value,
            color = Color.White,
            fontSize = 12.sp,
            maxLines = 1
        )

        if (icon != null){
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .background(extraColor.maroonColor, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    modifier = Modifier.size(12.dp),
                    imageVector = icon,
                    contentDescription = label,
                    tint = Color.Black.copy(alpha = 0.5f)
                )
            }
        }
    }
}

enum class ProfileTabType {
    PERSONAL, SALARY, ATTENDANCE
}

@Composable
fun ActionButtonsRow(
    selectedTab: ProfileTabType,
    onTabSelected: (ProfileTabType) -> Unit
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        ActionButton(
            text = "البيانات الشخصية",
            icon = Icons.Default.Person,
            modifier = Modifier.weight(1f),
            isSelected = selectedTab == ProfileTabType.PERSONAL,
            type = ProfileTabType.PERSONAL,
            onSelectTab = onTabSelected
        )

        ActionButton(
            text = "الأجور والتعويضات",
            icon = Icons.Default.ShoppingCart,
            modifier = Modifier.weight(1f),
            isSelected = selectedTab == ProfileTabType.SALARY,
            type = ProfileTabType.SALARY,
            onSelectTab = onTabSelected
        )

        ActionButton(
            text = "صحيفة الحضور والغياب",
            icon = Icons.Default.DateRange,
            modifier = Modifier.weight(1f),
            isSelected = selectedTab == ProfileTabType.ATTENDANCE,
            type = ProfileTabType.ATTENDANCE,
            onSelectTab = onTabSelected
        )
    }
}

@Composable
fun ActionButton(
    text: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    isSelected: Boolean,
    type: ProfileTabType,
    onSelectTab: (ProfileTabType) -> Unit = {}
) {

    val extraColors = LocalExtraColors.current

    Card(
        modifier = modifier
            .height(90.dp)
            .clickable { onSelectTab(type) },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) extraColors.maroonColor else Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isSelected) Color.White else extraColors.maroonColor,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = text,
                fontSize = 10.sp,
                textAlign = TextAlign.Center,
                color = if (isSelected) Color.White else extraColors.maroonColor,
                lineHeight = 14.sp
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ProfilePreview(){
    AppTheme {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
              ProfileScreen(navController = rememberNavController())
        }
    }
}