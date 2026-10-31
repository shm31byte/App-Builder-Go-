package com.example.appbuildergo.ui.wizard

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Extension
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WizardScreen() {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "App Builder Go",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                        Text(
                            text = "Web-to-App Generator",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 16.dp)
        ) {
            // Stepper Indicator (Garis waktu kecil di atas)
            StepIndicator(currentStep = 1)
            
            Spacer(modifier = Modifier.height(24.dp))

            // --- STEP 1 ---
            StepCard(
                step = "1",
                title = "Data Aplikasi",
                icon = Icons.Default.PhoneAndroid
            ) {
                Text("Mode Konversi", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                Spacer(Modifier.height(8.dp))
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth().clickable { }) {
                        RadioButton(selected = true, onClick = {}, colors = RadioButtonDefaults.colors(selectedColor = MaterialTheme.colorScheme.primary))
                        Text("Situs Web (URL)", color = MaterialTheme.colorScheme.onBackground)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth().clickable { }) {
                        RadioButton(selected = false, onClick = {}, colors = RadioButtonDefaults.colors(selectedColor = MaterialTheme.colorScheme.primary))
                        Text("ZIP Offline", color = MaterialTheme.colorScheme.onBackground)
                    }
                }

                Spacer(Modifier.height(16.dp))
                OutlinedTextField(
                    value = "",
                    onValueChange = {},
                    label = { Text("Nama Aplikasi") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.surfaceVariant,
                        focusedLabelColor = MaterialTheme.colorScheme.primary,
                        unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
                
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(
                    value = "",
                    onValueChange = {},
                    label = { Text("Kode HTML (Opsional)") },
                    leadingIcon = { Icon(Icons.Default.Code, contentDescription = "Code", tint = MaterialTheme.colorScheme.onSurfaceVariant) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.surfaceVariant,
                        focusedLabelColor = MaterialTheme.colorScheme.primary,
                        unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }

            // --- STEP 2 ---
            StepCard(
                step = "2",
                title = "Tampilan",
                icon = Icons.Default.ColorLens
            ) {
                var splashEnabled by remember { mutableStateOf(true) }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Splash Screen", color = MaterialTheme.colorScheme.onBackground, fontWeight = FontWeight.Medium)
                        Text("Tampilkan saat aplikasi dibuka", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
                    }
                    Switch(
                        checked = splashEnabled,
                        onCheckedChange = { splashEnabled = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = MaterialTheme.colorScheme.primary)
                    )
                }

                Spacer(Modifier.height(20.dp))
                Text("Warna Tema Utama", color = MaterialTheme.colorScheme.onBackground, fontWeight = FontWeight.Medium)
                Spacer(Modifier.height(12.dp))
                
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    ColorDot(Color(0xFF5C5CFF), isSelected = true)
                    ColorDot(Color(0xFF4CAF50), isSelected = false)
                    ColorDot(Color(0xFFF44336), isSelected = false)
                    ColorDot(Color(0xFFFF9800), isSelected = false)
                }
            }

            // --- STEP 3 ---
            StepCard(
                step = "3",
                title = "Fitur Tambahan",
                icon = Icons.Default.Extension
            ) {
                var offline by remember { mutableStateOf(false) }
                var pullRefresh by remember { mutableStateOf(true) }

                FeatureToggle(
                    title = "Dukungan Offline",
                    subtitle = "Simpan cache halaman web",
                    checked = offline,
                    onCheckedChange = { offline = it }
                )
                Spacer(Modifier.height(12.dp))
                FeatureToggle(
                    title = "Pull-to-Refresh",
                    subtitle = "Tarik ke bawah untuk memuat ulang",
                    checked = pullRefresh,
                    onCheckedChange = { pullRefresh = it }
                )
            }

            // --- STEP 4 ---
            StepCard(
                step = "4",
                title = "Izin Akses",
                icon = Icons.Default.Lock
            ) {
                var izinKamera by remember { mutableStateOf(false) }
                var izinLokasi by remember { mutableStateOf(false) }
                var izinStorage by remember { mutableStateOf(true) }

                Column {
                    PermissionCheckbox("Akses Kamera", izinKamera) { izinKamera = it }
                    PermissionCheckbox("Akses Lokasi (GPS)", izinLokasi) { izinLokasi = it }
                    PermissionCheckbox("Penyimpanan File", izinStorage) { izinStorage = it }
                }
            }

            // --- STEP 5 ---
            StepCard(
                step = "5",
                title = "Selesai",
                icon = Icons.Default.CheckCircle
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Ringkasan Aplikasi", color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(12.dp))
                        SummaryRow("Nama", "(Belum diisi)")
                        SummaryRow("Mode", "Situs Web")
                        SummaryRow("Warna Tema", "#5C5CFF")
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = { /* Aksi generate nanti di sini */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("Generate Aplikasi", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

// --- KOMPONEN PENDUKUNG (Dibuat agar kode rapi) ---

@Composable
private fun StepCard(
    step: String,
    title: String,
    icon: ImageVector,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "$step. $title",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            content()
        }
    }
}

@Composable
private fun StepIndicator(currentStep: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (i in 1..5) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(4.dp)
                    .padding(horizontal = 2.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(
                        if (i <= currentStep) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.surfaceVariant
                    )
            ) {}
        }
    }
}

@Composable
private fun ColorDot(color: Color, isSelected: Boolean) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(color)
            .border(
                width = if (isSelected) 3.dp else 0.dp,
                color = if (isSelected) Color.White else Color.Transparent,
                shape = CircleShape
            )
            .clickable { }
    )
}

@Composable
private fun FeatureToggle(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(title, color = MaterialTheme.colorScheme.onBackground, fontWeight = FontWeight.Medium)
            Text(subtitle, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = MaterialTheme.colorScheme.primary)
        )
    }
}

@Composable
private fun PermissionCheckbox(title: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) }
            .padding(vertical = 4.dp)
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = CheckboxDefaults.colors(checkedColor = MaterialTheme.colorScheme.primary, checkmarkColor = Color.White)
        )
        Text(title, color = MaterialTheme.colorScheme.onBackground)
    }
}

@Composable
private fun SummaryRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 14.sp)
        Text(value, color = MaterialTheme.colorScheme.onSurface, fontSize = 14.sp, fontWeight = FontWeight.Medium)
    }
}
