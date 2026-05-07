package com.ims.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DragIndicator
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Room
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.ims.app.data.Course
import com.ims.app.data.SampleData
import com.ims.app.data.TimetableDraft
import com.ims.app.data.TimetableSlot
import com.ims.app.ui.theme.Error
import com.ims.app.ui.theme.ErrorContainer
import com.ims.app.ui.theme.OnErrorContainer
import com.ims.app.ui.theme.OnPrimary
import com.ims.app.ui.theme.OnPrimaryContainer
import com.ims.app.ui.theme.OnPrimaryFixed
import com.ims.app.ui.theme.OnSecondaryContainer
import com.ims.app.ui.theme.OnSurface
import com.ims.app.ui.theme.OnSurfaceVariant
import com.ims.app.ui.theme.OnTertiaryContainer
import com.ims.app.ui.theme.Outline
import com.ims.app.ui.theme.OutlineVariant
import com.ims.app.ui.theme.Primary
import com.ims.app.ui.theme.PrimaryContainer
import com.ims.app.ui.theme.PrimaryFixed
import com.ims.app.ui.theme.SecondaryContainer
import com.ims.app.ui.theme.Surface
import com.ims.app.ui.theme.SurfaceContainer
import com.ims.app.ui.theme.SurfaceContainerLow
import com.ims.app.ui.theme.SurfaceContainerLowest
import com.ims.app.ui.theme.TertiaryContainer
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun TimetableScreen(
    onNavigateToDashboard: () -> Unit,
    onNavigateToAttendance: () -> Unit,
    onNavigateToProfile: () -> Unit,
    isAdmin: Boolean = true
) {
    val drafts = remember { SampleData.timetableDrafts.toMutableStateList() }
    var selectedDraftId by remember { mutableStateOf(SampleData.activeTimetableDraftId) }
    var showCreateDraftDialog by remember { mutableStateOf(false) }
    val activeDraft = drafts.firstOrNull { it.id == selectedDraftId } ?: drafts.first()
    val canEditDraft = isAdmin && activeDraft.status != "Published"
    val studentProfile = SampleData.students.firstOrNull { it.id == "2024111027" } ?: SampleData.students.first()
    val studentPublishedDraft = drafts.firstOrNull {
        it.status == "Published" && it.batch == studentProfile.batch && it.program == studentProfile.program
    }
    val slots = remember(selectedDraftId, isAdmin) {
        if (isAdmin) activeDraft.slots.map { it.copy() }.toMutableStateList()
        else (studentPublishedDraft?.slots ?: SampleData.timetableSlots).map { it.copy() }.toMutableStateList()
    }
    val availableCourses = SampleData.courses.filter { it.batch == activeDraft.batch && it.program == activeDraft.program }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var showAddDialog by remember { mutableStateOf(false) }
    var selectedDay by remember { mutableStateOf("Mon") }
    var selectedHour by remember { mutableStateOf(8) }
    var editingSlot by remember { mutableStateOf<TimetableSlot?>(null) }

    var draggedCourse by remember { mutableStateOf<Course?>(null) }
    var dragOffset by remember { mutableStateOf(Offset.Zero) }
    val cellBounds = remember { mutableStateMapOf<Pair<String, Int>, Rect>() }

    val days = listOf("Mon", "Tue", "Wed", "Thu", "Fri")
    val hours = (8..15).toList()
    val conflicts = remember(slots.toList()) { findConflictingSlotIds(slots) }
    val subjectOverload = remember(slots.toList()) {
        slots.groupBy { it.subjectCode }.filterValues { it.size > 3 }
    }
    val facultyOverload = remember(slots.toList()) {
        slots.mapNotNull { slot ->
            SampleData.courses.firstOrNull { courseShortCode(it) == slot.subjectCode }?.faculty
        }.groupingBy { it }.eachCount().filterValues { it > 4 }
    }
    val publishBlockReason = when {
        conflicts.isNotEmpty() -> "Resolve all timetable conflicts before publishing."
        subjectOverload.isNotEmpty() -> "Reduce subject weekly load before publishing."
        facultyOverload.isNotEmpty() -> "Reduce employee workload before publishing."
        else -> null
    }

    fun saveDraftChanges() {
        if (!canEditDraft) return
        activeDraft.slots.clear()
        activeDraft.slots.addAll(slots.map { it.copy() })
        activeDraft.status = if (activeDraft.status == "Published") "Published" else "Draft"
    }

    fun persistAdd(slot: TimetableSlot) {
        slots.add(slot)
        saveDraftChanges()
        val conflict = hasConflict(slot, slots.filter { it.id != slot.id })
        scope.launch {
            snackbarHostState.showSnackbar(
                if (conflict) "Conflict: ${slot.subjectCode} overlaps another course in this slot."
                else "${slot.subjectCode} scheduled for ${slot.day} ${slot.startHour}:00."
            )
        }
    }

    fun persistReplace(old: TimetableSlot, new: TimetableSlot) {
        val index = slots.indexOfFirst { it.id == old.id }
        if (index >= 0) slots[index] = new
        saveDraftChanges()
        val conflict = hasConflict(new, slots.filter { it.id != new.id })
        scope.launch {
            snackbarHostState.showSnackbar(
                if (conflict) "Conflict: ${new.subjectCode} overlaps another course in this slot."
                else "${new.subjectCode} updated."
            )
        }
    }

    fun persistDelete(slot: TimetableSlot) {
        slots.removeAll { it.id == slot.id }
        saveDraftChanges()
        scope.launch { snackbarHostState.showSnackbar("${slot.subjectCode} removed from timetable.") }
    }

    fun publishActiveDraft() {
        saveDraftChanges()
        if (publishBlockReason != null) {
            scope.launch { snackbarHostState.showSnackbar(publishBlockReason) }
            return
        }
        drafts.filter { it.batch == activeDraft.batch && it.program == activeDraft.program }
            .forEach { it.status = if (it.id == selectedDraftId) "Published" else "Draft" }
        activeDraft.status = "Published"
        if (activeDraft.batch == "UG2k24" && activeDraft.program == "CSE") {
            SampleData.timetableSlots.clear()
            SampleData.timetableSlots.addAll(slots.map { it.copy() })
        }
        scope.launch {
            snackbarHostState.showSnackbar("${activeDraft.name} published for ${activeDraft.program} ${activeDraft.batch}.")
        }
    }

    fun deleteActiveDraft() {
        if (activeDraft.status == "Published") {
            scope.launch { snackbarHostState.showSnackbar("Published timetables cannot be deleted.") }
            return
        }
        if (drafts.size <= 1) {
            scope.launch { snackbarHostState.showSnackbar("Keep at least one timetable draft.") }
            return
        }
        val removedName = activeDraft.name
        drafts.removeAll { it.id == selectedDraftId }
        SampleData.timetableDrafts.removeAll { it.id == selectedDraftId }
        selectedDraftId = drafts.first().id
        SampleData.activeTimetableDraftId = selectedDraftId
        scope.launch { snackbarHostState.showSnackbar("$removedName deleted.") }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            if (isAdmin) {
                AdminBottomNav(
                    selected = 1,
                    onDashboard = onNavigateToDashboard,
                    onSchedule = {},
                    onAttendance = onNavigateToAttendance,
                    onProfile = onNavigateToProfile
                )
            } else {
                StudentBottomNav(
                    selected = 1,
                    onDashboard = onNavigateToDashboard,
                    onSchedule = {},
                    onAttendance = onNavigateToAttendance,
                    onProfile = onNavigateToProfile
                )
            }
        },
        containerColor = Surface
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            Column(modifier = Modifier.fillMaxSize()) {
                AdminTimetableTopBar()

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    TimetableHeader(
                        isAdmin = isAdmin,
                        canEditDraft = canEditDraft,
                        totalSlots = slots.size,
                        conflictCount = conflicts.size,
                        onAdd = {
                            if (isAdmin && canEditDraft) {
                                selectedDay = "Mon"
                                selectedHour = 8
                                showAddDialog = true
                            }
                        }
                    )

                    if (isAdmin) {
                        TimetableDraftManager(
                            drafts = drafts,
                            selectedDraftId = selectedDraftId,
                            onSelectDraft = { draftId ->
                                saveDraftChanges()
                                selectedDraftId = draftId
                                SampleData.activeTimetableDraftId = draftId
                            },
                            onCreateDraft = {
                                saveDraftChanges()
                                showCreateDraftDialog = true
                            },
                            onSaveDraft = {
                                saveDraftChanges()
                                scope.launch { snackbarHostState.showSnackbar("${activeDraft.name} saved as ${activeDraft.status.lowercase()}.") }
                            },
                            onPublishDraft = ::publishActiveDraft,
                            onDeleteDraft = ::deleteActiveDraft,
                            publishBlockReason = publishBlockReason,
                            canEditDraft = canEditDraft
                        )
                    }

                    if (isAdmin && !canEditDraft) {
                        AlertBanner(
                            title = "Published timetable locked",
                            message = "Create a new draft to prepare changes. Published timetables cannot be edited or deleted.",
                            isError = false
                        )
                    }

                    if (isAdmin && conflicts.isNotEmpty()) {
                        AlertBanner(
                            title = "Class timing conflict",
                            message = "${conflicts.size} course card(s) overlap. Move or edit the highlighted slots.",
                            isError = true
                        )
                    }

                    if (isAdmin && subjectOverload.isNotEmpty()) {
                        AlertBanner(
                            title = "Subject limit exceeded",
                            message = subjectOverload.entries.joinToString { "${it.key}: ${it.value.size}/3 weekly slots" },
                            isError = false
                        )
                    }

                    if (isAdmin && facultyOverload.isNotEmpty()) {
                        AlertBanner(
                            title = "Employee workload alert",
                            message = facultyOverload.entries.joinToString { "${it.key}: ${it.value}/4 weekly slots" },
                            isError = false
                        )
                    }

                    val studentTags = remember { mutableStateMapOf<Int, String>() }
                    var studentTagSlot by remember { mutableStateOf<TimetableSlot?>(null) }

                    TimetableGrid(
                        days = days,
                        hours = hours,
                        slots = slots,
                        conflicts = conflicts,
                        editable = canEditDraft,
                        isAdmin = isAdmin,
                        studentTags = studentTags,
                        onCellPositioned = { day, hour, rect -> cellBounds[day to hour] = rect },
                        onEmptyCellClick = { day, hour ->
                            selectedDay = day
                            selectedHour = hour
                            showAddDialog = true
                        },
                        onEdit = { editingSlot = it },
                        onDelete = ::persistDelete,
                        onStudentSlotClick = { studentTagSlot = it }
                    )

                    if (!isAdmin && showAddDialog) {
                        StudentAddDialog(
                            day = selectedDay,
                            hour = selectedHour,
                            onDismiss = { showAddDialog = false },
                            onConfirm = { title, type ->
                                val slot = TimetableSlot(
                                    id = -(System.currentTimeMillis() % 100000).toInt(),
                                    day = selectedDay,
                                    startHour = selectedHour,
                                    durationHours = 1,
                                    subjectCode = type,
                                    subjectName = title,
                                    room = "",
                                    type = type
                                )
                                slots.add(slot)
                                showAddDialog = false
                            }
                        )
                    }

                    if (!isAdmin && studentTagSlot != null) {
                        StudentTagDialog(
                            slot = studentTagSlot!!,
                            currentTag = studentTags[studentTagSlot!!.id],
                            onDismiss = { studentTagSlot = null },
                            onSaveTag = { tag ->
                                if (tag.isEmpty()) studentTags.remove(studentTagSlot!!.id)
                                else studentTags[studentTagSlot!!.id] = tag
                                studentTagSlot = null
                            }
                        )
                    }

                    if (canEditDraft) {
                        CourseDragTray(
                            courses = availableCourses,
                            onDropCourse = { course, dropPoint ->
                                val target = cellBounds.entries.firstOrNull { it.value.contains(dropPoint) }?.key
                                if (target == null) {
                                    scope.launch { snackbarHostState.showSnackbar("Drop a course on a timetable slot.") }
                                } else {
                                    val slot = course.toTimetableSlot(day = target.first, hour = target.second)
                                    persistAdd(slot)
                                }
                            },
                            onDragState = { course, offset ->
                                draggedCourse = course
                                dragOffset = offset
                            }
                        )
                    }

                    Spacer(Modifier.height(8.dp))
                }
            }

            draggedCourse?.let { course ->
                DragPreview(
                    course = course,
                    modifier = Modifier
                        .offset { IntOffset((dragOffset.x - 92.dp.value).roundToInt(), (dragOffset.y - 32.dp.value).roundToInt()) }
                        .zIndex(10f)
                )
            }
        }
    }

    if (isAdmin && canEditDraft && showAddDialog) {
        AddSlotDialog(
            day = selectedDay,
            hour = selectedHour,
            courses = availableCourses,
            existing = null,
            onDismiss = { showAddDialog = false },
            onConfirm = { slot ->
                persistAdd(slot)
                showAddDialog = false
            }
        )
    }

    if (isAdmin && canEditDraft) editingSlot?.let { slot ->
        AddSlotDialog(
            day = slot.day,
            hour = slot.startHour,
            courses = availableCourses,
            existing = slot,
            onDismiss = { editingSlot = null },
            onConfirm = { updated ->
                persistReplace(slot, updated.copy(id = slot.id))
                editingSlot = null
            }
        )
    }

    if (isAdmin && showCreateDraftDialog) {
        CreateDraftDialog(
            onDismiss = { showCreateDraftDialog = false },
            onConfirm = { name, effectiveFrom, batch, program ->
                val draft = TimetableDraft(
                    id = "draft_${System.currentTimeMillis()}",
                    name = name,
                    batch = batch,
                    program = program,
                    effectiveFrom = effectiveFrom,
                    status = "Draft",
                    slots = if (batch == activeDraft.batch && program == activeDraft.program) {
                        slots.map { it.copy(id = it.id + 1000 + drafts.size) }.toMutableList()
                    } else {
                        mutableListOf()
                    }
                )
                drafts.add(draft)
                SampleData.timetableDrafts.add(draft)
                selectedDraftId = draft.id
                SampleData.activeTimetableDraftId = draft.id
                showCreateDraftDialog = false
                scope.launch { snackbarHostState.showSnackbar("${draft.name} created for ${draft.effectiveFrom}.") }
            }
        )
    }
}

