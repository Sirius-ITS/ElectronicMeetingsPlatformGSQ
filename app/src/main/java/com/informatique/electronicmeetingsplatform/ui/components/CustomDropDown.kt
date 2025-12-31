package com.informatique.electronicmeetingsplatform.ui.components

import com.informatique.electronicmeetingsplatform.R
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowDropDown
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.informatique.electronicmeetingsplatform.ui.components.localizedApp
import com.informatique.electronicmeetingsplatform.ui.theme.LocalExtraColors
import kotlinx.coroutines.launch

data class DropdownSection(
    val title: String,
    val items: List<String>
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomDropdown(
    label: String,
    options: List<String>,
    selectedOption: String?,
    onOptionSelected: (String) -> Unit,
    error: String? = null,
    mandatory: Boolean = false,
    enabled: Boolean = true,
    leadingIcon: ImageVector? = null,
    placeholder: String? = null,
    isLoading: Boolean = false,
    loadingMessage: String? = "جاري التحميل...",
    maxLength: Int? = null,
    minLength: Int? = null,
    enableSections: Boolean = false,
    sections: List<DropdownSection> = emptyList()
) {
    var showSheet by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
    val coroutineScope = rememberCoroutineScope()
    val extraColors = LocalExtraColors.current

    val filteredFlatOptions = if (enableSections) {
        sections.flatMap { section ->
            section.items.filter { it.contains(searchQuery, ignoreCase = true) }
        }
    } else {
        options.filter { it.contains(searchQuery, ignoreCase = true) }
    }

    val filteredSections = if (enableSections) {
        sections.mapNotNull { section ->
            val filteredItems = section.items.filter { it.contains(searchQuery, ignoreCase = true) }
            if (filteredItems.isNotEmpty()) section.copy(items = filteredItems) else null
        }
    } else emptyList()

    val isInteractionEnabled = enabled && !isLoading

    Column(modifier = Modifier.fillMaxWidth()) {
        // Label
        if (label.isNotEmpty()) {
            Text(
                text = buildAnnotatedString {
                    withStyle(style = SpanStyle(color = extraColors.whiteInDarkMode)) {
                        append(label)
                    }
                    if (mandatory) {
                        append(" ")
                        withStyle(style = SpanStyle(color = Color.Red)) {
                            append("*")
                        }
                    }
                },
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = when {
                    error != null -> Color(0xFFE74C3C)
                    !isInteractionEnabled -> extraColors.whiteInDarkMode.copy(alpha = 0.38f)
                    else -> extraColors.whiteInDarkMode
                },
                modifier = Modifier.padding(bottom = 8.dp, start = 4.dp)
            )
        }

        // Dropdown Field
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(enabled = isInteractionEnabled) {
                    showSheet = true
                }
        ) {
            OutlinedTextField(
                value = selectedOption ?: "",
                onValueChange = {},
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = extraColors.cardBackground,
                    unfocusedContainerColor = extraColors.cardBackground,
                    disabledContainerColor = extraColors.cardBackground,
                    focusedBorderColor = if (error != null) Color(0xFFE74C3C) else Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    disabledBorderColor = Color.Transparent,
                    focusedTextColor = extraColors.textSubTitle,
                    unfocusedTextColor = extraColors.textSubTitle,
                    disabledTextColor = extraColors.textSubTitle
                ),
                readOnly = true,
                enabled = false,
                placeholder = {
                    if ((selectedOption == null || selectedOption.isEmpty()) && placeholder != null) {
                        Text(
                            text = placeholder,
                            color = extraColors.textSubTitle,
                            fontSize = 16.sp
                        )
                    }
                },
                leadingIcon = if (leadingIcon != null) {
                    {
                        Icon(
                            imageVector = leadingIcon,
                            contentDescription = null,
                            tint = extraColors.iconBlueBackground,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                } else null,
                trailingIcon = {
                    when {
                        isLoading -> {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp,
                                color = extraColors.iconBlueBackground
                            )
                        }
                        error != null -> {
                            Icon(
                                imageVector = Icons.Default.Error,
                                contentDescription = "Error",
                                tint = Color(0xFFE74C3C)
                            )
                        }
                        else -> {
                            Icon(
                                imageVector = Icons.Rounded.ArrowDropDown,
                                contentDescription = "Dropdown Icon",
                                tint = if (isInteractionEnabled) extraColors.textSubTitle
                                else extraColors.textSubTitle.copy(alpha = 0.3f)
                            )
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                isError = error != null,
                singleLine = true
            )

            if (isLoading) {
                ShimmerEffect(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .padding(1.dp)
                )
            }
        }

        // Error or Loading Message
        if (error != null) {
            Text(
                text = error,
                fontSize = 12.sp,
                color = Color(0xFFE74C3C),
                modifier = Modifier.padding(top = 4.dp, start = 4.dp)
            )
        } else if (isLoading && loadingMessage != null) {
            Text(
                text = loadingMessage,
                fontSize = 12.sp,
                color = extraColors.iconBlueBackground,
                modifier = Modifier.padding(top = 4.dp, start = 4.dp)
            )
        }
    }

    // Bottom Sheet
    if (showSheet) {
        ModalBottomSheet(
            onDismissRequest = {
                showSheet = false
                searchQuery = ""
            },
            sheetState = sheetState,
            containerColor = extraColors.background,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
        ) {
            LaunchedEffect(Unit) {
                if (sheetState.hasPartiallyExpandedState) {
                    sheetState.partialExpand()
                }
            }
            val halfScreenHeight = LocalConfiguration.current.screenHeightDp.dp * 0.5f
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = halfScreenHeight)
            ) {
                // Header with Done button and title
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Done button
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(extraColors.cardBackground)
                            .clickable {
                                coroutineScope.launch {
                                    sheetState.hide()
                                    showSheet = false
                                    searchQuery = ""
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "تم",
                            color = extraColors.iconBlueBackground,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    // Title
                    Text(
                        text = if (mandatory) "$label *" else label,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = extraColors.whiteInDarkMode,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    // Invisible spacer for balance
                    Spacer(modifier = Modifier.size(48.dp))
                }

                // Search Field
                TextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Rounded.Search,
                            contentDescription = "Search Icon",
                            tint = extraColors.textSubTitle,
                            modifier = Modifier.size(22.dp)
                        )
                    },
                    placeholder = {
                        Text(
                            text = "", // localizedApp(R.string.search_go),
                            color = extraColors.textSubTitle,
                            fontSize = 15.sp,
                            textAlign = TextAlign.Start,
                            modifier = Modifier.fillMaxWidth()
                        )
                    },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 12.dp),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = extraColors.cardBackground,
                        unfocusedContainerColor = extraColors.cardBackground,
                        disabledContainerColor = extraColors.cardBackground,
                        cursorColor = extraColors.iconBlueBackground,
                        focusedTextColor = extraColors.whiteInDarkMode,
                        unfocusedTextColor = extraColors.whiteInDarkMode,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    shape = RoundedCornerShape(16.dp)
                )

                // Options List
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp)
                ) {
                    if (enableSections) {
                        // Sections mode
                        filteredSections.forEachIndexed { sectionIndex, section ->
                            item(key = "section_${sectionIndex}_${section.title}") {
                                Text(
                                    text = section.title,
                                    color = extraColors.textSubTitle,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.padding(
                                        horizontal = 8.dp,
                                        vertical = if (sectionIndex == 0) 0.dp else 8.dp
                                    )
                                )
                            }

                            items(section.items) { option ->
                                DropdownItemRow(
                                    option = option,
                                    isSelected = option == selectedOption,
                                    leadingIcon = leadingIcon,
                                    onClick = {
                                        onOptionSelected(option)
                                        searchQuery = ""
                                        coroutineScope.launch {
                                            sheetState.hide()
                                            showSheet = false
                                        }
                                    }
                                )

                                if (option != section.items.last()) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                }
                            }
                        }

                        if (filteredFlatOptions.isEmpty()) {
                            item {
                                EmptyStateBox()
                            }
                        }
                    } else {
                        // Regular list mode
                        items(filteredFlatOptions) { option ->
                            DropdownItemRow(
                                option = option,
                                isSelected = option == selectedOption,
                                leadingIcon = leadingIcon,
                                onClick = {
                                    onOptionSelected(option)
                                    searchQuery = ""
                                    coroutineScope.launch {
                                        sheetState.hide()
                                        showSheet = false
                                    }
                                }
                            )

                            if (option != filteredFlatOptions.last()) {
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                        }

                        if (filteredFlatOptions.isEmpty()) {
                            item {
                                EmptyStateBox()
                            }
                        }
                    }

                    // Bottom padding
                    item {
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun DropdownItemRow(
    option: String,
    isSelected: Boolean,
    leadingIcon: ImageVector?,
    onClick: () -> Unit
) {
    val extraColors = LocalExtraColors.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(
                if (isSelected)
                    extraColors.iconBlueBackground.copy(alpha = 0.15f)
                else
                    extraColors.cardBackground
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Leading icon
        if (leadingIcon != null) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(extraColors.iconBlueBackground.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = leadingIcon,
                    contentDescription = null,
                    tint = extraColors.iconBlueBackground,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
        }

        // Option text
        Text(
            text = option,
            color = extraColors.whiteInDarkMode,
            fontSize = 16.sp,
            fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal,
            textAlign = TextAlign.End
        )

        Spacer(modifier = Modifier.weight(1f))

        // Selection indicator (circle)
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(
                    if (isSelected)
                        extraColors.iconBlueBackground
                    else
                        Color.Transparent
                )
                .then(
                    if (!isSelected)
                        Modifier.border(
                            width = 2.dp,
                            color = extraColors.whiteInDarkMode.copy(alpha = 0.2f),
                            shape = CircleShape
                        )
                    else Modifier
                ),
            contentAlignment = Alignment.Center
        ) {
            if (isSelected) {
                Icon(
                    imageVector = Icons.Rounded.Check,
                    contentDescription = "Selected",
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
private fun EmptyStateBox() {
    val extraColors = LocalExtraColors.current
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 32.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            "لا توجد نتائج",
            color = extraColors.whiteInDarkMode.copy(alpha = 0.5f),
            fontSize = 16.sp
        )
    }
}

@Composable
fun ShimmerEffect(modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnim by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1200,
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer_translate"
    )

    val shimmerColors = listOf(
        Color.Gray.copy(alpha = 0.3f),
        Color.Gray.copy(alpha = 0.5f),
        Color.Gray.copy(alpha = 0.3f)
    )

    val brush = Brush.linearGradient(
        colors = shimmerColors,
        start = Offset(translateAnim - 200f, translateAnim - 200f),
        end = Offset(translateAnim, translateAnim)
    )

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(brush)
    )
}