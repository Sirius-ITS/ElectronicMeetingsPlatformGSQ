package com.informatique.electronicmeetingsplatform.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.informatique.electronicmeetingsplatform.R
import com.informatique.electronicmeetingsplatform.navigation.NavRoutes
import com.informatique.electronicmeetingsplatform.ui.theme.AppFontFamily

private val MaroonColor = Color(0xFF7D1F3F)
private val BlueColor = Color(0xFF0D4261)

private val TextGray = Color(0xFFABABAB)

@Composable
fun LoginScreen(navController: NavController) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFF5F5F5),
                        Color(0xFFEEEEEE),
                        Color(0xFFE8E8E8)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier. height(60.dp))

            // Logo and Header Section
            LogoSection()

            Spacer(modifier = Modifier.height(50.dp))

            // Login Card
            LoginCard(
                username = username,
                onUsernameChange = { username = it },
                password = password,
                onPasswordChange = { password = it },
                passwordVisible = passwordVisible,
                onPasswordVisibilityChange = { passwordVisible = it },
                onLogin = {
                    // Handle login logic
                    navController.navigate(NavRoutes.HomeRoute.route){
                        // Clear back stack and navigate to home screen
                        popUpTo(NavRoutes.LoginRoute.route) { inclusive = true }
                    }
                },
                onForgotPassword = {
                    // Handle forgot password
                },
                onBiometricLogin = {
                    // Handle biometric login
                },
                focusManager = focusManager
            )

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
fun LogoSection() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier. fillMaxWidth()
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_logo),
            contentDescription = "Qatar Council Emblem",
            colorFilter = ColorFilter.tint(MaroonColor),
            modifier = Modifier.size(70.dp),
            contentScale = ContentScale.Fit
        )

        Spacer(modifier = Modifier. height(6.dp))

        Column(
            verticalArrangement = Arrangement.spacedBy((-4).dp)
        ) {
            TightLineHeightText(
                text = "الأمانــــة العامـــــة لمجلــــس الـــوزراء",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaroonColor,
                letterSpacing = 0.5.sp,
                modifier = Modifier.fillMaxWidth()
            )

            TightLineHeightText(
                text = "Council of Ministers Secretariat General",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = MaroonColor,
                letterSpacing = 0.3.sp,
                modifier = Modifier.fillMaxWidth()
            )

            TightLineHeightText(
                text = "دولــــة قطـــر • State of Qatar",
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium,
                color = MaroonColor,
                letterSpacing = 0.2.sp,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun LoginCard(
    username: String,
    onUsernameChange: (String) -> Unit,
    password:  String,
    onPasswordChange:  (String) -> Unit,
    passwordVisible: Boolean,
    onPasswordVisibilityChange: (Boolean) -> Unit,
    onLogin: () -> Unit,
    onForgotPassword: () -> Unit,
    onBiometricLogin: () -> Unit,
    focusManager: androidx.compose.ui.focus.FocusManager
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(24.dp),
                spotColor = Color.Black.copy(alpha = 0.1f)
            ),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color. White
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Card Title
            Text(
                text = "منصة الاجتماعات الإلكترونية",
                style = TextStyle(
                    fontFamily = AppFontFamily,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium,
                    color = BlueColor,
                    textAlign = TextAlign.Center
                )
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Subtitle
            Text(
                text = "الأمانة العامة لمجلس الوزراء",
                style = TextStyle(
                    fontFamily = AppFontFamily,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                    color = TextGray,
                    textAlign = TextAlign.Center
                )
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Username Field
            Text(
                text = "اسم المستخدم",
                modifier = Modifier.align(Alignment.Start),
                style = TextStyle(
                    fontFamily = AppFontFamily,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = BlueColor
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            CustomTextField(
                value = username,
                onValueChange = onUsernameChange,
                placeholder = "أدخل اسم المستخدم",
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Username",
                        tint = Color(0xFF7D1F3F)
                    )
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                )
            )

            Spacer(modifier = Modifier. height(24.dp))

            // Password Field
            Text(
                text = "كلمة المرور",
                modifier = Modifier.align(Alignment.Start),
                style = TextStyle(
                    fontFamily = AppFontFamily,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = BlueColor
                )
            )

            Spacer(modifier = Modifier. height(8.dp))

            CustomTextField(
                value = password,
                onValueChange = onPasswordChange,
                placeholder = "أدخل كلمة المرور",
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Password",
                        tint = Color(0xFF7D1F3F)
                    )
                },
                trailingIcon = {
                    IconButton(onClick = { onPasswordVisibilityChange(!passwordVisible) }) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Default.Visibility else Icons. Default.VisibilityOff,
                            contentDescription = if (passwordVisible) "Hide password" else "Show password",
                            tint = Color(0xFF7D1F3F)
                        )
                    }
                },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        focusManager.clearFocus()
                        onLogin()
                    }
                )
            )

            Spacer(modifier = Modifier. height(24.dp))

            // Forgot Password and Biometric Row
            Row(
                modifier = Modifier. fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                // Forgot Password
                Text(
                    text = "نسيت كلمة المرور؟",
                    style = TextStyle(
                        fontFamily = AppFontFamily,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Normal,
                        color = BlueColor
                    ),
                    modifier = Modifier.clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) { onForgotPassword() }
                )

                // Biometric Login
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) { onBiometricLogin() }
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_fingerprint), // Add fingerprint icon
                        contentDescription = "Biometric Login",
                        tint = BlueColor,
                        modifier = Modifier.size(21.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "التعرف على الوجه",
                        style = TextStyle(
                            fontFamily = AppFontFamily,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Normal,
                            color = BlueColor
                        )
                    )
                }

            }

            Spacer(modifier = Modifier. height(32.dp))

            // Login Button
            Button(
                onClick = onLogin,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF7D1F3F),
                    contentColor = Color. White
                ),
                elevation = ButtonDefaults. buttonElevation(
                    defaultElevation = 4.dp,
                    pressedElevation = 8.dp
                )
            ) {
                Text(
                    text = "تسجيل الدخول",
                    style = TextStyle(
                        fontFamily = AppFontFamily,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier. fillMaxWidth(),
        placeholder = {
            Text(
                text = placeholder,
                style = TextStyle(
                    fontFamily = AppFontFamily,
                    fontSize = 14.sp,
                    color = Color(0xFFCCCCCC),
                    textAlign = TextAlign.Start
                ),
                modifier = Modifier.fillMaxWidth()
            )
        },
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        visualTransformation = visualTransformation,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        singleLine = true,
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = Color(0xFFFAFAFA),
            unfocusedContainerColor = Color(0xFFFAFAFA),
            focusedBorderColor = Color(0xFF7D1F3F),
            unfocusedBorderColor = Color(0xFFE0E0E0),
            cursorColor = Color(0xFF7D1F3F)
        ),
        textStyle = TextStyle(
            fontFamily = AppFontFamily,
            fontSize = 14.sp,
            textAlign = TextAlign.Start,
            color = Color(0xFF2C2C2C)
        )
    )
}