package com.informatique.electronicmeetingsplatform.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.AccessTimeFilled
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.FileCopy
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.informatique.electronicmeetingsplatform.R
import com.informatique.electronicmeetingsplatform.navigation.NavRoutes
import com.informatique.electronicmeetingsplatform.ui.theme.ExtraColors
import io.ktor.client.request.invoke
import kotlinx.coroutines.launch

// الألوان الصحيحة من الصور
val DarkBlueTab = Color(0xFF0D4261)
val LightBlueTab = Color(0xFFD0E2EE)
val DarkRedTab = Color(0xFF7D1F3F)
val LightRedTab = Color(0xFFE8D9DD)
val StatusBg = Color(0xFFF3EBE1)
val StatusIconColor = Color(0xFF9B8B7E)
val GrayText = Color(0xFF9E9E9E)
val LightGrayBg = Color(0xFFF5F5F5)
val CardWhite = Color(0xFFFFFFFF)
val DividerColor = Color(0xFFEEEEEE)
val DarkBlueText = Color(0xFF1A365D)
val FilterButtonBg = Color(0xFF435D6F)
val FilterSelectedColor = Color(0xFF6B4C5E)
val FilterUnselectedColor = Color(0xFFE8E8E8)

// حالات الطلبات
enum class RequestStatus(val arabicName: String, val color: Color, val bgColor: Color, val icon: ImageVector) {
    PENDING("قيد الانتظار", StatusIconColor, StatusBg, Icons.Default.AccessTimeFilled),
    ACCEPTED("مقبول", Color(0xFF4CAF50), Color(0xFFE8F5E9), Icons.Default.CheckCircle),
    REJECTED("مرفوض", Color(0xFFE53935), Color(0xFFFFEBEE), Icons.Default.Cancel)
}

// أنواع الفلاتر للبوتوم شيت
enum class FilterType(val arabicName: String) {
    ALL("الكل"),
    PENDING("قيد الانتظار"),
    ACCEPTED("مقبولة"),
    REJECTED("مرفوضة"),
    OFFICIAL_TASK("مهمة رسمية"),
    SALARY_ADJUSTMENT("بدل مهمة"),
    VACATION("إجازة"),
    CERTIFICATE("شهادة"),
    FILE_MODIFICATION("تعديل البيانات")
}

enum class TabType {
    PERSONAL, FOR_APPROVAL
}