@Composable
private fun AdminTimetableTopBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(SurfaceContainerLow)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Box(
                modifier = Modifier.size(36.dp).clip(CircleShape).background(SecondaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Text("T", color = OnSecondaryContainer, fontWeight = FontWeight.Bold)
            }
            Text("IMS", fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = Primary)
        }
        Icon(Icons.Default.Notifications, contentDescription = null, tint = OnSurfaceVariant)
    }
}

@Composable
private fun TimetableHeader(isAdmin: Boolean, canEditDraft: Boolean, totalSlots: Int, conflictCount: Int, onAdd: () -> Unit) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest)
    ) {
        Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(if (isAdmin) "Timetable Editor" else "Weekly Timetable", fontSize = 26.sp, fontWeight = FontWeight.ExtraBold, color = Primary)
                    Text(
                        if (isAdmin) "Drag a course into a slot, edit scheduled classes, and resolve highlighted conflicts."
                        else "View upcoming classes, rooms, and session types for the active cohort.",
                        fontSize = 13.sp,
                        color = OnSurfaceVariant
                    )
                }
                if (isAdmin) {
                    Button(
                        onClick = onAdd,
                        shape = RoundedCornerShape(10.dp),
                        enabled = canEditDraft,
                        colors = ButtonDefaults.buttonColors(containerColor = Primary),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Add", fontSize = 12.sp)
                    }
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                HeaderPill(Icons.Default.Group, "ACTIVE COHORT", "UG2k24", SecondaryContainer, OnSecondaryContainer)
                HeaderPill(Icons.Default.CalendarToday, "SLOTS", "$totalSlots planned", PrimaryContainer, OnPrimary)
                HeaderPill(
                    if (isAdmin) Icons.Default.Warning else Icons.Default.Schedule,
                    if (isAdmin) "CONFLICTS" else "WEEK",
                    if (isAdmin) "$conflictCount active" else "Mon-Fri",
                    if (conflictCount > 0 && isAdmin) ErrorContainer else TertiaryContainer,
                    if (conflictCount > 0 && isAdmin) OnErrorContainer else OnTertiaryContainer
                )
            }
        }
    }
}

