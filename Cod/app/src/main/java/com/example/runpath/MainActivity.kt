package com.example.runpath

import FeedReaderDbHelper
import HomePage
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
<<<<<<< Updated upstream
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.runpath.ui.theme.HomePage
import com.example.runpath.ui.theme.RegisterPage
=======
>>>>>>> Stashed changes
import com.example.runpath.ui.theme.RunPathTheme
import LoginPage
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

import com.example.runpath.ui.theme.MainInterface
import com.example.runpath.ui.theme.RegisterPage

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

            RunPathTheme {
                val dbHelper = FeedReaderDbHelper(this@MainActivity)
                val navController = rememberNavController()
                NavHost(navController, startDestination = "homePage") // momentan am pus loginpage pana termina serban homepage-ul
                {
                    composable("homePage"){
                        HomePage(dbHelper,navController)
                    }
                    composable("loginPage") {
                        LoginPage(navController, dbHelper)
                    }
                    composable("registerPage") {RegisterPage()}
                    composable("mainInterface") {MainInterface()}
                }
                /*Box(contentAlignment = Alignment.Center,modifier = Modifier.fillMaxWidth())
                {
                    HomePage(dbHelper)
                }*/


            }

        }
    }
}


