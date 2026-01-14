package com.informatique.electronicmeetingsplatform.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui. draw.scale
import androidx.compose. ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.informatique.electronicmeetingsplatform.ui.theme.AppTheme
import com.informatique.electronicmeetingsplatform.ui.theme.LocalExtraColors
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

// Data Models
data class Request(
    val id: String,
    val title: String,
    val subtitle: String,
    val userName: String,
    val userAvatar: String,
    val price: String,
    val date:  LocalDate,
    val duration: String,
    val status:  RequestStatus,
    val isPending:  Boolean = false
)

enum class RequestStatus {
    ALL, PENDING, ACCEPTED, REJECTED, OFFICIAL_TASK
}

enum class TabType {
    PERSONAL, FOR_APPROVAL
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyRequestScreen(navController: NavController) {

    val scope = rememberCoroutineScope()

    var selectedTab by remember { mutableStateOf(TabType.PERSONAL) }
    var searchQuery by remember { mutableStateOf("") }
    var showFilterSheet by remember { mutableStateOf(false) }
    var selectedFilters by remember { mutableStateOf(setOf<RequestStatus>()) }
    var expandedRequestId by remember { mutableStateOf<String?>(null) }
    var showDatePicker by remember { mutableStateOf(false) }

    // Sample data
    val requests = remember {
        listOf(
            Request(
                id = "1000300",
                title = "طلب الموافقة على مهمة رسمية",
                subtitle = "مهمة رسمية إلى فرنسا - مدة 3 أيام",
                userName = "محمد أحمد",
                userAvatar = "",
                price = "15000 ريال",
                date = LocalDate.of(2025, 12, 30),
                duration = "3 أيام",
                status = RequestStatus.OFFICIAL_TASK
            ),
            Request(
                id = "1000299",
                title = "طلب بدل مهمة رسمية",
                subtitle = "بدل مهمة رسمية عن شهر ديسمبر 2025",
                userName = "سارة خالد",
                userAvatar = "",
                price = "8500 ريال",
                date = LocalDate.of(2025, 12, 30),
                duration = "شهر",
                status = RequestStatus.PENDING,
                isPending = true
            ),
            Request(
                id = "1000298",
                title = "معالي الشيخ خالد بن عبدالله آل ثاني",
                subtitle = "طلب بدل مهمة رسمية",
                userName = "معالي الشيخ",
                userAvatar = "",
                price = "25000 ريال",
                date = LocalDate.of(2026, 1, 5),
                duration = "7 أيام",
                status = RequestStatus.PENDING,
                isPending = true
            )
        )
    }

    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = false
    )

    Scaffold(
        topBar = {
            RequestsTopBar(
                onAddClick = { /* Handle add */ }
            )
        },
        containerColor = Color(0xFFF5F5F5)
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Tab Row
            RequestsTabRow(
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it }
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Search Bar
            SearchBar(
                query = searchQuery,
                onQueryChange = { searchQuery = it },
                filterCount = selectedFilters.size,
                onFilterClick = { showFilterSheet = true },
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier. height(16.dp))

            // Filter Chips (visible when filters are applied)
            AnimatedVisibility(
                visible = selectedFilters.isNotEmpty(),
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                FilterChipsRow(
                    selectedFilters = selectedFilters,
                    onFilterRemove = { filter ->
                        selectedFilters = selectedFilters - filter
                    }
                )
            }

            // Requests List
            LazyColumn(
                modifier = Modifier. fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                val filteredRequests = if (selectedFilters.isEmpty()) {
                    requests
                } else {
                    requests.filter { it.status in selectedFilters }
                }

                items(
                    items = filteredRequests,
                    key = { it.id }
                ) { request ->
                    RequestCard(
                        request = request,
                        isExpanded = expandedRequestId == request.id,
                        onCardClick = {
                            expandedRequestId = if (expandedRequestId == request.id) {
                                null
                            } else {
                                request.id
                            }
                        },
                        onStatusChange = { /* Handle status change */ }
                    )
                }
            }
        }

