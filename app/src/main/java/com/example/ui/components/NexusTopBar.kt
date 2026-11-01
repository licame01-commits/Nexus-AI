package com.example.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ChatGptBackground
import com.example.ui.theme.ChatGptTextPrimary
import com.example.ui.theme.ChatGptTextSecondary

@Composable
fun NexusTopBar(
    recentChatsCount: Int,
    onOpenDrawer: () -> Unit,
    onNewChat: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .testTag("nexus_top_bar"),
        color = ChatGptBackground,
        tonalElevation = 0.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Left Drawer Trigger (Chats Recientes)
            IconButton(
                onClick = onOpenDrawer,
                modifier = Modifier.testTag("open_recent_chats_drawer_button")
            ) {
                BadgedBox(
                    badge = {
                        if (recentChatsCount > 0) {
                            Badge(
                                containerColor = ChatGptTextSecondary,
                                contentColor = ChatGptBackground
                            ) {
                                Text("$recentChatsCount")
                            }
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = "Abrir chats recientes",
                        tint = ChatGptTextPrimary
                    )
                }
            }

            // Center: ChatGPT style "Nexus 3.5" title
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(horizontal = 8.dp, vertical = 4.dp)
                    .clickable { onOpenDrawer() }
            ) {
                Text(
                    text = "Nexus",
                    style = androidx.compose.material3.MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.3.sp
                    ),
                    color = ChatGptTextPrimary
                )
                Spacer(modifier = Modifier.width(4.dp))
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = ChatGptTextSecondary.copy(alpha = 0.2f)
                ) {
                    Text(
                        text = "3.5",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = ChatGptTextSecondary,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
                Spacer(modifier = Modifier.width(2.dp))
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = ChatGptTextSecondary,
                    modifier = Modifier.size(16.dp)
                )
            }

            // Right Button: ChatGPT style "+ Nuevo Chat" (EditNote icon)
            IconButton(
                onClick = onNewChat,
                modifier = Modifier.testTag("top_bar_new_chat_button")
            ) {
                Icon(
                    imageVector = Icons.Default.EditNote,
                    contentDescription = "Nuevo Chat",
                    tint = ChatGptTextPrimary,
                    modifier = Modifier.size(26.dp)
                )
            }
        }
    }
}
