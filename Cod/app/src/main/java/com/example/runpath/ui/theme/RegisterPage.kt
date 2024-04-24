package com.example.runpath.ui.theme

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun RegisterPage() {
    var username by remember {mutableStateOf("")}
    var email by remember { mutableStateOf("")}
    var password by remember { mutableStateOf("")}
    var passwordConfirmation by remember { mutableStateOf("")}

    var passwordMatch by remember { mutableStateOf(true)}

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextField (
            value = username,
            onValueChange = {username = it},
            label = {Text("Username")},
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        TextField (
            value = email,
            onValueChange = {email = it},
            label = {Text("Email")},
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        TextField (
            value = password,
            onValueChange = {
                password = it
                passwordMatch = it == passwordConfirmation
            },
            label = {Text("Password")},
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        TextField (
            value = passwordConfirmation,
            onValueChange = {
                passwordConfirmation = it
                passwordMatch = it == password
            },
            label = {Text("Confirm Password")},
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button (
            onClick = {
                if(passwordMatch) {
                    // Register user
                } else {
                    // Handle different passwords
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Register")
        }

        if(!passwordMatch) {
            Text (
                text = "Passwords do not match",
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewRegisterPage() {
    RegisterPage()
}