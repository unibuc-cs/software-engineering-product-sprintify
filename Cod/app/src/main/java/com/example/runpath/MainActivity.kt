package com.example.runpath

import HomePage
import LoginPage
import com.example.runpath.ui.theme.ProfilePage
import android.os.Bundle
import androidx.activity.compose.setContent
import com.example.runpath.ui.theme.RunPathTheme
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.runpath.database.SessionManager
import com.example.runpath.ui.theme.CommunityPage
import com.example.runpath.ui.theme.MainInterface
import com.example.runpath.ui.theme.Previous_runs
import com.example.runpath.ui.theme.RegisterPage
import com.example.runpath.ui.theme.UserProfilePage

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val sessionManager = SessionManager(this)

        setContent {
            RunPathTheme {
                val navController = rememberNavController()
                NavHost(
                    navController = navController,
                    startDestination = if (sessionManager.isLoggedIn()) "mainInterface" else "homePage"
                ) {
                    composable("homePage") {
                        HomePage(navController)
                    }
                    composable("loginPage") {
                        LoginPage(navController)
                    }
                    composable("registerPage") {
                        RegisterPage(navController, sessionManager)
                    }
                    composable("mainInterface") {
                        MainInterface()
                    }
                    composable("profilePage") {
                        ProfilePage(navController, sessionManager)
                    }

                    composable("community") {
                        CommunityPage(navController, sessionManager)
                    }

                }
            }
        }
    }
}


