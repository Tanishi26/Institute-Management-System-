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
fun AttendanceReportScreen(
    studentId: String?,
    isAdmin: Boolean,
    onBack: () -> Unit,
    onNavigateToDashboard: () -> Unit,
    onNavigateToTimetable: () -> Unit,
    onNavigateToProfile: () -> Unit = {}
) {
    val targetStudent = if (studentId == "current") SampleData.students.getOrNull(1) else SampleData.students.find { it.id == studentId }
    val isSingleStudent = targetStudent != null

    var selectedTab by remember { mutableStateOf(0) }
    var selectedBatch by remember { mutableStateOf(targetStudent?.batch ?: "UG2k24") }
    var selectedProgram by remember { mutableStateOf(targetStudent?.program ?: "CSE") }
    val subjectOptions = SampleData.courses
        .filter { it.batch == selectedBatch && it.program == selectedProgram }
        .take(6)
    var selectedSubject by remember { mutableStateOf(subjectOptions.firstOrNull()?.code.orEmpty()) }
    LaunchedEffect(selectedBatch, selectedProgram, subjectOptions) {
        if (subjectOptions.none { it.code == selectedSubject }) {
            selectedSubject = subjectOptions.firstOrNull()?.code.orEmpty()
        }
    }
    val tabs = listOf("Daily", "Monthly", "Subject-wise")
    val filteredStudents = SampleData.students.filter { it.batch == selectedBatch && it.program == selectedProgram }
    
    val overallPercent: Int
    val presentCountStr: String
    val absentCountStr: String
    val reportLabel: String
    val studentIsPresent = targetStudent?.let { SampleData.attendanceRecords[it.id]?.isPresent == true } ?: false

    if (isSingleStudent) {
        overallPercent = if (studentIsPresent) 88 else 75
        presentCountStr = if (studentIsPresent) "44" else "38"
        absentCountStr = if (studentIsPresent) "6" else "12"
        reportLabel = targetStudent!!.name
    } else {
        val presentCount = filteredStudents.count { SampleData.attendanceRecords[it.id]?.isPresent == true }
        val absentCount = filteredStudents.size - presentCount
        overallPercent = if (filteredStudents.isEmpty()) 0 else presentCount * 100 / filteredStudents.size
        presentCountStr = presentCount.toString()
        absentCountStr = absentCount.toString()
        reportLabel = when (selectedTab) {
            0 -> "Apr 20"
            1 -> "April 2026"
            else -> selectedSubject
        }
    }

    val subjectData = subjectOptions.mapIndexed { index, course ->
        val baseP = if (isSingleStudent) (if(studentIsPresent) 85 else 75) else overallPercent
        val percent = if (course.code == selectedSubject) baseP else (baseP - 5 + index * 4).coerceIn(0, 100)
        Triple(course.code, course.name, course.faculty) to Pair(percent, "$percent/100")
    }

    Scaffold(
        bottomBar = {
            if (isAdmin) {
                AdminBottomNav(
                    selected = 2,
                    onDashboard = onNavigateToDashboard,
                    onSchedule = onNavigateToTimetable,
                    onAttendance = {},
                    onProfile = onNavigateToProfile
                )
            } else {
                StudentBottomNav(
                    selected = 2,
                    onDashboard = onNavigateToDashboard,
                    onSchedule = onNavigateToTimetable,
                    onAttendance = {},
                    onProfile = onNavigateToProfile
                )
            }
        },
        containerColor = Surface
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            // Top bar
            Row(
                modifier = Modifier.fillMaxWidth().background(SurfaceContainer).padding(horizontal = 20.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Icon(if (isAdmin) Icons.Default.AdminPanelSettings else Icons.Default.School, contentDescription = null, tint = Primary, modifier = Modifier.size(28.dp))
                    Text(if (isSingleStudent) "Student Report" else "Attendance Report", fontSize = 17.sp, fontWeight = FontWeight.ExtraBold, color = PrimaryContainer, letterSpacing = 0.5.sp)
                }
                Icon(Icons.Default.CalendarToday, contentDescription = null, tint = OnSurfaceVariant)
            }

            Column(
                modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (!isSingleStudent) {
                    // Tab row
                    Row(
                        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(SurfaceContainerLow).padding(4.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        tabs.forEachIndexed { index, label ->
                            Button(
                                onClick = { selectedTab = index },
                                modifier = Modifier.weight(1f).height(36.dp),
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (selectedTab == index) SurfaceContainerLowest else SurfaceContainerLow,
                                    contentColor = if (selectedTab == index) Primary else OnSurfaceVariant
                                ),
                                elevation = ButtonDefaults.buttonElevation(defaultElevation = if (selectedTab == index) 2.dp else 0.dp)
                            ) { Text(label, fontSize = 12.sp, fontWeight = if (selectedTab == index) FontWeight.SemiBold else FontWeight.Normal) }
                        }
                    }

                    // Filters card
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest)
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Text("Filters", fontSize = 17.sp, fontWeight = FontWeight.Bold)

                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text("DATE", fontSize = 9.sp, fontWeight = FontWeight.SemiBold, color = OnSurfaceVariant, letterSpacing = 1.sp)
                                Row(
                                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(4.dp)).background(SurfaceContainerLow).padding(10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(if (selectedTab == 1) "April 2026" else "Apr 20, 2026", fontSize = 13.sp)
                                    Icon(Icons.Default.Event, contentDescription = null, tint = Outline, modifier = Modifier.size(16.dp))
                                }
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                listOf("Batch" to selectedBatch, "Program" to selectedProgram).forEach { (label, value) ->
                                    Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                        Text(label.uppercase(), fontSize = 9.sp, fontWeight = FontWeight.SemiBold, color = OnSurfaceVariant, letterSpacing = 1.sp)
                                        Row(
                                            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(4.dp)).background(SurfaceContainerLow).padding(10.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(value, fontSize = 12.sp)
                                            Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = Outline, modifier = Modifier.size(16.dp))
                                        }
                                    }
                                }
                            }
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                SampleData.batches.forEach { batch ->
                                    FilterChip(
                                        selected = selectedBatch == batch,
                                        onClick = { selectedBatch = batch },
                                        label = { Text(batch, fontSize = 11.sp) }
                                    )
                                }
                            }
                            SampleData.programs.chunked(4).forEach { row ->
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    row.forEach { program ->
                                        FilterChip(
                                            selected = selectedProgram == program,
                                            onClick = { selectedProgram = program },
                                            label = { Text(program, fontSize = 11.sp) }
                                        )
                                    }
                                }
                            }
                            subjectOptions.chunked(2).forEach { row ->
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    row.forEach { course ->
                                        FilterChip(
                                            selected = selectedSubject == course.code,
                                            onClick = { selectedSubject = course.code },
                                            label = { Text(course.code, fontSize = 10.sp) }
                                        )
                                    }
                                }
                            }

                            Button(
                                onClick = {},
                                modifier = Modifier.fillMaxWidth().height(44.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Primary)
                            ) { Text("Apply Filters", fontWeight = FontWeight.Bold) }
                        }
                    }
                } // End if(!isSingleStudent)

                // Overview
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Text(if (isSingleStudent) "Student Overview" else "Overview", fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)
                        Text(if (isSingleStudent) reportLabel else "$selectedProgram $selectedBatch | $reportLabel", fontSize = 11.sp, color = OnSurfaceVariant)
                    }

                    // Overall % card
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(20.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("OVERALL ATTENDANCE", fontSize = 9.sp, color = OnSurfaceVariant, letterSpacing = 1.sp, fontWeight = FontWeight.SemiBold)
                                Spacer(Modifier.height(4.dp))
                                Row(verticalAlignment = Alignment.Bottom) {
                                    Text("$overallPercent", fontSize = 42.sp, fontWeight = FontWeight.ExtraBold, color = Primary)
                                    Text("%", fontSize = 24.sp, color = OnSurfaceVariant, modifier = Modifier.padding(bottom = 6.dp))
                                }
                            }
                            Box(
                                modifier = Modifier.size(48.dp).clip(CircleShape).background(TertiaryContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = OnTertiaryContainer, modifier = Modifier.size(28.dp))
                            }
                        }
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        // Present card
                        Card(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = SurfaceContainerLow)
                        ) {
                            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Icon(Icons.Default.Group, contentDescription = null, tint = Primary, modifier = Modifier.size(14.dp))
                                    Text("PRESENT", fontSize = 8.sp, color = OnSurfaceVariant, letterSpacing = 0.8.sp, fontWeight = FontWeight.SemiBold)
                                }
                                Text(presentCountStr, fontSize = 26.sp, fontWeight = FontWeight.Bold)
                                Text(if (isSingleStudent) "classes attended" else "out of ${filteredStudents.size}", fontSize = 11.sp, color = Outline)
                            }
                        }
                        // Absent card
                        Card(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = SurfaceContainerLow)
                        ) {
                            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Icon(Icons.Default.PersonOff, contentDescription = null, tint = Error, modifier = Modifier.size(14.dp))
                                    Text("ABSENT", fontSize = 8.sp, color = OnSurfaceVariant, letterSpacing = 0.8.sp, fontWeight = FontWeight.SemiBold)
                                }
                                Text(absentCountStr, fontSize = 26.sp, fontWeight = FontWeight.Bold)
                                Surface(shape = RoundedCornerShape(99.dp), color = ErrorContainer) {
                                    Text(if (isSingleStudent) "$absentCountStr missed" else "$absentCountStr note(s)", modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp), fontSize = 9.sp, color = OnErrorContainer, fontWeight = FontWeight.SemiBold)
                                }
                            }
                        }
                    }
                }

                // Subject breakdown
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Subject Breakdown", fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)

                    subjectData.forEach { (info, stat) ->
                        val (code, subject, prof) = info
                        val (percent, fraction) = stat
                        val isLow = percent < 80

                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                    if (isLow) {
                                        Box(modifier = Modifier.width(4.dp).height(52.dp).clip(RoundedCornerShape(2.dp)).background(Error))
                                    }
                                    Box(
                                        modifier = Modifier.size(40.dp).clip(RoundedCornerShape(10.dp)).background(SecondaryContainer),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(code, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = OnSecondaryContainer)
                                    }
                                    Column {
                                        Text(subject, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, lineHeight = 15.sp)
                                        Text(prof, fontSize = 10.sp, color = OnSurfaceVariant)
                                    }
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text("$percent%", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = if (isLow) Error else Primary)
                                    Text(fraction, fontSize = 10.sp, color = Outline)
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