        // Filter Bottom Sheet
        if (showFilterSheet) {
            FilterBottomSheet(
                sheetState = sheetState,
                selectedFilters = selectedFilters,
                onFilterChange = { filter ->
                    selectedFilters = if (filter in selectedFilters) {
                        selectedFilters - filter
                    } else {
                        selectedFilters + filter
                    }
                },
                onDismiss = {
                    scope.launch {
                        sheetState.hide()
                        showFilterSheet = false
                    }
                },
                onShowDatePicker = { showDatePicker = true }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RequestsTopBar(
    onAddClick: () -> Unit
) {

    val extraColors = LocalExtraColors.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ){
        Column {
            Text(
                "الطلبات",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = extraColors.blueColor
                ),
                textAlign = TextAlign.Center
            )

            Text(
                "7 طلبات",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = extraColors.textGray
                ),
                textAlign = TextAlign.Center
            )
        }

        Box(
            modifier = Modifier. padding(start = 16.dp)
        ) {
            IconButton(
                onClick = onAddClick,
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        color = extraColors.maroonColor,
                        shape = CircleShape
                    )
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "Add",
                    tint = Color.White
                )
            }
        }
    }
}

@Composable
fun RequestsTabRow(
    selectedTab:  TabType,
    onTabSelected: (TabType) -> Unit
) {

    val extraColors = LocalExtraColors.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement. spacedBy(8.dp)
    ) {
        TabButton(
            text = "طلباتي",
            icon = Icons.Default.FileCopy,
            isSelected = selectedTab == TabType.PERSONAL,
            selectedColor = extraColors.blueColor,
            unSelectedColor = extraColors.blueColor.copy(alpha = 0.2f),
            onClick = { onTabSelected(TabType.PERSONAL) },
            modifier = Modifier.weight(1f)
        )

        TabButton(
            text = "طلبات الاعتماد",
            icon = Icons.Default.CheckCircle,
            isSelected = selectedTab == TabType.FOR_APPROVAL,
            selectedColor = extraColors.maroonColor,
            unSelectedColor = extraColors.maroonColor.copy(alpha = 0.2f),
            onClick = { onTabSelected(TabType.FOR_APPROVAL) },
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun TabButton(
    text: String,
    icon: ImageVector,
    isSelected: Boolean,
    selectedColor: Color,
    unSelectedColor: Color,
    onClick: () -> Unit,
    modifier:  Modifier = Modifier
) {

    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected) selectedColor else unSelectedColor,
        animationSpec = tween(300)
    )

    val contentColor by animateColorAsState(
        targetValue = if (isSelected) Color.White else selectedColor,
        animationSpec = tween(300)
    )

    Surface(
        onClick = onClick,
        modifier = modifier. height(48.dp),
        shape = RoundedCornerShape(12.dp),
        color = backgroundColor
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text,
                color = contentColor,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Medium
                )
            )
        }
    }
}

@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    filterCount: Int,
    onFilterClick: () -> Unit,
    modifier: Modifier = Modifier
) {

    val extraColors = LocalExtraColors.current

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ){

        Box(
            modifier = Modifier
                .size(40.dp),
            contentAlignment = Alignment.Center
        ){
            Box(
                modifier = Modifier
                    .size(30.dp)
                    .background(
                        color = extraColors.blueColor, shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                IconButton(
                    modifier = Modifier
                        .size(20.dp),
                    onClick = onFilterClick
                ) {
                    Icon(
                        Icons.Default.FilterList,
                        contentDescription = "Filter",
                        tint = Color.White
                    )
                }
            }

            // Badge
            if (filterCount > 0) {
                Badge(
                    modifier = Modifier
                        .align(Alignment.TopEnd),
                    containerColor = Color(0xFFD32F2F)
                ) {
                    Text(
                        text = filterCount.toString(),
                        fontSize = 10.sp,
                        color = Color.White
                    )
                }
            }
        }

        OutlinedTextField(
            value = query,
            onValueChange = onQueryChange,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            placeholder = {
                Text(
                    "ابحث عن الطلبات",
                    color = Color.Gray,
                    fontSize = 14.sp
                )
            },
            leadingIcon = {
                Icon(
                    Icons.Default.Search,
                    contentDescription = "Search",
                    tint = Color.Gray
                )
            },
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = Color.White,
                focusedContainerColor = Color.White,
                unfocusedBorderColor = Color.Transparent,
                focusedBorderColor = Color(0xFF4A5F7F)
            ),
            singleLine = true
        )
    }
}

