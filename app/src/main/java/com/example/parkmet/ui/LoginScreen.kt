package com.example.parkmet.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.parkmet.data.ParkingDao
import com.example.parkmet.ui.components.AppScaffold
import com.example.parkmet.util.HashUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun LoginScreen(parkingDao: ParkingDao, onLoginSuccess: () -> Unit) {
    AppScaffold(title = "Login") { modifier ->
        val coroutineScope = rememberCoroutineScope()
        var username by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
        var loginError by remember { mutableStateOf("") }

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

            Button(
                onClick = {
                    coroutineScope.launch {
                        val user = withContext(Dispatchers.IO) {
                            parkingDao.getUserByUsername(username)
                        }
                        val hashedPassword = HashUtil.sha256(password)
                        if (user != null && user.passwordHash == hashedPassword) {
                            loginError = ""
                            onLoginSuccess()
                        } else {
                            loginError = "Invalid username or password"
                        }
                    }
                }
            ) {
                Text("Login")
            }

            if (loginError.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(loginError, color = MaterialTheme.colorScheme.error)
            }
        }
    }
}
