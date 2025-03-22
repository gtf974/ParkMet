package com.example.parkmet.ui

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material.icons.filled.Login
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.parkmet.data.ParkingDao
import com.example.parkmet.session.Session
import com.example.parkmet.ui.components.AppScaffold
import com.example.parkmet.ui.components.IconTextButton
import com.example.parkmet.util.HashUtil
import com.example.parkmet.util.ToastUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun LoginScreen(parkingDao: ParkingDao, context: Context, onLoginSuccess: () -> Unit) {
    AppScaffold(title = "Login") { modifier ->
        val coroutineScope = rememberCoroutineScope()
        var username by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
        var loginError by remember { mutableStateOf<String?>(null) }

        LaunchedEffect(loginError) {
            loginError?.let {
                ToastUtil.showToast(context, it)
                loginError = null
            }
        }

        Column(
            modifier = modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Login", style = MaterialTheme.typography.headlineMedium)

            Spacer(modifier = Modifier.height(16.dp))

            TextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Username") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            TextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            IconTextButton(
                text= "Login",
                icon= Icons.AutoMirrored.Filled.Login,
                isTakingFullWith = false,
                onClick = {
                    coroutineScope.launch {
                        val user = withContext(Dispatchers.IO) {
                            parkingDao.getUserByUsername(username)
                        }
                        val hashedPassword = HashUtil.sha256(password)
                        if (user != null && user.passwordHash == hashedPassword) {
                            loginError = ""
                            Session.currentUser = user;
                            onLoginSuccess()
                        } else {
                            loginError = "Invalid username or password"
                        }
                    }
                }
            )
        }
    }
}