data class Order(
    val id: String,
    val title: String,
    val description: String,
    val submitterName: String,
    val jobTitle: String,
    val department: String,
    val amount: String,
    val date: String,
    val duration: String,
    val status: RequestStatus,
    val isForApproval: Boolean = false,
    val type: FilterType = FilterType.OFFICIAL_TASK
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyRequestScreen(
    navController: NavController,
    onNavigateToAddRequest: (() -> Unit)? = null
) {
    var selectedTab by remember { mutableStateOf(TabType.PERSONAL) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedOrder by remember { mutableStateOf<Order?>(null) }
    var showDetailsBottomSheet by remember { mutableStateOf(false) }
    var showFilterBottomSheet by remember { mutableStateOf(false) }
    var selectedStatusFilter by remember { mutableStateOf<FilterType>(FilterType.ALL) }
    var selectedTypeFilter by remember { mutableStateOf<FilterType>(FilterType.ALL) }

    val detailsSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val filterSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
    val scope = rememberCoroutineScope()

    // طلباتي الشخصية
    val personalOrders = remember {
        listOf(
            Order(
                id = "1000010",
                title = "طلب موافقة على مهمة رسمية",
                description = "مهمة رسمية إلى فرنسا - مدة 3 أيام",
                submitterName = "معالي الشيخ خالد بن عبدالله آل ثاني",
                jobTitle = "الرقم الوظيفي",
                department = "وزير الداخلية",
                amount = "51000 ريال",
                date = "8 January 2026",
                duration = "3 يوم",
                status = RequestStatus.PENDING,
                isForApproval = false,
                type = FilterType.OFFICIAL_TASK
            ),
            Order(
                id = "1000011",
                title = "طلب إجازة سنوية",
                description = "إجازة سنوية لمدة أسبوع",
                submitterName = "معالي الشيخ خالد بن عبدالله آل ثاني",
                jobTitle = "الرقم الوظيفي",
                department = "وزير الداخلية",
                amount = "",
                date = "5 January 2026",
                duration = "7 يوم",
                status = RequestStatus.ACCEPTED,
                isForApproval = false,
                type = FilterType.VACATION
            ),
            Order(
                id = "1000012",
                title = "طلب بدل مهمة",
                description = "بدل سفر للمؤتمر الدولي",
                submitterName = "معالي الشيخ خالد بن عبدالله آل ثاني",
                jobTitle = "الرقم الوظيفي",
                department = "وزير الداخلية",
                amount = "25000 ريال",
                date = "3 January 2026",
                duration = "2 يوم",
                status = RequestStatus.REJECTED,
                isForApproval = false,
                type = FilterType.SALARY_ADJUSTMENT
            ),
            Order(
                id = "1000013",
                title = "طلب شهادة خبرة",
                description = "شهادة خبرة للعمل السابق",
                submitterName = "معالي الشيخ خالد بن عبدالله آل ثاني",
                jobTitle = "الرقم الوظيفي",
                department = "وزير الداخلية",
                amount = "",
                date = "10 January 2026",
                duration = "",
                status = RequestStatus.PENDING,
                isForApproval = false,
                type = FilterType.CERTIFICATE
            ),
            Order(
                id = "1000014",
                title = "طلب تعديل البيانات الشخصية",
                description = "تعديل رقم الهاتف والعنوان",
                submitterName = "معالي الشيخ خالد بن عبدالله آل ثاني",
                jobTitle = "الرقم الوظيفي",
                department = "وزير الداخلية",
                amount = "",
                date = "12 January 2026",
                duration = "",
                status = RequestStatus.ACCEPTED,
                isForApproval = false,
                type = FilterType.FILE_MODIFICATION
            ),
            Order(
                id = "1000015",
                title = "طلب موافقة مهمة رسمية إلى الإمارات",
                description = "مهمة رسمية إلى دبي لحضور المؤتمر",
                submitterName = "معالي الشيخ خالد بن عبدالله آل ثاني",
                jobTitle = "الرقم الوظيفي",
                department = "وزير الداخلية",
                amount = "35000 ريال",
                date = "14 January 2026",
                duration = "5 يوم",
                status = RequestStatus.PENDING,
                isForApproval = false,
                type = FilterType.OFFICIAL_TASK
            ),
            Order(
                id = "1000016",
                title = "طلب إجازة مرضية",
                description = "إجازة مرضية لمدة يومين",
                submitterName = "معالي الشيخ خالد بن عبدالله آل ثاني",
                jobTitle = "الرقم الوظيفي",
                department = "وزير الداخلية",
                amount = "",
                date = "2 January 2026",
                duration = "2 يوم",
                status = RequestStatus.REJECTED,
                isForApproval = false,
                type = FilterType.VACATION
            ),
            Order(
                id = "1000017",
                title = "طلب بدل انتقال",
                description = "بدل انتقال لمهمة خارجية",
                submitterName = "معالي الشيخ خالد بن عبدالله آل ثاني",
                jobTitle = "الرقم الوظيفي",
                department = "وزير الداخلية",
                amount = "15000 ريال",
                date = "9 January 2026",
                duration = "1 يوم",
                status = RequestStatus.ACCEPTED,
                isForApproval = false,
                type = FilterType.SALARY_ADJUSTMENT
            ),
            Order(
                id = "1000018",
                title = "طلب شهادة راتب",
                description = "شهادة راتب للبنك",
                submitterName = "معالي الشيخ خالد بن عبدالله آل ثاني",
                jobTitle = "الرقم الوظيفي",
                department = "وزير الداخلية",
                amount = "",
                date = "7 January 2026",
                duration = "",
                status = RequestStatus.PENDING,
                isForApproval = false,
                type = FilterType.CERTIFICATE
            ),
            Order(
                id = "1000019",
                title = "طلب موافقة مهمة رسمية للسعودية",
                description = "مهمة رسمية إلى الرياض - اجتماع وزاري",
                submitterName = "معالي الشيخ خالد بن عبدالله آل ثاني",
                jobTitle = "الرقم الوظيفي",
                department = "وزير الداخلية",
                amount = "42000 ريال",
                date = "11 January 2026",
                duration = "4 يوم",
                status = RequestStatus.ACCEPTED,
                isForApproval = false,
                type = FilterType.OFFICIAL_TASK
            ),
            Order(
                id = "1000020",
                title = "طلب تعديل بيانات الحساب البنكي",
                description = "تحديث معلومات الحساب البنكي",
                submitterName = "معالي الشيخ خالد بن عبدالله آل ثاني",
                jobTitle = "الرقم الوظيفي",
                department = "وزير الداخلية",
                amount = "",
                date = "6 January 2026",
                duration = "",
                status = RequestStatus.REJECTED,
                isForApproval = false,
                type = FilterType.FILE_MODIFICATION
            ),
            Order(
                id = "1000021",
                title = "طلب إجازة طارئة",
                description = "إجازة طارئة لظرف عائلي",
                submitterName = "معالي الشيخ خالد بن عبدالله آل ثاني",
                jobTitle = "الرقم الوظيفي",
                department = "وزير الداخلية",
                amount = "",
                date = "4 January 2026",
                duration = "3 يوم",
                status = RequestStatus.PENDING,
                isForApproval = false,
                type = FilterType.VACATION
            ),
            Order(
                id = "1000022",
                title = "طلب بدل سكن",
                description = "بدل سكن للعام الجديد",
                submitterName = "معالي الشيخ خالد بن عبدالله آل ثاني",
                jobTitle = "الرقم الوظيفي",
                department = "وزير الداخلية",
                amount = "18000 ريال",
                date = "1 January 2026",
                duration = "",
                status = RequestStatus.ACCEPTED,
                isForApproval = false,
                type = FilterType.SALARY_ADJUSTMENT
            ),
            Order(
                id = "1000023",
                title = "طلب شهادة للسفارة",
                description = "شهادة من العمل للسفارة الأمريكية",
                submitterName = "معالي الشيخ خالد بن عبدالله آل ثاني",
                jobTitle = "الرقم الوظيفي",
                department = "وزير الداخلية",
                amount = "",
                date = "15 January 2026",
                duration = "",
                status = RequestStatus.PENDING,
                isForApproval = false,
                type = FilterType.CERTIFICATE
            ),
            Order(
                id = "1000024",
                title = "طلب موافقة مهمة رسمية لمصر",
                description = "مهمة رسمية إلى القاهرة - لقاء وزاري",
                submitterName = "معالي الشيخ خالد بن عبدالله آل ثاني",
                jobTitle = "الرقم الوظيفي",
                department = "وزير الداخلية",
                amount = "38000 ريال",
                date = "13 January 2026",
                duration = "6 يوم",
                status = RequestStatus.REJECTED,
                isForApproval = false,
                type = FilterType.OFFICIAL_TASK
            )
        )
    }

    // طلبات الاعتماد
    val approvalOrders = remember {
        listOf(
            Order(
                id = "1000010",
                title = "معالي الشيخ خالد بن عبدالله آل ثاني",
                description = "طلب بدل مهمة رسمية",
                submitterName = "معالي الشيخ خالد بن عبدالله آل ثاني",
                jobTitle = "الرقم الوظيفي",
                department = "وزير الداخلية",
                amount = "125,000 ريال",
                date = "13 Jan 2026",
                duration = "7 يوم",
                status = RequestStatus.PENDING,
                isForApproval = true,
                type = FilterType.SALARY_ADJUSTMENT
            ),
            Order(
                id = "1000016",
                title = "معالي الشيخ خالد بن عبدالله آل ثاني",
                description = "طلب بدل مهمة رسمية",
                submitterName = "معالي الشيخ خالد بن عبدالله آل ثاني",
                jobTitle = "",
                department = "",
                amount = "85,000 ريال",
                date = "13 Jan 2026",
                duration = "",
                status = RequestStatus.ACCEPTED,
                isForApproval = true,
                type = FilterType.SALARY_ADJUSTMENT
            ),
            Order(
                id = "1000076",
                title = "معالي الشيخ خالد بن عبدالله آل ثاني",
                description = "طلباً إجازة",
                submitterName = "معالي الشيخ خالد بن عبدالله آل ثاني",
                jobTitle = "",
                department = "",
                amount = "",
                date = "11 Jan 2026",
                duration = "5 يوم",
                status = RequestStatus.REJECTED,
                isForApproval = true,
                type = FilterType.VACATION
            ),
            Order(
                id = "1000077",
                title = "أحمد محمد السيد",
                description = "طلب موافقة مهمة رسمية",
                submitterName = "أحمد محمد السيد",
                jobTitle = "",
                department = "",
                amount = "45,000 ريال",
                date = "14 Jan 2026",
                duration = "4 يوم",
                status = RequestStatus.PENDING,
                isForApproval = true,
                type = FilterType.OFFICIAL_TASK
            ),
            Order(
                id = "1000078",
                title = "فاطمة علي الكعبي",
                description = "طلب شهادة خبرة",
                submitterName = "فاطمة علي الكعبي",
                jobTitle = "",
                department = "",
                amount = "",
                date = "12 Jan 2026",
                duration = "",
                status = RequestStatus.ACCEPTED,
                isForApproval = true,
                type = FilterType.CERTIFICATE
            ),
            Order(
                id = "1000079",
                title = "محمد عبدالله النعيمي",
                description = "طلب تعديل البيانات",
                submitterName = "محمد عبدالله النعيمي",
                jobTitle = "",
                department = "",
                amount = "",
                date = "10 Jan 2026",
                duration = "",
                status = RequestStatus.PENDING,
                isForApproval = true,
                type = FilterType.FILE_MODIFICATION
            )
        )
    }

    val displayedOrders = if (selectedTab == TabType.PERSONAL) personalOrders else approvalOrders

    // تطبيق الفلاتر والبحث
    val filteredOrders = remember(displayedOrders, searchQuery, selectedStatusFilter, selectedTypeFilter) {
        displayedOrders.filter { order ->
            // فلتر البحث - البحث في العنوان
            val matchesSearch = if (searchQuery.isBlank()) {
                true
            } else {
                order.title.contains(searchQuery, ignoreCase = true) ||
                order.description.contains(searchQuery, ignoreCase = true)
            }

            // فلتر الحالة
            val matchesStatus = when (selectedStatusFilter) {
                FilterType.ALL -> true
                FilterType.PENDING -> order.status == RequestStatus.PENDING
                FilterType.ACCEPTED -> order.status == RequestStatus.ACCEPTED
                FilterType.REJECTED -> order.status == RequestStatus.REJECTED
                else -> true
            }

            // فلتر النوع
            val matchesType = when (selectedTypeFilter) {
                FilterType.ALL -> true
                FilterType.OFFICIAL_TASK -> order.type == FilterType.OFFICIAL_TASK
                FilterType.SALARY_ADJUSTMENT -> order.type == FilterType.SALARY_ADJUSTMENT
                FilterType.VACATION -> order.type == FilterType.VACATION
                FilterType.CERTIFICATE -> order.type == FilterType.CERTIFICATE
                FilterType.FILE_MODIFICATION -> order.type == FilterType.FILE_MODIFICATION
                else -> true
            }

            matchesSearch && matchesStatus && matchesType
        }
    }

    Scaffold(
        containerColor = LightGrayBg
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                RequestsTopBar(
                    showAddButton = selectedTab == TabType.PERSONAL,
                    onAddClick = {
                        onNavigateToAddRequest?.invoke() ?: navController.navigate(NavRoutes.AddRequestRoute.route)
                    },
                    ordersCount = filteredOrders.size
                )

                RequestsTabRow(
                    selectedTab = selectedTab,
                    onTabSelected = { selectedTab = it }
                )

                Spacer(modifier = Modifier.height(12.dp))

                SearchBar(
                    query = searchQuery,
                    onQueryChange = { searchQuery = it },
                    onFilterClick = { showFilterBottomSheet = true },
                    selectedTab = selectedTab,
                    modifier = Modifier.padding(horizontal = 16.dp),
                )

                Spacer(modifier = Modifier.height(16.dp))

                // القائمة مع الأنيميشن
                Crossfade(
                    targetState = selectedTab,
                    animationSpec = tween(
                        durationMillis = 200,
                        easing = FastOutSlowInEasing
                    ),
                    label = "orders_list_animation"
                ) { tabState ->
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(
                            items = filteredOrders,
                            key = { "${tabState.name}_${it.id}" }
                        ) { order ->
                            OrderCard(
                                order = order,
                                onClick = {
                                    selectedOrder = order
                                    showDetailsBottomSheet = true
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    // Details Bottom Sheet
    if (showDetailsBottomSheet && selectedOrder != null) {
        OrderDetailsBottomSheet(
            order = selectedOrder!!,
            sheetState = detailsSheetState,
            onDismiss = {
                scope.launch {
                    detailsSheetState.hide()
                    showDetailsBottomSheet = false
                }
            }
        )
    }

    // Filter Bottom Sheet
    if (showFilterBottomSheet) {
        FilterBottomSheet(
            sheetState = filterSheetState,
            selectedStatusFilter = selectedStatusFilter,
            selectedTypeFilter = selectedTypeFilter,
            onStatusFilterChange = { filter ->
                selectedStatusFilter = filter
            },
            onTypeFilterChange = { filter ->
                selectedTypeFilter = filter
            },
            onDismiss = {
                scope.launch {
                    filterSheetState.hide()
                    showFilterBottomSheet = false
                }
            },
            onApply = {
                scope.launch {
                    filterSheetState.hide()
                    showFilterBottomSheet = false
                }
            },
            selectedTab = selectedTab,
            displayedOrders = displayedOrders
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterBottomSheet(
    sheetState: SheetState,
    selectedStatusFilter: FilterType,
    selectedTypeFilter: FilterType,
    onStatusFilterChange: (FilterType) -> Unit,
    onTypeFilterChange: (FilterType) -> Unit,
    onDismiss: () -> Unit,
    onApply: () -> Unit,
    selectedTab: TabType,
    displayedOrders: List<Order>
) {
    // حساب عدد الطلبات لكل حالة
    val allCount = displayedOrders.size
    val pendingCount = displayedOrders.count { it.status == RequestStatus.PENDING }
    val acceptedCount = displayedOrders.count { it.status == RequestStatus.ACCEPTED }
    val rejectedCount = displayedOrders.count { it.status == RequestStatus.REJECTED }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        containerColor = LightGrayBg,
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
                .padding(horizontal = 24.dp)
                .padding(bottom = 24.dp)
        ) {
            Text(
                "تصنيف النتائج",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            // الحالة Section - فقط في طلباتي
            if (selectedTab == TabType.PERSONAL) {
                Text(
                    "الحالة",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    textAlign = TextAlign.Start,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Row 1: إلغاء والكل
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    FilterButton(
                        text = FilterType.ALL.arabicName,
                        count = allCount,
                        isSelected = selectedStatusFilter == FilterType.ALL,
                        onClick = { onStatusFilterChange(FilterType.ALL) },
                        modifier = Modifier.weight(1f)
                    )
                    FilterButton(
                        text = FilterType.PENDING.arabicName,
                        count = pendingCount,
                        isSelected = selectedStatusFilter == FilterType.PENDING,
                        onClick = { onStatusFilterChange(FilterType.PENDING) },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Row 2: مرفوضة ومقبولة
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    FilterButton(
                        text = FilterType.REJECTED.arabicName,
                        count = rejectedCount,
                        isSelected = selectedStatusFilter == FilterType.REJECTED,
                        onClick = { onStatusFilterChange(FilterType.REJECTED) },
                        modifier = Modifier.weight(1f)
                    )

                    FilterButton(
                        text = FilterType.ACCEPTED.arabicName,
                        count = acceptedCount,
                        isSelected = selectedStatusFilter == FilterType.ACCEPTED,
                        onClick = { onStatusFilterChange(FilterType.ACCEPTED) },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
            }

            // النوع Section
            Text(
                "النوع",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                textAlign = TextAlign.Start,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Row 1: مهمة رسمية، بدل مهمة، الكل
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterButton(
                    text = FilterType.OFFICIAL_TASK.arabicName,
                    count = null,
                    isSelected = selectedTypeFilter == FilterType.OFFICIAL_TASK,
                    onClick = { onTypeFilterChange(FilterType.OFFICIAL_TASK) },
                    modifier = Modifier.weight(1f),
                    isSmall = true
                )
                FilterButton(
                    text = FilterType.SALARY_ADJUSTMENT.arabicName,
                    count = null,
                    isSelected = selectedTypeFilter == FilterType.SALARY_ADJUSTMENT,
                    onClick = { onTypeFilterChange(FilterType.SALARY_ADJUSTMENT) },
                    modifier = Modifier.weight(1f),
                    isSmall = true
                )
                FilterButton(
                    text = FilterType.ALL.arabicName,
                    count = null,
                    isSelected = selectedTypeFilter == FilterType.ALL,
                    onClick = { onTypeFilterChange(FilterType.ALL) },
                    modifier = Modifier.weight(1f),
                    isSmall = true
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Row 2: تعديل البيانات، شهادة، إجازة
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterButton(
                    text = FilterType.FILE_MODIFICATION.arabicName,
                    count = null,
                    isSelected = selectedTypeFilter == FilterType.FILE_MODIFICATION,
                    onClick = { onTypeFilterChange(FilterType.FILE_MODIFICATION) },
                    modifier = Modifier.weight(1f),
                    isSmall = true
                )

                FilterButton(
                    text = FilterType.CERTIFICATE.arabicName,
                    count = null,
                    isSelected = selectedTypeFilter == FilterType.CERTIFICATE,
                    onClick = { onTypeFilterChange(FilterType.CERTIFICATE) },
                    modifier = Modifier.weight(1f),
                    isSmall = true
                )

                FilterButton(
                    text = FilterType.VACATION.arabicName,
                    count = null,
                    isSelected = selectedTypeFilter == FilterType.VACATION,
                    onClick = { onTypeFilterChange(FilterType.VACATION) },
                    modifier = Modifier.weight(1f),
                    isSmall = true
                )
            }

//            Spacer(modifier = Modifier.height(24.dp))
//
//            // Apply Button
//            Button(
//                onClick = onApply,
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .height(56.dp),
//                shape = RoundedCornerShape(12.dp),
//                colors = ButtonDefaults.buttonColors(
//                    containerColor = DarkRedTab
//                )
//            ) {
//                Text(
//                    "تطبيق",
//                    fontSize = 16.sp,
//                    fontWeight = FontWeight.Bold
//                )
//            }
        }
    }
}

@Composable
fun FilterButton(
    text: String,
    count: Int?,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isSmall: Boolean = false
) {
    val backgroundColor = if (isSelected) DarkRedTab else CardWhite
    val contentColor = if (isSelected) Color.White else Color.Black
    val height = if (isSmall) 48.dp else 60.dp

    Card(
        onClick = onClick,
        modifier = modifier.height(height),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        ),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(if (isSmall) 8.dp else 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text,
                fontSize = if (isSmall) 13.sp else 14.sp,
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                color = contentColor,
                textAlign = TextAlign.Center
            )
            if (count != null) {
//                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    "$count",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = contentColor
                )
            }
        }
    }
}

@Composable
fun RequestsTopBar(
    showAddButton: Boolean,
    onAddClick: () -> Unit,
    ordersCount: Int
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(LightGrayBg)
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                "الطلبات",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = DarkBlueText
            )
            Text(
                "$ordersCount طلب",
                fontSize = 13.sp,
                color = GrayText
            )
        }
        if (showAddButton) {
            IconButton(
                onClick = onAddClick,
                modifier = Modifier
                    .size(48.dp)
                    .background(DarkRedTab, CircleShape)
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "إضافة",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
        } else {
            Spacer(modifier = Modifier.width(48.dp))
        }
    }
}

@Composable
fun RequestsTabRow(
    selectedTab: TabType,
    onTabSelected: (TabType) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        TabButton(
            text = "طلباتي",
            icon = Icons.Default.FileCopy,
            isSelected = selectedTab == TabType.PERSONAL,
            onClick = { onTabSelected(TabType.PERSONAL) },
            modifier = Modifier.weight(1f)
        )

        TabButton(
            text = "طلبات الاعتماد",
            icon = Icons.Default.CheckCircle,
            isSelected = selectedTab == TabType.FOR_APPROVAL,
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
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val (selectedColor, unselectedColor) = if (text == "طلباتي") {
        Pair(DarkBlueTab, LightBlueTab)
    } else {
        Pair(DarkRedTab, LightRedTab)
    }

    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected) selectedColor else unselectedColor,
        animationSpec = tween(300)
    )

    val contentColor by animateColorAsState(
        targetValue = if (isSelected) Color.White else selectedColor,
        animationSpec = tween(300)
    )

    Surface(
        onClick = onClick,
        modifier = modifier.height(48.dp),
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
                fontSize = 14.sp,
                fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal
            )
        }
    }
}

@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onFilterClick: () -> Unit,
    selectedTab: TabType,
    modifier: Modifier = Modifier
) {
    val filterColor = if (selectedTab == TabType.PERSONAL) DarkBlueTab else DarkRedTab

    val animatedFilterColor by animateColorAsState(
        targetValue = filterColor,
        animationSpec = tween(300)
    )

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(50.dp)
                .background(DividerColor, shape = RoundedCornerShape(14.dp)),
            contentAlignment = Alignment.Center,
        ) {
            IconButton(
                onClick = onFilterClick,
                modifier = Modifier
                    .background(animatedFilterColor, CircleShape)
                    .size(26.dp)
            ) {
                Icon(
                    Icons.Default.FilterList,
                    contentDescription = "Filter",
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
// في الكود بتاعك
        SearchTextField(
            query = query,
            onQueryChange = onQueryChange,
            selectedTab = selectedTab,
            modifier = Modifier.weight(1f)
        )
//        OutlinedTextField(
//            value = query,
//            onValueChange = onQueryChange,
//            modifier = Modifier
//                .weight(1f)
//                .height(50.dp)
//                .align((Alignment.CenterVertically)),
//            leadingIcon = {
//                Icon(
//                    Icons.Default.Search,
//                    contentDescription = "Search",
//                    tint = GrayText,
//                )
//            },
//            placeholder = {
//                Text(
//                    "ابحث في الطلبات",
//                    color = GrayText,
//                    fontSize = 15.sp,
//                    textAlign = TextAlign.Center,
//                    modifier = Modifier.height(25.dp)
//                )
//            },
//            shape = RoundedCornerShape(16.dp),
//            colors = OutlinedTextFieldDefaults.colors(
//                unfocusedContainerColor = DividerColor,
//                focusedContainerColor = DividerColor,
//                unfocusedBorderColor = Color.Transparent,
//                focusedBorderColor = animatedFilterColor
//            ),
//            singleLine = true
//        )
    }
}
@Composable
fun SearchTextField(
    query: String,
    onQueryChange: (String) -> Unit,
    selectedTab: TabType,
    modifier: Modifier = Modifier

) {
    val filterColor = if (selectedTab == TabType.PERSONAL) DarkBlueTab else DarkRedTab
    val focusRequester = remember { FocusRequester() }
    var isFocused by remember { mutableStateOf(false) }

    val animatedFilterColor by animateColorAsState(
        targetValue = filterColor,
        animationSpec = tween(300)
    )

    val borderColor by animateColorAsState(
        targetValue = if (isFocused || query.isNotEmpty()) animatedFilterColor else Color.Transparent,
        animationSpec = tween(300)
    )

    BasicTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier
            .height(50.dp)
            .background(
                color = DividerColor,
                shape = RoundedCornerShape(16.dp)
            )
            .border(
                width = 1.5.dp,
                color = borderColor,
                shape = RoundedCornerShape(16.dp)
            )
            .onFocusChanged { focusState ->
                isFocused = focusState.isFocused
            }
            .focusRequester(focusRequester),
        singleLine = true,
        textStyle = TextStyle(
            fontSize = 15.sp,
            textAlign = TextAlign.Start // للعربي
        ),
        decorationBox = { innerTextField ->
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = if (isFocused) animatedFilterColor else GrayText
                )

                Spacer(modifier = Modifier.width(12.dp))

                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.CenterStart
                ) {
                    if (query.isEmpty()) {
                        Text(
                            text = "ابحث في الطلبات",
                            color = GrayText,
                            fontSize = 15.sp
                        )
                    }
                    innerTextField()
                }
            }
        }
    )
}
@Composable
fun OrderCard(
    order: Order,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // النصوص على اليمين
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        order.title,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2C3E50),
                        textAlign = TextAlign.Start
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        order.description,
                        fontSize = 12.sp,
                        color = Color(0xFF7C8A99),
                        textAlign = TextAlign.Start,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        "# ${order.id}",
                        fontSize = 11.sp,
                        color = Color(0xFFB8C1CC),
                        textAlign = TextAlign.Start
                    )
                }
                // Status Box على اليسار
                if (!order.isForApproval) {
                    StatusBox(status = order.status)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = DividerColor, thickness = 1.dp)
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    InfoItem(Icons.Default.CalendarMonth, order.date)
                    if (order.amount.isNotEmpty()) {
                        InfoItem(Icons.Default.Payments, order.amount)
                    }
                    if (order.duration.isNotEmpty()) {
                        InfoItem(Icons.Default.AccessTime, order.duration)
                    }
                }
                Icon(
                    Icons.Default.ChevronLeft,
                    contentDescription = "تفاصيل",
                    tint = Color(0xFFBDBDBD),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
fun StatusBox(status: RequestStatus) {
    Column(
        modifier = Modifier
            .size(width = 70.dp, height = 70.dp)
            .background(status.bgColor, RoundedCornerShape(12.dp)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            status.icon,
            contentDescription = null,
            tint = status.color,
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            status.arabicName,
            fontSize = 9.sp,
            color = status.color,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center,
            lineHeight = 12.sp
        )
    }
}

@Composable
fun InfoItem(icon: ImageVector, text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = Color(0xFF3A4857),
            modifier = Modifier.size(14.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(text, fontSize = 11.sp, color = Color(0xFF3A4857))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderDetailsBottomSheet(
    order: Order,
    sheetState: SheetState,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        containerColor = Color(0xFFF6F7F8),
        dragHandle = null
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth()
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF8F9FA))
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "تفاصيل الطلب",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0D4261),
                    textAlign = TextAlign.Start
                )
                Spacer(modifier = Modifier.weight(1f))
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        Icons.Default.ChevronLeft,
                        contentDescription = "إغلاق",
                        tint = Color(0xFF721D3C),
                        modifier = Modifier.size(28.dp)
                    )
                }

            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                }

                // بيانات مقدم الطلب
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth().border(width = 1.dp , color = Color(0xFFE4D8D9) , shape = RoundedCornerShape(16.dp)),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = CardWhite),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp)
                        ) {
                            // إذا كان من طلبات الاعتماد - نعرض الصورة
                            if (order.isForApproval) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    // الصورة
                                    Box(
                                        modifier = Modifier
                                            .size(56.dp)
                                            .background(
                                                Color(0xFFE8E8E8),
                                                CircleShape
                                            )
                                            .border(
                                                width = 2.dp,
                                                color = Color(0xFFD7BEC4),
                                                shape = CircleShape
                                            )
                                    ) {
                                        Image(
                                            painter = painterResource(id = R.drawable.user_profile_icon),
                                            contentDescription = "صورة المستخدم",
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .clip(CircleShape)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(
                                        "مقدم الطلب",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF0D4261),
                                        textAlign = TextAlign.Start
                                    )
                                }

                                Spacer(modifier = Modifier.height(16.dp))
                                HorizontalDivider(color = Color(0xFFF0F0F0), thickness = 1.dp)
                                Spacer(modifier = Modifier.height(16.dp))

                                DetailRow("الاسم", order.submitterName)
                                Spacer(modifier = Modifier.height(16.dp))
                                DetailRow("الرقم الوظيفي", "EMP-2024-1547")
                                Spacer(modifier = Modifier.height(16.dp))
                                DetailRow("المسمى الوظيفي", order.department)
                            } else {
                                // التصميم العادي لطلباتي
                                Text(
                                    "بيانات مقدم الطلب",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF0D4261),
                                    textAlign = TextAlign.Start,
                                    modifier = Modifier.fillMaxWidth()
                                )

                                Spacer(modifier = Modifier.height(16.dp))
                                HorizontalDivider(color = Color(0xFFF0F0F0), thickness = 1.dp)
                                Spacer(modifier = Modifier.height(16.dp))

                                DetailRow("مقدم الطلب", order.submitterName)
                                Spacer(modifier = Modifier.height(16.dp))
                                DetailRow("الرقم الوظيفي", "EMP-2024-1547")
                                Spacer(modifier = Modifier.height(16.dp))
                                DetailRow("المسمى الوظيفي", order.department)
                            }
                        }
                    }
                }
