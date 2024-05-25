//package com.example.runpath
//
package com.example.runpath
import HomePage
import LoginPage
import ProfilePage
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import com.example.runpath.ui.theme.RunPathTheme
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.runpath.database.SessionManager
import com.example.runpath.tracking.LocationViewModel
import com.example.runpath.ui.theme.MainInterface
import com.example.runpath.ui.theme.RegisterPage


class MainActivity : AppCompatActivity() {
    private val locationViewModel by viewModels<LocationViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val sessionManager = SessionManager(this)

        if(ContextCompat.checkSelfPermission(
            this, android.Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED
            ) {
            requestPermissions.launch(arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION))
        }

        setContent {
            RunPathTheme {
                val navController = rememberNavController()

                NavHost(navController, startDestination = if (sessionManager.isLoggedIn()) "mainInterface" else "homePage") {
                    composable("homePage") {
                        HomePage(navController)
                    }
                    composable("loginPage") {
                        LoginPage(navController)
                    }
                    composable("registerPage") { RegisterPage(navController) }
                    composable("mainInterface") { MainInterface(locationViewModel)}
                    composable("profilePage") { ProfilePage(navController, sessionManager) }
                }
            }
        }
    }

    private val requestPermissions = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {permissions ->
        if(permissions[android.Manifest.permission.ACCESS_FINE_LOCATION] == true) {
            locationViewModel.startTracking()
        }
    }
}