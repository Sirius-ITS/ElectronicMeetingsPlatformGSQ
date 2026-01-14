package com.informatique.electronicmeetingsplatform.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation. background
import androidx.compose.foundation. border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid. LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit. LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.informatique.electronicmeetingsplatform.ui.theme.AppFontFamily
import com.informatique.electronicmeetingsplatform.ui.theme.AppTheme
import com.informatique.electronicmeetingsplatform.ui.theme.LocalExtraColors

@Composable
fun HomeScreen(navController: NavController) {

    var isMeetingExpanded by remember { mutableStateOf(false) }
    var showTopCards by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        showTopCards = true
    }

    Scaffold(
        containerColor = Color(0xFFF5F5F5)
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { TopHeaderSection() }

            item {
                TopStatisticsCardsExpanded()
//                AnimatedVisibility(
//                    visible = showTopCards && isMeetingExpanded,
//                    enter = expandVertically(
//                        animationSpec = spring(
//                            dampingRatio = Spring.DampingRatioMediumBouncy,
//                            stiffness = Spring.StiffnessLow
//                        )
//                    ) + fadeIn(),
//                    exit = shrinkVertically(
//                        animationSpec = spring(
//                            dampingRatio = Spring.DampingRatioMediumBouncy,
//                            stiffness = Spring.StiffnessMedium
//                        )
//                    ) + fadeOut()
//                ) {
//                    TopStatisticsCardsExpanded()
//                }
            }

            item {
                AnimatedVisibility(
                    visible = showTopCards && !isMeetingExpanded,
                    enter = expandVertically() + fadeIn(),
                    exit = shrinkVertically() + fadeOut()
                ) {
                    TopStatisticsCards()
                }
            }

            item {
                WeeklyMeetingCard(
                    isExpanded = isMeetingExpanded,
                    onExpandChange = { isMeetingExpanded = it }
                )
            }

            item {
                AnimatedVisibility(
                    visible = !isMeetingExpanded,
                    enter = expandVertically() + fadeIn(),
                    exit = shrinkVertically() + fadeOut()
                ) {
                    Column(verticalArrangement = Arrangement. spacedBy(16.dp)) {
                        CreateNewMeetingCard()
                        //PreviousMeetingsCard()
                        ServicesSection()
                    }
                }
            }
        }
    }

}

