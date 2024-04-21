package com.example.runpath

import FeedReaderDbHelper
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.runpath.database.LeaderboardDAO
import com.example.runpath.ui.theme.RunPathTheme
import kotlin.time.Duration
import kotlin.time.hours
import kotlin.time.minutes
import kotlin.time.seconds

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

            RunPathTheme {

                val dbHelper = FeedReaderDbHelper(this@MainActivity)
                val leaderboardDAO = LeaderboardDAO(this@MainActivity, dbHelper)

                Box(contentAlignment = Alignment.Center,modifier = Modifier.fillMaxWidth())
                {
                    SimpleForm(this@MainActivity, dbHelper)
                }


            }

        }
    }
}

@Composable
fun SimpleForm(context: Context, dbHelper: FeedReaderDbHelper) {
    // Define state variables for the fields
    var leaderboardId by remember { mutableStateOf("") }
    var circuitId by remember { mutableStateOf("")}
    var userId by remember { mutableStateOf("")}
    var rank by remember { mutableStateOf("")}
    var time by remember { mutableStateOf("")}

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        // Text field for Leaderboard ID
        TextField(
            value = leaderboardId,
            onValueChange = { leaderboardId = it },
            label = { Text("Leaderboard ID") },
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions.Default,
            modifier = Modifier.fillMaxWidth()
        )

        // Text field for Circuit ID
        TextField(
            value = circuitId,
            onValueChange = { circuitId = it },
            label = { Text("Circuit ID") },
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions.Default,
            modifier = Modifier.fillMaxWidth()
        )

        // Text field for User ID
        TextField(
            value = userId,
            onValueChange = { userId = it },
            label = { Text("User ID") },
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions.Default,
            modifier = Modifier.fillMaxWidth()
        )

        // Text field for Leaderboard Rank
        TextField(
            value = rank,
            onValueChange = { rank = it },
            label = { Text("Rank") },
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions.Default,
            modifier = Modifier.fillMaxWidth()
        )

        // Text field for Leaderboard Time
        TextField(
            value = time,
            onValueChange = { time = it },
            label = { Text("Time") },
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions.Default,
            modifier = Modifier.fillMaxWidth()
        )

        // Button to simulate saving the form data
        Button(
            onClick = {
                val leaderboardDAO = LeaderboardDAO(context, dbHelper)
                leaderboardDAO.insertLeaderboard(leaderboardId.toInt(), circuitId.toInt(),
                    userId.toInt(), rank.toInt(), time)

            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Submit")
        }
    }
}
