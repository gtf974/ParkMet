package com.example.parkmet.ui

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.parkmet.data.ParkingDao
import com.example.parkmet.session.Session
import com.example.parkmet.ui.components.AppScaffold
import com.example.parkmet.ui.components.IconTextButton
import com.example.parkmet.util.HashUtil
import com.example.parkmet.util.ToastUtil
import kotlinx.coroutines.launch

@Composable
fun ManageUserScreen(parkingDao: ParkingDao, context: Context){

    var password by remember { mutableStateOf("") }
    var passwordConfirmation by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var passwordConfirmationVisible by remember { mutableStateOf(false) }
    var login by remember { mutableStateOf(Session.currentUser?.username ?:"") }
    var message by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(message) {
        message?.let {
            ToastUtil.showToast(context, it)
            message = null
        }
    }
    val coroutineScope = rememberCoroutineScope();

    AppScaffold(title= "Change Password") { modifier ->
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column (
                modifier= modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterVertically)
            ) {

                Text("Edit user", style = MaterialTheme.typography.headlineMedium)

                OutlinedTextField(
                    singleLine = true,
                    value = login,
                    onValueChange = { login = it },
                    label = { Text("Username") }
                )

                OutlinedTextField(
                    singleLine = true,
                    label = { Text("Password") },
                    value= password,
                    onValueChange= { password = it },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    trailingIcon = {
                        val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                        val description = if (passwordVisible) "Hide password" else "Show password"
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(imageVector = image, contentDescription = description)
                        }
                    }
                )

                OutlinedTextField(
                    singleLine = true,
                    label = { Text("Password Confirmation") },
                    value= passwordConfirmation,
                    onValueChange= { passwordConfirmation = it },
                    visualTransformation = if (passwordConfirmationVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    trailingIcon = {
                        val image = if (passwordConfirmationVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                        val description = if (passwordConfirmationVisible) "Hide password" else "Show password"
                        IconButton(onClick = { passwordConfirmationVisible = !passwordConfirmationVisible }) {
                            Icon(imageVector = image, contentDescription = description)
                        }
                    }
                )

                IconTextButton(
                    text= "Update",
                    isTakingFullWith = false,
                    icon= Icons.Filled.Save,
                    onClick = {
                        if (password.isBlank() || passwordConfirmation.isBlank() || login.isBlank()){
                            message = "Please fill in the fields"
                            return@IconTextButton
                        }

                        if (password != passwordConfirmation){
                            message = "The passwords don't match"
                            return@IconTextButton
                        }

                        if (!password.matches(Regex("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z\\d])[\\S]{12,}$"))){
                            message = "Password is too weak"
                            return@IconTextButton
                        }

                        coroutineScope.launch {
                            if (Session.currentUser != null && login != Session.currentUser!!.username) {
                                if (parkingDao.isUsernameTaken(login)) {
                                    message = "Username is already taken"
                                    return@launch
                                }
                            }

                            parkingDao.updateUser(
                                Session.currentUser!!.copy(
                                    username = login,
                                    passwordHash = HashUtil.sha256(password)
                                )
                            )
                            message = "Changes saved!"
                        }
                    }
                )
            }
        }
    }
}