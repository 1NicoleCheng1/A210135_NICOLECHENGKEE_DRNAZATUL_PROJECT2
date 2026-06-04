package com.example.a210135_nicolechengkee_drnazatul_project2

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController


@Composable
fun FeaturedJobCard(title: String, company: String, salary: String, onClick: () -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    Column(
        Modifier
            .width(200.dp)
            .padding(8.dp)
            .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(20.dp))
            .clickable { expanded = !expanded; if (!expanded) onClick() }
            .animateContentSize()
            .padding(16.dp)
    ) {
        Text(title, color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Bold)
        Text(company, color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f), fontSize = 13.sp)
        if (expanded) {
            Spacer(Modifier.height(8.dp))
            Text("Salary: $salary", color = MaterialTheme.colorScheme.onPrimary, fontSize = 13.sp)
            Spacer(Modifier.height(8.dp))
            TextButton(
                onClick = onClick,
                colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.onPrimary)
            ) { Text("View Details →") }
        }
    }
}

@Composable
fun JobCard(
    title: String,
    company: String,
    location: String,
    salary: String,
    jobType: String = "Full Time",
    isBookmarked: Boolean = false,
    hasApplied: Boolean = false,
    onBookmarkToggle: () -> Unit = {},
    onClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(Modifier.padding(16.dp).animateContentSize()) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    Modifier
                        .size(46.dp)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f), RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Work, null, tint = MaterialTheme.colorScheme.primary)
                }
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text(title, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Text(company, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp)
                    Text(location, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    IconButton(onClick = onBookmarkToggle, modifier = Modifier.size(32.dp)) {
                        Icon(
                            if (isBookmarked) Icons.Default.Bookmark else Icons.Outlined.FavoriteBorder,
                            null,
                            tint = if (isBookmarked) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    if (hasApplied) {
                        Icon(Icons.Default.CheckCircle, null, tint = Color(0xFF4CAF50), modifier = Modifier.size(18.dp))
                    }
                }
            }
            Spacer(Modifier.height(10.dp))
            Row {
                TagChip(jobType)
                TagChip(salary)
            }
        }
    }
}

//Tag Chips

@Composable
fun TagChip(text: String) {
    Box(
        Modifier
            .padding(end = 6.dp)
            .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(10.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(text, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
fun TagChipLight(text: String) {
    Box(
        Modifier
            .padding(end = 6.dp)
            .background(MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.2f), RoundedCornerShape(10.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(text, fontSize = 12.sp, color = MaterialTheme.colorScheme.onPrimary)
    }
}

@Composable
fun CategoryChip(text: String, selected: Boolean, onClick: () -> Unit) {
    Box(
        Modifier
            .padding(end = 8.dp)
            .background(
                if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                RoundedCornerShape(20.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text,
            color = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 13.sp,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal
        )
    }
}



@Composable
fun InfoCard(label: String, value: String, icon: ImageVector, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(Modifier.padding(16.dp)) {
            Icon(icon, null, tint = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.height(8.dp))
            Text(label, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(value, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface, fontSize = 14.sp)
        }
    }
}

//Form Section

@Composable
fun FormSection(label: String, content: @Composable () -> Unit) {
    Column(Modifier.padding(bottom = 16.dp)) {
        Text(
            label,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(bottom = 6.dp)
        )
        content()
    }
}

// Status Badge

@Composable
fun StatusBadge(status: String) {
    val (bg, fg) = when (status) {
        "Pending"  -> Color(0xFFFFF3E0) to Color(0xFFE65100)
        "Viewed"   -> Color(0xFFE3F2FD) to Color(0xFF1565C0)
        "Rejected" -> Color(0xFFFFEBEE) to Color(0xFFC62828)
        else       -> Color(0xFFF5F5F5) to Color(0xFF616161)
    }
    Box(
        Modifier
            .background(bg, RoundedCornerShape(8.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(status, fontSize = 11.sp, color = fg, fontWeight = FontWeight.SemiBold)
    }
}

//Bottom Navigation Bar
@Composable
fun BottomNav(currentRoute: String?, navController: NavController) {
    NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
        listOf(
            Triple(Icons.Default.Home,    "Home",      Screen.Home.route),
            Triple(Icons.Default.Work,    "Jobs",      Screen.Jobs.route),
            Triple(Icons.Default.Groups,  "Community", Screen.Community.route),
            Triple(Icons.Default.Email,   "Chat",      Screen.Chat.route),
            Triple(Icons.Default.Person,  "Profile",   Screen.Profile.route),
        ).forEach { (icon, label, route) ->
            NavigationBarItem(
                icon     = { Icon(icon, contentDescription = label) },
                label    = { Text(label, fontSize = 11.sp) },
                selected = currentRoute == route,
                onClick  = { navController.navigate(route) { launchSingleTop = true } }
            )
        }
    }
}

// Custom Text Field

@Composable
fun CustomTextField(value: String, label: String = "City or remote", onChange: (String) -> Unit) {
    OutlinedTextField(
        value           = value,
        onValueChange   = onChange,
        placeholder     = { Text(label) },
        modifier        = Modifier.fillMaxWidth(),
        shape           = RoundedCornerShape(16.dp),
        singleLine      = true,
        leadingIcon     = { Icon(Icons.Default.LocationOn, null, tint = MaterialTheme.colorScheme.primary) }
    )
}

// Loading Indicator

@Composable
fun LoadingCard() {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        shape    = RoundedCornerShape(16.dp),
        colors   = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Box(Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.height(12.dp))
                Text("Loading jobs...", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 14.sp)
            }
        }
    }
}

//Empty State

@Composable
fun EmptyState(message: String, icon: ImageVector = Icons.Default.SearchOff) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(top = 24.dp),
        shape    = RoundedCornerShape(20.dp),
        colors   = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            Modifier.fillMaxWidth().padding(40.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(48.dp))
            Spacer(Modifier.height(12.dp))
            Text(message, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 14.sp)
        }
    }
}
