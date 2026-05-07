package com.ims.app.data

data class Student(
    val id: String,
    val name: String,
    val batch: String,
    val program: String = "CSE"
)

data class AttendanceRecord(
    val studentId: String,
    var isPresent: Boolean,
    var note: String = ""
)

data class TimetableSlot(
    val id: Int,
    val day: String,
    val startHour: Int,
    val durationHours: Int,
    val subjectCode: String,
    val subjectName: String,
    val room: String,
    val type: String
)

data class TimetableDraft(
    val id: String,
    var name: String,
    var batch: String,
    var program: String,
    var effectiveFrom: String,
    var status: String,
    val slots: MutableList<TimetableSlot>
)

data class Course(
    val code: String,
    val name: String,
    val faculty: String,
    val batch: String,
    val credits: Int,
    val program: String = "CSE"
)

data class NewsItem(
    val id: String,
    val title: String,
    val category: String,
    val timeAgo: String,
    val body: String = ""
)

data class PendingApproval(
    val id: String,
    val title: String,
    val subtitle: String,
    val type: String
)
