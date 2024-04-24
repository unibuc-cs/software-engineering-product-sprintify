package com.example.runpath.ui.theme

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun MainInterface() {

    Column (
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // Start run button
        Button (
            onClick = {
                // Logic for starting the run
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Start Run")
        }

        // Circuit selection button
        Button (
            onClick = {
                // Logic for selecting the circuit
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Select Circuit")
        }

        // Join community button
        Button (
            onClick = {
                // Logic for joining community
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Join Community")
        }

        // Account button
        Button (
            onClick = {
                // Logic for seeing user account
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Account")
        }
    }
}


@Preview(showBackground = true)
@Composable
fun PreviewMainInterface() {
    MainInterface()
}