@Composable
private fun TimetableDraftManager(
    drafts: List<TimetableDraft>,
    selectedDraftId: String,
    onSelectDraft: (String) -> Unit,
    onCreateDraft: () -> Unit,
    onSaveDraft: () -> Unit,
    onPublishDraft: () -> Unit,
    onDeleteDraft: () -> Unit,
    publishBlockReason: String?,
    canEditDraft: Boolean
) {
    val selectedDraft = drafts.firstOrNull { it.id == selectedDraftId } ?: drafts.first()
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Advance Timetables", fontSize = 15.sp, fontWeight = FontWeight.ExtraBold, color = Primary)
                    Text("${selectedDraft.program} ${selectedDraft.batch}", fontSize = 11.sp, color = Primary, fontWeight = FontWeight.Bold)
                    Text("${selectedDraft.effectiveFrom} | ${selectedDraft.status}", fontSize = 11.sp, color = OnSurfaceVariant)
                }
                Button(
                    onClick = onCreateDraft,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryContainer),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 8.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(15.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("New", fontSize = 11.sp)
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                drafts.forEach { draft ->
                    val selected = draft.id == selectedDraftId
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (selected) PrimaryFixed else SurfaceContainerLow)
                            .border(1.dp, if (selected) Primary else OutlineVariant, RoundedCornerShape(10.dp))
                            .clickable { onSelectDraft(draft.id) }
                            .padding(10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(draft.name, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = OnSurface)
                            Text("${draft.program} ${draft.batch} | ${draft.effectiveFrom}", fontSize = 10.sp, color = OnSurfaceVariant)
                        }
                        Surface(
                            shape = RoundedCornerShape(99.dp),
                            color = if (draft.status == "Published") TertiaryContainer else SurfaceContainer
                        ) {
                            Text(
                                draft.status,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                fontSize = 9.sp,
                                color = if (draft.status == "Published") OnTertiaryContainer else OnSurfaceVariant,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            if (publishBlockReason != null) {
                Text(publishBlockReason, fontSize = 11.sp, color = Error, fontWeight = FontWeight.SemiBold)
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = onSaveDraft,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    enabled = canEditDraft,
                    colors = ButtonDefaults.buttonColors(containerColor = SecondaryContainer, contentColor = OnSecondaryContainer)
                ) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(15.dp))
                    Spacer(Modifier.width(5.dp))
                    Text("Save Draft", fontSize = 11.sp)
                }
                Button(
                    onClick = onPublishDraft,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    enabled = canEditDraft,
                    colors = ButtonDefaults.buttonColors(containerColor = TertiaryContainer, contentColor = OnTertiaryContainer)
                ) {
                    Icon(Icons.Default.Schedule, contentDescription = null, modifier = Modifier.size(15.dp))
                    Spacer(Modifier.width(5.dp))
                    Text("Publish", fontSize = 11.sp)
                }
                Button(
                    onClick = onDeleteDraft,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    enabled = selectedDraft.status != "Published",
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ErrorContainer,
                        contentColor = OnErrorContainer,
                        disabledContainerColor = SurfaceContainer,
                        disabledContentColor = Outline
                    )
                ) {
                    Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(15.dp))
                    Spacer(Modifier.width(5.dp))
                    Text("Delete", fontSize = 11.sp)
                }
            }
        }
    }
}

