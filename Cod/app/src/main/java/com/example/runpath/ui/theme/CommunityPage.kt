package com.example.runpath.ui.theme

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import java.time.LocalDateTime
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import com.example.runpath.database.SessionManager

@Composable
fun CommunityPage(navController: NavController, sessionManager: SessionManager) {
    val sharedPreferences = sessionManager.getsharedPreferences()
    var text by remember { mutableStateOf("Community") }
    val username = sharedPreferences.getString("username", "N/A") ?: "N/A"
    var posts by remember { mutableStateOf(listOf<Post>()) } // o sa adaugam in baza de date postul
    var showDialog by remember { mutableStateOf(false) }
    var newPostContent by remember { mutableStateOf("") }

    Box(
        modifier = Modifier.fillMaxSize(),
    ) {
        Text(
            text = text,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(16.dp)
        )

        // feed-ul de posturi
        LazyColumn(
            modifier = Modifier
                .padding(top=58.dp)
        ) {
            itemsIndexed(posts) { index, post ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "${post.author}: ${post.content} at ${post.timestamp}")

                    // momentan o sa adaug ceva care doar nu afiseaza indexul postarii cand i se da delete
                    if (post.author == username) {
                        IconButton(
                            onClick = {
                                posts = posts.filterIndexed { i, _ -> i != index }
                            }
                        ) {
                            Icon(Icons.Default.Close, contentDescription = "Delete Post")
                        }
                    }
                }
            }
        }
        // butonul de creare a unui post
        Button(
            onClick = { showDialog = true },
            modifier = Modifier
                .padding(bottom=58.dp, end=8.dp)
                .align(Alignment.BottomEnd)
        ) {
            Text("Create Post")
        }

        // dialogul de creare a unui post daca butonul este apasat
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
                    //momentan ce e aici e un mockup
                    Button(
                        onClick = {
                            val post = Post(
                                author = username,
                                content = newPostContent,
                                timestamp = LocalDateTime.now().toString()
                            )
                            posts = posts + post
                            newPostContent = ""
                            showDialog = false
                        }
                    ) {
                        Text("Create Post")
                    }
                }
            }
        }
    }
}


//creem o clasa temporara pentru posturi ca sa vad daca merge
class Post(author: String, content: String, timestamp: String) {
    var author: String by mutableStateOf(author)
    var content: String by mutableStateOf(content)
    var timestamp: String by mutableStateOf(timestamp)

}
