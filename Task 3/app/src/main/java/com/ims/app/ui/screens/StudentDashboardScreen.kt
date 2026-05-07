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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ims.app.data.SampleData
import com.ims.app.ui.theme.*

@Composable
fun StudentDashboardScreen(
    onNavigateToTimetable: () -> Unit,
    onNavigateToAttendance: () -> Unit,
    onNavigateToProfile: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var showAttendanceAlert by remember { mutableStateOf(true) } // Show on launch

    if (showAttendanceAlert) {
        val studentId = "2024111027" // Using Tanishi's ID as dummy
        val attendanceRecord = SampleData.attendanceRecords[studentId]
        val isGood = attendanceRecord?.isPresent ?: true
        AlertDialog(
            onDismissRequest = { showAttendanceAlert = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(
                        if (isGood) Icons.Default.CheckCircle else Icons.Default.Warning,
                        contentDescription = null,
                        tint = if (isGood) Color(0xFF388E3C) else Error
                    )
                    Text(if (isGood) "Great Job!" else "Attendance Alert", fontWeight = FontWeight.Bold, color = if (isGood) Color(0xFF388E3C) else Error)
                }
            },
            text = {
                Text(
                    if (isGood) "Your attendance is looking stellar this week. Keep up the good work and maintain your streak!"
                    else "You have low attendance in some of your recent courses (e.g. Operating Systems). Try to attend more classes to avoid penalties.",
                    fontSize = 14.sp,
                    color = OnSurfaceVariant
                )
            },
            confirmButton = {
                Button(
                    onClick = { showAttendanceAlert = false },
                    colors = ButtonDefaults.buttonColors(containerColor = Primary)
                ) { Text("Got it") }
            },
            containerColor = SurfaceContainerLowest
        )
    }

    Scaffold(
        bottomBar = {
            StudentBottomNav(
                selected = 0,
                onDashboard = {},
                onSchedule = onNavigateToTimetable,
                onAttendance = onNavigateToAttendance,
                onProfile = onNavigateToProfile
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
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Box(
                        modifier = Modifier.size(36.dp).clip(CircleShape).background(SecondaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("T", color = OnSecondaryContainer, fontWeight = FontWeight.Bold)
                    }
                    Text("IMS", fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = Primary)
                }
                BadgedBox(badge = { Badge(containerColor = Error) {} }) {
                    Icon(Icons.Default.Notifications, contentDescription = null, tint = OnSurfaceVariant)
                }
            }

            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(20.dp)) {

                // Search
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Search courses, faculty, or resources...", fontSize = 13.sp, color = Outline) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = OnSurfaceVariant) },
                    singleLine = true,
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Primary,
                        unfocusedBorderColor = OutlineVariant,
                        focusedContainerColor = SurfaceContainerLow,
                        unfocusedContainerColor = SurfaceContainerLow
                    )
                )

                // Welcome hero
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(Brush.linearGradient(listOf(Primary, PrimaryContainer)))
                        .padding(20.dp)
                ) {
                    Column {
                        Text("Welcome back, Tanishi.", fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = OnPrimary)
                        Spacer(Modifier.height(6.dp))
                        Text(
                            "Your academic progress is looking strong. You have 2 assignments due this week.",
                            fontSize = 13.sp, color = PrimaryFixedDim, lineHeight = 18.sp
                        )
                        Spacer(Modifier.height(16.dp))
                        Button(
                            onClick = onNavigateToTimetable,
                            colors = ButtonDefaults.buttonColors(containerColor = SurfaceContainerLowest),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("View Detailed Schedule", color = Primary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }
                }

                // Academic Hub
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Academic Hub", fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
                        Text("View All", fontSize = 11.sp, color = Primary, fontWeight = FontWeight.SemiBold)
                    }
                    val modules = listOf(
                        Triple(Icons.Default.CalendarToday, "Time Table", onNavigateToTimetable),
                        Triple(Icons.Default.FactCheck, "Attendance", onNavigateToAttendance),
                        Triple(Icons.Default.Assignment, "Grades", {}),
                        Triple(Icons.Default.MenuBook, "Library", {})
                    )
                    val rows = modules.chunked(2)
                    rows.forEach { row ->
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            row.forEach { (icon, label, action) ->
                                Card(
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(20.dp),
                                    colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest),
                                    onClick = { action() }
                                ) {
                                    Column(
                                        modifier = Modifier.padding(16.dp).fillMaxWidth(),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier.size(40.dp).clip(CircleShape).background(SecondaryContainer),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(icon, contentDescription = null, tint = OnSecondaryContainer, modifier = Modifier.size(20.dp))
                                        }
                                        Text(label, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                                    }
                                }
                            }
                        }
                    }
                }

                // Performance
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceContainer)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("Current Semester Performance", fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
                        Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest)) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("OVERALL CGPA", fontSize = 9.sp, color = OnSurfaceVariant, letterSpacing = 1.sp)
                                Spacer(Modifier.height(4.dp))
                                Text("8.3", fontSize = 36.sp, fontWeight = FontWeight.ExtraBold, color = Primary)
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Icon(Icons.Default.TrendingUp, contentDescription = null, tint = TertiaryContainer, modifier = Modifier.size(14.dp))
                                    Text("+0.2 from last semester", fontSize = 11.sp, color = TertiaryContainer)
                                }
                            }
                        }
                        Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest)) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("CREDITS EARNED", fontSize = 9.sp, color = OnSurfaceVariant, letterSpacing = 1.sp)
                                Spacer(Modifier.height(4.dp))
                                Text("XX/YY", fontSize = 28.sp, fontWeight = FontWeight.ExtraBold)
                                Spacer(Modifier.height(8.dp))
                                LinearProgressIndicator(
                                    progress = { 0.7f },
                                    modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(4.dp)),
                                    color = Primary,
                                    trackColor = SurfaceVariant
                                )
                            }
                        }
                    }
                }

                // Latest News
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceContainerLow)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("Latest News", fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
                        SampleData.newsItems.forEach { news ->
                            Card(
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest)
                            ) {
                                Row(modifier = Modifier.padding(12.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                    Box(
                                        modifier = Modifier.size(56.dp).clip(RoundedCornerShape(10.dp)).background(SecondaryContainer),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(Icons.Default.Article, contentDescription = null, tint = OnSecondaryContainer)
                                    }
                                    Column {
                                        Surface(shape = RoundedCornerShape(99.dp), color = SecondaryContainer) {
                                            Text(news.category, modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp), fontSize = 9.sp, color = OnSecondaryContainer, fontWeight = FontWeight.Bold)
                                        }
                                        Spacer(Modifier.height(4.dp))
                                        Text(news.title, fontSize = 13.sp, fontWeight = FontWeight.Bold, lineHeight = 17.sp)
                                        Text(news.timeAgo, fontSize = 11.sp, color = OnSurfaceVariant)
                                    }
                                }
                            }
                        }
                    }
                }

                // Upcoming Deadlines
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("Upcoming Deadlines", fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
                        listOf("APR" to "20" to "DASS A3" to "Due at 11:59 PM" to true,
                            "APR" to "18" to "MDL A4"  to "Due at 11:59 PM" to false
                        ).forEach { item ->
                            val (rest, urgent) = item
                            val (r2, due) = rest
                            val (r3, name) = r2
                            val (month, day) = r3
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(if (urgent) ErrorContainer else SurfaceContainerHigh),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(month, fontSize = 8.sp, fontWeight = FontWeight.Bold, color = if (urgent) OnErrorContainer else OnSurfaceVariant)
                                        Text(day, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = if (urgent) OnErrorContainer else OnSurface)
                                    }
                                }
                                Column {
                                    Text(name, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                                    Text(due, fontSize = 11.sp, color = OnSurfaceVariant)
                                }
                            }
                        }
                    }
                }

                Spacer(Modifier.height(8.dp))
            }
        }
    }
}