@Composable
private fun HeaderPill(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    background: Color,
    color: Color
) {
    Row(
        modifier = Modifier.clip(RoundedCornerShape(12.dp)).background(background.copy(alpha = 0.9f)).padding(horizontal = 10.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(15.dp))
        Column {
            Text(label, fontSize = 8.sp, color = color.copy(alpha = 0.8f), fontWeight = FontWeight.Bold)
            Text(value, fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, color = color)
        }
    }
}

@Composable
private fun AlertBanner(title: String, message: String, isError: Boolean) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = if (isError) ErrorContainer else PrimaryFixed)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(14.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.Warning, contentDescription = null, tint = if (isError) Error else Primary)
            Column {
                Text(title, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = if (isError) OnErrorContainer else OnPrimaryFixed)
                Text(message, fontSize = 11.sp, color = if (isError) OnErrorContainer else OnPrimaryFixed, lineHeight = 15.sp)
            }
        }
    }
}

@Composable
private fun TimetableGrid(
    days: List<String>,
    hours: List<Int>,
    slots: List<TimetableSlot>,
    conflicts: Set<Int>,
    editable: Boolean,
    isAdmin: Boolean,
    studentTags: Map<Int, String>,
    onCellPositioned: (String, Int, Rect) -> Unit,
    onEmptyCellClick: (String, Int) -> Unit,
    onEdit: (TimetableSlot) -> Unit,
    onDelete: (TimetableSlot) -> Unit,
    onStudentSlotClick: (TimetableSlot) -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest)
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Row {
                Box(modifier = Modifier.width(48.dp))
                days.forEach { day ->
                    Box(modifier = Modifier.weight(1f).padding(vertical = 6.dp), contentAlignment = Alignment.Center) {
                        Text(day, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = OnSurface)
                    }
                }
            }
            Divider(color = SurfaceContainer, thickness = 1.dp)

            hours.forEach { hour ->
                Row(
                    modifier = Modifier.fillMaxWidth().height(82.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Box(modifier = Modifier.width(48.dp).fillMaxHeight(), contentAlignment = Alignment.TopCenter) {
                        Text("${hour}:00", fontSize = 9.sp, color = OnSurfaceVariant, modifier = Modifier.padding(top = 6.dp))
                    }
                    days.forEach { day ->
                        val daySlots = slots.filter { it.day == day && hour in it.startHour until (it.startHour + it.durationHours) }
                        val isStudentEmpty = !isAdmin && daySlots.isEmpty()
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .padding(2.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isStudentEmpty) Color.Transparent else SurfaceContainerLow)
                                .let {
                                    if (isStudentEmpty) it 
                                    else it.border(1.dp, OutlineVariant.copy(alpha = 0.35f), RoundedCornerShape(8.dp))
                                }
                                .onGloballyPositioned { onCellPositioned(day, hour, it.boundsInRoot()) }
                                .clickable(enabled = (editable && daySlots.isEmpty()) || (!isAdmin && daySlots.isEmpty())) { onEmptyCellClick(day, hour) }
                        ) {
                            if (daySlots.isEmpty()) {
                                if (isAdmin && editable) {
                                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                        Icon(Icons.Default.Add, contentDescription = null, tint = OutlineVariant, modifier = Modifier.size(14.dp))
                                    }
                                } else if (!isAdmin) {
                                    // empty transparent click target for students to add items
                                }
                            } else {
                                Column(modifier = Modifier.fillMaxSize().padding(3.dp), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                                    daySlots.take(2).forEach { slot ->
                                        SlotCard(
                                            slot = slot,
                                            isConflict = slot.id in conflicts,
                                            editable = editable || (!isAdmin && slot.id < 0),
                                            studentTag = studentTags[slot.id],
                                            modifier = Modifier.weight(1f).clickable {
                                                if (editable) onEdit(slot)
                                                else if (!isAdmin && slot.id >= 0) onStudentSlotClick(slot)
                                            },
                                            onEdit = { onEdit(slot) },
                                            onDelete = { onDelete(slot) }
                                        )
                                    }
                                    if (daySlots.size > 2) {
                                        Text("+${daySlots.size - 2} more", fontSize = 8.sp, color = Error, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
                Divider(color = SurfaceContainer.copy(alpha = 0.5f), thickness = 0.5.dp)
            }
        }
    }
}

@Composable
private fun SlotCard(
    slot: TimetableSlot,
    isConflict: Boolean,
    editable: Boolean,
    studentTag: String?,
    modifier: Modifier = Modifier,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val isSkip = studentTag?.contains("skip", true) == true
    val isImportant = studentTag?.contains("important", true) == true
    val isActivity = studentTag?.contains("activity", true) == true

    val bg = when {
        isImportant -> ErrorContainer
        isSkip -> Color(0xFFC8E6C9)
        isActivity -> Color(0xFFFFCC80)
        slot.id < 0 -> TertiaryContainer
        isConflict -> ErrorContainer
        else -> PrimaryFixed
    }

    val textColor = when {
        isImportant -> OnErrorContainer
        isSkip -> Color(0xFF1B5E20)
        isActivity -> Color(0xFFE65100)
        slot.id < 0 -> OnTertiaryContainer
        isConflict -> OnErrorContainer
        else -> OnPrimaryFixed
    }

    Box(
        modifier = modifier.fillMaxWidth().clip(RoundedCornerShape(6.dp)).background(bg).padding(5.dp)
    ) {
        Column(modifier = Modifier.padding(end = if (editable) 28.dp else 0.dp)) {
            Text("${slot.type} | ${slot.subjectCode}", fontSize = 8.sp, color = textColor.copy(0.72f), fontWeight = FontWeight.Bold)
            Text(slot.subjectName, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = textColor, maxLines = 1)
            if (slot.room.isNotBlank()) Text(slot.room, fontSize = 8.sp, color = textColor.copy(0.8f), maxLines = 1)
            if (studentTag != null) {
                Spacer(Modifier.height(2.dp))
                Text(studentTag, fontSize = 8.sp, color = textColor, fontWeight = FontWeight.ExtraBold)
            }
        }
        if (editable) {
            Row(modifier = Modifier.align(Alignment.TopEnd)) {
                if (slot.id >= 0) {
                    IconButton(onClick = onEdit, modifier = Modifier.size(15.dp)) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit", tint = textColor, modifier = Modifier.size(10.dp))
                    }
                }
                IconButton(onClick = onDelete, modifier = Modifier.size(15.dp)) {
                    Icon(Icons.Default.Close, contentDescription = "Delete", tint = textColor, modifier = Modifier.size(10.dp))
                }
            }
        }
    }
}

@Composable
private fun CourseDragTray(
    courses: List<Course>,
    onDropCourse: (Course, Offset) -> Unit,
    onDragState: (Course?, Offset) -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text("Course Pool", fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Text("Long-press and drag any course into a timetable cell.", fontSize = 11.sp, color = OnSurfaceVariant)
            courses.forEach { course ->
                var cardBounds by remember(course.code) { mutableStateOf(Rect.Zero) }
                var currentDropPoint by remember(course.code) { mutableStateOf(Offset.Zero) }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(PrimaryContainer.copy(alpha = 0.09f))
                        .border(1.dp, OutlineVariant.copy(alpha = 0.35f), RoundedCornerShape(10.dp))
                        .onGloballyPositioned { cardBounds = it.boundsInRoot() }
                        .pointerInput(course.code) {
                            detectDragGesturesAfterLongPress(
                                onDragStart = {
                                    currentDropPoint = cardBounds.topLeft + it
                                    onDragState(course, currentDropPoint)
                                },
                                onDrag = { change, dragAmount ->
                                    change.consume()
                                    currentDropPoint += dragAmount
                                    onDragState(course, currentDropPoint)
                                },
                                onDragEnd = {
                                    onDropCourse(course, currentDropPoint)
                                    onDragState(null, Offset.Zero)
                                },
                                onDragCancel = { onDragState(null, Offset.Zero) }
                            )
                        }
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.DragIndicator, contentDescription = null, tint = OnSurfaceVariant, modifier = Modifier.size(18.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("${courseShortCode(course)} | ${course.credits} credits", fontSize = 9.sp, color = OnSurfaceVariant, fontWeight = FontWeight.Bold)
                        Text(course.name, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = OnSurface)
                        Text(course.faculty, fontSize = 11.sp, color = OnSurfaceVariant)
                    }
                    Text("Drag", fontSize = 11.sp, color = Primary, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun DragPreview(course: Course, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.width(190.dp),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = Primary),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Row(modifier = Modifier.padding(10.dp), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.DragIndicator, contentDescription = null, tint = OnPrimary, modifier = Modifier.size(16.dp))
            Column {
                Text(courseShortCode(course), fontSize = 11.sp, color = OnPrimary, fontWeight = FontWeight.ExtraBold)
                Text(course.name, fontSize = 10.sp, color = OnPrimary.copy(alpha = 0.82f), maxLines = 1)
            }
        }
    }
}

@Composable
private fun CreateDraftDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String, String, String) -> Unit
) {
    var name by remember { mutableStateOf("t1") }
    var effectiveFrom by remember { mutableStateOf("May 1, 2026") }
    var batch by remember { mutableStateOf(SampleData.batches.first()) }
    var program by remember { mutableStateOf(SampleData.programs.first()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Create timetable draft", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("This copies the current draft so you can prepare a timetable in advance.", fontSize = 12.sp, color = OnSurfaceVariant)
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Draft name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Primary)
                )
                OutlinedTextField(
                    value = effectiveFrom,
                    onValueChange = { effectiveFrom = it },
                    label = { Text("Effective from") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Primary)
                )
                Text("Batch", fontSize = 11.sp, color = OnSurfaceVariant, fontWeight = FontWeight.SemiBold)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    SampleData.batches.forEach { option ->
                        FilterChip(
                            selected = batch == option,
                            onClick = { batch = option },
                            label = { Text(option, fontSize = 10.sp) }
                        )
                    }
                }
                Text("Program", fontSize = 11.sp, color = OnSurfaceVariant, fontWeight = FontWeight.SemiBold)
                SampleData.programs.chunked(4).forEach { row ->
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        row.forEach { option ->
                            FilterChip(
                                selected = program == option,
                                onClick = { program = option },
                                label = { Text(option, fontSize = 10.sp) }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onConfirm(name.ifBlank { "Future timetable" }, effectiveFrom.ifBlank { "TBA" }, batch, program)
                },
                colors = ButtonDefaults.buttonColors(containerColor = Primary)
            ) { Text("Create") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } },
        containerColor = SurfaceContainerLowest
    )
}