@Composable
fun FilterChipsRow(
    selectedFilters: Set<RequestStatus>,
    onFilterRemove: (RequestStatus) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        selectedFilters.forEach { filter ->
            FilterChip(
                selected = true,
                onClick = { onFilterRemove(filter) },
                label = {
                    Text(
                        getFilterLabel(filter),
                        fontSize = 13.sp
                    )
                },
                trailingIcon = {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = "Remove",
                        modifier = Modifier.size(16.dp)
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = Color(0xFF6B4C5E),
                    selectedLabelColor = Color. White
                ),
                shape = RoundedCornerShape(20.dp)
            )
        }
    }
}

@Composable
fun RequestCard(
    request: Request,
    isExpanded: Boolean,
    onCardClick: () -> Unit,
    onStatusChange:  (RequestStatus) -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = if (isExpanded) 0.98f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        )
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
            .animateContentSize(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMedium
                )
            )
            .clickable(onClick = onCardClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults. cardElevation(
            defaultElevation = if (isExpanded) 8.dp else 2.dp
        )
    ) {
        Column(
            modifier = Modifier. padding(16.dp)
        ) {
            Row(
                modifier = Modifier. fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                // User Avatar
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFE8E8E8)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = null,
                        tint = Color.Gray,
                        modifier = Modifier. size(28.dp)
                    )
                }

                Spacer(modifier = Modifier. width(12.dp))

                // Content
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = request.title,
                        style = MaterialTheme.typography.bodyLarge. copy(
                            fontWeight = FontWeight.Bold
                        ),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = request.subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color. Gray,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = request.id,
                            fontSize = 12.sp,
                            color = Color. Gray
                        )

                        Spacer(modifier = Modifier.width(4.dp))

                        Icon(
                            Icons.Default. Info,
                            contentDescription = null,
                            tint = Color.Gray,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Info Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                InfoItem(
                    icon = Icons.Default.Star,
                    text = request.duration
                )

                InfoItem(
                    icon = Icons. Default.Favorite,
                    text = request. price
                )

                InfoItem(
                    icon = Icons.Default.DateRange,
                    text = request.date. format(DateTimeFormatter.ofPattern("dd MMM yyyy"))
                )
            }

            // Expanded Content
            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column {
                    Spacer(modifier = Modifier. height(16.dp))

                    HorizontalDivider(color = Color(0xFFE8E8E8))

                    Spacer(modifier = Modifier.height(16.dp))

                    // Status Grid
                    StatusGrid(
                        selectedStatus = request.status,
                        onStatusSelected = onStatusChange
                    )
                }
            }
        }
    }
}

@Composable
fun InfoItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement. spacedBy(4.dp)
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = Color.Gray,
            modifier = Modifier.size(16.dp)
        )
        Text(
            text = text,
            fontSize = 12.sp,
            color = Color.Gray
        )
    }
}

