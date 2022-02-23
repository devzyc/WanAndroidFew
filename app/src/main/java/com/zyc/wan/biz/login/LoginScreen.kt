package com.zyc.wan.biz.login

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.MaterialTheme.colors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.edit
import androidx.hilt.navigation.compose.hiltViewModel
import arrow.core.Either
import com.ramcosta.composedestinations.annotation.Destination
import com.zyc.wan.*
import com.zyc.wan.R
import com.zyc.wan.ui.DefaultTransitions
import com.zyc.wan.biz.destinations.LoginScreenDestination
import com.zyc.wan.data.remote.AppError
import com.zyc.wan.reusable.composable.CenterTopAppBar
import com.zyc.wan.reusable.composable.Loading
import com.zyc.wan.reusable.extension.toast
import com.zyc.wan.ui.theme.cyan500
import com.zyc.wan.ui.theme.grey500
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

/**
 * @author devzyc
 */
@OptIn(ExperimentalComposeUiApi::class)
@Destination(
    start = true,
    style = DefaultTransitions::class
)
@Composable
fun LoginScreen(
    onBack: () -> Unit,
    onLoginSuccess: () -> Unit,
) {
    LoginScreen(
        viewModel = hiltViewModel(),
        onBack = onBack,
        onLoginSuccess = onLoginSuccess
    )
}

@ExperimentalComposeUiApi
@Composable
internal fun LoginScreen(
    viewModel: LoginViewModel,
    onBack: () -> Unit,
    onLoginSuccess: () -> Unit,
) {
    Scaffold(
        topBar = {
            CenterTopAppBar(
                backgroundColor = colors.primary,
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "go back",
                            tint = colors.onPrimary,
                        )
                    }
                }
            ) {
                Text(
                    text = stringResource(R.string.user_login),
                    color = colors.onPrimary,
                )
            }
        },
    ) {
        Box {
            var isLoading by remember { mutableStateOf(false) }
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp, vertical = 40.dp),
            ) {
                Spacer(modifier = Modifier.height(30.dp))

                var userName by remember { mutableStateOf("") }
                var password by remember { mutableStateOf("") }
                var isPasswordVisible by remember { mutableStateOf(false) }
                var isPasswordFocused by remember { mutableStateOf(false) }

                val scope = rememberCoroutineScope()
                val context = LocalContext.current
                val keyboardController = LocalSoftwareKeyboardController.current
                val (passwordFocusRequester) = FocusRequester.createRefs()

                fun onLoginPress() {
                    if (userName.isEmpty()) {
                        context.toast(R.string.username_not_empty)
                        return
                    }
                    if (password.isEmpty()) {
                        context.toast(R.string.password_not_empty)
                        return
                    }
                    isLoading = true
                    keyboardController?.hide()
                    scope.launch {
                        viewModel.login(userName, password)
                            .collect {
                                when (it) {
                                    is Either.Left -> {
                                        if (it.value is AppError.BusinessError) {
                                            context.toast(it.value.message!!)
                                        } else {
                                            context.toast(R.string.login_failed)
                                            Log.e(
                                                LoginScreenDestination.route,
                                                "call login api encountered an exception, detail: ${it.value.message}"
                                            )
                                        }
                                    }
                                    is Either.Right -> {
                                        context.dataStore.edit {
                                            prefIsLogin = true
                                            prefUserName = userName
                                            prefPassword = password
                                        }
                                        context.toast(R.string.login_success)
                                        onLoginSuccess()
                                    }
                                }
                                isLoading = false
                            }
                    }
                }

                LoginField(
                    text = userName,
                    labelStrId = R.string.username,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    keyboardActions = KeyboardActions(onNext = { passwordFocusRequester.requestFocus() }),
                    onValueChange = { userName = it },
                )
                Spacer(modifier = Modifier.height(10.dp))
                LoginField(
                    text = password,
                    labelStrId = R.string.password,
                    onFocusChange = { isPasswordFocused = it },
                    onValueChange = { password = it },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = { onLoginPress() }),
                    focusRequester = passwordFocusRequester,
                    visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        if (password.isNotEmpty()) {
                            IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                                Icon(
                                    painter = painterResource(if (isPasswordVisible) R.drawable.ic_to_hide_password else R.drawable.ic_to_show_password),
                                    contentDescription = "password",
                                    tint = if (isPasswordFocused) cyan500 else grey500
                                )
                            }
                        }
                    },
                )
                Spacer(modifier = Modifier.height(30.dp))
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = ButtonDefaults.elevation(5.dp),
                    colors = ButtonDefaults.buttonColors(backgroundColor = cyan500),
                    onClick = { onLoginPress() },
                ) {
                    Text(
                        text = stringResource(R.string.login),
                        modifier = Modifier.padding(8.dp),
                        color = colors.onPrimary,
                        fontSize = 17.sp,
                    )
                }
            }
            Loading(isShown = isLoading)
        }
    }
}

@Composable
fun LoginField(
    text: String,
    labelStrId: Int,
    onFocusChange: (Boolean) -> Unit = {},
    onValueChange: (String) -> Unit,
    keyboardOptions: KeyboardOptions,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    focusRequester: FocusRequester = FocusRequester.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    trailingIcon: @Composable (() -> Unit)? = null,
) {
    OutlinedTextField(
        colors = TextFieldDefaults.outlinedTextFieldColors(
            focusedBorderColor = cyan500,
            unfocusedBorderColor = grey500,
            focusedLabelColor = cyan500,
            unfocusedLabelColor = grey500,
            cursorColor = cyan500,
        ),
        value = text,
        label = { Text(stringResource(labelStrId)) },
        modifier = Modifier
            .fillMaxWidth()
            .focusRequester(focusRequester)
            .onFocusChanged { onFocusChange(it.isFocused) },
        onValueChange = onValueChange,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        visualTransformation = visualTransformation,
        trailingIcon = trailingIcon,
        singleLine = true,
        maxLines = 1,
    )
}