@Composable
private fun AddSlotDialog(
    day: String,
    hour: Int,
    courses: List<Course>,
    existing: TimetableSlot?,
    onDismiss: () -> Unit,
    onConfirm: (TimetableSlot) -> Unit
) {
    if (courses.isEmpty()) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("No courses available", fontWeight = FontWeight.Bold) },
            text = { Text("This batch and program has no course entries in the current sample data.", fontSize = 13.sp, color = OnSurfaceVariant) },
            confirmButton = { TextButton(onClick = onDismiss) { Text("Close") } },
            containerColor = SurfaceContainerLowest
        )
        return
    }

    var selectedCourse by remember(existing) {
        mutableStateOf(
            courses.firstOrNull { courseShortCode(it) == existing?.subjectCode } ?: courses.first()
        )
    }
    var room by remember(existing) { mutableStateOf(existing?.room.orEmpty()) }
    var type by remember(existing) { mutableStateOf(existing?.type ?: "Lecture") }
    var duration by remember(existing) { mutableStateOf(existing?.durationHours ?: 1) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                if (existing == null) "Add class | $day ${hour}:00" else "Edit ${existing.subjectCode}",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Course", fontSize = 11.sp, color = OnSurfaceVariant, fontWeight = FontWeight.SemiBold)
                courses.forEach { course ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (selectedCourse.code == course.code) PrimaryContainer.copy(alpha = 0.16f) else SurfaceContainerLow)
                            .clickable { selectedCourse = course }
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            if (selectedCourse.code == course.code) Icons.Default.CheckCircle else Icons.Default.Person,
                            contentDescription = null,
                            tint = if (selectedCourse.code == course.code) Primary else Outline,
                            modifier = Modifier.size(17.dp)
                        )
                        Column {
                            Text("${courseShortCode(course)} - ${course.name}", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            Text(course.faculty, fontSize = 10.sp, color = OnSurfaceVariant)
                        }
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("Lecture", "Tutorial", "Lab").forEach { option ->
                        FilterChip(selected = type == option, onClick = { type = option }, label = { Text(option, fontSize = 11.sp) })
                    }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(1, 2, 3).forEach { option ->
                        FilterChip(selected = duration == option, onClick = { duration = option }, label = { Text("${option}h", fontSize = 11.sp) })
                    }
                }

                OutlinedTextField(
                    value = room,
                    onValueChange = { room = it },
                    label = { Text("Room") },
                    leadingIcon = { Icon(Icons.Default.Room, contentDescription = null) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Primary)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onConfirm(
                        TimetableSlot(
                            id = existing?.id ?: System.currentTimeMillis().toInt(),
                            day = day,
                            startHour = hour,
                            durationHours = duration,
                            subjectCode = courseShortCode(selectedCourse),
                            subjectName = selectedCourse.name,
                            room = room.ifBlank { "TBA" },
                            type = type
                        )
                    )
                },
                colors = ButtonDefaults.buttonColors(containerColor = Primary)
            ) { Text(if (existing == null) "Add" else "Save") }
        },
        dismissButton = {
            if (existing != null) {
                TextButton(onClick = onDismiss) { Text("Cancel") }
            } else {
                TextButton(onClick = onDismiss) { Text("Cancel") }
            }
        },
        containerColor = SurfaceContainerLowest
    )
}

