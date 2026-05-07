package com.ims.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
fun StudentProfileScreen(
    onNavigateToDashboard: () -> Unit,
    onNavigateToTimetable: () -> Unit,
    onNavigateToAttendance: () -> Unit,
    onLogout: () -> Unit
) {
    val student = SampleData.students.firstOrNull { it.id == "2024111027" } ?: SampleData.students.first()
    
    Scaffold(
        bottomBar = {
            StudentBottomNav(
                selected = 3,
                onDashboard = onNavigateToDashboard,
                onSchedule = onNavigateToTimetable,
                onAttendance = onNavigateToAttendance,
                onProfile = {}
            )
        },
        containerColor = Surface
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            // Top bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SurfaceContainerLow)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Profile", fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = Primary)
                IconButton(onClick = onLogout) {
                    Icon(Icons.Default.Logout, contentDescription = "Logout", tint = Error)
                }
            }

            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(20.dp)) {
                // Profile Header
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(Brush.linearGradient(listOf(Primary, PrimaryContainer)))
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .clip(CircleShape)
                                .background(SecondaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                student.name.first().toString(),
                                fontSize = 36.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = OnSecondaryContainer
                            )
                        }
                        Text(student.name, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = OnPrimary)
                        Surface(shape = RoundedCornerShape(99.dp), color = SurfaceContainerLowest.copy(alpha = 0.2f)) {
                            Text(
                                "ID: ${student.id}",
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                                fontSize = 12.sp,
                                color = OnPrimary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // Academic Information
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        Text("Academic Details", fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = Primary)
                        
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            ProfileDetailItem(icon = Icons.Default.School, label = "Program", value = student.program, modifier = Modifier.weight(1f))
                            ProfileDetailItem(icon = Icons.Default.Group, label = "Batch", value = student.batch, modifier = Modifier.weight(1f))
                        }
                        Divider(color = SurfaceContainer, thickness = 0.5.dp)
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            ProfileDetailItem(icon = Icons.Default.MenuBook, label = "Current Sem", value = "Semester 4", modifier = Modifier.weight(1f))
                            ProfileDetailItem(icon = Icons.Default.Email, label = "Institute Email", value = "${student.name.lowercase().replace(" ", ".")}@institute.edu", modifier = Modifier.weight(1f))
                        }
                    }
                }

                // Grades and Performance
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        Text("Performance & Grades", fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = Primary)
                        
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Box(
                                modifier = Modifier.weight(1f).clip(RoundedCornerShape(12.dp)).background(SurfaceContainerLow).padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("CGPA", fontSize = 10.sp, color = OnSurfaceVariant, fontWeight = FontWeight.Bold)
                                    Text("8.34", fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, color = Primary)
                                }
                            }
                            Box(
                                modifier = Modifier.weight(1f).clip(RoundedCornerShape(12.dp)).background(SurfaceContainerLow).padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("Credits", fontSize = 10.sp, color = OnSurfaceVariant, fontWeight = FontWeight.Bold)
                                    Text("72", fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, color = Primary)
                                }
                            }
                        }
                        
                        Text("Recent Courses", fontSize = 14.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 8.dp))
                        listOf(
                            Triple("Data Structures", "A-", "4 Credits"),
                            Triple("Machine Learning", "B+", "4 Credits"),
                            Triple("Web Systems", "A", "3 Credits")
                        ).forEach { (course, grade, credits) ->
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(course, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                                    Text(credits, fontSize = 11.sp, color = OnSurfaceVariant)
                                }
                                Surface(shape = RoundedCornerShape(8.dp), color = TertiaryContainer) {
                                    Text(
                                        grade,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = OnTertiaryContainer
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun ProfileDetailItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.size(36.dp).clip(CircleShape).background(SurfaceContainerLow),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = Primary, modifier = Modifier.size(16.dp))
        }
        Column {
            Text(label, fontSize = 10.sp, color = OnSurfaceVariant)
            Text(value, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = OnSurface)
        }
    }
}
