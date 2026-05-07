package com.ims.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ims.app.ui.theme.*

@Composable
fun AdminBottomNav(
    selected: Int,
    onDashboard: () -> Unit,
    onSchedule: () -> Unit,
    onAttendance: () -> Unit,
    onProfile: () -> Unit
) {
    BottomNavBar(
        selected = selected,
        items = listOf(
            Triple(Icons.Default.GridView, "Dashboard", onDashboard),
            Triple(Icons.Default.CalendarToday, "Schedule", onSchedule),
            Triple(Icons.Default.FactCheck, "Attendance", onAttendance),
            Triple(Icons.Default.Person, "Profile", onProfile)
        )
    )
}

@Composable
fun StudentBottomNav(
    selected: Int,
    onDashboard: () -> Unit,
    onSchedule: () -> Unit,
    onAttendance: () -> Unit,
    onProfile: () -> Unit
) {
    BottomNavBar(
        selected = selected,
        items = listOf(
            Triple(Icons.Default.GridView, "Dashboard", onDashboard),
            Triple(Icons.Default.CalendarToday, "Schedule", onSchedule),
            Triple(Icons.Default.FactCheck, "Attendance", onAttendance),
            Triple(Icons.Default.AccountCircle, "Profile", onProfile)
        )
    )
}

@Composable
private fun BottomNavBar(
    selected: Int,
    items: List<Triple<ImageVector, String, () -> Unit>>
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(SurfaceContainerLowest)
            .padding(horizontal = 8.dp, vertical = 8.dp)
            .navigationBarsPadding(),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        items.forEachIndexed { index, (icon, label, action) ->
            val isActive = selected == index
            if (isActive) {
                Column(
                    modifier = Modifier
                        .clip(RoundedCornerShape(14.dp))
                        .background(Brush.linearGradient(listOf(Primary, PrimaryContainer)))
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(icon, contentDescription = label, tint = OnPrimary, modifier = Modifier.size(22.dp))
                    Spacer(Modifier.height(2.dp))
                    Text(label.uppercase(), fontSize = 8.sp, color = OnPrimary, fontWeight = FontWeight.SemiBold, letterSpacing = 0.8.sp)
                }
            } else {
                IconButton(onClick = action) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(icon, contentDescription = label, tint = OnSurfaceVariant, modifier = Modifier.size(22.dp))
                        Text(label.uppercase(), fontSize = 8.sp, color = OnSurfaceVariant, fontWeight = FontWeight.Medium, letterSpacing = 0.8.sp)
                    }
                }
            }
        }
    }
}