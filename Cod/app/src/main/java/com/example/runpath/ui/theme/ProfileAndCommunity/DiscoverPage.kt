package com.example.runpath.ui.theme.ProfileAndCommunity

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.example.runpath.database.CommunityDAO
import com.example.runpath.models.Community
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun DiscoverPage(userId: String, navController: NavController) {
    val communityDAO = remember { CommunityDAO() }
    var communities by remember { mutableStateOf<List<Community>>(emptyList()) }
    var joinedCommunities by remember { mutableStateOf<List<Community>>(emptyList()) }
    var showDialog by remember { mutableStateOf(false) }
    var newCommunityName by remember { mutableStateOf("") }
    var newCommunityDescription by remember { mutableStateOf("") }
    var isProcessing by remember { mutableStateOf(false) }

    // Refresh data from database
    suspend fun refreshCommunityData(onComplete: () -> Unit = {}) {
        val updatedJoinedCommunities = communityDAO.getJoinedCommunities(userId)
        communityDAO.listenForCommunities { updatedCommunities: List<Community> ->
            CoroutineScope(Dispatchers.Main).launch {
                joinedCommunities = updatedJoinedCommunities
                communities = updatedCommunities
                onComplete()
            }
        }
    }

    DisposableEffect(Unit) {
        val listener = communityDAO.listenForCommunities { updated ->
            communities = updated
        }
        // Add this listener for joined communities
        val joinedListener = communityDAO.listenForJoinedCommunities(userId) { updated ->
            joinedCommunities = updated
        }
        onDispose {
            listener.remove()
            joinedListener.remove()
        }
    }

    LaunchedEffect(userId) {
        CoroutineScope(Dispatchers.IO).launch {
            refreshCommunityData()
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.weight(1f).padding(8.dp)) {
            Column(modifier = Modifier.weight(1f).padding(8.dp)) {
                Text("Available Communities", style = MaterialTheme.typography.h6, modifier = Modifier.padding(8.dp))
                LazyColumn {
                    itemsIndexed(communities) { _, community ->
                        val isMember = joinedCommunities.any { it.communityId == community.communityId }
                        var isCreator by remember { mutableStateOf(false) }

                        LaunchedEffect(community.communityId) {
                            communityDAO.isUserCreatorOfCommunity(community.communityId ?: "", userId) { isCreator = it }
                        }

                        CommunityItem(
                            community = community,
                            isMember = isMember,
                            onJoin = {
                                if (!isProcessing) {
                                    isProcessing = true
                                    community.communityId?.let {
                                        CoroutineScope(Dispatchers.IO).launch {
                                            communityDAO.joinCommunity(it, userId)
                                            refreshCommunityData { isProcessing = false }
                                        }
                                    }
                                }
                            },
                            // Update the leave handler
                            onLeave = {
                                if (!isProcessing) {
                                    isProcessing = true
                                    community.communityId?.let { commId ->
                                        CoroutineScope(Dispatchers.IO).launch {
                                            try {
                                                communityDAO.leaveCommunity(commId, userId)
                                                // No need to manually refresh - listeners will update automatically
                                            } finally {
                                                withContext(Dispatchers.Main) {
                                                    isProcessing = false
                                                }
                                            }
                                        }
                                    }
                                }
                            },
                            onDelete = {
                                if (!isProcessing) {
                                    isProcessing = true
                                    community.communityId?.let {
                                        CoroutineScope(Dispatchers.IO).launch {
                                            communityDAO.deleteCommunity(it)
                                            refreshCommunityData { isProcessing = false }
                                        }
                                    }
                                }
                            },
                            isCreator = isCreator
                        )
                    }
                }
            }

            Column(modifier = Modifier.weight(1f).padding(8.dp)) {
                Text("Joined Communities", style = MaterialTheme.typography.h6, modifier = Modifier.padding(8.dp))
                LazyColumn {
                    itemsIndexed(joinedCommunities) { _, community ->
                        CommunityItem(
                            community = community,
                            isMember = true,
                            onJoin = {},
                            // Update the leave handler
                            onLeave = {
                                if (!isProcessing) {
                                    isProcessing = true
                                    community.communityId?.let { commId ->
                                        CoroutineScope(Dispatchers.IO).launch {
                                            try {
                                                communityDAO.leaveCommunity(commId, userId)
                                                // No need to manually refresh - listeners will update automatically
                                            } finally {
                                                withContext(Dispatchers.Main) {
                                                    isProcessing = false
                                                }
                                            }
                                        }
                                    }
                                }
                            },
                            onDelete = {},
                            isCreator = false
                        )
                    }
                }
            }
        }

        Button(
            onClick = { showDialog = true },
            modifier = Modifier.align(Alignment.CenterHorizontally).padding(70.dp).fillMaxWidth()
        ) {
            Text("Create New Community")
        }
    }

    if (showDialog) {
        CreateCommunityDialog(
            onDismiss = { showDialog = false },
            onCreate = { name, description ->
                CoroutineScope(Dispatchers.IO).launch {
                    communityDAO.insertCommunity(Community(name = name, description = description, createdBy = userId)) {
                        CoroutineScope(Dispatchers.Main).launch {
                            refreshCommunityData {
                                showDialog = false
                            }
                        }
                    }
                }
            }
        )
    }
}

@Composable
private fun CommunityItem(
    community: Community,
    isMember: Boolean,
    onJoin: () -> Unit,
    onLeave: () -> Unit,
    onDelete: () -> Unit,
    isCreator: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = community.name,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.body1
        )

        if (isMember) {
            IconButton(onClick = onLeave) {
                Icon(Icons.Filled.Close, "Leave Community")
            }
        } else {
            Button(onClick = onJoin) {
                Text("Join")
            }
        }

        if (isCreator) {
            IconButton(onClick = onDelete) {
                Icon(Icons.Filled.Delete, "Delete Community")
            }
        }
    }
}

@Composable
private fun CreateCommunityDialog(
    onDismiss: () -> Unit,
    onCreate: (String, String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    val communityDAO = remember { CommunityDAO() }

    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .background(MaterialTheme.colors.background),
            verticalArrangement = Arrangement.Center
        ) {
            TextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Community Name") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            TextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (errorMessage.isNotEmpty()) {
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colors.error,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Button(
                    onClick = onDismiss,
                    modifier = Modifier.padding(end = 8.dp)
                ) {
                    Text("Cancel")
                }
                Button(
                    onClick = {
                        communityDAO.isCommunityNameUnique(name) { isUnique ->
                            if (isUnique) {
                                onCreate(name, description)
                                onDismiss()
                            } else {
                                errorMessage = "Community name already exists."
                            }
                        }
                    },
                    enabled = name.isNotBlank()
                ) {
                    Text("Create")
                }
            }
        }
    }
}