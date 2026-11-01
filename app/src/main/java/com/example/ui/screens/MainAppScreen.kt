package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.NexusViewModel
import com.example.ui.components.NexusTopBar
import com.example.ui.components.RecentChatsDrawerContent
import com.example.ui.components.SubjectAndLevelBar
import kotlinx.coroutines.launch

@Composable
fun MainAppScreen(
    viewModel: NexusViewModel,
    modifier: Modifier = Modifier
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val currentSession by viewModel.currentSession.collectAsStateWithLifecycle()
    val messages by viewModel.currentMessages.collectAsStateWithLifecycle()
    val streamState by viewModel.streamState.collectAsStateWithLifecycle()
    val allSessions by viewModel.allSessions.collectAsStateWithLifecycle()
    val filteredSessions by viewModel.filteredSessions.collectAsStateWithLifecycle()
    val currentSessionId by viewModel.currentSessionId.collectAsStateWithLifecycle()
    val recentChatsQuery by viewModel.recentChatsQuery.collectAsStateWithLifecycle()
    val educationLevel by viewModel.educationLevel.collectAsStateWithLifecycle()
    val selectedSubject by viewModel.selectedSubject.collectAsStateWithLifecycle()

    // Handle back button when drawer is open
    BackHandler(enabled = drawerState.isOpen) {
        coroutineScope.launch { drawerState.close() }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(drawerContainerColor = com.example.ui.theme.ChatGptDrawerBg) {
                RecentChatsDrawerContent(
                    sessions = filteredSessions,
                    activeSessionId = currentSessionId,
                    searchQuery = recentChatsQuery,
                    onSearchChanged = { viewModel.setRecentChatsQuery(it) },
                    onSelectSession = { sessionId ->
                        viewModel.selectSession(sessionId)
                        coroutineScope.launch { drawerState.close() }
                    },
                    onNewChat = {
                        viewModel.startNewChat()
                        coroutineScope.launch { drawerState.close() }
                    },
                    onDeleteSession = { sessionId ->
                        viewModel.deleteSession(sessionId)
                    },
                    onClearAll = {
                        viewModel.clearAllSessions()
                        coroutineScope.launch {
                            drawerState.close()
                            snackbarHostState.showSnackbar("Historial de chats vaciado.")
                        }
                    }
                )
            }
        }
    ) {
        Scaffold(
            modifier = modifier.fillMaxSize().testTag("main_app_scaffold"),
            containerColor = MaterialTheme.colorScheme.background,
            topBar = {
                Column {
                    NexusTopBar(
                        recentChatsCount = allSessions.size,
                        onOpenDrawer = {
                            coroutineScope.launch { drawerState.open() }
                        },
                        onNewChat = {
                            viewModel.startNewChat()
                        }
                    )
                    SubjectAndLevelBar(
                        selectedLevel = educationLevel,
                        onLevelSelected = { viewModel.setEducationLevel(it) },
                        selectedSubject = selectedSubject,
                        onSubjectSelected = { viewModel.setSelectedSubject(it) }
                    )
                }
            },
            snackbarHost = { SnackbarHost(snackbarHostState) }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                // Automatically loads directly into Chat Screen!
                NexusChatScreen(
                    currentSession = currentSession,
                    messages = messages,
                    streamState = streamState,
                    selectedSubject = selectedSubject,
                    educationLevel = educationLevel,
                    onSendMessage = { messageText, imageUri ->
                        viewModel.sendUserMessage(messageText, imageUri)
                    }
                )
            }
        }
    }
}