//                // بيانات مقدم الطلب
//                item {
//                    Card(
//                        modifier = Modifier.fillMaxWidth().border(width = 1.dp , color = Color(0xFFE4D8D9) , shape = RoundedCornerShape(16.dp)),
//                        shape = RoundedCornerShape(16.dp),
//                        colors = CardDefaults.cardColors(containerColor = CardWhite),
//                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
//                    ) {
//                        Column(
//                            modifier = Modifier.padding(20.dp)
//                        ) {
//                            Text(
//                                "بيانات مقدم الطلب",
//                                fontSize = 15.sp,
//                                fontWeight = FontWeight.Bold,
//                                color = Color(0xFF0D4261),
//                                textAlign = TextAlign.Start,
//                                modifier = Modifier.fillMaxWidth()
//                            )
//
//                            Spacer(modifier = Modifier.height(16.dp))
//                            HorizontalDivider(color = Color(0xFFF0F0F0), thickness = 1.dp)
//                            Spacer(modifier = Modifier.height(16.dp))
//
//                            DetailRow("مقدم الطلب", order.submitterName)
//                            Spacer(modifier = Modifier.height(16.dp))
//                            DetailRow("الرقم الوظيفي", "EMP-2024-1547")
//                            Spacer(modifier = Modifier.height(16.dp))
//                            DetailRow("المسمى الوظيفي", order.department)
//                        }
//                    }
//                }

                // معلومات الطلب
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth().border(width = 1.dp , color = Color(0xFFE4D8D9) , shape = RoundedCornerShape(16.dp)),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = CardWhite),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp)
                        ) {
                            // Row الرئيسي
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Column جواه التايتل والوصف
                                Column(
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(
                                        "معلومات الطلب",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF0D4261)
                                    )
                                    Text(
                                        order.title,
                                        fontSize = 13.sp,
                                        color = Color(0xFF7C8A99),
                                        textAlign = TextAlign.Start,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }

                                // Status badge - يظهر فقط لو مش من طلبات الاعتماد
                                if (!order.isForApproval) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                                        modifier = Modifier
                                            .background(
                                                order.status.bgColor,
                                                RoundedCornerShape(20.dp)
                                            )
                                            .padding(horizontal = 14.dp, vertical = 4.dp)
                                    ) {
                                        Text(
                                            order.status.arabicName,
                                            fontSize = 12.sp,
                                            color = order.status.color,
                                            fontWeight = FontWeight.Medium
                                        )
                                        Icon(
                                            order.status.icon,
                                            contentDescription = null,
                                            tint = order.status.color,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            HorizontalDivider(color = Color(0xFFF0F0F0), thickness = 1.dp)
                            Spacer(modifier = Modifier.height(16.dp))

                            // التفاصيل
                            DetailRow("رقم الطلب", order.id)
                            Spacer(modifier = Modifier.height(16.dp))
                            DetailRow("تاريخ الطلب", order.date)
                            Spacer(modifier = Modifier.height(16.dp))
                            DetailRow("الوصف", order.description)
                            if (order.amount.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(16.dp))
                                DetailRow("المبلغ المستحق", order.amount , colors = DarkRedTab)
                            }
                            if (order.duration.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(16.dp))
                                DetailRow("عدد الأيام", order.duration ,  colors = DarkRedTab)
                            }
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(40.dp))
                }
            }
        }
    }
}

@Composable
fun DetailRow(label: String, value: String , colors: Color = DarkBlueText ) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Text(
            label,
            fontSize = 13.sp,
            color = GrayText,
            textAlign = TextAlign.Start
        )
        Spacer(modifier = Modifier.weight(1f))
        Text(
            value,
            fontSize = 13.sp,
            color = colors,
            textAlign = TextAlign.End,
            maxLines = 1
//            modifier = Modifier.weight(1f)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun MyRequestsPreview() {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        MyRequestScreen(navController = rememberNavController())
    }
}