private fun Course.toTimetableSlot(day: String, hour: Int): TimetableSlot {
    return TimetableSlot(
        id = System.currentTimeMillis().toInt(),
        day = day,
        startHour = hour,
        durationHours = 1,
        subjectCode = courseShortCode(this),
        subjectName = name,
        room = "TBA",
        type = "Lecture"
    )
}

private fun courseShortCode(course: Course): String {
    return when (course.code) {
        "CS101" -> "DSA"
        "CS204" -> "DASS"
        "CS301" -> "MDL"
        "CS205" -> "OS"
        "MA201" -> "P&S"
        else -> course.code
    }
}

private fun hasConflict(candidate: TimetableSlot, others: List<TimetableSlot>): Boolean {
    return others.any { other ->
        other.day == candidate.day &&
            candidate.startHour < other.startHour + other.durationHours &&
            other.startHour < candidate.startHour + candidate.durationHours
    }
}

private fun findConflictingSlotIds(slots: List<TimetableSlot>): Set<Int> {
    val conflicts = mutableSetOf<Int>()
    slots.forEachIndexed { index, first ->
        slots.drop(index + 1).forEach { second ->
            if (hasConflict(first, listOf(second))) {
                conflicts += first.id
                conflicts += second.id
            }
        }
    }
    return conflicts
}

