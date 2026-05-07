package com.ims.app.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ims.app.data.SampleData
import com.ims.app.ui.theme.*

@Composable
fun AdminProfileScreen(
    onNavigateToDashboard: () -> Unit,
    onNavigateToTimetable: () -> Unit,
    onNavigateToAttendance: () -> Unit,
    onLogout: () -> Unit
) {
    var showLogoutDialog by remember { mutableStateOf(false) }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Confirm Logout", fontWeight = FontWeight.Bold) },
            text = { Text("Are you sure you want to log out?") },
            confirmButton = {
                Button(onClick = onLogout, colors = ButtonDefaults.buttonColors(containerColor = Error)) { Text("Logout") }
            },
            dismissButton = { TextButton(onClick = { showLogoutDialog = false }) { Text("Cancel") } },
            containerColor = SurfaceContainerLowest
        )
    }

    Scaffold(
        bottomBar = {
            AdminBottomNav(
                selected = 3,
                onDashboard = onNavigateToDashboard,
                onSchedule = onNavigateToTimetable,
                onAttendance = onNavigateToAttendance,
                onProfile = {}
            )
        },
        containerColor = Surface
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState())) {

            // Profile hero
            Box(
                modifier = Modifier.fillMaxWidth().background(Brush.linearGradient(listOf(Primary, PrimaryContainer))).padding(vertical = 32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Box(
                        modifier = Modifier.size(80.dp).clip(CircleShape).background(OnPrimary.copy(0.2f)).border(3.dp, OnPrimary, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("AD", fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, color = OnPrimary)
                    }
                    Text("Administrator", fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = OnPrimary)
                    Text("admin@iiit.ac.in", fontSize = 14.sp, color = OnPrimary.copy(0.8f))
                    Surface(shape = RoundedCornerShape(99.dp), color = OnPrimary.copy(0.15f)) {
                        Text("Super Admin", modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp), fontSize = 11.sp, color = OnPrimary, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {

                // Institute info
                Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest)) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("Institute Info", fontSize = 15.sp, fontWeight = FontWeight.ExtraBold, color = Primary)
                        listOf(
                            Icons.Default.AccountBalance to "IIIT Hyderabad",
                            Icons.Default.LocationOn to "Gachibowli, Hyderabad",
                            Icons.Default.Language to SampleData.language,
                            Icons.Default.Public to SampleData.country,
                            Icons.Default.CurrencyRupee to SampleData.currency,
                            Icons.Default.Schedule to SampleData.timeZone
                        ).forEach { (icon, value) ->
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                Icon(icon, contentDescription = null, tint = Secondary, modifier = Modifier.size(18.dp))
                                Text(value, fontSize = 14.sp, color = OnSurface)
                            }
                        }
                    }
                }

                // System stats
                Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest)) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("System Stats", fontSize = 15.sp, fontWeight = FontWeight.ExtraBold, color = Primary)
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            listOf("${SampleData.students.size}" to "Students", "${SampleData.courses.size}" to "Courses", "${SampleData.batches.size}" to "Batches").forEach { (val_, label) ->
                                Card(modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = SurfaceContainerLow)) {
                                    Column(modifier = Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(val_, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = Primary)
                                        Text(label, fontSize = 10.sp, color = OnSurfaceVariant)
                                    }
                                }
                            }
                        }
                    }
                }

                // Grading system
                Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest)) {
                    Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Icon(Icons.Default.Grade, contentDescription = null, tint = Secondary, modifier = Modifier.size(20.dp))
                            Column {
                                Text("Grading System", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                                Text(SampleData.gradingSystem, fontSize = 12.sp, color = OnSurfaceVariant)
                            }
                        }
                        Surface(shape = RoundedCornerShape(99.dp), color = TertiaryFixed.copy(0.3f)) {
                            Text("Active", modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp), fontSize = 10.sp, color = Tertiary, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                // Logout
                Button(
                    onClick = { showLogoutDialog = true },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ErrorContainer, contentColor = OnErrorContainer)
                ) {
                    Icon(Icons.Default.Logout, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Logout", fontWeight = FontWeight.Bold)
                }

                Spacer(Modifier.height(8.dp))
            }
        }
    }
}