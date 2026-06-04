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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.launch

@Composable
fun ApplyScreen(navController: NavController, viewModel: JobViewModel, jobId: String) {

    val scope      = rememberCoroutineScope()
    val profile    by viewModel.profileFlow.collectAsState()

    var job by remember { mutableStateOf<com.example.a210135_nicolechengkee_drnazatul_project2.data.SavedJobEntity?>(null) }

    LaunchedEffect(jobId) { job = viewModel.getJobById(jobId) }

    if (job == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
        return
    }

    val j = job!!

    // Auto-fill from Room profile
    var fullName    by remember(profile) { mutableStateOf(profile.name) }
    var email       by remember(profile) { mutableStateOf(profile.email) }
    var phone       by remember(profile) { mutableStateOf(profile.phone) }
    var coverLetter by remember { mutableStateOf("") }
    var submitted   by remember { mutableStateOf(false) }

    Column(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        Column(
            Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Back
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier          = Modifier.clickable { navController.popBackStack() }
            ) {
                Icon(Icons.Default.ArrowBack, "Back", tint = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.width(8.dp))
                Text("Back", color = MaterialTheme.colorScheme.primary)
            }

            Spacer(Modifier.height(16.dp))

            Text("Apply for Position", fontWeight = FontWeight.Bold, fontSize = 22.sp,
                color = MaterialTheme.colorScheme.primary)

            Spacer(Modifier.height(12.dp))

            //  Job Summary
            Card(
                shape  = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Work, null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(Modifier.width(10.dp))
                    Column {
                        Text(j.title, fontWeight = FontWeight.Bold)
                        Text(j.company, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp)
                        Text(j.location, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            if (submitted) {
                // ── Success State ──────────
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape    = RoundedCornerShape(20.dp),
                    colors   = CardDefaults.cardColors(containerColor = Color(0xFF4CAF50).copy(alpha = 0.1f))
                ) {
                    Column(
                        Modifier.padding(32.dp).fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.CheckCircle, null, tint = Color(0xFF4CAF50),
                            modifier = Modifier.size(64.dp))
                        Spacer(Modifier.height(16.dp))
                        Text("Application Submitted!", fontWeight = FontWeight.Bold, fontSize = 20.sp,
                            color = Color(0xFF4CAF50))
                        Spacer(Modifier.height(8.dp))
                        Text("We'll reach out to you at $email",
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(Modifier.height(8.dp))
                        Text("Saved to your application history",
                            fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(Modifier.height(24.dp))
                        Button(onClick = {
                            navController.navigate(Screen.Home.route) {
                                popUpTo(Screen.Home.route) { inclusive = true }
                            }
                        }) { Text("Back to Home") }
                    }
                }
            } else {
                //  Scanner Shortcut
                OutlinedButton(
                    onClick  = { navController.navigate(Screen.Scanner.createRoute("apply")) },
                    modifier = Modifier.fillMaxWidth(),
                    shape    = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.DocumentScanner, null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Scan Resume to Auto-Fill")
                }

                Spacer(Modifier.height(16.dp))

                //Form
                FormSection("Full Name") {
                    OutlinedTextField(
                        value         = fullName,
                        onValueChange = { fullName = it },
                        placeholder   = { Text("e.g. Nicole Cheng") },
                        modifier      = Modifier.fillMaxWidth(),
                        singleLine    = true,
                        shape         = RoundedCornerShape(12.dp)
                    )
                }
                FormSection("Email Address") {
                    OutlinedTextField(
                        value         = email,
                        onValueChange = { email = it },
                        placeholder   = { Text("e.g. nicole@gmail.com") },
                        modifier      = Modifier.fillMaxWidth(),
                        singleLine    = true,
                        shape         = RoundedCornerShape(12.dp)
                    )
                }
                FormSection("Phone Number") {
                    OutlinedTextField(
                        value         = phone,
                        onValueChange = { phone = it },
                        placeholder   = { Text("e.g. 01118738758") },
                        modifier      = Modifier.fillMaxWidth(),
                        singleLine    = true,
                        shape         = RoundedCornerShape(12.dp)
                    )
                }
                FormSection("Cover Letter") {
                    OutlinedTextField(
                        value         = coverLetter,
                        onValueChange = { coverLetter = it },
                        placeholder   = { Text("Tell us why you're a great fit…") },
                        modifier      = Modifier.fillMaxWidth().height(140.dp),
                        shape         = RoundedCornerShape(12.dp)
                    )
                }

                if (profile.name.isNotBlank()) {
                    Card(
                        shape  = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer)
                    ) {
                        Row(Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.AutoAwesome, null,
                                tint     = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("Auto-filled from your saved profile",
                                fontSize = 12.sp,
                                color    = MaterialTheme.colorScheme.onPrimaryContainer)
                        }
                    }
                    Spacer(Modifier.height(12.dp))
                }

                Button(
                    onClick  = {
                        if (fullName.isNotBlank() && email.isNotBlank()) {
                            viewModel.applyToJob(j.jobId, j.title, j.company, coverLetter)
                            submitted = true
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape    = RoundedCornerShape(12.dp),
                    enabled  = fullName.isNotBlank() && email.isNotBlank()
                ) {
                    Icon(Icons.Default.Send, null)
                    Spacer(Modifier.width(8.dp))
                    Text("Submit Application", fontSize = 16.sp,
                        modifier = Modifier.padding(vertical = 4.dp))
                }
            }
        }
    }
}
