package com.informatique.electronicmeetingsplatform.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.informatique.electronicmeetingsplatform.data.model.profile.Data
import com.informatique.electronicmeetingsplatform.ui.theme.LocalExtraColors
import com.informatique.electronicmeetingsplatform.ui.viewModel.ProfileState
import com.informatique.electronicmeetingsplatform.ui.viewModel.ProfileViewModel

@Composable
fun PersonalInformationSection(viewModel: ProfileViewModel){

    val profileState by viewModel.profileState.collectAsStateWithLifecycle()

    Column {
        // Personal Information Section
        PersonalInformation(
            data = (profileState as ProfileState.Success).data,
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Addresses
        AddressesSection(
            data = (profileState as ProfileState.Success).data
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Bank details
        BankDetailsSection()

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun PersonalInformation(data: Data) {

    val extraColors = LocalExtraColors.current

    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Text(
            text = "المعلومات الشخصية",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = extraColors.blueColor,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Start
        )

        Spacer(modifier = Modifier.height(16.dp))

        InfoField("الاسم الأول بالإنجليزية", data.person.englishFirstName)
        InfoField("الاسم الثاني بالإنجليزية", data.person.englishSecondName)
        InfoField("اللغة الأم", data.person.motherTongue)
        InfoField("الجنسية", data.person.nationality)
        InfoField("الحالة الإجتماعية", data.person.maritalStatusName ?: "غير محدد")
        InfoField("تاريخ الميلاد", data.person.formattedDateOfBirth)
        InfoField("الجنس", data.person.gender ?: "غير محدد")
    }
}

@Composable
fun InfoField(label: String, value: String) {

    val extraColors = LocalExtraColors.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = label,
                fontSize = 14.sp,
                color = DarkGray
            )

            Text(text = value, fontSize = 15.sp, color = extraColors.blueColor)
        }
    }
}

@Composable
fun AddressesSection(data: Data) {

    val extraColors = LocalExtraColors.current

    Column(
        modifier = Modifier.padding(horizontal = 16.dp)
    ){
        Text(
            text = "العناوين",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = extraColors.blueColor,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Start
        )

        Spacer(modifier = Modifier.height(16.dp))

        // National Address Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Header with star
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(extraColors.blueColor)
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = Color(0xFFFFA726),
                        modifier = Modifier.size(20.dp)
                    )

                    Text(
                        text = "العنوان الوطني",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                // Address details
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {

                    AddressDetailItem(
                        icon = Icons.Default.Menu,
                        label = "شارع",
                        value = data.person.streetAddress
                    )

                    HorizontalDivider(
                        modifier = Modifier
                            .width(1.dp)
                            .height(60.dp),
                        color = Color.LightGray.copy(alpha = 0.3f)
                    )

                    AddressDetailItem(
                        icon = Icons.Default.LocationOn,
                        label = "منطقة",
                        value = "غير محدد"
                    )

                    HorizontalDivider(
                        modifier = Modifier
                            .width(1.dp)
                            .height(60.dp),
                        color = Color.LightGray.copy(alpha = 0.3f)
                    )

                    AddressDetailItem(
                        icon = Icons.Default.Home,
                        label = "مبنى",
                        value = "120"
                    )

                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Show All Addresses Button
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { /* Navigate to all addresses */ }
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            color = extraColors.maroonColor.copy(alpha = 0.3f),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ){
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = extraColors.maroonColor,
                        modifier = Modifier.size(25.dp)
                    )
                }

                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = "عرض جميع العناوين",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        color = QatarDarkBg
                    )
                    Text(
                        text = "إدارة وإضافة عناوين جديدة",
                        fontSize = 12.sp,
                        color = DarkGray.copy(alpha = 0.6f)
                    )
                }

                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    tint = DarkGray,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
fun AddressDetailItem(
    icon: ImageVector,
    label: String,
    value: String
) {

    val extraColors = LocalExtraColors.current

    Card(
        modifier = Modifier
            .width(105.dp)
            .padding(vertical = 6.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = extraColors.blueColor.copy(alpha = 0.2f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(extraColors.blueColor.copy(alpha = 0.3f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = extraColors.blueColor,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = label,
                fontSize = 12.sp,
                color = DarkGray.copy(alpha = 0.6f)
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = value,
                fontSize = 11.sp,
                color = QatarDarkBg,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                maxLines = 1
            )
        }

    }
}

@Composable
fun BankDetailsSection() {

    val extraColors = LocalExtraColors.current

    Column(
        modifier = Modifier.padding(horizontal = 16.dp)
    ) {
        Text(
            text = "تفاصيل البنك",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = extraColors.blueColor,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Start
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Main Bank Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { /* Navigate to bank details */ }
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(extraColors.maroonColor.copy(alpha = 0.1f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AccountBalance,
                        contentDescription = null,
                        tint = extraColors.maroonColor,
                        modifier = Modifier.size(28.dp)
                    )
                }

                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = "البنك الرئيسي",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        color = QatarDarkBg
                    )
                    Text(
                        text = "بنك قطر الوطني",
                        fontSize = 12.sp,
                        color = DarkGray.copy(alpha = 0.6f)
                    )
                }

                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    tint = DarkGray,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}