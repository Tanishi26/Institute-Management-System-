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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ims.app.data.Course
import com.ims.app.data.SampleData
import com.ims.app.ui.theme.*

@Composable
fun ManageCoursesScreen(onBack: () -> Unit) {
    val courses = remember { SampleData.courses.toMutableStateList() }
    val batches = remember { SampleData.batches.toMutableStateList() }
    var selectedBatch by remember { mutableStateOf("All") }
    var searchQuery by remember { mutableStateOf("") }
    var showAddCourseDialog by remember { mutableStateOf(false) }
    var showAddBatchDialog by remember { mutableStateOf(false) }
    var selectedTab by remember { mutableStateOf(0) }

    val filteredCourses = courses.filter {
        (selectedBatch == "All" || it.batch == selectedBatch) &&
                (searchQuery.isEmpty() || it.name.lowercase().contains(searchQuery.lowercase()) || it.code.lowercase().contains(searchQuery.lowercase()))
    }

    if (showAddCourseDialog) {
        AddCourseDialog(
            batches = batches,
            onDismiss = { showAddCourseDialog = false },
            onAdd = { course -> courses.add(course); SampleData.courses.add(course); showAddCourseDialog = false }
        )
    }
    if (showAddBatchDialog) {
        AddBatchDialog(
            onDismiss = { showAddBatchDialog = false },
            onAdd = { batch -> batches.add(batch); SampleData.batches.add(batch); showAddBatchDialog = false }
        )
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { if (selectedTab == 0) showAddCourseDialog = true else showAddBatchDialog = true },
                containerColor = Primary, contentColor = OnPrimary
            ) { Icon(Icons.Default.Add, contentDescription = "Add") }
        },
        containerColor = Surface
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth().background(SurfaceContainerLow).padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = null, tint = Primary) }
                Text("Courses & Batches", fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = Primary)
            }

            // Tabs
            Row(
                modifier = Modifier.fillMaxWidth().background(SurfaceContainerLow).padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("Courses", "Batches").forEachIndexed { i, label ->
                    Button(
                        onClick = { selectedTab = i },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (selectedTab == i) Primary else SurfaceContainerHigh,
                            contentColor = if (selectedTab == i) OnPrimary else OnSurfaceVariant
                        ),
                        modifier = Modifier.height(36.dp)
                    ) { Text(label, fontSize = 13.sp, fontWeight = FontWeight.SemiBold) }
                }
            }

            if (selectedTab == 0) {
                // Courses tab
                Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {

                    // Search
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Search courses...", fontSize = 13.sp, color = Outline) },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = OnSurfaceVariant) },
                        singleLine = true, shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Primary, unfocusedBorderColor = OutlineVariant, focusedContainerColor = SurfaceContainerLowest, unfocusedContainerColor = SurfaceContainerLowest)
                    )

                    // Batch filter chips
                    Row(
                        modifier = Modifier.horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        (listOf("All") + batches.toList()).forEach { batch ->
                            FilterChip(
                                selected = selectedBatch == batch,
                                onClick = { selectedBatch = batch },
                                label = { Text(batch, fontSize = 12.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = PrimaryContainer,
                                    selectedLabelColor = OnPrimaryContainer
                                )
                            )
                        }
                    }

                    // Stats
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Card(modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = PrimaryContainer.copy(0.15f))) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text("${filteredCourses.size}", fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = Primary)
                                Text("Courses", fontSize = 11.sp, color = OnSurfaceVariant)
                            }
                        }
                        Card(modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = SecondaryContainer.copy(0.3f))) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text("${filteredCourses.sumOf { it.credits }}", fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = Secondary)
                                Text("Total Credits", fontSize = 11.sp, color = OnSurfaceVariant)
                            }
                        }
                    }

                    // Course list
                    filteredCourses.forEach { course ->
                        Card(shape = RoundedCornerShape(14.dp), colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest)) {
                            Row(modifier = Modifier.fillMaxWidth().padding(14.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                    Box(modifier = Modifier.size(44.dp).clip(RoundedCornerShape(10.dp)).background(PrimaryContainer.copy(0.2f)), contentAlignment = Alignment.Center) {
                                        Text(course.code.take(2), fontSize = 12.sp, fontWeight = FontWeight.ExtraBold, color = Primary)
                                    }
                                    Column {
                                        Text(course.name, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, lineHeight = 16.sp)
                                        Text(course.faculty, fontSize = 11.sp, color = OnSurfaceVariant)
                                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                            Surface(shape = RoundedCornerShape(99.dp), color = SecondaryContainer) {
                                                Text(course.batch, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp), fontSize = 9.sp, color = OnSecondaryContainer, fontWeight = FontWeight.Bold)
                                            }
                                            Surface(shape = RoundedCornerShape(99.dp), color = PrimaryFixed) {
                                                Text("${course.credits} Cr", modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp), fontSize = 9.sp, color = Primary, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    }
                                }
                                IconButton(onClick = { courses.remove(course); SampleData.courses.remove(course) }) {
                                    Icon(Icons.Default.DeleteOutline, contentDescription = null, tint = Error, modifier = Modifier.size(18.dp))
                                }
                            }
                        }
                    }
                    Spacer(Modifier.height(60.dp))
                }
            } else {
                // Batches tab
                Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    batches.toList().forEach { batch ->
                        Card(shape = RoundedCornerShape(14.dp), colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest)) {
                            Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                    Box(modifier = Modifier.size(40.dp).clip(CircleShape).background(PrimaryContainer.copy(0.2f)), contentAlignment = Alignment.Center) {
                                        Icon(Icons.Default.Group, contentDescription = null, tint = Primary, modifier = Modifier.size(18.dp))
                                    }
                                    Column {
                                        Text(batch, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                                        Text("${courses.count { it.batch == batch }} courses", fontSize = 11.sp, color = OnSurfaceVariant)
                                    }
                                }
                                IconButton(onClick = { batches.remove(batch); SampleData.batches.remove(batch) }) {
                                    Icon(Icons.Default.DeleteOutline, contentDescription = null, tint = Error, modifier = Modifier.size(18.dp))
                                }
                            }
                        }
                    }
                    Spacer(Modifier.height(60.dp))
                }
            }
        }
    }
}

