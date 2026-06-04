package com.example.a210135_nicolechengkee_drnazatul_project2

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import kotlinx.coroutines.launch

@Composable
fun JobDetailScreen(navController: NavController, viewModel: JobViewModel, jobId: String) {

    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
    val scope        = rememberCoroutineScope()
    val snackbar     = remember { SnackbarHostState() }

    var job         by remember { mutableStateOf<com.example.a210135_nicolechengkee_drnazatul_project2.data.SavedJobEntity?>(null) }
    var hasApplied  by remember { mutableStateOf(false) }
    var bookmarked  by remember { mutableStateOf(false) }
    var sharedTip   by remember { mutableStateOf(false) }

    LaunchedEffect(jobId) {
        job        = viewModel.getJobById(jobId)
        hasApplied = viewModel.hasApplied(jobId)
        bookmarked = job?.isBookmarked ?: false
    }

    if (job == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    val j = job!!

    Scaffold(
        snackbarHost = { SnackbarHost(snackbar) },
        bottomBar    = { BottomNav(currentRoute, navController) }
    ) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            //Back
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier          = Modifier.clickable { navController.popBackStack() }
            ) {
                Icon(Icons.Default.ArrowBack, "Back", tint = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.width(6.dp))
                Text("Back", color = MaterialTheme.colorScheme.primary)
            }

            Spacer(Modifier.height(16.dp))

            //  Hero Card
            Card(
                shape  = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Column(Modifier.fillMaxWidth().padding(20.dp)) {
                    Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.Top) {
                        Box(
                            Modifier
                                .size(56.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Work, null,
                                tint     = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(32.dp))
                        }
                        // Bookmark icon
                        IconButton(onClick = {
                            bookmarked = !bookmarked
                            viewModel.toggleBookmark(j.jobId, bookmarked)
                            scope.launch {
                                snackbar.showSnackbar(if (bookmarked) "Job saved!" else "Bookmark removed")
                            }
                        }) {
                            Icon(
                                if (bookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                null,
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }
                    Spacer(Modifier.height(12.dp))
                    Text(j.title, fontWeight = FontWeight.Bold, fontSize = 22.sp,
                        color = MaterialTheme.colorScheme.onPrimary)
                    Text(j.company, color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f))
                    Spacer(Modifier.height(10.dp))
                    Row {
                        TagChipLight(j.location)
                        Spacer(Modifier.width(8.dp))
                        TagChipLight(j.jobType)
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            //  Info Cards
            Row(Modifier.fillMaxWidth(), Arrangement.spacedBy(12.dp)) {
                InfoCard("Salary", j.salary.ifBlank { "Competitive" }, Icons.Default.AttachMoney, Modifier.weight(1f))
                InfoCard("Type",   j.jobType,                          Icons.Default.Schedule,    Modifier.weight(1f))
            }

            Spacer(Modifier.height(20.dp))

            //  Description
            Text("Job Description", fontWeight = FontWeight.Bold, fontSize = 18.sp,
                color = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.height(8.dp))
            Card(
                shape  = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Text(
                    j.description.ifBlank {
                        "Great opportunity to grow your career in a fast-paced environment. " +
                        "You will work closely with a talented team to deliver high-quality solutions."
                    },
                    Modifier.padding(16.dp),
                    color      = MaterialTheme.colorScheme.onSurface,
                    lineHeight = 22.sp,
                    fontSize   = 14.sp
                )
            }

            Spacer(Modifier.height(20.dp))

            //  Requirements
            Text("Requirements", fontWeight = FontWeight.Bold, fontSize = 18.sp,
                color = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.height(8.dp))
            listOf(
                "Minimum Diploma / Degree in related field",
                "1–3 years of relevant experience",
                "Good communication skills",
                "Ability to work independently and in a team"
            ).forEach { req ->
                Row(Modifier.padding(vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CheckCircle, null, tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(req, color = MaterialTheme.colorScheme.onSurface, fontSize = 14.sp)
                }
            }

            Spacer(Modifier.height(20.dp))

            //  Share to Community
            if (!sharedTip) {
                OutlinedButton(
                    onClick  = {
                        viewModel.submitCommunityPost(
                            content  = "I found a great ${j.jobType} opportunity at ${j.company} for ${j.title}. Check it out!",
                            category = "General"
                        )
                        sharedTip = true
                        scope.launch { snackbar.showSnackbar("Shared to Community Board!") }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape    = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Groups, null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Share to Community Board")
                }
            } else {
                Card(
                    shape  = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF4CAF50).copy(alpha = 0.1f))
                ) {
                    Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Check, null, tint = Color(0xFF4CAF50))
                        Spacer(Modifier.width(8.dp))
                        Text("Shared to Community!", color = Color(0xFF4CAF50), fontSize = 13.sp)
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            // ── Apply / Applied ───────────
            if (hasApplied) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors   = CardDefaults.cardColors(containerColor = Color(0xFF4CAF50).copy(alpha = 0.15f)),
                    shape    = RoundedCornerShape(12.dp)
                ) {
                    Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CheckCircle, null, tint = Color(0xFF4CAF50))
                        Spacer(Modifier.width(8.dp))
                        Text("You've already applied!", color = Color(0xFF4CAF50), fontWeight = FontWeight.SemiBold)
                    }
                }
            } else {
                Button(
                    onClick  = { navController.navigate(Screen.Apply.createRoute(j.jobId)) },
                    modifier = Modifier.fillMaxWidth(),
                    shape    = RoundedCornerShape(12.dp)
                ) {
                    Text("Apply Now", fontSize = 16.sp, modifier = Modifier.padding(vertical = 4.dp))
                }
            }

            Spacer(Modifier.height(8.dp))
        }
    }
}
