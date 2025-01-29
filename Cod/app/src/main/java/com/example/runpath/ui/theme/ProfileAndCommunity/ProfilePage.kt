package com.example.runpath.ui.theme.ProfileAndCommunity

import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.runpath.MainActivity
import com.example.runpath.R
import com.example.runpath.database.SessionManager
import com.example.runpath.database.UserDAO
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter


@Composable
fun ProfilePage(navController: NavController, sessionManager: SessionManager) {
    val sharedPreferences = sessionManager.getsharedPreferences()
    val username = sharedPreferences.getString("username", "N/A")
    val email = sharedPreferences.getString("email", "N/A")
    val context = LocalContext.current

    val userDAO = UserDAO(context)
    val userId = sessionManager.getsharedPreferences().getString(SessionManager.KEY_USER_ID, "N/A")!!

    var dateCreated by remember { mutableStateOf("N/A") }
    LaunchedEffect(userId) {
        userDAO.getDateCreated(userId) { formattedDate ->
            dateCreated = formattedDate
        }
    }

    val showDialog = remember { mutableStateOf(false) }
    val newPassword = remember { mutableStateOf("") }
    val confirmPassword = remember { mutableStateOf("") }

    val showDialog2 = remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF6A1B9A), Color(0xFF8A2BE2))
                )
            )
            .padding(16.dp)
    ) {
        Spacer(modifier = Modifier.height(40.dp))
        Text(
            text = "Profile",
            fontSize = 24.sp,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(20.dp))
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(Color.White)
                .align(Alignment.CenterHorizontally)
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_launcher_foreground),
                contentDescription = "Profile Picture",
                modifier = Modifier.fillMaxSize()
            )
        }
        Spacer(modifier = Modifier.height(20.dp))
        ProfileDetailItem(label = "Username", value = username)
        ProfileDetailItem(label = "Email", value = email)
        ProfileDetailItem(label = "Date Created", value = dateCreated)

        Spacer(modifier = Modifier.height(20.dp))
        Button(
            onClick = {
                navController.navigate("previous_runsHistory")
            },
            colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF6A1B9A)),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .height(48.dp)
                .clip(RoundedCornerShape(24.dp))
        ) {
            Text(text = "Previous Runs", color = Color.White)
        }
        Spacer(modifier = Modifier.height(20.dp))
        Button(
            onClick = {
                sessionManager.clearSession()
                val intent = Intent(context, MainActivity::class.java)
                context.startActivity(intent)
            },
            colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF6A1B9A)),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .height(48.dp)
                .clip(RoundedCornerShape(24.dp))
        ) {
            Text(text = "Logout", color = Color.White)
        }

        Spacer(modifier = Modifier.height(20.dp))
        Button(
            onClick = { showDialog.value = true },
            colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF6A1B9A)),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .height(48.dp)
                .clip(RoundedCornerShape(24.dp))
        ) {
            Text(text = "Change Password", color = Color.White)
        }

        if (showDialog.value) {
            AlertDialog(
                onDismissRequest = { showDialog.value = false },
                title = { Text(text = "Change Password") },
                text = {
                    Column {
                        TextField(
                            value = newPassword.value,
                            onValueChange = { newPassword.value = it },
                            label = { Text("New Password") },
                            visualTransformation = PasswordVisualTransformation()
                        )
                        TextField(
                            value = confirmPassword.value,
                            onValueChange = { confirmPassword.value = it },
                            label = { Text("Confirm Password") },
                            visualTransformation = PasswordVisualTransformation()
                        )
                    }
                },
                confirmButton = {
                    TextButton(onClick = {
                        if (newPassword.value == confirmPassword.value) {
                            userDAO.setPassword(userId, newPassword.value)
                            showDialog.value = false
                        } else {
                            // show error
                        }
                    }) {
                        Text("Confirm")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDialog.value = false }) {
                        Text("Cancel")
                    }
                }
            )
        }

        Spacer(modifier = Modifier.height(20.dp))
        Button(
            onClick = { showDialog2.value = true },
            colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF6A1B9A)),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .height(48.dp)
                .clip(RoundedCornerShape(24.dp))
        ) {
            Text(text = "Delete Account", color = Color.White)
        }

        if (showDialog2.value) {
            AlertDialog(
                onDismissRequest = { showDialog2.value = false },
                title = { Text(text = "Delete Account") },
                text = { Text("Are you sure you want to delete your account?") },
                confirmButton = {
                    TextButton(onClick = {
                        userDAO.deleteUser(userId)
                        sessionManager.clearSession()
                        val intent = Intent(context, MainActivity::class.java)
                        context.startActivity(intent)
                    }) {
                        Text("Confirm")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDialog2.value = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}
// afisarea detaliilor utilizatorului
@Composable
fun ProfileDetailItem(label: String, value: String?) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Icon(
            imageVector = when(label) {
                "Username" -> Icons.Default.Person
                "Email" -> Icons.Default.Email
                "Date Created" -> Icons.Default.DateRange
                else -> Icons.Default.Info
            },
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(text = label, color = Color.White, fontWeight = FontWeight.Bold)
            Text(text = value ?: "N/A", color = Color.White)
        }
    }
}

