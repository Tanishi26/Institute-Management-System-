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
fun AttendanceMarkingScreen(
    onBack: () -> Unit,
    onViewReport: (String) -> Unit,
    onViewGeneralReport: () -> Unit,
    onNavigateToDashboard: () -> Unit,
    onNavigateToTimetable: () -> Unit,
    onNavigateToProfile: () -> Unit
) {
    val attendance = remember {
        mutableStateMapOf<String, Boolean>().apply {
            SampleData.students.forEach { student ->
                put(student.id, SampleData.attendanceRecords[student.id]?.isPresent ?: false)
            }
        }
    }
    val notes = remember {
        mutableStateMapOf<String, String>().apply {
            SampleData.students.forEach { student ->
                put(student.id, SampleData.attendanceRecords[student.id]?.note ?: "")
            }
        }
    }
    var saved by remember { mutableStateOf(false) }
    var selectedBatch by remember { mutableStateOf("UG2k24") }
    var selectedProgram by remember { mutableStateOf("CSE") }
    val subjectOptions = SampleData.courses
        .filter { it.batch == selectedBatch && it.program == selectedProgram }
        .map { it.code }
        .take(6)
    var selectedSubject by remember { mutableStateOf(subjectOptions.firstOrNull().orEmpty()) }
    LaunchedEffect(selectedBatch, selectedProgram, subjectOptions) {
        if (selectedSubject !in subjectOptions) {
            selectedSubject = subjectOptions.firstOrNull().orEmpty()
        }
    }
    val filteredStudents = SampleData.students.filter { it.batch == selectedBatch && it.program == selectedProgram }
    val presentCount = filteredStudents.count { attendance[it.id] == true }

    Scaffold(
        bottomBar = {
            AdminBottomNav(
                selected = 2,
                onDashboard = onNavigateToDashboard,
                onSchedule = onNavigateToTimetable,
                onAttendance = {},
                onProfile = onNavigateToProfile
            )
        },
        containerColor = Surface
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding)
        ) {
            // Top bar
            Row(
                modifier = Modifier.fillMaxWidth().background(SurfaceContainerLow).padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(modifier = Modifier.size(36.dp).clip(CircleShape).background(SecondaryContainer), contentAlignment = Alignment.Center) {
                        Text("A", color = OnSecondaryContainer, fontWeight = FontWeight.Bold)
                    }
                    Text("IMS", fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = Primary)
                }
                Icon(Icons.Default.Notifications, contentDescription = null, tint = OnSurfaceVariant)
            }

            Column(
                modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header card
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text("Attendance", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Primary)
                            OutlinedButton(onClick = onViewGeneralReport, shape = RoundedCornerShape(10.dp), contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp), modifier = Modifier.height(28.dp)) {
                                Text("General Report", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Icon(Icons.Default.Group, contentDescription = null, tint = OnSurfaceVariant, modifier = Modifier.size(16.dp))
                                Text("$selectedProgram $selectedBatch", fontSize = 12.sp, color = OnSurfaceVariant)
                            }
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Icon(Icons.Default.CalendarToday, contentDescription = null, tint = OnSurfaceVariant, modifier = Modifier.size(16.dp))
                                Text("Apr 20, 2026", fontSize = 12.sp, color = OnSurfaceVariant)
                            }
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            SampleData.batches.forEach { batch ->
                                FilterChip(
                                    selected = selectedBatch == batch,
                                    onClick = {
                                        selectedBatch = batch
                                        saved = false
                                    },
                                    label = { Text(batch, fontSize = 11.sp) }
                                )
                            }
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            SampleData.programs.forEach { program ->
                                FilterChip(
                                    selected = selectedProgram == program,
                                    onClick = {
                                        selectedProgram = program
                                        saved = false
                                    },
                                    label = { Text(program, fontSize = 11.sp) }
                                )
                            }
                        }
                        subjectOptions.chunked(3).forEach { row ->
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                row.forEach { subject ->
                                    FilterChip(
                                        selected = selectedSubject == subject,
                                        onClick = {
                                            selectedSubject = subject
                                            saved = false
                                        },
                                        label = { Text(subject, fontSize = 10.sp) }
                                    )
                                }
                            }
                        }
                        if (subjectOptions.isEmpty()) {
                            Text("No subjects configured for this batch/program.", fontSize = 11.sp, color = Error)
                        }
                        Text(
                            "$presentCount/${filteredStudents.size} present for $selectedSubject",
                            fontSize = 12.sp,
                            color = OnSurfaceVariant,
                            fontWeight = FontWeight.SemiBold
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedButton(
                                onClick = {
                                    filteredStudents.forEach { attendance[it.id] = true }
                                    saved = false
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp)
                            ) { Text("Mark all present", fontSize = 12.sp) }
                            OutlinedButton(
                                onClick = {
                                    filteredStudents.forEach { attendance[it.id] = false }
                                    saved = false
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp)
                            ) { Text("Mark all absent", fontSize = 12.sp) }
                        }
                        Spacer(Modifier.height(4.dp))
                        Button(
                            onClick = {
                                // Save to SampleData
                                filteredStudents.forEach { student ->
                                    SampleData.attendanceRecords[student.id]?.let { record ->
                                        record.isPresent = attendance[student.id] ?: false
                                        record.note = notes[student.id].orEmpty()
                                    }
                                }
                                saved = true
                            },
                            modifier = Modifier.fillMaxWidth().height(48.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Primary)
                        ) {
                            Icon(Icons.Default.Save, contentDescription = null, tint = OnPrimary, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("Save Attendance", fontWeight = FontWeight.SemiBold)
                        }
                        if (saved) {
                            Text("Attendance saved with notes.", fontSize = 12.sp, color = TertiaryContainer, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }

                // Student list
                filteredStudents.forEach { student ->
                    val isPresent = attendance[student.id] ?: false
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest)
                    ) {
                        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                    Box(
                                        modifier = Modifier.size(48.dp).clip(CircleShape).background(
                                            if (isPresent) TertiaryContainer.copy(0.3f) else ErrorContainer.copy(0.4f)
                                        ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(student.name.first().toString(), fontSize = 18.sp, fontWeight = FontWeight.Bold,
                                            color = if (isPresent) TertiaryContainer else Error)
                                    }
                                    Column {
                                        Text(student.name, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                                        Text("ID: ${student.id}", fontSize = 11.sp, color = OnSurfaceVariant)
                                        TextButton(
                                            onClick = { onViewReport(student.id) },
                                            contentPadding = PaddingValues(0.dp),
                                            modifier = Modifier.height(24.dp)
                                        ) {
                                            Icon(Icons.Default.BarChart, contentDescription = null, tint = Primary, modifier = Modifier.size(12.dp))
                                            Spacer(Modifier.width(4.dp))
                                            Text("View Report", fontSize = 9.sp, color = Primary, fontWeight = FontWeight.Bold, letterSpacing = 0.8.sp)
                                        }
                                    }
                                }
                            }

                            // Present / Absent toggle
                            Row(
                                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp)).background(SurfaceContainerLow).padding(4.dp),
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Button(
                                    onClick = { attendance[student.id] = true },
                                    modifier = Modifier.weight(1f).height(40.dp),
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (isPresent) TertiaryContainer else SurfaceContainerLow,
                                        contentColor = if (isPresent) OnTertiaryContainer else OnSurfaceVariant
                                    )
                                ) { Text("Present", fontSize = 13.sp, fontWeight = FontWeight.Medium) }

                                Button(
                                    onClick = { attendance[student.id] = false },
                                    modifier = Modifier.weight(1f).height(40.dp),
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (!isPresent) ErrorContainer else SurfaceContainerLow,
                                        contentColor = if (!isPresent) OnErrorContainer else OnSurfaceVariant
                                    )
                                ) { Text("Absent", fontSize = 13.sp, fontWeight = FontWeight.Medium) }
                            }

                            // Note input
                            OutlinedTextField(
                                value = notes[student.id] ?: "",
                                onValueChange = { notes[student.id] = it },
                                modifier = Modifier.fillMaxWidth(),
                                placeholder = { Text("Add note...", fontSize = 12.sp, color = Outline) },
                                leadingIcon = { Icon(Icons.Default.EditNote, contentDescription = null, tint = OnSurfaceVariant, modifier = Modifier.size(18.dp)) },
                                singleLine = true,
                                shape = RoundedCornerShape(10.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Primary,
                                    unfocusedBorderColor = OutlineVariant
                                )
                            )
                        }
                    }
                }
                Spacer(Modifier.height(8.dp))
            }
        }
    }
}
