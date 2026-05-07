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
import androidx.compose.ui.window.Dialog
import com.ims.app.data.NewsItem
import com.ims.app.data.PendingApproval
import com.ims.app.data.SampleData
import com.ims.app.ui.theme.*

@Composable
fun AdminDashboardScreen(
    onNavigateToAttendance: () -> Unit,
    onNavigateToAttendanceReport: () -> Unit,
    onNavigateToTimetable: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToCourses: () -> Unit,
    onNavigateToProfile: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var searchResults by remember { mutableStateOf<List<Pair<String, String>>>(emptyList()) }
    var showSearchResults by remember { mutableStateOf(false) }
    val approvals = remember { SampleData.pendingApprovals.toMutableStateList() }
    var selectedNews by remember { mutableStateOf<NewsItem?>(null) }

    // Show latest news on login (first time)
    var showNewsOnLogin by remember { mutableStateOf(true) }

    // Search function
    fun performSearch(query: String) {
        if (query.length < 2) {
            searchResults = emptyList()
            showSearchResults = false
            return
        }
        val q = query.lowercase().trim()
        val results = mutableListOf<Pair<String, String>>()

        // Search students
        SampleData.students.filter {
            it.name.lowercase().contains(q) || it.id.contains(q)
        }.forEach { results.add(it.name to "Student — ${it.batch}") }

        // Search courses
        SampleData.courses.filter {
            it.name.lowercase().contains(q) || it.code.lowercase().contains(q) || it.faculty.lowercase().contains(q)
        }.forEach { results.add(it.name to "Course — ${it.code}") }

        // Search news
        SampleData.newsItems.filter {
            it.title.lowercase().contains(q) || it.category.lowercase().contains(q)
        }.forEach { results.add(it.title to "News — ${it.category}") }

        // Search navigation
        SampleData.searchIndex.entries.filter { it.key.contains(q) }.forEach {
            results.add("Go to: ${it.key.replaceFirstChar { c -> c.uppercase() }}" to "Navigation")
        }

        searchResults = results.distinctBy { it.first }.take(6)
        showSearchResults = results.isNotEmpty()
    }

    fun handleSearchSelect(result: Pair<String, String>) {
        searchQuery = ""
        showSearchResults = false
        val dest = result.second
        when {
            dest.contains("attendance_marking") || result.first.lowercase().contains("attendance") -> onNavigateToAttendance()
            dest.contains("timetable") || result.first.lowercase().contains("schedule") || result.first.lowercase().contains("timetable") -> onNavigateToTimetable()
            dest.contains("settings") || result.first.lowercase().contains("settings") -> onNavigateToSettings()
            dest.contains("manage_courses") || result.first.lowercase().contains("course") || result.first.lowercase().contains("batch") -> onNavigateToCourses()
            dest.contains("admin_profile") || result.first.lowercase().contains("profile") -> onNavigateToProfile()
        }
    }

    // News on login dialog
    if (showNewsOnLogin && SampleData.newsItems.isNotEmpty()) {
        val latestNews = SampleData.newsItems.first()
        Dialog(onDismissRequest = { showNewsOnLogin = false }) {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest)
            ) {
                Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Default.Campaign, contentDescription = null, tint = Primary, modifier = Modifier.size(24.dp))
                        Text("Latest News", fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = Primary)
                    }
                    Surface(shape = RoundedCornerShape(99.dp), color = SecondaryContainer) {
                        Text(latestNews.category, modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp), fontSize = 10.sp, color = OnSecondaryContainer, fontWeight = FontWeight.Bold)
                    }
                    Text(latestNews.title, fontSize = 16.sp, fontWeight = FontWeight.Bold, lineHeight = 20.sp)
                    Text(latestNews.body, fontSize = 13.sp, color = OnSurfaceVariant, lineHeight = 18.sp)
                    Text(latestNews.timeAgo, fontSize = 11.sp, color = Outline)
                    Button(
                        onClick = { showNewsOnLogin = false },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Primary),
                        shape = RoundedCornerShape(12.dp)
                    ) { Text("Dismiss") }
                }
            }
        }
    }

    // News detail dialog
    selectedNews?.let { news ->
        Dialog(onDismissRequest = { selectedNews = null }) {
            Card(shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest)) {
                Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Surface(shape = RoundedCornerShape(99.dp), color = SecondaryContainer) {
                        Text(news.category, modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp), fontSize = 10.sp, color = OnSecondaryContainer, fontWeight = FontWeight.Bold)
                    }
                    Text(news.title, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Text(news.body, fontSize = 13.sp, color = OnSurfaceVariant, lineHeight = 18.sp)
                    Text(news.timeAgo, fontSize = 11.sp, color = Outline)
                    TextButton(onClick = { selectedNews = null }, modifier = Modifier.align(Alignment.End)) {
                        Text("Close", color = Primary)
                    }
                }
            }
        }
    }

    Scaffold(
        bottomBar = {
            AdminBottomNav(
                selected = 0,
                onDashboard = {},
                onSchedule = onNavigateToTimetable,
                onAttendance = onNavigateToAttendance,
                onProfile = onNavigateToProfile
            )
        },
        containerColor = Surface
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState())) {

            // Top bar
            Row(
                modifier = Modifier.fillMaxWidth().background(SurfaceContainerLow).padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Box(modifier = Modifier.size(40.dp).clip(CircleShape).background(PrimaryContainer), contentAlignment = Alignment.Center) {
                        Text("A", color = OnPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                    Text("Dashboard", fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = Primary)
                }
                BadgedBox(badge = { Badge(containerColor = Error) {} }) {
                    Icon(Icons.Default.Notifications, contentDescription = null, tint = OnSurfaceVariant)
                }
            }

            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(20.dp)) {

                // Smart Search Bar
                Column {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = {
                            searchQuery = it
                            performSearch(it)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Search students, courses, navigate...", fontSize = 13.sp, color = Outline) },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Primary) },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { searchQuery = ""; showSearchResults = false }) {
                                    Icon(Icons.Default.Close, contentDescription = null, tint = OnSurfaceVariant, modifier = Modifier.size(18.dp))
                                }
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Primary,
                            unfocusedBorderColor = OutlineVariant,
                            focusedContainerColor = SurfaceContainerLowest,
                            unfocusedContainerColor = SurfaceContainerLowest
                        )
                    )
                    if (showSearchResults) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(bottomStart = 12.dp, bottomEnd = 12.dp),
                            colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest),
                            elevation = CardDefaults.cardElevation(4.dp)
                        ) {
                            Column {
                                searchResults.forEach { result ->
                                    Row(
                                        modifier = Modifier.fillMaxWidth().clickable { handleSearchSelect(result) }.padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        Icon(
                                            when {
                                                result.second.contains("Student") -> Icons.Default.Person
                                                result.second.contains("Course") -> Icons.Default.MenuBook
                                                result.second.contains("News") -> Icons.Default.Article
                                                else -> Icons.Default.ArrowForward
                                            },
                                            contentDescription = null, tint = Primary, modifier = Modifier.size(18.dp)
                                        )
                                        Column {
                                            Text(result.first, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                                            Text(result.second, fontSize = 11.sp, color = OnSurfaceVariant)
                                        }
                                    }
                                    if (searchResults.last() != result) Divider(color = SurfaceContainer, thickness = 0.5.dp)
                                }
                            }
                        }
                    }
                }

                // Overview
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Icon(Icons.Default.BarChart, contentDescription = null, tint = Primary, modifier = Modifier.size(20.dp))
                        Text("Overview", fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = Primary)
                    }
                    Box(
                        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp))
                            .background(Brush.linearGradient(listOf(Primary, PrimaryContainer))).padding(20.dp)
                    ) {
                        Column {
                            Text("TOTAL STUDENTS", fontSize = 10.sp, color = OnPrimary.copy(0.8f), letterSpacing = 1.sp)
                            Spacer(Modifier.height(4.dp))
                            Text("${SampleData.students.size}", fontSize = 40.sp, fontWeight = FontWeight.ExtraBold, color = OnPrimary)
                            Text("+2.4% vs last semester", fontSize = 11.sp, color = OnPrimary.copy(0.8f))
                        }
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Card(modifier = Modifier.weight(1f), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest)) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("COURSES", fontSize = 9.sp, color = OnSurfaceVariant, letterSpacing = 1.sp)
                                Spacer(Modifier.height(8.dp))
                                Text("${SampleData.courses.size}", fontSize = 26.sp, fontWeight = FontWeight.ExtraBold, color = Primary)
                                Text("Active", fontSize = 10.sp, color = TertiaryContainer)
                            }
                        }
                        Card(modifier = Modifier.weight(1f), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest)) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("BATCHES", fontSize = 9.sp, color = OnSurfaceVariant, letterSpacing = 1.sp)
                                Spacer(Modifier.height(8.dp))
                                Text("${SampleData.batches.size}", fontSize = 26.sp, fontWeight = FontWeight.ExtraBold, color = Primary)
                                Text("Running", fontSize = 10.sp, color = TertiaryContainer)
                            }
                        }
                    }
                }

                // Quick Navigation Tools
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Icon(Icons.Default.Bolt, contentDescription = null, tint = Primary, modifier = Modifier.size(20.dp))
                        Text("Admin Tools", fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = Primary)
                    }
                    val tools = listOf(
                        Triple(Icons.Default.FactCheck, "Mark Attendance", onNavigateToAttendance),
                        Triple(Icons.Default.Assessment, "Reports", onNavigateToAttendanceReport),
                        Triple(Icons.Default.CalendarToday, "Timetable", onNavigateToTimetable),
                        Triple(Icons.Default.MenuBook, "Courses", onNavigateToCourses),
                        Triple(Icons.Default.Settings, "Settings", onNavigateToSettings),
                        Triple(Icons.Default.Person, "Profile", onNavigateToProfile)
                    )
                    val rows = tools.chunked(2)
                    rows.forEach { row ->
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            row.forEach { (icon, label, action) ->
                                Card(
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(16.dp),
                                    colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest),
                                    onClick = action
                                ) {
                                    Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                        Box(modifier = Modifier.size(36.dp).clip(CircleShape).background(PrimaryFixed), contentAlignment = Alignment.Center) {
                                            Icon(icon, contentDescription = null, tint = Primary, modifier = Modifier.size(18.dp))
                                        }
                                        Text(label, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                                    }
                                }
                            }
                        }
                    }
                }

                // Pending Approvals
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Icon(Icons.Default.PendingActions, contentDescription = null, tint = Primary, modifier = Modifier.size(20.dp))
                            Text("Pending Approvals", fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = Primary)
                        }
                        Surface(shape = RoundedCornerShape(99.dp), color = ErrorContainer) {
                            Text("${approvals.size}", modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp), fontSize = 11.sp, color = OnErrorContainer, fontWeight = FontWeight.Bold)
                        }
                    }
                    if (approvals.isEmpty()) {
                        Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest)) {
                            Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.Center) {
                                Text("No pending approvals", fontSize = 13.sp, color = OnSurfaceVariant)
                            }
                        }
                    }
                    approvals.toList().forEach { approval ->
                        Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest)) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                    Box(modifier = Modifier.size(40.dp).clip(CircleShape).background(SecondaryContainer), contentAlignment = Alignment.Center) {
                                        Icon(Icons.Default.Description, contentDescription = null, tint = OnSecondaryContainer, modifier = Modifier.size(18.dp))
                                    }
                                    Column {
                                        Text(approval.title, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                                        Text(approval.subtitle, fontSize = 11.sp, color = OnSurfaceVariant)
                                    }
                                }
                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    IconButton(
                                        onClick = { approvals.remove(approval); SampleData.pendingApprovals.remove(approval) },
                                        modifier = Modifier.size(32.dp).clip(CircleShape).background(SurfaceContainerLow)
                                    ) { Icon(Icons.Default.Close, contentDescription = null, tint = Error, modifier = Modifier.size(16.dp)) }
                                    IconButton(
                                        onClick = { approvals.remove(approval); SampleData.pendingApprovals.remove(approval) },
                                        modifier = Modifier.size(32.dp).clip(CircleShape).background(Primary)
                                    ) { Icon(Icons.Default.Check, contentDescription = null, tint = OnPrimary, modifier = Modifier.size(16.dp)) }
                                }
                            }
                        }
                    }
                }

                // Latest News section
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Icon(Icons.Default.Campaign, contentDescription = null, tint = Primary, modifier = Modifier.size(20.dp))
                        Text("Latest News", fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = Primary)
                    }
                    SampleData.newsItems.forEach { news ->
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest),
                            onClick = { selectedNews = news }
                        ) {
                            Row(modifier = Modifier.padding(12.dp), horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                Box(modifier = Modifier.size(48.dp).clip(RoundedCornerShape(10.dp)).background(SecondaryContainer), contentAlignment = Alignment.Center) {
                                    Icon(Icons.Default.Article, contentDescription = null, tint = OnSecondaryContainer)
                                }
                                Column(modifier = Modifier.weight(1f)) {
                                    Surface(shape = RoundedCornerShape(99.dp), color = SecondaryContainer) {
                                        Text(news.category, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp), fontSize = 9.sp, color = OnSecondaryContainer, fontWeight = FontWeight.Bold)
                                    }
                                    Spacer(Modifier.height(3.dp))
                                    Text(news.title, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, lineHeight = 16.sp)
                                    Text(news.timeAgo, fontSize = 10.sp, color = OnSurfaceVariant)
                                }
                                Icon(Icons.Default.ChevronRight, contentDescription = null, tint = OnSurfaceVariant, modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                }

                Spacer(Modifier.height(8.dp))
            }
        }
    }
}