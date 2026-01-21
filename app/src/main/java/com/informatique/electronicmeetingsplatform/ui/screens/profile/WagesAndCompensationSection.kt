package com.informatique.electronicmeetingsplatform.ui.screens.profile

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Print
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import com.informatique.electronicmeetingsplatform.ui.theme.AppFontFamily
import com.informatique.electronicmeetingsplatform.ui.theme.AppTheme
import com.informatique.electronicmeetingsplatform.ui.theme.LocalExtraColors
import com.informatique.electronicmeetingsplatform.ui.viewModel.LoginState.Loading
import com.informatique.electronicmeetingsplatform.ui.viewModel.LoginState.Success
import com.informatique.electronicmeetingsplatform.ui.viewModel.ProfileViewModel

@Composable
fun WagesAndCompensationSection(/*viewModel: ProfileViewModel*/){

    //val profileState by viewModel.profileState.collectAsStateWithLifecycle()

    val extraColors = LocalExtraColors.current

    Column{
        SalaryAndCompensation()

        Spacer(modifier = Modifier.height(8.dp))

        SalaryDetailsCard()

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {},
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = extraColors.maroonColor,
                contentColor = Color.White
            ),
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 4.dp,
                pressedElevation = 8.dp
            )
        ) {
            Text(
                text = "عرض قسائم الراتب",
                style = TextStyle(
                    fontFamily = AppFontFamily,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            )
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun SalaryAndCompensation(){

    val extraColors = LocalExtraColors.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Title
        Text(
            text = "الأجور والتعويضات",
            fontSize = 20.sp,
            color = extraColors.blueColor,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Start,
            modifier = Modifier.fillMaxWidth()
        )

        Card(
            modifier = Modifier
                .fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {

            Column(
                modifier = Modifier.padding(16.dp)
            ){
                // Cards Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {

                    // Total Salary Card
                    WageCard(
                        amount = "58761",
                        label = "الراتب الحالي",
                        backgroundColor = Color(0xFFE8F5E9),
                        textColor = Color(0xFF4CAF50),
                        modifier = Modifier.weight(1f)
                    )

                    // Deductions Card
                    WageCard(
                        amount = "8814",
                        label = "الاستقطاعات",
                        backgroundColor = Color(0xFFFFE5E5),
                        textColor = Color(0xFFE53935),
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Chart Section
                SalaryChart()
            }

        }

    }
}

@Composable
private fun WageCard(
    amount: String,
    label: String,
    backgroundColor: Color,
    textColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = amount,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = textColor
            )
            Text(
                text = "QAR",
                fontSize = 12.sp,
                color = textColor.copy(alpha = 0.7f)
            )
            Text(
                text = label,
                fontSize = 14.sp,
                color = Color.Gray
            )
        }
    }
}

@Composable
private fun SalaryChart() {

    val extraColors = LocalExtraColors.current

    val months = listOf("يناير", "فبراير", "مارس", "إبريل", "مايو", "يونيو")
    val values = listOf(55000f, 58000f, 60000f, 57000f, 59000f, 58000f)
    val maxValue = 60000f

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Chart Title
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = "الراتب والمكافأة",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = extraColors.blueColor
            )
            Text(
                text = "أخر 6 أشهر من العام الحالي",
                fontSize = 10.sp,
                color = Color.Gray
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row {
            // Y-axis labels
            Row(
                modifier = Modifier.weight(0.1f),
                horizontalArrangement = Arrangement.Start
            ) {
                Column(
                    modifier = Modifier.height(200.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("60000", fontSize = 10.sp, color = Color.Gray)
                    Text("40000", fontSize = 10.sp, color = Color.Gray)
                    Text("20000", fontSize = 10.sp, color = Color.Gray)
                    Text("0", fontSize = 10.sp, color = Color.Gray)
                }
            }

            // Chart
            Row(
                modifier = Modifier
                    .weight(0.9f)
                    .height(200.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.Bottom
            ) {
                values.forEachIndexed { index, value ->
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.Bottom)
                    ) {
                        // Bar
                        Box(
                            modifier = Modifier
                                .width(40.dp)
                                .height((value / maxValue * 150).dp)
                                .background(
                                    color = extraColors.maroonColor,
                                    shape = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)
                                )
                        )
                        // Month Label
                        Text(
                            text = months[index],
                            fontSize = 12.sp,
                            color = Color.Gray,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SalaryDetailsCard(
    month: String = "ديسمبر",
    year: String = "2025",
    grossSalary: String = "57664.76",
    deductions: String = "8649.71",
    netSalary: String = "49015.04"
) {
    val extraColors = LocalExtraColors.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Header
            Text(
                text = "$year $month",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = extraColors.blueColor,
                textAlign = TextAlign.Start,
                modifier = Modifier.fillMaxWidth()
            )

            Text(
                text = "مدفوع",
                fontSize = 12.sp,
                color = Color.Gray,
                textAlign = TextAlign.Start,
                modifier = Modifier.fillMaxWidth()
            )

            HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))

            // Salary Items
            SalaryItem(
                label = "المبلغ الإجمالي",
                amount = "QAR $grossSalary",
                amountColor = Color(0xFF4CAF50)
            )

            SalaryItem(
                label = "الاستقطاعات",
                amount = "QAR $deductions",
                amountColor = Color(0xFFE53935)
            )

            SalaryItem(
                label = "الصافي",
                amount = "QAR $netSalary",
                amountColor = Color.Gray
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                ActionButton(
                    text = "تحميل",
                    icon = Icons.Default.Download,
                    modifier = Modifier.weight(1f)
                )

                ActionButton(
                    text = "طباعة",
                    icon = Icons.Default.Print,
                    modifier = Modifier.weight(1f)
                )

                ActionButton(
                    text = "التفاصيل",
                    icon = Icons.Default.Description,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun SalaryItem(
    label: String,
    amount: String,
    amountColor: Color
) {

    val extraColors = LocalExtraColors.current

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {

        Text(
            text = label,
            fontSize = 14.sp,
            color = extraColors.blueColor
        )

        Text(
            text = amount,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = amountColor
        )
    }
}

@Composable
private fun ActionButton(
    text: String,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    val extraColors = LocalExtraColors.current

    Button(
        onClick = { /* Handle action */ },
        modifier = modifier.height(48.dp),
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = extraColors.maroonColor
        )
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ){
            Icon(
                imageVector = icon,
                contentDescription = text,
                modifier = Modifier.size(11.dp)
            )

            Text(
                text = text,
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
fun WagesAndCompensationPreview(){
    AppTheme {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            WagesAndCompensationSection()
        }
    }
}