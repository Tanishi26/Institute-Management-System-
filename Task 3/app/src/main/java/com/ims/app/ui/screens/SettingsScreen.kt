package com.ims.app.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
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
import com.ims.app.data.SampleData
import com.ims.app.ui.theme.*
import androidx.compose.ui.graphics.Color

@Composable
fun SettingsScreen(onBack: () -> Unit) {
    var gradingSystem by remember { mutableStateOf(SampleData.gradingSystem) }
    var country by remember { mutableStateOf(SampleData.country) }
    var currency by remember { mutableStateOf(SampleData.currency) }
    var timeZone by remember { mutableStateOf(SampleData.timeZone) }
    var language by remember { mutableStateOf(SampleData.language) }
    var autoIds by remember { mutableStateOf(SampleData.autoUniqueIds) }
    var smsEnabled by remember { mutableStateOf(SampleData.smsEnabled) }
    var saved by remember { mutableStateOf(false) }

    val gradingOptions = listOf("10-point GPA", "4-point GPA", "Percentage", "Letter Grade (A-F)", "CCE", "CWA")
    val countryOptions = listOf("India", "USA", "UK", "Germany", "Singapore")
    val currencyOptions = listOf("INR (₹)", "USD ($)", "EUR (€)", "GBP (£)", "SGD (S$)")
    val timeZoneOptions = listOf("IST (UTC+5:30)", "UTC+0", "EST (UTC-5)", "PST (UTC-8)", "SGT (UTC+8)")
    val languageOptions = listOf("English", "Hindi", "Telugu", "Tamil", "Kannada")

    var showGradingPicker by remember { mutableStateOf(false) }
    var showCountryPicker by remember { mutableStateOf(false) }
    var showCurrencyPicker by remember { mutableStateOf(false) }
    var showTimeZonePicker by remember { mutableStateOf(false) }
    var showLanguagePicker by remember { mutableStateOf(false) }

    @Composable
    fun PickerDialog(title: String, options: List<String>, selected: String, onSelect: (String) -> Unit, onDismiss: () -> Unit) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text(title, fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    options.forEach { option ->
                        Row(
                            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp))
                                .background(if (selected == option) PrimaryContainer.copy(0.15f) else Color.Transparent)
                                .clickable { onSelect(option); onDismiss() }.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            RadioButton(selected = selected == option, onClick = { onSelect(option); onDismiss() }, colors = RadioButtonDefaults.colors(selectedColor = Primary))
                            Text(option, fontSize = 14.sp)
                        }
                    }
                }
            },
            confirmButton = { TextButton(onClick = onDismiss) { Text("Cancel") } },
            containerColor = SurfaceContainerLowest
        )
    }

    if (showGradingPicker) PickerDialog("Grading System", gradingOptions, gradingSystem, { gradingSystem = it; SampleData.gradingSystem = it }) { showGradingPicker = false }
    if (showCountryPicker) PickerDialog("Country", countryOptions, country, { country = it; SampleData.country = it }) { showCountryPicker = false }
    if (showCurrencyPicker) PickerDialog("Currency", currencyOptions, currency, { currency = it; SampleData.currency = it }) { showCurrencyPicker = false }
    if (showTimeZonePicker) PickerDialog("Time Zone", timeZoneOptions, timeZone, { timeZone = it; SampleData.timeZone = it }) { showTimeZonePicker = false }
    if (showLanguagePicker) PickerDialog("Language", languageOptions, language, { language = it; SampleData.language = it }) { showLanguagePicker = false }

    Scaffold(containerColor = Surface) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState())) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth().background(SurfaceContainerLow).padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = null, tint = Primary) }
                Text("Settings", fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = Primary)
            }

            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(20.dp)) {

                // Language & Region
                SettingsSection(title = "Language & Region", icon = Icons.Default.Language) {
                    SettingsRow("Language", language, Icons.Default.Translate) { showLanguagePicker = true }
                    SettingsRow("Country", country, Icons.Default.Public) { showCountryPicker = true }
                    SettingsRow("Currency", currency, Icons.Default.CurrencyRupee) { showCurrencyPicker = true }
                    SettingsRow("Time Zone", timeZone, Icons.Default.Schedule) { showTimeZonePicker = true }
                }

                // Academic Configuration
                SettingsSection(title = "Academic Configuration", icon = Icons.Default.School) {
                    SettingsRow("Grading System", gradingSystem, Icons.Default.Grade) { showGradingPicker = true }
                    SettingsToggleRow("Auto Unique ID Generation", "Automatically assign IDs to new students/staff", autoIds) {
                        autoIds = it; SampleData.autoUniqueIds = it
                    }
                }

                // Communication
                SettingsSection(title = "Communication", icon = Icons.Default.Sms) {
                    SettingsToggleRow("SMS Alerts", "Enable SMS module for group/single alerts", smsEnabled) {
                        smsEnabled = it; SampleData.smsEnabled = it
                    }
                }

                // System info
                SettingsSection(title = "System", icon = Icons.Default.Info) {
                    Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = SurfaceContainerLow)) {
                        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            listOf("App Version" to "2.4.0", "Institute" to "IIIT Hyderabad", "Academic Year" to "2025-26", "Admin" to "admin@iiit.ac.in").forEach { (k, v) ->
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text(k, fontSize = 13.sp, color = OnSurfaceVariant)
                                    Text(v, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = OnSurface)
                                }
                            }
                        }
                    }
                }

                if (saved) {
                    Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = TertiaryFixed.copy(0.3f))) {
                        Row(modifier = Modifier.padding(12.dp), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Tertiary, modifier = Modifier.size(18.dp))
                            Text("Settings saved successfully!", fontSize = 13.sp, color = Tertiary, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }

                Button(
                    onClick = { saved = true },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Primary)
                ) {
                    Icon(Icons.Default.Save, contentDescription = null, tint = OnPrimary, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Save Settings", fontWeight = FontWeight.Bold)
                }

                Spacer(Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun SettingsSection(title: String, icon: androidx.compose.ui.graphics.vector.ImageVector, content: @Composable ColumnScope.() -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            Icon(icon, contentDescription = null, tint = Primary, modifier = Modifier.size(18.dp))
            Text(title, fontSize = 15.sp, fontWeight = FontWeight.ExtraBold, color = Primary)
        }
        Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest)) {
            Column(modifier = Modifier.padding(4.dp), content = content)
        }
    }
}

@Composable
fun SettingsRow(label: String, value: String, icon: androidx.compose.ui.graphics.vector.ImageVector, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).clickable(onClick = onClick).padding(14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Icon(icon, contentDescription = null, tint = Secondary, modifier = Modifier.size(18.dp))
            Text(label, fontSize = 14.sp, fontWeight = FontWeight.Medium)
        }
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(value, fontSize = 13.sp, color = OnSurfaceVariant)
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = OnSurfaceVariant, modifier = Modifier.size(16.dp))
        }
    }
}

@Composable
fun SettingsToggleRow(label: String, subtitle: String, checked: Boolean, onToggle: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(label, fontSize = 14.sp, fontWeight = FontWeight.Medium)
            Text(subtitle, fontSize = 11.sp, color = OnSurfaceVariant, lineHeight = 14.sp)
        }
        Switch(checked = checked, onCheckedChange = onToggle, colors = SwitchDefaults.colors(checkedThumbColor = OnPrimary, checkedTrackColor = Primary))
    }
}