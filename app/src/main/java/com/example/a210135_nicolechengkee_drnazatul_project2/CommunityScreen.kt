package com.example.a210135_nicolechengkee_drnazatul_project2

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.a210135_nicolechengkee_drnazatul_project2.data.CommunityPost
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun CommunityScreen(navController: NavController, viewModel: JobViewModel) {

    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
    val posts        by viewModel.communityPostsFlow.collectAsState()
    val profile      by viewModel.profileFlow.collectAsState()

    val categories   = listOf("All", "Interview", "CV", "Remote", "General")
    var selectedCat  by remember { mutableStateOf("All") }
    var showDialog   by remember { mutableStateOf(false) }

    val filteredPosts = remember(posts, selectedCat) {
        if (selectedCat == "All") posts
        else posts.filter { it.category == selectedCat }
    }

    // New-post dialog
    if (showDialog) {
        NewPostDialog(
            onDismiss = { showDialog = false },
            onSubmit  = { content, category ->
                viewModel.submitCommunityPost(content, category)
                showDialog = false
            }
        )
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick            = { showDialog = true },
                containerColor     = MaterialTheme.colorScheme.primary,
                contentColor       = MaterialTheme.colorScheme.onPrimary,
                shape              = CircleShape
            ) {
                Icon(Icons.Default.Edit, "New post")
            }
        },
        bottomBar = { BottomNav(currentRoute, navController) }
    ) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(padding)
        ) {
            // Header
            Column(Modifier.padding(horizontal = 16.dp, vertical = 16.dp)) {
                Text("Community Board", fontWeight = FontWeight.Bold, fontSize = 26.sp,
                    color = MaterialTheme.colorScheme.primary)
                Text("Share tips, celebrate wins, support fellow job seekers",
                    color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 14.sp)

                Spacer(Modifier.height(4.dp))

                // SDG badge
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        Modifier
                            .background(MaterialTheme.colorScheme.secondaryContainer, RoundedCornerShape(20.dp))
                            .padding(horizontal = 12.dp, vertical = 5.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Public, null,
                                tint     = MaterialTheme.colorScheme.onSecondaryContainer,
                                modifier = Modifier.size(14.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("SDG 1 Community", fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                                fontWeight = FontWeight.Medium)
                        }
                    }
                    Spacer(Modifier.width(8.dp))
                    Text("${posts.size} posts", fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }

                Spacer(Modifier.height(12.dp))

                // Category filter
                Row(Modifier.horizontalScroll(rememberScrollState())) {
                    categories.forEach { cat ->
                        CategoryChip(cat, cat == selectedCat) { selectedCat = cat }
                    }
                }
            }

            // Post List
            if (posts.isEmpty()) {
                Box(Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Forum, null,
                            tint     = MaterialTheme.colorScheme.onSurfaceVariant.copy(0.4f),
                            modifier = Modifier.size(64.dp))
                        Spacer(Modifier.height(12.dp))
                        Text("No posts yet. Be the first to share!",
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                        if (viewModel.isPostingCommunity) {
                            Spacer(Modifier.height(12.dp))
                            CircularProgressIndicator(modifier = Modifier.size(24.dp))
                        }
                    }
                }
            } else {
                LazyColumn(
                    state             = rememberLazyListState(),
                    modifier          = Modifier.weight(1f),
                    contentPadding    = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(filteredPosts, key = { it.docId }) { post ->
                        PostCard(post = post, onLike = { viewModel.likePost(post.docId) })
                    }
                    item { Spacer(Modifier.height(80.dp)) } // FAB clearance
                }
            }
        }
    }
}

//  Post Card
@Composable
fun PostCard(post: CommunityPost, onLike: () -> Unit) {

    val categoryColors = mapOf(
        "Interview" to (Color(0xFFE8F5E9) to Color(0xFF2E7D32)),
        "CV"        to (Color(0xFFE3F2FD) to Color(0xFF1565C0)),
        "Remote"    to (Color(0xFFFFF3E0) to Color(0xFFE65100)),
        "General"   to (Color(0xFFF3E5F5) to Color(0xFF6A1B9A))
    )
    val (bg, fg) = categoryColors[post.category] ?: (Color(0xFFF5F5F5) to Color(0xFF616161))

    Card(
        shape  = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            // Author row
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary.copy(0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        post.authorName.firstOrNull()?.toString() ?: "?",
                        fontWeight = FontWeight.Bold,
                        color      = MaterialTheme.colorScheme.primary
                    )
                }
                Spacer(Modifier.width(10.dp))
                Column(Modifier.weight(1f)) {
                    Text(post.authorName.ifBlank { "Anonymous" },
                        fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                    Text(formatTimestamp(post.timestamp),
                        fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                // Category badge
                Box(
                    Modifier
                        .background(bg, RoundedCornerShape(10.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(post.category, fontSize = 11.sp, color = fg, fontWeight = FontWeight.SemiBold)
                }
            }

            Spacer(Modifier.height(12.dp))

            // Content
            Text(post.content, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface,
                lineHeight = 21.sp)

            Spacer(Modifier.height(12.dp))

            // Like row
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onLike, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Default.ThumbUp, null,
                        tint     = MaterialTheme.colorScheme.primary.copy(0.7f),
                        modifier = Modifier.size(18.dp))
                }
                Spacer(Modifier.width(4.dp))
                Text("${post.likes}", fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

//  New Post Dialog

@Composable
fun NewPostDialog(onDismiss: () -> Unit, onSubmit: (String, String) -> Unit) {

    val categories = listOf("General", "Interview", "CV", "Remote")
    var content    by remember { mutableStateOf("") }
    var category   by remember { mutableStateOf("General") }

    AlertDialog(
        onDismissRequest = onDismiss,
        shape            = RoundedCornerShape(24.dp),
        title = {
            Text("Share with the Community", fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary)
        },
        text = {
            Column {
                OutlinedTextField(
                    value         = content,
                    onValueChange = { content = it },
                    placeholder   = { Text("Share a tip, ask for advice, or celebrate a win…") },
                    modifier      = Modifier.fillMaxWidth().height(140.dp),
                    shape         = RoundedCornerShape(14.dp)
                )
                Spacer(Modifier.height(12.dp))
                Text("Category", fontWeight = FontWeight.Medium, fontSize = 14.sp)
                Spacer(Modifier.height(8.dp))
                Row(Modifier.horizontalScroll(rememberScrollState())) {
                    categories.forEach { cat ->
                        CategoryChip(cat, cat == category) { category = cat }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick  = { if (content.isNotBlank()) onSubmit(content, category) },
                enabled  = content.isNotBlank(),
                shape    = RoundedCornerShape(12.dp)
            ) { Text("Post") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

//  Timestamp formatter

private fun formatTimestamp(timestamp: Timestamp?): String {
    if (timestamp == null) return "Just now"
    return try {
        val date = timestamp.toDate()
        val now  = Date()
        val diff = now.time - date.time
        when {
            diff < 60_000L              -> "Just now"
            diff < 3_600_000L           -> "${diff / 60_000}m ago"
            diff < 86_400_000L          -> "${diff / 3_600_000}h ago"
            diff < 604_800_000L         -> "${diff / 86_400_000}d ago"
            else -> SimpleDateFormat("d MMM yyyy", Locale.getDefault()).format(date)
        }
    } catch (_: Exception) { "Just now" }
}
