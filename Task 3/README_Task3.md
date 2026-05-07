# Task 3: Restructuring Attendance, Timetables, and Student Interfaces

This document outlines the specific features, changes, and UI/UX structures implemented under the scope of Task 3. We successfully mapped the portal capabilities to separate logic layers (Admin vs User), enhanced the interactivity of the native student experience, and implemented a smart feedback loop regarding attendance reporting.

## 1. Complete Separation of Portals
We restructured how the ecosystem is served based strictly on authenticated role parameters.
* **Navigation Isolation:** The `NavGraph` strictly filters and distinguishes routing depending on the role (`isAdmin` boolean constraint). Access maps were updated so an Admin views specific system tools, while a Student never triggers raw admin components.

## 2. Restructured Attendance Reporting 
Attendance viewing needed proper separation—general for the institute and individualized for students.
* **Polymorphic `AttendanceReportScreen`:** Adjusted the singular `AttendanceReportScreen` implementation so that it now takes an optional `studentId` argument dynamically.
* **Student View (Personalised):** Navigating to the attendance view as a Student automatically populates the parameter with their specific `studentId` ("current"), locking them onto their personal individual metrics completely.
* **Admin View (General):** Admins are provided direct shortcuts in their `AdminDashboardScreen` via a new `Reports` tile or specifically via a `General Report` button sitting atop the `AttendanceMarkingScreen`. Omitting the studentId drops Admins directly into the broader batch-wide overview covering absence and aggregate stats.
* **Reactive Pop-up Alerts:** Hooked into the main `StudentDashboardScreen` lifecycle. When a student logs in, an `AlertDialog` automatically runs a background check against `SampleData` attendance records. It greets students with positive enforcement (green *"Great job!"* on good streaks) or a negative warning (red *"Attendance Alert"* to warn them about missing their courses).

## 3. Student Timetable Experience & Features
Tasked with eliminating chaotic Admin functionality from standard grids while amplifying Student interaction.
* **UI Clean-up:** Eliminated the dotted-border generic `add (+)` placeholders from the timetable grid whenever an active student views the chart. Empty cells are visually rendered as completely empty/transparent, keeping the schedule pristine and course-focused.
* **Custom Slot Injection (`StudentAddDialog`):** Despite removing the explicit box shapes, empty spaces retain clickable touch areas for Students. Clicking an empty space pulls up an intuitive dialog where they can slot in personal entries into their grid *(e.g., "Personal Study", "Extra Class", "Alert")*. These receive distinct visual theming within the grid (mapped dynamically to negative IDs to flag custom entities).
* **Vibrant Class Tagging (`StudentTagDialog`):** When a student clicks an admin-appointed curriculum course in their timetable, it launches a custom tagger. Students can mark the importance of their ongoing schedule by categorizing it as:
  * **"Important to go"** -> Shifts the entire timeline block to **Red**.
  * **"Can skip"** -> Shifts the entire timeline block to **Green**.
  * **"Class Activity"** -> Shifts the entire timeline block to **Orange**.

## 4. Fully-Fledged Student Profile Map
Added a dedicated page logic to fill in missing gaps about displaying raw demographic metrics and mock progress data.
* **`StudentProfileScreen` Generation:** Built from scratch to emulate Android M3 styles utilizing high-end rounded corners and gradient surfaces.
* **Metrics Injected:** Accurately sources the user ID (`SampleData`) displaying full attributes including: Name String, System ID Tag, Cohort Batch *(UG2k24)*, Enrolled Program *(CSE)*, and current calculated mock metrics like **CGPA** (e.g. `8.34`), **Overall Credits**, and explicit breakdowns summarizing recently finalized courses.
* **Wired `BottomNav`**: Cleanly merged into the `StudentBottomNav` paradigm across all endpoints, providing a quick fallback loop to their profile via the user icon.
