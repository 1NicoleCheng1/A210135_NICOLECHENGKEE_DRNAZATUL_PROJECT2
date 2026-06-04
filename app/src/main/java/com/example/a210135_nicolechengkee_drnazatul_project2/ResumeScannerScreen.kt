package com.example.a210135_nicolechengkee_drnazatul_project2

import android.Manifest
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import java.io.File

@Composable
fun ResumeScannerScreen(
    navController: NavController,
    viewModel: JobViewModel,
    source: String = "apply"
) {
    val context = LocalContext.current

    //  State
    var cameraPermissionGranted by remember { mutableStateOf(false) }
    var capturedUri             by remember { mutableStateOf<Uri?>(null) }
    var isProcessing            by remember { mutableStateOf(false) }
    var extractedName           by remember { mutableStateOf("") }
    var extractedEmail          by remember { mutableStateOf("") }
    var rawText                 by remember { mutableStateOf("") }
    var scanDone                by remember { mutableStateOf(false) }
    var errorMsg                by remember { mutableStateOf<String?>(null) }

    //  File for camera output
    val photoFile = remember { File(context.cacheDir, "resume_scan_${System.currentTimeMillis()}.jpg") }
    val photoUri: Uri = remember {
        FileProvider.getUriForFile(context, "${context.packageName}.provider", photoFile)
    }

    // Permission launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted -> cameraPermissionGranted = granted }

    //  Camera launcher
    val cameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            capturedUri  = photoUri
            isProcessing = true
            errorMsg     = null

            //  ML Kit OCR
            try {
                val image      = InputImage.fromFilePath(context, photoUri)
                val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

                recognizer.process(image)
                    .addOnSuccessListener { visionText ->
                        rawText = visionText.text

                        // Extract email via regex
                        val emailRegex = Regex("[a-zA-Z0-9._%+\\-]+@[a-zA-Z0-9.\\-]+\\.[a-zA-Z]{2,}")
                        extractedEmail = emailRegex.find(rawText)?.value ?: ""

                        // Extract name heuristic:
                        // First non-empty line that is NOT an email, URL, or phone number
                        val phoneRegex = Regex("(\\+?6?0[\\s\\-]?[0-9]{8,10})|([0-9]{3}[\\s.\\-][0-9]{3,4}[\\s.\\-][0-9]{4})")
                        val urlRegex   = Regex("https?://\\S+|www\\.\\S+")
                        extractedName  = rawText.lines()
                            .map { it.trim() }
                            .firstOrNull { line ->
                                line.length in 3..50
                                        && !emailRegex.containsMatchIn(line)
                                        && !phoneRegex.containsMatchIn(line)
                                        && !urlRegex.containsMatchIn(line)
                                        && line.none { it.isDigit() }
                            } ?: ""

                        isProcessing = false
                        scanDone     = true
                    }
                    .addOnFailureListener { e ->
                        errorMsg     = "OCR failed: ${e.localizedMessage}"
                        isProcessing = false
                    }
            } catch (e: Exception) {
                errorMsg     = "Could not read image: ${e.localizedMessage}"
                isProcessing = false
            }
        }
    }

    // Request camera permission on first composition
    LaunchedEffect(Unit) {
        permissionLauncher.launch(Manifest.permission.CAMERA)
    }

    //UI
    Column(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Back
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier          = Modifier
                .padding(bottom = 16.dp)
                .let { mod ->
                    mod.then(
                        Modifier.clickable(
                            interactionSource = null,
                            indication        = null
                        ) { navController.popBackStack() }
                    )
                }
        ) {
            Icon(Icons.Default.ArrowBack, "Back", tint = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.width(8.dp))
            Text("Back", color = MaterialTheme.colorScheme.primary)
        }

        // Title
        Text("Resume Scanner", fontWeight = FontWeight.Bold, fontSize = 24.sp,
            color = MaterialTheme.colorScheme.primary)
        Text(
            "Point your camera at your printed resume or LinkedIn QR. " +
            "We'll extract your name and email to auto-fill your profile.",
            fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 4.dp, bottom = 20.dp), lineHeight = 20.sp
        )

        // Preview box / placeholder
        Box(
            Modifier
                .fillMaxWidth()
                .height(240.dp)
                .clip(RoundedCornerShape(20.dp))
                .border(2.dp, MaterialTheme.colorScheme.primary.copy(0.3f), RoundedCornerShape(20.dp))
                .background(MaterialTheme.colorScheme.surface),
            contentAlignment = Alignment.Center
        ) {
            if (capturedUri != null) {
                AsyncImage(
                    model              = capturedUri,
                    contentDescription = "Captured resume",
                    modifier           = Modifier.fillMaxSize().clip(RoundedCornerShape(20.dp)),
                    contentScale       = ContentScale.Crop
                )
                if (isProcessing) {
                    Box(
                        Modifier.fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.45f), RoundedCornerShape(20.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(color = Color.White)
                            Spacer(Modifier.height(10.dp))
                            Text("Reading text…", color = Color.White, fontSize = 14.sp)
                        }
                    }
                }
            } else {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.DocumentScanner, null,
                        tint     = MaterialTheme.colorScheme.primary.copy(0.4f),
                        modifier = Modifier.size(64.dp))
                    Spacer(Modifier.height(8.dp))
                    Text("No photo yet", color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 14.sp)
                }
            }
        }

        Spacer(Modifier.height(20.dp))

        //Scan Button
        Button(
            onClick  = {
                if (cameraPermissionGranted) {
                    scanDone  = false
                    errorMsg  = null
                    cameraLauncher.launch(photoUri)
                } else {
                    permissionLauncher.launch(Manifest.permission.CAMERA)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            shape    = RoundedCornerShape(14.dp),
            enabled  = !isProcessing
        ) {
            Icon(Icons.Default.CameraAlt, null)
            Spacer(Modifier.width(8.dp))
            Text(if (capturedUri == null) "Open Camera" else "Scan Again", fontSize = 16.sp,
                modifier = Modifier.padding(vertical = 4.dp))
        }

        // Error
        if (errorMsg != null) {
            Spacer(Modifier.height(12.dp))
            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)) {
                Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Error, null,
                        tint = MaterialTheme.colorScheme.onErrorContainer, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(errorMsg!!, color = MaterialTheme.colorScheme.onErrorContainer, fontSize = 13.sp)
                }
            }
        }

        // Extracted Results
        if (scanDone) {
            Spacer(Modifier.height(24.dp))

            Card(
                shape  = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.AutoAwesome, null,
                            tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Extracted Information", fontWeight = FontWeight.Bold, fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.primary)
                    }

                    Spacer(Modifier.height(16.dp))

                    // Editable name field
                    FormSection("Detected Name") {
                        OutlinedTextField(
                            value         = extractedName,
                            onValueChange = { extractedName = it },
                            modifier      = Modifier.fillMaxWidth(),
                            shape         = RoundedCornerShape(12.dp),
                            singleLine    = true,
                            placeholder   = { Text("Not detected — type manually") },
                            leadingIcon   = { Icon(Icons.Default.Person, null) }
                        )
                    }

                    // Editable email field
                    FormSection("Detected Email") {
                        OutlinedTextField(
                            value         = extractedEmail,
                            onValueChange = { extractedEmail = it },
                            modifier      = Modifier.fillMaxWidth(),
                            shape         = RoundedCornerShape(12.dp),
                            singleLine    = true,
                            placeholder   = { Text("Not detected — type manually") },
                            leadingIcon   = { Icon(Icons.Default.Email, null) }
                        )
                    }

                    // Raw text preview (collapsed)
                    if (rawText.isNotBlank()) {
                        var showRaw by remember { mutableStateOf(false) }
                        TextButton(onClick = { showRaw = !showRaw }) {
                            Icon(if (showRaw) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(4.dp))
                            Text(if (showRaw) "Hide raw text" else "Show raw OCR text", fontSize = 13.sp)
                        }
                        if (showRaw) {
                            Box(
                                Modifier
                                    .fillMaxWidth()
                                    .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(10.dp))
                                    .padding(12.dp)
                            ) {
                                Text(rawText.take(600) + if (rawText.length > 600) "…" else "",
                                    fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    lineHeight = 17.sp)
                            }
                        }
                    }

                    Spacer(Modifier.height(16.dp))

                    //Save & Return
                    Button(
                        onClick  = {
                            viewModel.updateProfileFromScan(extractedName, extractedEmail)
                            navController.popBackStack()
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape    = RoundedCornerShape(12.dp),
                        enabled  = extractedName.isNotBlank() || extractedEmail.isNotBlank()
                    ) {
                        Icon(Icons.Default.Save, null)
                        Spacer(Modifier.width(8.dp))
                        Text("Save & Auto-Fill", fontSize = 16.sp,
                            modifier = Modifier.padding(vertical = 4.dp))
                    }

                    Spacer(Modifier.height(8.dp))

                    OutlinedButton(
                        onClick  = { navController.popBackStack() },
                        modifier = Modifier.fillMaxWidth(),
                        shape    = RoundedCornerShape(12.dp)
                    ) {
                        Text("Skip")
                    }
                }
            }
        }

        //Tips
        if (!scanDone) {
            Spacer(Modifier.height(24.dp))
            Card(
                shape  = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text("Tips for best results", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                    Spacer(Modifier.height(8.dp))
                    listOf(
                        "Use good lighting — avoid shadows over text",
                        "Keep the camera steady and close to the page",
                        "Ensure your name and email are clearly visible",
                        "Works with printed CVs, LinkedIn QR cards, and certificates"
                    ).forEach { tip ->
                        Row(Modifier.padding(vertical = 3.dp), verticalAlignment = Alignment.Top) {
                            Icon(Icons.Default.TipsAndUpdates, null,
                                tint     = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(15.dp).padding(top = 2.dp))
                            Spacer(Modifier.width(8.dp))
                            Text(tip, fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant, lineHeight = 19.sp)
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(24.dp))
    }
}

private fun Modifier.clickable(
    interactionSource: androidx.compose.foundation.interaction.MutableInteractionSource?,
    indication: androidx.compose.foundation.Indication?,
    onClick: () -> Unit
): Modifier = this.clickable(
    interactionSource = interactionSource
        ?: androidx.compose.foundation.interaction.MutableInteractionSource(),
    indication        = indication,
    onClick           = onClick
)