@Composable
fun StatusGrid(
    selectedStatus: RequestStatus,
    onStatusSelected: (RequestStatus) -> Unit
) {
    Column(
        verticalArrangement = Arrangement. spacedBy(8.dp)
    ) {
        Text(
            "تصنيف النتائج",
            style = MaterialTheme.typography.titleSmall. copy(
                fontWeight = FontWeight.Bold
            ),
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            StatusButton(
                text = "إلغاء",
                isSelected = false,
                onClick = { },
                modifier = Modifier.weight(1f),
                backgroundColor = Color(0xFFE8E8E8)
            )

            StatusButton(
                text = "الكل",
                isSelected = selectedStatus == RequestStatus.ALL,
                onClick = { onStatusSelected(RequestStatus.ALL) },
                modifier = Modifier.weight(1f),
                backgroundColor = Color(0xFF6B4C5E)
            )
        }

        Row(
            modifier = Modifier. fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            StatusButton(
                text = "مرفوضة",
                isSelected = selectedStatus == RequestStatus.REJECTED,
                onClick = { onStatusSelected(RequestStatus.REJECTED) },
                modifier = Modifier.weight(1f)
            )

            StatusButton(
                text = "مقبولة",
                isSelected = selectedStatus == RequestStatus. ACCEPTED,
                onClick = { onStatusSelected(RequestStatus.ACCEPTED) },
                modifier = Modifier. weight(1f)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            StatusButton(
                text = "تحميل الملفات",
                isSelected = false,
                onClick = { },
                modifier = Modifier.weight(1f)
            )

            StatusButton(
                text = "إدارة",
                isSelected = false,
                onClick = { },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun StatusButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier:  Modifier = Modifier,
    backgroundColor: Color = Color(0xFFE8E8E8)
) {
    Button(
        onClick = onClick,
        modifier = modifier. height(48.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) Color(0xFF6B4C5E) else backgroundColor,
            contentColor = if (isSelected) Color.White else Color.Black
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(
            text,
            style = MaterialTheme. typography.bodyMedium.copy(
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
            )
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterBottomSheet(
    sheetState: SheetState,
    selectedFilters: Set<RequestStatus>,
    onFilterChange: (RequestStatus) -> Unit,
    onDismiss: () -> Unit,
    onShowDatePicker: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        containerColor = Color.White,
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
                .padding(24.dp)
        ) {
            Text(
                "تصنيف النتائج",
                style = MaterialTheme. typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold
                ),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Status Grid
            Row(
                modifier = Modifier. fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                FilterStatusButton(
                    text = "إلغاء",
                    isSelected = false,
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f),
                    backgroundColor = Color(0xFFE8E8E8)
                )

                FilterStatusButton(
                    text = "الكل",
                    isSelected = RequestStatus.ALL in selectedFilters,
                    onClick = { onFilterChange(RequestStatus.ALL) },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier. fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                FilterStatusButton(
                    text = "مرفوضة",
                    isSelected = RequestStatus. REJECTED in selectedFilters,
                    onClick = { onFilterChange(RequestStatus.REJECTED) },
                    modifier = Modifier.weight(1f)
                )

                FilterStatusButton(
                    text = "مقبولة",
                    isSelected = RequestStatus.ACCEPTED in selectedFilters,
                    onClick = { onFilterChange(RequestStatus. ACCEPTED) },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                FilterStatusButton(
                    text = "قيد الانتظار",
                    isSelected = RequestStatus.PENDING in selectedFilters,
                    onClick = { onFilterChange(RequestStatus.PENDING) },
                    modifier = Modifier.weight(1f)
                )

                FilterStatusButton(
                    text = "مهمة رسمية",
                    isSelected = RequestStatus.OFFICIAL_TASK in selectedFilters,
                    onClick = { onFilterChange(RequestStatus.OFFICIAL_TASK) },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Date Picker Button
            OutlinedButton(
                onClick = onShowDatePicker,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color(0xFF6B4C5E)
                )
            ) {
                Icon(
                    Icons.Default. DateRange,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier. width(8.dp))
                Text("اختيار التاريخ")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Apply Button
            Button(
                onClick = onDismiss,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF6B4C5E)
                )
            ) {
                Text(
                    "تطبيق",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }
    }
}

@Composable
fun FilterStatusButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    backgroundColor: Color?  = null
) {
    val containerColor = backgroundColor ?: if (isSelected) {
        Color(0xFF6B4C5E)
    } else {
        Color(0xFFE8E8E8)
    }

    val contentColor = if (isSelected) Color.White else Color.Black

    Button(
        onClick = onClick,
        modifier = modifier. height(52.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(
            text,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
            )
        )
    }
}

fun getFilterLabel(status: RequestStatus): String {
    return when (status) {
        RequestStatus.ALL -> "الكل"
        RequestStatus.PENDING -> "قيد الانتظار"
        RequestStatus. ACCEPTED -> "مقبولة"
        RequestStatus.REJECTED -> "مرفوضة"
        RequestStatus.OFFICIAL_TASK -> "مهمة رسمية"
    }
}

@Preview(showBackground = true)
@Composable
fun MyRequestsPreview(){
    AppTheme {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            MyRequestScreen(navController = rememberNavController())
        }
    }
}