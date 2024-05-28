package com.example.runpath.ui.theme

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.example.runpath.database.PostDAO
import com.example.runpath.database.SessionManager
import com.example.runpath.models.Post
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.Locale

@Composable
fun CommunityPage(navController: NavController, sessionManager: SessionManager) {
    val sharedPreferences = sessionManager.getsharedPreferences()
    var text by remember { mutableStateOf("Community") }
    val username = sharedPreferences.getString("username", "N/A") ?: "N/A"
    val userId = sharedPreferences.getString("user_id", "N/A")
    var posts by remember { mutableStateOf(listOf<Post>()) }
    var showDialog by remember { mutableStateOf(false) }
    var newPostContent by remember { mutableStateOf("") }

    val postDAO = PostDAO()

    // Listener for posts
    DisposableEffect(Unit) {
        val listenerRegistration = postDAO.listenForPosts { updatedPosts ->
            posts = updatedPosts
        }

        onDispose {
            listenerRegistration.remove()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF8A2BE2))
    ) {
        Text(
            text = text,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = Color.White,
            modifier = Modifier.padding(16.dp)
        )

        LazyColumn(
            modifier = Modifier
                .padding(top = 58.dp, bottom = 58.dp)
        ) {
            itemsIndexed(posts) { index, post ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.White)
                        .padding(16.dp)
                        .clickable { Log.d("Navigation", "Navigating to userProfile/${post.author}")
                            navController.navigate("userProfile/${post.author}")
                        },
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = post.author,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.clickable { Log.d("Navigation", "Navigating to userProfile/${post.author}")
                                navController.navigate("userProfile/${post.author}")
                            }
                        )
                        Text(
                            text = formatDate2(post.timestamp),
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray
                        )
                        Text(text = "\n${post.content}")
                    }

                    if (post.author == username) {
                        IconButton(
                            onClick = {
                                post.postId?.let { postDAO.deletePost(it) }
                            },
                            modifier = Modifier
                                .size(24.dp)
                                .background(Color(0xFF8A2BE2), shape = RoundedCornerShape(12.dp))
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Close,
                                contentDescription = "Delete Post",
                                tint = Color.White
                            )
                        }
                    }
                }
            }
        }

        Button(
            onClick = { showDialog = true },
            modifier = Modifier
                .padding(bottom = 58.dp, end = 8.dp)
                .align(Alignment.BottomEnd)
        ) {
            Text("Create Post")
        }

        if (showDialog) {
            Dialog(onDismissRequest = { showDialog = false }) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    TextField(
                        value = newPostContent,
                        onValueChange = { newPostContent = it },
                        label = { Text("Post Content") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            val post = Post(
                                userId = userId ?: "N/A",
                                author = username,
                                content = newPostContent,
                                timestamp = LocalDateTime.now().toString()
                            )
                            postDAO.insertPost(post) {
                                newPostContent = ""
                                showDialog = false
                            }
                        }
                    ) {
                        Text("Create Post")
                    }
                }
            }
        }
    }
}

fun formatDate2(timestamp: String): String {
    return try {
        val sdf = SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault())
        val date = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(timestamp)
        sdf.format(date)
    } catch (e: Exception) {
        "Invalid Date"
    }
}
