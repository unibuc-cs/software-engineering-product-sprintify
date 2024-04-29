//package com.example.runpath
//
package com.example.runpath
import FeedReaderDbHelper
import HomePage
import LoginPage
import ProfilePage
import android.os.Bundle
import androidx.activity.compose.setContent
import com.example.runpath.ui.theme.RunPathTheme
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.runpath.database.SessionManager
import com.example.runpath.databinding.ActivityMapsBinding
import com.example.runpath.ui.theme.MainInterface
import com.example.runpath.ui.theme.RegisterPage




class MainActivity : AppCompatActivity() /*, OnMapReadyCallback*/ {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sessionManager = SessionManager(this)

        setContent {

            RunPathTheme {
                val dbHelper = FeedReaderDbHelper(this@MainActivity)
                val navController = rememberNavController()
                //pentru cazul in care userul nu este logat in cont
                if (sessionManager.isLoggedIn() == false) {
                    NavHost(navController, startDestination = "homePage")
                    {
                        composable("homePage") {
                            HomePage(dbHelper, navController)
                        }
                        composable("loginPage") {
                            LoginPage(navController, dbHelper)
                        }
                        composable("registerPage") { RegisterPage(navController, dbHelper) }
                        composable("mainInterface") { MainInterface() }
                        composable("profilePage") { ProfilePage(navController, sessionManager) }
                    }
//                Box(contentAlignment = Alignment.Center,modifier = Modifier.fillMaxWidth())
//                {
//                    HomePage(dbHelper)
//                }
                }
                //pentru cazul in care userul este deja logat in cont
                else {
                    NavHost(navController, startDestination = "mainInterface")
                    {
                        composable("homePage") {
                            HomePage(dbHelper, navController)
                        }
                        composable("loginPage") {
                            LoginPage(navController, dbHelper)
                        }
                        composable("registerPage") { RegisterPage(navController, dbHelper) }
                        composable("mainInterface") { MainInterface() }
                        composable("profilePage") { ProfilePage(navController, sessionManager) }

                    }
                }

            }

        }
    }
}