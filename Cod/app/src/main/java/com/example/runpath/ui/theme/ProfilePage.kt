package com.example.runpath.ui.theme

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
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.runpath.MainActivity
import com.example.runpath.R
import com.example.runpath.database.SessionManager
import com.example.runpath.database.UserDAO
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ProfilePage(navController: NavController, sessionManager: SessionManager) {
    // variabile pentru datele utilizatorului
    val sharedPreferences = sessionManager.getsharedPreferences()
    val username = sharedPreferences.getString("username", "N/A")
    val email = sharedPreferences.getString("email", "N/A")
    val context = LocalContext.current
    val dateCreated = sharedPreferences.getString("dateCreated", "N/A")
    val userDAO = UserDAO(context)
    val userId = sessionManager.getsharedPreferences().getString(SessionManager.KEY_USER_ID, "N/A")!!

    val showDialog = remember { mutableStateOf(false) }
    val newPassword = remember { mutableStateOf("") }
    val confirmPassword = remember { mutableStateOf("") }

    val showDialog2 = remember { mutableStateOf(false) }
    // afisarea paginii de profil
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
        // afisarea datelor utilizatorului
        Spacer(modifier = Modifier.height(20.dp))
        ProfileDetailItem(label = "Username", value = username)
        ProfileDetailItem(label = "Email", value = email)
        ProfileDetailItem(label = "Date Created", value = formatDate(dateCreated ?: "N/A"))

        Spacer(modifier = Modifier.height(20.dp))
        //buton pentru previous runs
        Button(
            onClick = {
                navController.navigate("previous_runs")
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
        // buton pentru logout
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
        // buton pentru schimbarea parolei
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
        // buton pentru stergerea contului
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
        // dialog pentru stergerea contului
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
// formatarea datei
fun formatDate(timestamp: String): String {
    return try {
        val sdf = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        val date = Date(timestamp.toLong())
        sdf.format(date)
    } catch (e: Exception) {
        "Invalid Date"
    }

}
