package com.example.a210135_nicolechengkee_drnazatul_project2

import android.Manifest
import android.annotation.SuppressLint
import android.location.Geocoder
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.google.android.gms.location.LocationServices
import java.util.Locale

@SuppressLint("MissingPermission")
@Composable
fun JobHomeScreen(navController: NavController, viewModel: JobViewModel) {

    val context         = LocalContext.current
    val currentRoute    = navController.currentBackStackEntryAsState().value?.destination?.route
    val profile         by viewModel.profileFlow.collectAsState()
    val featuredJobs    by viewModel.featuredJobsFlow.collectAsState()
    val recommendedJobs by viewModel.recommendedJobsFlow.collectAsState()
    val applications    by viewModel.applicationsFlow.collectAsState()
    val appliedIds      = remember(applications) { applications.map { it.jobId }.toSet() }

    var locationText by remember { mutableStateOf("Selangor, Malaysia") }

    val locationPermLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            val client = LocationServices.getFusedLocationProviderClient(context)
            client.lastLocation.addOnSuccessListener { loc ->
                if (loc != null) {
                    try {
                        val geo   = Geocoder(context, Locale.getDefault())
                        @Suppress("DEPRECATION")
                        val addrs = geo.getFromLocation(loc.latitude, loc.longitude, 1)
                        val city    = addrs?.firstOrNull()?.locality ?: ""
                        val state   = addrs?.firstOrNull()?.adminArea ?: ""
                        val country = addrs?.firstOrNull()?.countryName ?: ""

                        if (country.equals("Malaysia", ignoreCase = true)) {
                            val detected = listOf(city.ifBlank { state }, country)
                                .filter { it.isNotBlank() }
                                .joinToString(", ")
                            if (detected.isNotBlank()) {
                                locationText = detected
                            }
                        }


                    } catch (_: Exception) {
                    }
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        locationPermLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    Column(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        Column(
            Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Header Card
            Card(
                shape  = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Row(
                    Modifier.fillMaxWidth().padding(20.dp),
                    Arrangement.SpaceBetween,
                    Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            "Good Morning,",
                            color    = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f),
                            fontSize = 14.sp
                        )
                        Text(
                            profile.name.split(" ").firstOrNull() ?: "there",
                            fontWeight = FontWeight.Bold,
                            fontSize   = 24.sp,
                            color      = MaterialTheme.colorScheme.onPrimary
                        )
                        Spacer(Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.LocationOn, null,
                                tint     = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f),
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(Modifier.width(4.dp))
                            Text(
                                locationText,
                                color    = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f),
                                fontSize = 12.sp
                            )
                        }
                    }
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.2f))
                            .clickable { navController.navigate(Screen.Profile.route) },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Person, null,
                            tint     = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            // Search / Location
            CustomTextField(locationText, "Search by city or remote") { locationText = it }
            Spacer(Modifier.height(10.dp))
            Button(
                onClick  = { viewModel.searchRemoteJobs(locationText) },
                modifier = Modifier.fillMaxWidth(),
                shape    = RoundedCornerShape(14.dp)
            ) {
                Icon(Icons.Default.Search, null)
                Spacer(Modifier.width(8.dp))
                Text("Find Jobs in Malaysia")
            }

            if (viewModel.jobsError != null) {
                Spacer(Modifier.height(8.dp))
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer)
                ) {
                    Text(
                        viewModel.jobsError!!,
                        Modifier.padding(12.dp),
                        color    = MaterialTheme.colorScheme.onErrorContainer,
                        fontSize = 13.sp
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            // Featured Jobs
            Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                Text(
                    "Featured Jobs in Malaysia",
                    fontWeight = FontWeight.Bold,
                    fontSize   = 18.sp,
                    color      = MaterialTheme.colorScheme.primary
                )
                TextButton(onClick = { navController.navigate(Screen.Jobs.route) }) {
                    Text("See all →", fontSize = 13.sp)
                }
            }

            if (viewModel.isLoadingJobs) {
                Box(
                    Modifier.fillMaxWidth().padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(modifier = Modifier.size(32.dp))
                }
            } else {
                Row(Modifier.horizontalScroll(rememberScrollState())) {
                    featuredJobs.forEach { job ->
                        FeaturedJobCard(job.title, job.company, job.salary) {
                            navController.navigate(Screen.Detail.createRoute(job.jobId))
                        }
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            // Recommended Jobs
            Text(
                "Recommended Jobs in Selangor",
                fontWeight = FontWeight.Bold,
                fontSize   = 18.sp,
                color      = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.height(8.dp))

            recommendedJobs.forEach { job ->
                JobCard(
                    title            = job.title,
                    company          = job.company,
                    location         = job.location,
                    salary           = job.salary,
                    jobType          = job.jobType,
                    isBookmarked     = job.isBookmarked,
                    hasApplied       = job.jobId in appliedIds,
                    onBookmarkToggle = { viewModel.toggleBookmark(job.jobId, !job.isBookmarked) },
                    onClick          = { navController.navigate(Screen.Detail.createRoute(job.jobId)) }
                )
            }

            if (recommendedJobs.isEmpty() && !viewModel.isLoadingJobs) {
                EmptyState("Pull-to-refresh or check your connection")
            }
        }

        BottomNav(currentRoute, navController)
    }
}