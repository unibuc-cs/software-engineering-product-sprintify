import android.content.Intent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.example.runpath.MainActivity
import com.example.runpath.database.SessionManager
import com.example.runpath.ui.theme.BottomNavItem

@Composable
fun ProfilePage(navController: NavController, sessionManager: SessionManager) {
    val sharedPreferences = sessionManager.getsharedPreferences()
    val username = sharedPreferences.getString("username", "N/A")
    val email = sharedPreferences.getString("email", "N/A")
    val context = LocalContext.current
    val dateCreated = sharedPreferences.getString("dateCreated", "N/A")

    println("profile accessed")  // Debugging statement

    Column(modifier = Modifier.fillMaxSize()) {
        Text(text = "Username: $username")
        Text(text = "Email: $email")
        Text(text = "Date Created: $dateCreated")

        Button(onClick = {
            sessionManager.clearSession()
            val intent = Intent(context, MainActivity::class.java)
            context.startActivity(intent)
        }) {
            Text("Logout")
        }

        Button(
            onClick = {
                navController.navigate("changePasswordPage")
            }
        ) {
            Text("Change Password")
        }
    }
}
