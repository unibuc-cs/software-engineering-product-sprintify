package com.example.runpath.ui.theme.StarRating

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.AlertDialog
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ModifierLocalBeyondBoundsLayout
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun RatingStars(
    rating: Int,
    onRatingChanged: (Int) -> Unit
) {
    Row {
        for (i in 1..5) {
            Icon(
                imageVector = if(i <= rating) Icons.Filled.Star else Icons.Outlined.Star,
                contentDescription = null,
                modifier = Modifier
                    .padding(4.dp)
                    .clickable{onRatingChanged(i)}
            )
        }
    }
}

@Composable
fun RatingDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    onRatingSubmitted: (Int) -> Unit
) {
    if(showDialog) {
        val (rating, setRating) = remember { mutableStateOf(0)}

        AlertDialog(
            onDismissRequest = onDismiss,
            title = {Text(text = "Rate the circuit")},
            text = {
                RatingStars(rating = rating, onRatingChanged = setRating)
            },
            confirmButton = {
                Button(
                    onClick = {
                        onRatingSubmitted(rating)
                        onDismiss()
                    }
                ) {
                    Text("Submit")
                }
            },
            dismissButton = {
                Button(onClick = onDismiss) {
                    Text("Cancel")
                }
            }
        )
    }
}

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun MyApp() {
    val (showDialog, setShowDialog) = remember { mutableStateOf(false) }
    val (rating, setRating) = remember { mutableStateOf(0) }

    Scaffold(
        content = {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(onClick = { setShowDialog(true) }) {
                    Text("Rate something")
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(text = "Current Rating: $rating stars")

                RatingDialog(
                    showDialog = showDialog,
                    onDismiss = { setShowDialog(false) },
                    onRatingSubmitted = { newRating ->
                        setRating(newRating)
                        setShowDialog(false)
                    }
                )
            }
        }
    )
}

@Composable
@Preview(showBackground = true)
fun PreviewRatingSystem() {
    MyApp()
}