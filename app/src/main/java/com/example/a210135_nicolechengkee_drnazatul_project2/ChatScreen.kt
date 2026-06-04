package com.example.a210135_nicolechengkee_drnazatul_project2

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.a210135_nicolechengkee_drnazatul_project2.data.ChatMessage
import com.example.a210135_nicolechengkee_drnazatul_project2.data.RecruiterChat

@Composable
fun ChatScreen(navController: NavController, viewModel: JobViewModel) {

    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
    var activeChat by remember { mutableStateOf<RecruiterChat?>(null) }

    val recruiters = remember {
        listOf(
            RecruiterChat("Sarah Lim",  "Hatricks Tech",         "Hi! We'd love to schedule an interview.",           "10:30 AM", 2, "UI/UX Designer"),
            RecruiterChat("Daniel Wong","Two95 International",   "Thanks for applying! Could you share your portfolio?","Yesterday",1, "Android Developer"),
            RecruiterChat("Amirah Zain","Astra Academy",         "We have a part-time slot this weekend. Are you free?","Mon",      0, "Math Tutor"),
            RecruiterChat("HR Team",    "D2D Mavericks",         "Your application is under review.",                  "Sun",      0, "Financial Maverick")
        )
    }

    Column(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        if (activeChat == null) {
            Column(Modifier.weight(1f).padding(16.dp)) {
                Text("Messages", fontWeight = FontWeight.Bold, fontSize = 26.sp,
                    color = MaterialTheme.colorScheme.primary)
                Text("${recruiters.sumOf { it.unreadCount }} unread",
                    color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 14.sp)
                Spacer(Modifier.height(16.dp))
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(recruiters) { r ->
                        RecruiterRow(r) { activeChat = r }
                    }
                }
            }
        } else {
            ChatConversation(activeChat!!) { activeChat = null }
        }
        BottomNav(currentRoute, navController)
    }
}

@Composable
fun RecruiterRow(recruiter: RecruiterChat, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        shape    = RoundedCornerShape(16.dp),
        colors   = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier.size(50.dp).clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary.copy(0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Text(recruiter.name.first().toString(), fontWeight = FontWeight.Bold,
                    fontSize = 20.sp, color = MaterialTheme.colorScheme.primary)
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                    Text(recruiter.name, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Text(recruiter.time, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Text(recruiter.company + " • " + recruiter.jobTitle,
                    fontSize = 12.sp, color = MaterialTheme.colorScheme.primary.copy(0.7f))
                Spacer(Modifier.height(2.dp))
                Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                    Text(recruiter.lastMessage, fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1,
                        modifier = Modifier.weight(1f))
                    if (recruiter.unreadCount > 0) {
                        Box(
                            Modifier.padding(start = 8.dp).size(20.dp)
                                .background(MaterialTheme.colorScheme.primary, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(recruiter.unreadCount.toString(), fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ChatConversation(recruiter: RecruiterChat, onBack: () -> Unit) {
    val listState = rememberLazyListState()
    var inputText by remember { mutableStateOf("") }
    val messages  = remember {
        mutableStateListOf(
            ChatMessage("Hi! I'm interested in the ${recruiter.jobTitle} role.", true, "10:00 AM"),
            ChatMessage(recruiter.lastMessage, false, recruiter.time)
        )
    }
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) listState.animateScrollToItem(messages.size - 1)
    }

    Column(Modifier.fillMaxSize()) {
        // Header
        Card(
            shape  = RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.ArrowBack, "Back",
                    tint     = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.clickable { onBack() })
                Spacer(Modifier.width(12.dp))
                Box(
                    Modifier.size(40.dp).clip(CircleShape)
                        .background(MaterialTheme.colorScheme.onPrimary.copy(0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(recruiter.name.first().toString(), fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary)
                }
                Spacer(Modifier.width(10.dp))
                Column(Modifier.weight(1f)) {
                    Text(recruiter.name, fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary)
                    Text(recruiter.company, fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onPrimary.copy(0.8f))
                }
                Icon(Icons.Default.MoreVert, null, tint = MaterialTheme.colorScheme.onPrimary)
            }
        }

        // Messages
        LazyColumn(
            state     = listState,
            modifier  = Modifier.weight(1f).padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(messages) { msg -> MessageBubble(msg) }
        }

        // Input
        Row(
            Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surface)
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value         = inputText,
                onValueChange = { inputText = it },
                placeholder   = { Text("Type a message…") },
                modifier      = Modifier.weight(1f),
                shape         = RoundedCornerShape(24.dp),
                singleLine    = true
            )
            Spacer(Modifier.width(8.dp))
            IconButton(
                onClick  = {
                    if (inputText.isNotBlank()) {
                        messages.add(ChatMessage(inputText.trim(), true, "Now"))
                        inputText = ""
                        messages.add(ChatMessage("Thanks for your message! We'll get back to you soon.", false, "Now"))
                    }
                },
                modifier = Modifier.size(48.dp).background(MaterialTheme.colorScheme.primary, CircleShape)
            ) {
                Icon(Icons.Default.Send, "Send", tint = MaterialTheme.colorScheme.onPrimary)
            }
        }
    }
}

@Composable
fun MessageBubble(message: ChatMessage) {
    Column(
        Modifier.fillMaxWidth(),
        horizontalAlignment = if (message.isFromUser) Alignment.End else Alignment.Start
    ) {
        Box(
            Modifier
                .widthIn(max = 280.dp)
                .background(
                    if (message.isFromUser) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.surface,
                    RoundedCornerShape(
                        topStart    = 16.dp, topEnd     = 16.dp,
                        bottomStart = if (message.isFromUser) 16.dp else 4.dp,
                        bottomEnd   = if (message.isFromUser) 4.dp  else 16.dp
                    )
                )
                .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            Text(
                message.text,
                color    = if (message.isFromUser) MaterialTheme.colorScheme.onPrimary
                           else MaterialTheme.colorScheme.onSurface,
                fontSize = 14.sp
            )
        }
        Spacer(Modifier.height(2.dp))
        Text(message.timestamp, fontSize = 11.sp,
            color    = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 4.dp))
    }
}
