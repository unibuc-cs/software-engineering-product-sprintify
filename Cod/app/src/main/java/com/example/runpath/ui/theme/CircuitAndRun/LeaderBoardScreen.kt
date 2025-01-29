package com.example.runpath.ui.theme.CircuitAndRun

import RunViewModel
import android.icu.text.SimpleDateFormat
import android.text.format.DateUtils.formatElapsedTime
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.runpath.database.UserDAO
import com.example.runpath.models.Leaderboard
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeaderboardScreen(circuitId: String, navController: NavController,userDAO: UserDAO) {
    val viewModel: RunViewModel = viewModel()
    val leaderboardEntries by viewModel.leaderboardEntries

    LaunchedEffect(circuitId) {
        viewModel.loadLeaderboard(circuitId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Circuit Leaderboard") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            itemsIndexed(leaderboardEntries.sortedBy { it.time }) { index, entry ->
                LeaderboardItem(entry, userDAO, index + 1)
            }
        }
    }
}

@Composable
fun LeaderboardItem(entry: Leaderboard,userDAO: UserDAO,rank: Int) {
    val userName = remember { mutableStateOf<String?>(null) }
    userDAO.getUserById(entry.userId) { user ->
        userName.value = user?.username
    }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {

                Text("$rank. User: ${userName.value}", style = MaterialTheme.typography.bodyMedium)
                Text("Date: ${formatDate(entry.timestamp)}", style = MaterialTheme.typography.bodySmall)
            }
            Spacer(modifier = Modifier.weight(1f))
            Column(horizontalAlignment = Alignment.End) {
                var time = entry.time
                time /= 1000
                Text(formatElapsedTime(time), style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}

private fun formatDate(timestamp: Long): String {
    val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    return sdf.format(Date(timestamp))
}
class UserViewModel(private val userDAO: UserDAO) : ViewModel() {

    private val _username = MutableLiveData<String?>()
    val username: LiveData<String?> get() = _username

    fun fetchUsername(userId: String) {
        userDAO.getUserById(userId) { user ->
            _username.postValue(user?.username)
        }
    }
}

