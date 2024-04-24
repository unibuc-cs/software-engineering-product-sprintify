package com.example.runpath

import FeedReaderDbHelper
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.runpath.ui.theme.HomePage
import com.example.runpath.ui.theme.RegisterPage
import com.example.runpath.ui.theme.RunPathTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

            RunPathTheme {
                val dbHelper = FeedReaderDbHelper(this@MainActivity)
                Box(contentAlignment = Alignment.Center,modifier = Modifier.fillMaxWidth())
                {
                    HomePage(dbHelper)
                }


            }

        }
    }
}