@Composable
fun TopHeaderSection() {

    val extraColors = LocalExtraColors.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        AsyncImage(
            model = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcT8oMvrWLAG0nQ61RG5nARJYWluVy9cH8Yvpw&s",
            contentDescription = "Profile Picture",
            modifier = Modifier
                .size(50.dp)
                .clip(CircleShape)
                .border(2.dp, extraColors.blueColor, CircleShape),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = "مرحباً، معالي الشيخ محمد",
                fontFamily = AppFontFamily,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                color = extraColors.blueColor
            )
            Text(
                text = "الأربعاء، 31 ديسمبر 2025",
                fontFamily = AppFontFamily,
                fontSize = 14.sp,
                color = Color.Gray
            )
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Settings",
                    modifier = Modifier
                        .size(32.dp)
                        .clickable { },
                    tint = extraColors.maroonColor
                )
            }

            Box {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = "Notifications",
                    modifier = Modifier
                        .size(32.dp)
                        .clickable { },
                    tint = extraColors.maroonColor
                )
                Badge(
                    modifier = Modifier
                        .padding(horizontal = 6.dp)
                        .align(Alignment.TopEnd)
                        .offset(x = 4.dp, y = (-4).dp),
                    containerColor = Color(0xFFE53935)
                ) {
                    Text(
                        "15",
                        fontFamily = AppFontFamily,
                        fontSize = 10.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun TopStatisticsCardsExpanded() {
    Column(verticalArrangement = Arrangement. spacedBy(12.dp)) {
        Row(
            modifier = Modifier. fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ExpandedStatCard(
                number = "5",
                label = "إجتماعات اليوم",
                icon = Icons.Default.DateRange,
                iconColor = Color(0xFF7D3C4F),
                modifier = Modifier.weight(1f)
            )
            ExpandedStatCard(
                number = "18",
                label = "ساعات الإجتماعات",
                icon = Icons. Default. Refresh,
                iconColor = Color(0xFF7D3C4F),
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun TopStatisticsCards() {

    val extraColors = LocalExtraColors.current

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        StatCard(
            number = "12",
            label = "إجمالي الإجتماعات",
            icon = Icons.Default.DateRange,
            iconColor = extraColors.blueColor,
            modifier = Modifier.weight(1f)
        )
        StatCard(
            number = "38",
            label = "طلبات مكتملة",
            icon = Icons.Default.CheckCircle,
            iconColor = Color(0xFF4CAF50),
            modifier = Modifier.weight(1f)
        )
        StatCard(
            number = "5",
            label = "إجتماعات عاجلة",
            icon = Icons.Default.AccessTimeFilled,
            iconColor = extraColors.maroonColor,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun StatCard(
    number: String,
    label: String,
    icon: ImageVector,
    iconColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.wrapContentHeight(),
        colors = CardDefaults. cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults. cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(iconColor.copy(alpha = 0.1f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = number,
                fontFamily = AppFontFamily,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = iconColor
            )

            Text(
                text = label,
                fontFamily = AppFontFamily,
                fontSize = 10.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun ExpandedStatCard(
    number:  String,
    label: String,
    icon: ImageVector,
    iconColor: Color,
    modifier: Modifier = Modifier
) {

    val extraColors = LocalExtraColors.current

    Card(
        modifier = modifier.wrapContentHeight(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .padding(top = 16.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .background(iconColor.copy(alpha = 0.1f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(28.dp)
                )
            }


            Text(
                text = label,
                fontFamily = AppFontFamily,
                fontSize = 13.sp,
                color = extraColors.blueColor,
                textAlign = TextAlign.Center
            )

            Text(
                text = number,
                fontFamily = AppFontFamily,
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                color = extraColors.blueColor
            )
        }
    }
}

@Composable
fun WeeklyMeetingCard(
    isExpanded: Boolean,
    onExpandChange: (Boolean) -> Unit
) {

    val extraColors = LocalExtraColors.current

    val rotation by animateFloatAsState(
        targetValue = if (isExpanded) 180f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ), label = "rotation"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            ),
        colors = CardDefaults.cardColors(containerColor = extraColors.maroonColor),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "إجتماع مجلس الوزراء الأسبوعي",
                    fontFamily = AppFontFamily,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White
                )

                IconButton(
                    onClick = { onExpandChange(!isExpanded) }
                ) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = if (isExpanded) "Collapse" else "Expand",
                        tint = Color. White,
                        modifier = Modifier. rotate(rotation)
                    )
                }
            }

            Row(
                modifier = Modifier. fillMaxWidth(),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.AccessTime,
                    contentDescription = null,
                    tint = Color.White. copy(alpha = 0.8f),
                    modifier = Modifier. size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "كل أربعاء، 10:00 صباحاً",
                    fontSize = 14.sp,
                    color = Color.White. copy(alpha = 0.9f)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.Start),
                modifier = Modifier.fillMaxWidth()
            ) {
                PriorityBadge("!!! عالية", Color.White)
                AttendanceBadge("7", true)
                AttendanceBadge("1", false)
                AttendanceBadge("2", null)
            }

            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically(
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                ) + fadeIn(),
                exit = shrinkVertically(
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring. StiffnessMedium
                    )
                ) + fadeOut()
            ) {
                Column {
                    Spacer(modifier = Modifier. height(20.dp))

                    AttendanceProgressBar(70)

                    Spacer(modifier = Modifier.height(20.dp))

                    AttendeeItem(
                        name = "معالي الشيخ محمد بن عبدالرحمن آل ثاني",
                        position = "رئيس مجلس الوزراء وزير الخارجية",
                        status = AttendanceStatus.CONFIRMED,
                        imageUrl = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQTI2xxZDQn3sPSsU0qs9VYmlx3z35pLVOeIQ&s"
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    AttendeeItem(
                        name = "معالي السيد علي بن أحمد الكواري",
                        position = "وزير المالية",
                        status = AttendanceStatus.CONFIRMED,
                        imageUrl = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTfSTyfh4Xx2Mvifa_3DY6wV8ouX8cFTtsWCg&s"
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    AttendeeItem(
                        name = "سعادة السيدة بثنة بنت علي الجبر النعيمي",
                        position = "وزيرة التربية والتعليم والتعليم العالي",
                        status = AttendanceStatus.DECLINED,
                        imageUrl = "https://images.ctfassets.net/2h1qowfuxkq7/3fwfxtwQ24eknV6kF1yqZV/d3bd5d628d20f86425cf049a31623b80/Her_Excellency_Buthaina_bint_Ali_Al-Nuaim.jpg?w=888&h=1196&fl=progressive&q=85&fm=jpg&fit=fill"
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = Color. White.copy(alpha = 0.15f)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { }
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            Icon(
                                imageVector = Icons.Filled.FileCopy,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )

                            Text(
                                text = "عرض التفاصيل الكاملة",
                                fontFamily = AppFontFamily,
                                fontSize = 16.sp,
                                color = Color.White,
                                fontWeight = FontWeight.Medium
                            )

                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )

                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PriorityBadge(text: String, color: Color) {
    Box(
        modifier = Modifier
            .background(color.copy(alpha = 0.2f), RoundedCornerShape(20.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
//            Icon(
//                imageVector = Icons.Default.Warning,
//                contentDescription = null,
//                tint = color,
//                modifier = Modifier.size(16.dp)
//            )
            Text(
                text = text,
                fontFamily = AppFontFamily,
                fontSize = 12.sp,
                color = color,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun AttendanceBadge(number: String, status: Boolean?) {
    val (icon, color) = when (status) {
        true -> Icons.Default.CheckCircle to Color.White
        false -> Icons.Default.AccessTimeFilled to Color.White
        null -> Icons.Default.RemoveCircle to Color.White
    }

    Box(
        modifier = Modifier
            .background(color.copy(alpha = 0.2f), RoundedCornerShape(20.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(12.dp)
            )
            Text(
                text = number,
                fontFamily = AppFontFamily,
                fontSize = 14.sp,
                color = Color.White,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun AttendanceProgressBar(percentage: Int) {
    Card(
        modifier = Modifier. fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.15f)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Row(
                modifier = Modifier. fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "نسبة الحضور",
                    fontFamily = AppFontFamily,
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.9f)
                )
                Text(
                    text = "$percentage%",
                    fontFamily = AppFontFamily,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            LinearProgressIndicator(
                progress = { percentage / 100f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = Color(0xFF4CAF50),
                trackColor = Color.White.copy(alpha = 0.2f)
            )
        }
    }
}

enum class AttendanceStatus {
    CONFIRMED, DECLINED
}

@Composable
fun AttendeeItem(
    name: String,
    position: String,
    status: AttendanceStatus,
    imageUrl: String
) {

    val extraColors = LocalExtraColors.current

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.White. copy(alpha = 0.15f)
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box {
                AsyncImage(
                    model = imageUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )

                when (status) {
                    AttendanceStatus.CONFIRMED -> {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .size(20.dp)
                                .background(Color(0xFF4CAF50), CircleShape)
                                .padding(2.dp)
                        )
                    }
                    AttendanceStatus.DECLINED -> {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .size(20.dp)
                                .background(extraColors.maroonColor, CircleShape)
                                .padding(2.dp)
                        )
                    }
                }
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(2.dp),
                horizontalAlignment = Alignment.Start,
                modifier = Modifier.weight(1f)
                    .padding(horizontal = 10.dp)
            ) {
                Text(
                    text = name,
                    fontFamily = AppFontFamily,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White
                )

                Text(
                    text = position,
                    fontSize = 11.sp,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }

            Box(
                modifier = Modifier
                    .background(
                        color = Color.White.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(20.dp)
                    )
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    text = when (status) {
                        AttendanceStatus.CONFIRMED -> "مؤكد"
                        AttendanceStatus. DECLINED -> "معتذر"
                    },
                    fontSize = 12.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
fun CreateNewMeetingCard() {

    val extraColors = LocalExtraColors.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { },
        colors = CardDefaults. cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults. cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(Color(0xFF7D3C4F).copy(alpha = 0.1f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.AddCircleOutline,
                    contentDescription = null,
                    tint = extraColors.maroonColor,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = "إنشاء إجتماع جديد",
                    fontFamily = AppFontFamily,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = extraColors.blueColor
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "قم بجدولة إجتماع أو إبدأه الآن",
                    fontFamily = AppFontFamily,
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = extraColors.maroonColor,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

/*@Composable
fun PreviousMeetingsCard() {

    val extraColors = LocalExtraColors.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(Color(0xFF7D3C4F).copy(alpha = 0.1f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.CalendarMonth,
                    contentDescription = null,
                    tint = extraColors.maroonColor,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = "الاجتماعات السابقة",
                    fontFamily = AppFontFamily,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = extraColors.blueColor
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "عرض سجل الاجتماعات",
                    fontFamily = AppFontFamily,
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = extraColors.maroonColor,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}*/

@Composable
fun ServicesSection() {

    val extraColors = LocalExtraColors.current

    Column {
        Row(
            modifier = Modifier. fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.GridView,
                contentDescription = null,
                tint = extraColors.maroonColor,
                modifier = Modifier.size(28.dp)
            )

            Text(
                text = "الخدمات",
                fontFamily = AppFontFamily,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = extraColors.blueColor
            )
        }

        Spacer(modifier = Modifier. height(16.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            horizontalArrangement = Arrangement. spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.height(300.dp)
        ) {
            items(servicesItems) { service ->
                ServiceCard(service)
            }
        }
    }
}

data class ServiceItem(
    val title: String,
    val icon: ImageVector
)

val servicesItems = listOf(
    ServiceItem("طلب شهادة", Icons.Default.Edit),
    ServiceItem("حافظة المستندات الرقمية", Icons.Default.Folder),
    ServiceItem("دليل التواصل", Icons.Default. Menu),
    ServiceItem("إحتساب نهاية الخدمة", Icons.Default.Person),
    ServiceItem("الراتب الشهري", Icons.Default.AccountBox),
    ServiceItem("بدل تعليم", Icons.Default.School)
)

@Composable
fun ServiceCard(service: ServiceItem) {

    val extraColors = LocalExtraColors.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clickable { },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .background(extraColors.maroonColor.copy(alpha = 0.1f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = service.icon,
                    contentDescription = null,
                    tint = extraColors.maroonColor,
                    modifier = Modifier.size(28.dp)
                )
            }

            Text(
                text = service.title,
                fontFamily = AppFontFamily,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = extraColors.blueColor,
                textAlign = TextAlign.Center,
                maxLines = 2
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
fun HomePreview(){
    AppTheme {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            HomeScreen(navController = rememberNavController())
        }
    }
}