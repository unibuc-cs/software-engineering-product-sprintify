import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun LoginPage() {
    var username by remember { mutableStateOf("")}
    var password by remember { mutableStateOf("")}

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    )
    {
        Column (
            modifier = Modifier.padding(16.dp).fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally
        ){
            Text(
                text = "RunPath",
                fontSize =  24.sp,
                modifier = Modifier.padding(16.dp)
            )
            // campul pentru username
            BasicTextField(
                value = username,
                onValueChange = { username = it},
                singleLine = true,
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text),
                decorationBox = { innerTextField ->
                    Box(
                        modifier = Modifier.background(Color.LightGray).padding(8.dp)
                    ){
                        if(username.isEmpty())
                        {
                            Text("Username", color = Color.Gray)
                        }
                        innerTextField()
                    }

                }
            )
            Spacer(modifier = Modifier.height(8.dp))
            // campul pentru parola
            BasicTextField(
                value = password,
                onValueChange = { password = it},
                singleLine = true,
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Password),
                visualTransformation = PasswordVisualTransformation(),
                decorationBox = { innerTextField ->
                    Box(
                        modifier = Modifier.background(Color.LightGray).padding(8.dp)
                    )
                    {
                        if(password.isEmpty())
                        {
                            Text("Password", color = Color.Gray)
                        }
                        innerTextField()
                    }
                }

            )

            Spacer(modifier = Modifier.height(16.dp))

            // buton de login

            @Composable
            fun LoginPage() {
                var username by remember { mutableStateOf("")}
                var password by remember { mutableStateOf("")}
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column (
                        modifier = Modifier.padding(16.dp).fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally
                    ){
                        Text(
                            text = "RunPath",
                            fontSize =  24.sp,
                            modifier = Modifier.padding(16.dp)
                        )
                        BasicTextField(
                            value = username,
                            onValueChange = { username = it},
                            singleLine = true,
                            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text),
                            decorationBox = { innerTextField ->
                                Box(
                                    modifier = Modifier.background(Color.LightGray).padding(8.dp)
                                ){
                                    if(username.isEmpty())
                                    {
                                        Text("Username", color = Color.Gray)
                                    }
                                    innerTextField()
                                }

                            }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        BasicTextField(
                            value = password,
                            onValueChange = { password = it},
                            singleLine = true,
                            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Password),
                            visualTransformation = PasswordVisualTransformation(),
                            decorationBox = { innerTextField ->
                                Box(
                                    modifier = Modifier.background(Color.LightGray).padding(8.dp)
                                )
                                {
                                    if(password.isEmpty())
                                    {
                                        Text("Password", color = Color.Gray)
                                    }
                                    innerTextField()
                                }
                            }

                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = { /* aici logica pentru login cand termina bote*/},
                            modifier = Modifier.fillMaxWidth()
                        ){
                            Text("Login")
                        }
                        Spacer(modifier = Modifier.height(8.dp))

                        TextButton(
                            onClick = { /* functia pentru a uita parola */ },
                        ){
                            Text("Ai uitat parola?", color = MaterialTheme.colorScheme.primary)
                        }
                        Spacer(modifier = Modifier.height(8.dp))

                        TextButton(
                            onClick = { /* functia pentru a te inregistra */ },
                        ){
                            Text("Register", color = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
            }
        }
    }
}