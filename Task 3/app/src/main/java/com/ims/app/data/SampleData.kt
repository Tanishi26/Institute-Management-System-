package com.ims.app.data

object SampleData {

    val programs = mutableListOf("CSD", "CHD", "CGD", "CND", "CLD", "ECD", "ECE", "CSE")
    val batches = mutableListOf("UG2k26", "UG2k25", "UG2k24", "UG2k23")

    private val studentNames = listOf(
        "Shravan Kannan", "Tanishi Tyagi", "Aarav Menon", "Meera Nair",
        "Kabir Sharma", "Ira Gupta", "Dev Rao", "Anika Bose",
        "Reyansh Iyer", "Priya Sharma", "Arjun Mehta", "Zoya Khan",
        "Nikhil Verma", "Sara Thomas", "Vihaan Reddy", "Diya Kapoor",
        "Rohan Das", "Aisha Siddiqui", "Kunal Batra", "Naina Roy",
        "Aditya Iyer", "Maya Krishnan", "Omkar Joshi", "Rhea Malhotra",
        "Vivaan Shah", "Tara Banerjee", "Samar Gill", "Avni Kulkarni",
        "Yash Nanda", "Kiara Sethi", "Eshan Bose", "Myra Rao"
    )

    private val courseNames = mapOf(
        "CSD" to listOf("Data and Society", "Applied Statistics", "Data Ethics", "Social Data Mining", "Public Data Systems", "Visual Analytics", "Data Journalism", "Causal Inference", "Data Governance", "Civic Technology", "Data Warehousing", "Survey Systems", "Policy Analytics", "Open Data Platforms", "Research Methods"),
        "CHD" to listOf("Human Centred Design", "Design Research", "Interaction Design", "Usability Engineering", "Product Studio", "Design Systems", "Accessibility Design", "Information Architecture", "Service Design", "UX Evaluation", "Creative Interfaces", "Design Ethics", "Visual Communication", "Prototype Lab", "Behaviour Design"),
        "CGD" to listOf("Computer Graphics", "Game Design", "Animation Systems", "Rendering Engines", "3D Modelling", "Graphics Programming", "AR Interfaces", "Game Physics", "Shader Design", "Level Design", "Visual Effects", "Game AI", "Interactive Storytelling", "Simulation Studio", "Graphics Lab"),
        "CND" to listOf("Computer Networks", "Network Security", "Distributed Systems", "Cloud Infrastructure", "Wireless Networks", "Internet Protocols", "Network Programming", "Edge Computing", "Systems Security", "Routing Algorithms", "IoT Networks", "DevOps Foundations", "Data Centre Design", "Reliable Systems", "Network Lab"),
        "CLD" to listOf("Computational Linguistics", "Natural Language Processing", "Phonetics", "Syntax and Parsing", "Semantics", "Speech Systems", "Corpus Linguistics", "Machine Translation", "Dialogue Systems", "Language Technology", "Information Retrieval", "Text Mining", "Linguistic Annotation", "Speech Lab", "Language Models"),
        "ECD" to listOf("Embedded Controller Design", "Microcontrollers", "IoT Hardware", "Real Time Systems", "Sensor Interfaces", "Digital Electronics", "Embedded C", "Robotics Control", "Hardware Verification", "PCB Design", "Signal Conditioning", "Firmware Studio", "Actuator Systems", "Embedded Linux", "Controller Lab"),
        "ECE" to listOf("Electronic Circuits", "Signals and Systems", "Digital System Design", "Communication Systems", "Analog Electronics", "VLSI Design", "Control Systems", "Electromagnetics", "DSP Foundations", "Semiconductor Devices", "Circuit Lab", "Communication Lab", "Mixed Signal Design", "Microwave Engineering", "Instrumentation"),
        "CSE" to listOf("Data Structures & Algorithms", "Design & Analysis of Software Systems", "Machine Data Learning", "Operating Systems", "Database Systems", "Computer Architecture", "Algorithm Design", "Software Engineering", "Theory of Computation", "Compiler Design", "Artificial Intelligence", "Information Security", "Web Systems", "Mobile Computing", "Parallel Programming")
    )

    private val facultyNames = listOf(
        "Prof. Girish Varma", "Prof. Raghu Reddy", "Prof. Parchuri", "Prof. Karthik V",
        "Prof. Neha Sinha", "Prof. Mira Sen", "Prof. Alok Jain", "Prof. Vikram Rao",
        "Prof. Sanjay Das", "Prof. Kavya Nair", "Prof. Ritu Malhotra", "Prof. Leela Krishnan",
        "Prof. Arvind Kumar", "Prof. Tejas Bodas", "Prof. Nandita Rao"
    )

    val students = mutableListOf<Student>().apply {
        add(Student("2024117003", "Shravan Kannan", "UG2k24", "CSE"))
        add(Student("2024111027", "Tanishi Tyagi", "UG2k24", "CSE"))
        batches.forEachIndexed { batchIndex, batch ->
            repeat(10) { index ->
                val program = programs[(batchIndex * 2 + index) % programs.size]
                val idYear = batch.substring(3, 5)
                add(
                    Student(
                        id = "20${idYear}${(batchIndex + 1)}${(index + 1).toString().padStart(3, '0')}",
                        name = studentNames[(batchIndex * 10 + index) % studentNames.size],
                        batch = batch,
                        program = program
                    )
                )
            }
        }
    }.distinctBy { it.id }.take(40).toMutableList()

