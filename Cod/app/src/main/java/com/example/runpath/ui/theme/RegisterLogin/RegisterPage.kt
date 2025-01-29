package com.example.runpath.ui.theme.RegisterLogin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.runpath.database.SessionManager
import com.example.runpath.database.UserDAO
import com.example.runpath.models.User

@Composable
fun RegisterPage(navController: NavController, sessionManager: SessionManager) {
    // variabile pentru crearea unui cont
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordConfirmation by remember { mutableStateOf("") }

    var validEmail by remember { mutableStateOf(true) }
    var passwordConfirmationTouched by remember { mutableStateOf(false) }
    val passwordMatch = password == passwordConfirmation
    val focusManager = LocalFocusManager.current
    val canRegister = username.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty() &&
            passwordConfirmation.isNotEmpty() && passwordMatch
    val scrollState = rememberScrollState()

    val context = LocalContext.current
    val userDAO by remember { mutableStateOf(UserDAO(context)) }
    var isUsernameValid by remember { mutableStateOf(true) }
    var isPasswordValid by remember { mutableStateOf(true) }

    // afisarea paginii de inregistrare
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text("Register Account", fontSize = 30.sp, fontWeight = FontWeight.Medium)
        }

        Spacer(modifier = Modifier.height(80.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start
        ) {
            Text("Username", fontSize = 14.sp, fontWeight = FontWeight.Medium, textAlign = TextAlign.Start)
        }
        // campul pentru username
        TextField(
            value = username,
            onValueChange = {
                username = it
                isUsernameValid = hasSpace(username) // Validate input
            },
            label = { Text("Username") },
            isError = !isUsernameValid, // Highlights the field in red if invalid
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next,
                keyboardType = KeyboardType.Text
            ),
            keyboardActions = KeyboardActions(
                onNext = { focusManager.moveFocus(FocusDirection.Down) }
            ),
            modifier = Modifier.fillMaxWidth()
        )
        if (!isUsernameValid) {
            Text(
                text = "Username cannot contain spaces",
                color = Color.Red,
                modifier = Modifier.fillMaxWidth()
            )
        }


        Spacer(modifier = Modifier.height(12.dp))
        // campul pentru email
        Row(
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
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = { focusManager.moveFocus(FocusDirection.Down) }
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))
        // campul pentru parola
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start
        ) {
            Text("Password", fontSize = 14.sp, fontWeight = FontWeight.Medium, textAlign = TextAlign.Start)
        }

        TextField(
            value = password,
            onValueChange = {
                password = it
                isPasswordValid = hasSpace(password) // Validate input
            },
            label = { Text("Password") },
            isError = !isPasswordValid, // Highlights the field in red if invalid
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = { focusManager.moveFocus(FocusDirection.Down) }
            ),
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )
        if (!isPasswordValid) {
            Text(
                text = "Password cannot contain spaces",
                color = Color.Red,
                modifier = Modifier.fillMaxWidth()
            )
        }


        Spacer(modifier = Modifier.height(12.dp))
        // campul pentru confirmarea parolei
        Row(
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
                }
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(40.dp))
        // buton pentru inregistrare
        Button(
            onClick = {
                if (passwordMatch) {
                    // logica pentru inregistrare
                    val dateCreated = System.currentTimeMillis().toString()
                    val user = User(username = username, email = email, password = password, dateCreated = dateCreated)

                    // daca inregistrarea este reusita, se salveaza datele in SharedPreferences
                    userDAO.insertUser(user) { newUser ->
                        val sharedPreferences = sessionManager.getsharedPreferences()
                        with(sharedPreferences.edit()) {
                            putString("username", newUser.username)
                            putString("email", newUser.email)
                            putString("dateCreated", newUser.dateCreated)
                            apply()
                        }
                        // se navigheaza catre pagina de login
                        navController.navigate("loginPage")
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = canRegister
        ) {
            Text("Register")
        }
    }
}
// functie pentru validarea email-ului
fun isEmailValid(email: String): Boolean {
    val emailPattern = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}\$"
    return emailPattern.toRegex().matches(email)
}
fun hasSpace(input: String): Boolean {
    return !input.contains(" ")
}
