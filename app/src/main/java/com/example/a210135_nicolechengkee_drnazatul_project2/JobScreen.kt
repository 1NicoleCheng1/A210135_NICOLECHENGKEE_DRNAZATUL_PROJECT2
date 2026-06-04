package com.example.a210135_nicolechengkee_drnazatul_project2

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun JobScreen(navController: NavController, viewModel: JobViewModel) {

    val currentRoute  = navController.currentBackStackEntryAsState().value?.destination?.route
    val allJobs       by viewModel.allJobsFlow.collectAsState()
    val applications  by viewModel.applicationsFlow.collectAsState()
    val appliedIds    = remember(applications) { applications.map { it.jobId }.toSet() }

    val filterTabs    = listOf("All", "Remote", "Hybrid", "Full Time", "Part Time", "Contract")
    var selectedFilter by remember { mutableStateOf("All") }
    var searchQuery   by remember { mutableStateOf("") }
    var showBookmarks by remember { mutableStateOf(false) }

    val displayedJobs = remember(allJobs, selectedFilter, searchQuery, showBookmarks) {
        allJobs.filter { job ->
            val matchFilter   = selectedFilter == "All" || job.jobType == selectedFilter
            val matchSearch   = searchQuery.isBlank() ||
                    job.title.contains(searchQuery, ignoreCase = true) ||
                    job.company.contains(searchQuery, ignoreCase = true) ||
                    job.location.contains(searchQuery, ignoreCase = true)
            val matchBookmark = !showBookmarks || job.isBookmarked
            matchFilter && matchSearch && matchBookmark
        }
    }

    Column(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        Column(
            Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Header
            Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                Column {
                    Text("Browse Jobs", fontWeight = FontWeight.Bold, fontSize = 26.sp,
                        color = MaterialTheme.colorScheme.primary)
                    Text("${allJobs.size} opportunities available",
                        color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 14.sp)
                }
                IconButton(onClick = { showBookmarks = !showBookmarks }) {
                    Icon(
                        if (showBookmarks) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                        contentDescription = "Saved",
                        tint = if (showBookmarks) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            // Search Bar
            OutlinedTextField(
                value          = searchQuery,
                onValueChange  = { searchQuery = it },
                placeholder    = { Text("Search title, company, location…") },
                leadingIcon    = { Icon(Icons.Default.Search, null) },
                trailingIcon   = {
                    if (searchQuery.isNotEmpty()) {
                        Icon(Icons.Default.Close, null,
                            modifier = Modifier.clickable { searchQuery = "" })
                    }
                },
                modifier       = Modifier.fillMaxWidth(),
                shape          = RoundedCornerShape(16.dp),
                singleLine     = true
            )

            Spacer(Modifier.height(6.dp))

            // API Search Button
            if (searchQuery.isNotBlank()) {
                TextButton(onClick = { viewModel.searchRemoteJobs(searchQuery) }) {
                    Icon(Icons.Default.CloudDownload, null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Search online for \"$searchQuery\"", fontSize = 13.sp)
                }
            }

            Spacer(Modifier.height(8.dp))

            //  Filter Chips
            Row(Modifier.horizontalScroll(rememberScrollState())) {
                filterTabs.forEach { tab ->
                    CategoryChip(
                        text     = tab,
                        selected = tab == selectedFilter,
                        onClick  = { selectedFilter = tab }
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            val suffix      = if (displayedJobs.size != 1) "s" else ""
            val filterLabel = if (selectedFilter != "All") " — $selectedFilter" else ""
            Text("${displayedJobs.size} result$suffix$filterLabel",
                fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

            Spacer(Modifier.height(8.dp))

            //  Loading / List
            if (viewModel.isLoadingJobs) {
                LoadingCard()
            } else if (displayedJobs.isEmpty()) {
                EmptyState(
                    if (showBookmarks) "No saved jobs yet. Bookmark jobs to see them here."
                    else "No jobs found. Try a different filter or search online."
                )
            } else {
                displayedJobs.forEach { job ->
                    JobCard(
                        title    = job.title,
                        company  = job.company,
                        location = job.location,
                        salary   = job.salary,
                        jobType  = job.jobType,
                        isBookmarked = job.isBookmarked,
                        hasApplied   = job.jobId in appliedIds,
                        onBookmarkToggle = { viewModel.toggleBookmark(job.jobId, !job.isBookmarked) },
                        onClick  = { navController.navigate(Screen.Detail.createRoute(job.jobId)) }
                    )
                }
            }

            Spacer(Modifier.height(8.dp))
        }

        BottomNav(currentRoute, navController)
    }
}