    val attendanceRecords = students.associate { student ->
        student.id to AttendanceRecord(
            studentId = student.id,
            isPresent = student.id.last().digitToIntOrNull()?.let { it % 3 != 0 } ?: true
        )
    }.toMutableMap()

    val timetableSlots = mutableListOf(
        TimetableSlot(1, "Mon", 8, 1, "DASS", "Design & Analysis of Software Systems", "Rm 302", "Lecture"),
        TimetableSlot(2, "Mon", 10, 1, "DSA", "Data Structures & Algorithms", "Rm 101", "Lecture"),
        TimetableSlot(3, "Tue", 9, 1, "MDL", "Machine Data Learning", "Rm 204", "Lecture"),
        TimetableSlot(4, "Tue", 10, 1, "OS", "Operating Systems", "Rm 105", "Lecture"),
        TimetableSlot(5, "Tue", 12, 1, "DB", "Database Systems", "Rm 203", "Lecture"),
        TimetableSlot(6, "Wed", 11, 2, "DASS", "DASS Lab", "Lab 1", "Lab"),
        TimetableSlot(7, "Thu", 8, 1, "MDL", "MDL Tutorial", "Rm 302", "Tutorial"),
        TimetableSlot(8, "Fri", 14, 1, "DSA", "DSA Lab", "Lab 2", "Lab")
    )

    val timetableDrafts = mutableListOf(
        TimetableDraft(
            id = "published_apr",
            name = "Published timetable",
            batch = "UG2k24",
            program = "CSE",
            effectiveFrom = "Apr 20, 2026",
            status = "Published",
            slots = timetableSlots.map { it.copy() }.toMutableList()
        ),
        TimetableDraft(
            id = "draft_may",
            name = "May revision draft",
            batch = "UG2k24",
            program = "CSE",
            effectiveFrom = "May 1, 2026",
            status = "Draft",
            slots = timetableSlots.filterNot { it.id == 5 }.map { it.copy(id = it.id + 100) }.toMutableList()
        ),
        TimetableDraft(
            id = "published_ece_25",
            name = "ECE lab rotation",
            batch = "UG2k25",
            program = "ECE",
            effectiveFrom = "Apr 22, 2026",
            status = "Published",
            slots = mutableListOf(
                TimetableSlot(301, "Mon", 9, 1, "ECN", "Electronic Circuits", "ECE 201", "Lecture"),
                TimetableSlot(302, "Tue", 11, 2, "DSD", "Digital System Design Lab", "Lab E2", "Lab"),
                TimetableSlot(303, "Thu", 10, 1, "SIG", "Signals and Systems", "ECE 105", "Lecture")
            )
        )
    )

    var activeTimetableDraftId = "draft_may"

    val courses = batches.flatMapIndexed { batchIndex, batch ->
        (0 until 15).map { index ->
            val program = programs[(batchIndex + index) % programs.size]
            val names = courseNames.getValue(program)
            Course(
                code = "$program-${batch.takeLast(2)}-${(index + 1).toString().padStart(2, '0')}",
                name = names[index % names.size],
                faculty = facultyNames[index % facultyNames.size],
                batch = batch,
                credits = if (index % 4 == 0) 4 else 3,
                program = program
            )
        }
    }.toMutableList()

    val newsItems = mutableListOf(
        NewsItem("1", "Cult Night Results OUT!!!", "Campus", "2 hours ago",
            "The results for Cult Night 2026 have been declared. Check the notice board for details."),
        NewsItem("2", "Talk by Prof. Raghu Reddy on Software Systems", "Academics", "Yesterday",
            "Prof. Raghu Reddy will deliver a talk on modern software systems architecture tomorrow at 3 PM in the auditorium."),
        NewsItem("3", "Summer Internship Drive begins May 1", "Placements", "2 days ago",
            "The placement cell announces the summer internship drive starting May 1. Register on the portal."),
        NewsItem("4", "Library extended hours during exams", "Campus", "3 days ago",
            "The central library will remain open 24x7 during the examination period from April 25 to May 15.")
    )

    val pendingApprovals = mutableListOf(
        PendingApproval("1", "Curriculum Revision", "DSA", "curriculum"),
        PendingApproval("2", "Class Timing Change", "Prof. Raghu Reddy", "timing"),
        PendingApproval("3", "New Elective Proposal", "Prof. Girish Varma", "elective"),
        PendingApproval("4", "Lab Booking Request", "CS Lab 3", "lab")
    )

    // Settings
    var gradingSystem = "10-point GPA"
    var country = "India"
    var currency = "INR (₹)"
    var timeZone = "IST (UTC+5:30)"
    var language = "English"
    var autoUniqueIds = true
    var smsEnabled = true

    var currentRole: String = "admin"

    // Search index — maps keywords to destinations
    val searchIndex = mapOf(
        "attendance" to "attendance_marking",
        "mark attendance" to "attendance_marking",
        "present" to "attendance_marking",
        "absent" to "attendance_marking",
        "timetable" to "timetable",
        "schedule" to "timetable",
        "time table" to "timetable",
        "class" to "timetable",
        "settings" to "settings",
        "config" to "settings",
        "language" to "settings",
        "timezone" to "settings",
        "grading" to "settings",
        "courses" to "manage_courses",
        "batches" to "manage_courses",
        "subjects" to "manage_courses",
        "electives" to "manage_courses",
        "profile" to "admin_profile"
    )
}
