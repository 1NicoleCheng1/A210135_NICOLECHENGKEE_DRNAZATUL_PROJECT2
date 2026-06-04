package com.example.a210135_nicolechengkee_drnazatul_project2

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import coil.compose.AsyncImage
import com.example.a210135_nicolechengkee_drnazatul_project2.data.UserProfileEntity
import java.io.File

@Composable
fun ProfileScreen(navController: NavController, viewModel: JobViewModel) {

    val context      = LocalContext.current
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
    val profile      by viewModel.profileFlow.collectAsState()
    val applications by viewModel.applicationsFlow.collectAsState()

    var isEditing by remember { mutableStateOf(false) }
    var editName  by remember(profile) { mutableStateOf(profile.name) }
    var editEmail by remember(profile) { mutableStateOf(profile.email) }
    var editPhone by remember(profile) { mutableStateOf(profile.phone) }
    var editSkills by remember(profile) { mutableStateOf(profile.skills) }

    // Camera for profile photo
    val photoFile = remember { File(context.cacheDir, "profile_photo.jpg") }
    val photoUri: Uri = remember {
        FileProvider.getUriForFile(context, "${context.packageName}.provider", photoFile)
    }
    val cameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) viewModel.updateProfilePhoto(photoUri.toString())
    }

    Column(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        Column(
            Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            //  Header
            Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                Text("My Profile", fontWeight = FontWeight.Bold, fontSize = 24.sp,
                    color = MaterialTheme.colorScheme.primary)
                TextButton(onClick = {
                    if (isEditing) {
                        viewModel.updateProfile(UserProfileEntity(
                            name   = editName,
                            email  = editEmail,
                            phone  = editPhone,
                            skills = editSkills,
                            photoUri = profile.photoUri
                        ))
                    }
                    isEditing = !isEditing
                }) {
                    Icon(if (isEditing) Icons.Default.Check else Icons.Default.Edit, null,
                        modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(4.dp))
                    Text(if (isEditing) "Save" else "Edit")
                }
            }

            Spacer(Modifier.height(20.dp))

            // Avatar + Name Card
            Card(
                shape  = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Row(
                    Modifier.fillMaxWidth().padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Avatar with camera tap
                    Box(
                        Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .clickable { cameraLauncher.launch(photoUri) }
                            .border(2.dp, MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.3f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        if (profile.photoUri != null) {
                            AsyncImage(
                                model             = profile.photoUri,
                                contentDescription = "Profile photo",
                                modifier           = Modifier.fillMaxSize().clip(CircleShape),
                                contentScale       = ContentScale.Crop
                            )
                        } else {
                            Box(
                                Modifier
                                    .fillMaxSize()
                                    .background(MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.CameraAlt, null,
                                    tint     = MaterialTheme.colorScheme.onPrimary,
                                    modifier = Modifier.size(32.dp))
                            }
                        }
                    }

                    Spacer(Modifier.width(16.dp))

                    if (isEditing) {
                        Column(Modifier.weight(1f)) {
                            OutlinedTextField(
                                value         = editName,
                                onValueChange = { editName = it },
                                label         = { Text("Name") },
                                singleLine    = true,
                                shape         = RoundedCornerShape(10.dp),
                                colors        = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor  = MaterialTheme.colorScheme.onPrimary,
                                    unfocusedTextColor = MaterialTheme.colorScheme.onPrimary.copy(0.9f),
                                    focusedLabelColor = MaterialTheme.colorScheme.onPrimary.copy(0.8f),
                                    unfocusedLabelColor = MaterialTheme.colorScheme.onPrimary.copy(0.7f),
                                    focusedBorderColor  = MaterialTheme.colorScheme.onPrimary,
                                    unfocusedBorderColor = MaterialTheme.colorScheme.onPrimary.copy(0.5f)
                                )
                            )
                        }
                    } else {
                        Column(Modifier.weight(1f)) {
                            Text(profile.name, fontWeight = FontWeight.Bold, fontSize = 20.sp,
                                color = MaterialTheme.colorScheme.onPrimary)
                            Text(profile.email, color = MaterialTheme.colorScheme.onPrimary.copy(0.8f),
                                fontSize = 14.sp)
                            if (profile.phone.isNotBlank())
                                Text(profile.phone, color = MaterialTheme.colorScheme.onPrimary.copy(0.7f),
                                    fontSize = 13.sp)
                        }
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            if (isEditing) {
                Text("Contact Details", fontWeight = FontWeight.Bold, fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.height(10.dp))
                FormSection("Email") {
                    OutlinedTextField(
                        value         = editEmail,
                        onValueChange = { editEmail = it },
                        modifier      = Modifier.fillMaxWidth(),
                        shape         = RoundedCornerShape(12.dp),
                        singleLine    = true
                    )
                }
                FormSection("Phone") {
                    OutlinedTextField(
                        value         = editPhone,
                        onValueChange = { editPhone = it },
                        modifier      = Modifier.fillMaxWidth(),
                        shape         = RoundedCornerShape(12.dp),
                        singleLine    = true
                    )
                }
                FormSection("Skills (comma-separated)") {
                    OutlinedTextField(
                        value         = editSkills,
                        onValueChange = { editSkills = it },
                        modifier      = Modifier.fillMaxWidth(),
                        shape         = RoundedCornerShape(12.dp),
                        singleLine    = true,
                        placeholder   = { Text("HTML, CSS, JavaScript") }
                    )
                }

                // Scanner shortcut
                OutlinedButton(
                    onClick  = { navController.navigate(Screen.Scanner.createRoute("profile")) },
                    modifier = Modifier.fillMaxWidth(),
                    shape    = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.DocumentScanner, null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Scan Resume to Auto-Fill")
                }

                Spacer(Modifier.height(8.dp))
            }

            // Skills
            if (!isEditing) {
                Text("Skills", fontWeight = FontWeight.Bold, fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.height(8.dp))
                Row(Modifier.horizontalScroll(rememberScrollState())) {
                    profile.skills.split(",").map { it.trim() }.filter { it.isNotBlank() }.forEach { skill ->
                        Box(
                            Modifier
                                .padding(end = 8.dp)
                                .background(MaterialTheme.colorScheme.primary.copy(0.1f), RoundedCornerShape(20.dp))
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Text(skill, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Medium)
                        }
                    }
                }

                Spacer(Modifier.height(20.dp))
            }

            // Application History
            Text("Application History (${applications.size})", fontWeight = FontWeight.Bold,
                fontSize = 18.sp, color = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.height(8.dp))

            if (applications.isEmpty()) {
                EmptyState("No applications yet. Start applying!", Icons.Default.WorkOff)
            } else {
                applications.forEach { app ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clickable { navController.navigate(Screen.Detail.createRoute(app.jobId)) },
                        shape    = RoundedCornerShape(14.dp),
                        colors   = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Work, null, tint = MaterialTheme.colorScheme.primary)
                            Spacer(Modifier.width(12.dp))
                            Column(Modifier.weight(1f)) {
                                Text(app.jobTitle, fontWeight = FontWeight.Bold)
                                Text(app.company, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp)
                                Text(
                                    java.text.SimpleDateFormat("dd MMM yyyy", java.util.Locale.getDefault())
                                        .format(java.util.Date(app.appliedAt)),
                                    fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            StatusBadge(app.status)
                        }
                    }
                }
            }

            Spacer(Modifier.height(8.dp))
        }

        BottomNav(currentRoute, navController)
    }
}
