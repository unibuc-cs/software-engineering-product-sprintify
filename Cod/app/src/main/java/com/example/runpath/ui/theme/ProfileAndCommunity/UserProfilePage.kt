package com.example.runpath.ui.theme.ProfileAndCommunity

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.runpath.database.UserDAO
import com.example.runpath.models.User
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun UserProfilePage(navController: NavController, username: String) {
    val context = LocalContext.current
    val userDAO = remember { UserDAO(context) }
    var user by remember { mutableStateOf<User?>(null) }

    LaunchedEffect(username) { Log.d("UserProfilePage", "Received username: $username")
        userDAO.getUserByUsername(username) { users ->
            if (users.isNotEmpty()) {
                user = users[0]
            } else {
                user = null
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF6A1B9A), Color(0xFF8A2BE2))
                )
            )
            .padding(16.dp)
    ) {
        user?.let {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
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
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Profile Picture",
                        modifier = Modifier.fillMaxSize(),
                        tint = Color.Gray
                    )
                }
                Spacer(modifier = Modifier.height(20.dp))
                ProfileDetailItem2(label = "Username", value = it.username)
                ProfileDetailItem2(label = "Email", value = it.email)
                val dateCreated = formatDate3(it.dateCreated)
                ProfileDetailItem2(label = "Date Created", value = dateCreated)
            }
        } ?: run {
            Text(text = "Loading...", color = Color.White)
        }
    }
}

@Composable
fun ProfileDetailItem2(label: String, value: String?) {
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
                else -> Icons.Default.Person
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
fun formatDate3(timestamp: String): String {
    return try {
        val sdf = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        val date = Date(timestamp.toLong())
        sdf.format(date)
    } catch (e: Exception) {
        "Invalid Date"
    }
}
