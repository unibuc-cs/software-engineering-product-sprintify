import android.content.Intent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.example.runpath.MainActivity
import com.example.runpath.database.SessionManager
import com.example.runpath.database.UserDAO
import com.example.runpath.ui.theme.BottomNavItem

@Composable
fun ProfilePage(navController: NavController, sessionManager: SessionManager) {
    val sharedPreferences = sessionManager.getsharedPreferences()
    val username = sharedPreferences.getString("username", "N/A")
    val email = sharedPreferences.getString("email", "N/A")
    val context = LocalContext.current
    val dateCreated = sharedPreferences.getString("dateCreated", "N/A")
    val userDAO = UserDAO(context)
    val userId = sessionManager.getsharedPreferences().getString(SessionManager.KEY_USER_ID, "N/A")!!


    println("profile accessed")  // debugging

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



        val showDialog = remember { mutableStateOf(false) }
        val newPassword = remember { mutableStateOf("") }
        val confirmPassword = remember { mutableStateOf("") }

        if (showDialog.value) {
            AlertDialog(
                onDismissRequest = { showDialog.value = false },
                title = { Text(text = "Change Password") },
                text = {
                    Column {
                        TextField(
                            //field for the new password
                            value = newPassword.value,
                            onValueChange = { newPassword.value = it },
                            label = { Text("New Password") }
                        )
                        TextField(
                            //field for the confirmation of the new password
                            value = confirmPassword.value,
                            onValueChange = { confirmPassword.value = it },
                            label = { Text("Confirm Password") }
                        )
                    }
                },
                confirmButton = {
                    TextButton(onClick = {
                        if (newPassword.value == confirmPassword.value) {
                            // update password in database
                            userDAO.setPassword(userId, newPassword.value)
                            showDialog.value = false
                        } else {
                            // we will add a red text that will say that the passwords do not match


                        }
                    }) {
                        Text("Confirm")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDialog.value = false }) {
                        Text("Cancel")
                    }
                }
            )
        }

        Button(
            onClick = { showDialog.value = true }
        ) {
            Text("Change Password")
        }

        //delete account section
        val showDialog2 = remember { mutableStateOf(false) }
        if(showDialog2.value){
            //add a pop up that will have a confirmation text
            AlertDialog(
                onDismissRequest = { showDialog2.value = false },
                title = { Text(text = "Delete Account") },
                text = {
                    Text("Are you sure you want to delete your account?")
                },
                confirmButton = {
                    TextButton(onClick = {
                        //if accepted delete the account and clear the session like in the logout
                        userDAO.deleteUser(userId)
                        sessionManager.clearSession()
                        val intent = Intent(context, MainActivity::class.java)
                        context.startActivity(intent)
                    }) {
                        Text("Confirm")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDialog2.value = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
        //buttton for showing the dialogue
        Button(
            onClick = {
                showDialog2.value = true
            }
        )
        {
            Text("Delete Account")
        }

    }
}
