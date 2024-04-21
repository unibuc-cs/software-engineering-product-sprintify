package com.example.runpath

import FeedReaderDbHelper
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
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
                Box(contentAlignment = Alignment.Center,modifier = Modifier.fillMaxWidth())
                {
                    Button(
                        onClick = {

                            val dbHelper = FeedReaderDbHelper(this@MainActivity)
                            val leaderboardDAO = LeaderboardDAO(this@MainActivity, dbHelper)

                            var done = leaderboardDAO.insertLeaderboard(1, 1, 1, 1, "1:00:00")
                            println("done: $done")


                        },

                        ) {
                        Text(text = "click me")
                    }
                }



            }

        }
    }
}

@Composable
private fun createLeaderBoard() {
    Row {
        Text(text = "Leaderboard", modifier = Modifier.fillMaxWidth())
    }
}