@Composable
fun AddCourseDialog(batches: List<String>, onDismiss: () -> Unit, onAdd: (Course) -> Unit) {
    var code by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var faculty by remember { mutableStateOf("") }
    var selectedBatch by remember { mutableStateOf(batches.firstOrNull() ?: "") }
    var credits by remember { mutableStateOf("3") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add New Course", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                listOf(
                    Triple("Course Code", code) { v: String -> code = v },
                    Triple("Course Name", name) { v: String -> name = v },
                    Triple("Faculty", faculty) { v: String -> faculty = v },
                    Triple("Credits", credits) { v: String -> credits = v }
                ).forEach { (label, value, onChange) ->
                    OutlinedTextField(value = value, onValueChange = onChange, label = { Text(label) }, singleLine = true, modifier = Modifier.fillMaxWidth(), colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Primary))
                }
                Text("Batch", fontSize = 12.sp, color = OnSurfaceVariant, fontWeight = FontWeight.SemiBold)
                Row(modifier = Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    batches.forEach { batch ->
                        FilterChip(selected = selectedBatch == batch, onClick = { selectedBatch = batch }, label = { Text(batch, fontSize = 11.sp) }, colors = FilterChipDefaults.filterChipColors(selectedContainerColor = PrimaryContainer))
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (code.isNotEmpty() && name.isNotEmpty()) {
                        onAdd(Course(code, name, faculty, selectedBatch, credits.toIntOrNull() ?: 3))
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Primary)
            ) { Text("Add Course") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } },
        containerColor = SurfaceContainerLowest
    )
}

@Composable
fun AddBatchDialog(onDismiss: () -> Unit, onAdd: (String) -> Unit) {
    var batchName by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add New Batch", fontWeight = FontWeight.Bold) },
        text = {
            OutlinedTextField(value = batchName, onValueChange = { batchName = it }, label = { Text("Batch Name (e.g. UG2k25)") }, singleLine = true, modifier = Modifier.fillMaxWidth(), colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Primary))
        },
        confirmButton = {
            Button(onClick = { if (batchName.isNotEmpty()) onAdd(batchName) }, colors = ButtonDefaults.buttonColors(containerColor = Primary)) { Text("Add") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } },
        containerColor = SurfaceContainerLowest
    )
}