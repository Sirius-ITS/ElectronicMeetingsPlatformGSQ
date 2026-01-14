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
import androidx.compose.material.icons.filled.Done
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
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.informatique.electronicmeetingsplatform.R
import com.informatique.electronicmeetingsplatform.navigation.NavRoutes
import com.informatique.electronicmeetingsplatform.ui.components.rememberAlertPopupState
import com.informatique.electronicmeetingsplatform.ui.theme.AppFontFamily
import com.informatique.electronicmeetingsplatform.ui.theme.AppTheme
import com.informatique.electronicmeetingsplatform.ui.theme.LocalExtraColors
import com.informatique.electronicmeetingsplatform.ui.viewModel.LoginState
import com.informatique.electronicmeetingsplatform.ui.viewModel.LoginState.*
import com.informatique.electronicmeetingsplatform.ui.viewModel.LoginViewModel

@Composable
fun LoginScreen(navController: NavController) {

    val extraColors = LocalExtraColors.current
    val viewModel = hiltViewModel<LoginViewModel>()

    var email by remember { mutableStateOf("") }
    var validEmail by remember { mutableStateOf(false) }
    var password by remember { mutableStateOf("") }
    var validPassword by remember { mutableStateOf(false) }

    var passwordVisible by remember { mutableStateOf(false) }

    val loginState by viewModel.loginState.collectAsStateWithLifecycle()

    val focusManager = LocalFocusManager.current
    val alertState = rememberAlertPopupState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                color = extraColors.background
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
                viewModel = viewModel,
                email = email,
                onEmailChange = {
                    email = it
                    validEmail = viewModel.validateEmailInput(it)
                    if (validEmail) {
                        viewModel.clearEmailError()
                    }
                },
                password = password,
                onPasswordChange = {
                    password = it
                    validPassword = viewModel.validatePasswordInput(it)
                    if (validPassword) {
                        viewModel.clearPasswordError()
                    }
                },
                passwordVisible = passwordVisible,
                onPasswordVisibilityChange = { passwordVisible = it },
                isLoginEnabled = validEmail && validPassword,
                onLogin = {
                    // Handle login logic
                    viewModel.login(email = email, password = password)
                },
                onForgotPassword = {
                    // Handle forgot password
                },
                onBiometricLogin = {
                    // Handle biometric login
                },
                focusManager = focusManager,
                loginState = loginState
            )

            Spacer(modifier = Modifier.height(40.dp))

            if (loginState is Success){
                navController.navigate(NavRoutes.MainRoute.route){
                    // Clear back stack and navigate to home screen
                    popUpTo(NavRoutes.LoginRoute.route) { inclusive = true }
                }
            } else if (loginState is Error) {
                alertState.showError(
                    message = (loginState as Error).message,
                    title = "Error"
                )
                viewModel.resetLoginState()
            }
        }
    }
}

@Composable
fun LogoSection() {

    val extraColors = LocalExtraColors.current

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier. fillMaxWidth()
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_logo),
            contentDescription = "Qatar Council Emblem",
            colorFilter = ColorFilter.tint(extraColors.maroonColor),
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
                color = extraColors.maroonColor,
                letterSpacing = 0.5.sp,
                modifier = Modifier.fillMaxWidth()
            )

            TightLineHeightText(
                text = "Council of Ministers Secretariat General",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = extraColors.maroonColor,
                letterSpacing = 0.3.sp,
                modifier = Modifier.fillMaxWidth()
            )

            TightLineHeightText(
                text = "دولــــة قطـــر • State of Qatar",
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium,
                color = extraColors.maroonColor,
                letterSpacing = 0.2.sp,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun LoginCard(
    viewModel: LoginViewModel,
    email: String,
    onEmailChange: (String) -> Unit,
    password: String,
    onPasswordChange: (String) -> Unit,
    passwordVisible: Boolean,
    onPasswordVisibilityChange: (Boolean) -> Unit,
    onLogin: () -> Unit,
    isLoginEnabled: Boolean,
    onForgotPassword: () -> Unit,
    onBiometricLogin: () -> Unit,
    focusManager: FocusManager,
    loginState: LoginState
) {

    val extraColors = LocalExtraColors.current

    val validateEmail = viewModel.emailError.collectAsStateWithLifecycle()
    val validatePassword = viewModel.passwordError.collectAsStateWithLifecycle()


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
            containerColor = Color.White
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
                    color = extraColors.blueColor,
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
                    color = extraColors.textGray,
                    textAlign = TextAlign.Center
                )
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Username Field
            Text(
                text = "البريد الألكتروني",
                modifier = Modifier.align(Alignment.Start),
                style = TextStyle(
                    fontFamily = AppFontFamily,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = extraColors.blueColor
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            CustomTextField(
                value = email,
                onValueChange = onEmailChange,
                placeholder = "أدخل البريد الألكتروني",
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Email",
                        tint = extraColors.maroonColor
                    )
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (validateEmail.value != null) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "⚠️",
                        fontSize = 14.sp,
                        modifier = Modifier.padding(end = 4.dp)
                    )
                    Text(
                        text = validateEmail.value!!,
                        color = Color.Red
                    )
                }
            }

            Spacer(modifier = Modifier. height(24.dp))

            // Password Field
            Text(
                text = "كلمة المرور",
                modifier = Modifier.align(Alignment.Start),
                style = TextStyle(
                    fontFamily = AppFontFamily,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = extraColors.blueColor
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            CustomTextField(
                value = password,
                onValueChange = onPasswordChange,
                placeholder = "أدخل كلمة المرور",
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Password",
                        tint = extraColors.maroonColor
                    )
                },
                trailingIcon = {
                    IconButton(onClick = { onPasswordVisibilityChange(!passwordVisible) }) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
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

            Spacer(modifier = Modifier.height(8.dp))

            if (validatePassword.value != null) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "⚠️",
                        fontSize = 14.sp,
                        modifier = Modifier.padding(end = 4.dp)
                    )
                    Text(
                        text = validatePassword.value!!,
                        color = Color.Red
                    )
                }
            }

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
                        color = extraColors.blueColor
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
                        tint = extraColors.blueColor,
                        modifier = Modifier.size(21.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "التعرف على الوجه",
                        style = TextStyle(
                            fontFamily = AppFontFamily,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Normal,
                            color = extraColors.blueColor
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
                    containerColor = if (isLoginEnabled) extraColors.maroonColor else Color.Gray,
                    contentColor = Color.White
                ),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 4.dp,
                    pressedElevation = 8.dp
                )
            ) {
                when (loginState) {
                    is Loading -> {
                        CircularProgressIndicator(color = Color.White)
                    }

                    is Success -> {
                        Icon(
                            imageVector = Icons.Default.Done,
                            contentDescription = "Login",
                            tint = Color.White
                        )
                    }

                    else -> {
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

    val extraColors = LocalExtraColors.current

    Column {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text(
                    text = placeholder,
                    style = TextStyle(
                        fontFamily = AppFontFamily,
                        fontSize = 14.sp,
                        color = LocalTextStyle.current.color,
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
                focusedBorderColor = extraColors.maroonColor,
                unfocusedBorderColor = Color(0xFFE0E0E0),
                cursorColor = extraColors.maroonColor
            ),
            textStyle = TextStyle(
                fontFamily = AppFontFamily,
                fontSize = 14.sp,
                textAlign = TextAlign.Start,
                color = LocalTextStyle.current.color
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun LoginPreview(){
    AppTheme {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            LoginScreen(navController = rememberNavController())
        }
    }
}