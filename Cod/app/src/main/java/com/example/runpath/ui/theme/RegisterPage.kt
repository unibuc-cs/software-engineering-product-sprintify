package com.example.runpath.ui.theme

import FeedReaderDbHelper
import androidx.compose.foundation.interaction.DragInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun RegisterPage(navcontroller : NavController, dbHelper: FeedReaderDbHelper) {
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordConfirmation by remember { mutableStateOf("") }

    var validEmail by remember { mutableStateOf(true)}
    var passwordConfirmationTouched by remember { mutableStateOf(false) }
    val passwordMatch = password == passwordConfirmation
    val focusManager = LocalFocusManager.current
    val canRegister = username.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty() &&
            passwordConfirmation.isNotEmpty() && passwordMatch
    val scrollState = rememberScrollState()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Row (
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text("Register Account", fontSize = 30.sp, fontWeight = FontWeight.Medium)
        }

        Spacer(modifier = Modifier.height(80.dp))

        Row (
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start
        ) {
            Text("Username", fontSize = 14.sp, fontWeight = FontWeight.Medium, textAlign = TextAlign.Start)
        }

        TextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Username") },
            keyboardOptions = KeyboardOptions (
                imeAction = ImeAction.Next,
                keyboardType = KeyboardType.Text
            ),
            keyboardActions = KeyboardActions (
                onNext = {focusManager.moveFocus(FocusDirection.Down)}
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row (
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start
        ) {
            Text("Email", fontSize = 14.sp, fontWeight = FontWeight.Medium, textAlign = TextAlign.Start)
        }

        if (!validEmail) {
            Text(
                text = "Invalid email format",
                color = Color.Red,
                modifier = Modifier.fillMaxWidth()
            )
        }

        TextField(
            value = email,
            onValueChange = {
                email = it
                validEmail = isEmailValid(email)
                            },
            label = { Text("Email") },
            keyboardOptions = KeyboardOptions (
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions (
                onNext = {focusManager.moveFocus(FocusDirection.Down)}
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row (
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start
        ) {
            Text("Password", fontSize = 14.sp, fontWeight = FontWeight.Medium, textAlign = TextAlign.Start)
        }

        TextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            keyboardOptions = KeyboardOptions (
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions (
                onNext = {focusManager.moveFocus(FocusDirection.Down)}
            ),
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row (
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start
        ) {
            Text("Password Confirmation", fontSize = 14.sp, fontWeight = FontWeight.Medium, textAlign = TextAlign.Start)
        }

        if (passwordConfirmationTouched && !passwordMatch) {
            Text(
                text = "Passwords do not match",
                color = Color.Red,
                modifier = Modifier.fillMaxWidth()
            )
        }

        TextField(
            value = passwordConfirmation,
            onValueChange = {
                passwordConfirmation = it
                passwordConfirmationTouched = true
            },
            label = { Text("Confirm Password") },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            visualTransformation = PasswordVisualTransformation(),
            keyboardActions = KeyboardActions(
                onDone = {
                focusManager.clearFocus() // Field loses focus when done
            }),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(40.dp))

        Button(
            onClick = {
                if (passwordMatch) {
                    // Register user logic
                    navcontroller.navigate("loginPage")
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = canRegister
        ) {
            Text("Register")
        }
    }
}


fun isEmailValid(email: String): Boolean {
    // General email regex (RFC 5322 official standard)
    val emailPattern =
        "(?:[a-z0-9!#\$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#\$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])"

    return emailPattern.toRegex().matches(email)
}


//@Preview(showBackground = true)
//@Composable
//fun PreviewRegisterPage() {
//    RegisterPage()
//}