@Composable
private fun StudentAddDialog(
    day: String,
    hour: Int,
    onDismiss: () -> Unit,
    onConfirm: (String, String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("Personal Study") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Personal Item | $day ${hour}:00", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Item Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Primary)
                )
                Text("Type", fontSize = 11.sp, color = OnSurfaceVariant, fontWeight = FontWeight.SemiBold)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("Personal Study", "Alert", "Extra Class").forEach { option ->
                        FilterChip(
                            selected = type == option,
                            onClick = { type = option },
                            label = { Text(option, fontSize = 11.sp) }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(title.ifBlank { "Personal Item" }, type) },
                colors = ButtonDefaults.buttonColors(containerColor = Primary)
            ) { Text("Add") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        },
        containerColor = SurfaceContainerLowest
    )
}

@Composable
private fun StudentTagDialog(
    slot: TimetableSlot,
    currentTag: String?,
    onDismiss: () -> Unit,
    onSaveTag: (String) -> Unit
) {
    var tag by remember { mutableStateOf(currentTag ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Tag ${slot.subjectCode}", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Set a personal alert or tag for this course.", fontSize = 12.sp, color = OnSurfaceVariant)
                OutlinedTextField(
                    value = tag,
                    onValueChange = { tag = it },
                    label = { Text("Custom Tag") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Primary)
                )
                Text("Quick Tags", fontSize = 11.sp, color = OnSurfaceVariant, fontWeight = FontWeight.SemiBold)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("Important to go", "Can skip", "Class Activity").forEach { option ->
                        FilterChip(
                            selected = tag == option,
                            onClick = { tag = option },
                            label = { Text(option, fontSize = 11.sp) }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onSaveTag(tag.trim()) },
                colors = ButtonDefaults.buttonColors(containerColor = Primary)
            ) { Text("Save") }
        },
        dismissButton = {
            if (currentTag != null) {
                TextButton(onClick = { onSaveTag("") }) { Text("Clear Tag", color = Error) }
            } else {
                TextButton(onClick = onDismiss) { Text("Cancel") }
            }
        },
        containerColor = SurfaceContainerLowest
    )
}