package com.ims.app.navigation

import androidx.navigation.compose.*
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.ims.app.data.SampleData
import com.ims.app.ui.screens.*

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object AdminDashboard : Screen("admin_dashboard")
    object StudentDashboard : Screen("student_dashboard")
    object Timetable : Screen("timetable")
    object AttendanceMarking : Screen("attendance_marking")
    object AttendanceReport : Screen("attendance_report?studentId={studentId}") {
        fun createRoute(studentId: String? = null) = if (studentId != null) "attendance_report?studentId=$studentId" else "attendance_report"
    }
    object AdminProfile : Screen("admin_profile")
    object Settings : Screen("settings")
    object ManageCourses : Screen("manage_courses")
    object NewsDisplay : Screen("news_display")
    object StudentProfile : Screen("student_profile")
}

@Composable
fun NavGraph() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Screen.Login.route) {
        composable(Screen.Login.route) {
            LoginScreen(
                onAdminLogin = {
                    navController.navigate(Screen.AdminDashboard.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onStudentLogin = {
                    navController.navigate(Screen.StudentDashboard.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.AdminDashboard.route) {
            AdminDashboardScreen(
                onNavigateToAttendance = { navController.navigate(Screen.AttendanceMarking.route) },
                onNavigateToAttendanceReport = { navController.navigate(Screen.AttendanceReport.createRoute(null)) },
                onNavigateToTimetable = { navController.navigate(Screen.Timetable.route) },
                onNavigateToSettings = { navController.navigate(Screen.Settings.route) },
                onNavigateToCourses = { navController.navigate(Screen.ManageCourses.route) },
                onNavigateToProfile = { navController.navigate(Screen.AdminProfile.route) }
            )
        }
        composable(Screen.StudentDashboard.route) {
            StudentDashboardScreen(
                onNavigateToTimetable = { navController.navigate(Screen.Timetable.route) },
                onNavigateToAttendance = { navController.navigate(Screen.AttendanceReport.createRoute("current")) },
                onNavigateToProfile = { navController.navigate(Screen.StudentProfile.route) }
            )
        }
        composable(Screen.Timetable.route) {
            val isAdmin = SampleData.currentRole == "admin"
            TimetableScreen(
                onNavigateToDashboard = {
                    navController.navigate(if (isAdmin) Screen.AdminDashboard.route else Screen.StudentDashboard.route) {
                        popUpTo(if (isAdmin) Screen.AdminDashboard.route else Screen.StudentDashboard.route) { inclusive = true }
                    }
                },
                onNavigateToAttendance = {
                    navController.navigate(if (isAdmin) Screen.AttendanceMarking.route else Screen.AttendanceReport.createRoute("current"))
                },
                onNavigateToProfile = {
                    if (isAdmin) navController.navigate(Screen.AdminProfile.route)
                    else navController.navigate(Screen.StudentProfile.route)
                },
                isAdmin = isAdmin
            )
        }
        composable(Screen.AttendanceMarking.route) {
            AttendanceMarkingScreen(
                onBack = { navController.popBackStack() },
                onViewReport = { studentId -> navController.navigate(Screen.AttendanceReport.createRoute(studentId)) },
                onViewGeneralReport = { navController.navigate(Screen.AttendanceReport.createRoute(null)) },
                onNavigateToDashboard = {
                    navController.navigate(Screen.AdminDashboard.route) {
                        popUpTo(Screen.AdminDashboard.route) { inclusive = true }
                    }
                },
                onNavigateToTimetable = { navController.navigate(Screen.Timetable.route) },
                onNavigateToProfile = { navController.navigate(Screen.AdminProfile.route) }
            )
        }
        composable(
            route = Screen.AttendanceReport.route,
            arguments = listOf(navArgument("studentId") {
                type = NavType.StringType
                nullable = true
                defaultValue = null
            })
        ) { backStackEntry ->
            val studentId = backStackEntry.arguments?.getString("studentId")
            val isAdmin = SampleData.currentRole == "admin"
            AttendanceReportScreen(
                studentId = studentId,
                isAdmin = isAdmin,
                onBack = { navController.popBackStack() },
                onNavigateToDashboard = {
                    val dest = if (isAdmin) Screen.AdminDashboard.route else Screen.StudentDashboard.route
                    navController.navigate(dest) {
                        popUpTo(dest) { inclusive = true }
                    }
                },
                onNavigateToTimetable = { navController.navigate(Screen.Timetable.route) },
                onNavigateToProfile = {
                    if (isAdmin) navController.navigate(Screen.AdminProfile.route)
                    else navController.navigate(Screen.StudentProfile.route)
                }
            )
        }
        composable(Screen.AdminProfile.route) {
            AdminProfileScreen(
                onNavigateToDashboard = {
                    navController.navigate(Screen.AdminDashboard.route) {
                        popUpTo(Screen.AdminDashboard.route) { inclusive = true }
                    }
                },
                onNavigateToTimetable = { navController.navigate(Screen.Timetable.route) },
                onNavigateToAttendance = { navController.navigate(Screen.AttendanceMarking.route) },
                onLogout = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.Settings.route) {
            SettingsScreen(onBack = { navController.popBackStack() })
        }
        composable(Screen.ManageCourses.route) {
            ManageCoursesScreen(onBack = { navController.popBackStack() })
        }
        composable(Screen.StudentProfile.route) {
            StudentProfileScreen(
                onNavigateToDashboard = {
                    navController.navigate(Screen.StudentDashboard.route) {
                        popUpTo(Screen.StudentDashboard.route) { inclusive = true }
                    }
                },
                onNavigateToTimetable = { navController.navigate(Screen.Timetable.route) },
                onNavigateToAttendance = { navController.navigate(Screen.AttendanceReport.createRoute("current")) },
                onLogout = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
    }
}
