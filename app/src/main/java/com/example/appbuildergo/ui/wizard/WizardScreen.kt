package com.example.appbuildergo.ui.wizard

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
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
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.runtime.mutableIntStateOf
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

// MODEL DATA UNTUK MENYIMPAN STATE WIZARD
data class WizardData(
    val appName: String = "",
    val conversionMode: String = "Situs Web (URL)",
    val htmlCode: String = "",
    val splashEnabled: Boolean = true,
    val themeColor: Color = Color(0xFF5C5CFF),
    val offlineSupport: Boolean = false,
    val pullToRefresh: Boolean = true,
    val permCamera: Boolean = false,
    val permLocation: Boolean = false,
    val permStorage: Boolean = true
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WizardScreen() {
    var currentStep by remember { mutableIntStateOf(1) }
    var wizardData by remember { mutableStateOf(WizardData()) }

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
                            text = "Langkah $currentStep dari 5",
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
        bottomBar = {
            BottomNavigationBar(
                currentStep = currentStep,
                onBack = { if (currentStep > 1) currentStep-- },
                onNext = { if (currentStep < 5) currentStep++ },
                onGenerate = { /* Logic generate APK nanti di sini */ }
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
            // Stepper Indicator
            StepIndicator(currentStep = currentStep)
            Spacer(modifier = Modifier.height(24.dp))

            // Animasi transisi antar step
            AnimatedContent(
                targetState = currentStep,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "StepTransition"
            ) { step ->
                when (step) {
                    1 -> Step1App(
                        data = wizardData,
                        onDataChange = { wizardData = it }
                    )
                    2 -> Step2Appearance(
                        data = wizardData,
                        onDataChange = { wizardData = it }
                    )
                    3 -> Step3Features(
                        data = wizardData,
                        onDataChange = { wizardData = it }
                    )
                    4 -> Step4Permissions(
                        data = wizardData,
                        onDataChange = { wizardData = it }
                    )
                    5 -> Step5Done(data = wizardData)
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun BottomNavigationBar(
    currentStep: Int,
    onBack: () -> Unit,
    onNext: () -> Unit,
    onGenerate: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (currentStep > 1) {
            OutlinedButton(
                onClick = onBack,
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Text("Kembali", color = MaterialTheme.colorScheme.onSurface)
            }
        }

        Button(
            onClick = { if (currentStep == 5) onGenerate() else onNext() },
            modifier = Modifier
                .weight(if (currentStep > 1) 2f else 1f)
                .height(50.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Text(
                text = if (currentStep == 5) "Generate Aplikasi" else "Selanjutnya",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}

// --- KOMPONEN STEP 1 ---
@Composable
private fun Step1App(data: WizardData, onDataChange: (WizardData) -> Unit) {
    StepCard(step = "1", title = "Data Aplikasi", icon = Icons.Default.PhoneAndroid) {
        Text("Mode Konversi", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 14.sp, fontWeight = FontWeight.Medium)
        Spacer(Modifier.height(8.dp))
        
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically, 
                modifier = Modifier.fillMaxWidth().clickable { onDataChange(data.copy(conversionMode = "Situs Web (URL)")) }
            ) {
                RadioButton(
                    selected = data.conversionMode == "Situs Web (URL)", 
                    onClick = { onDataChange(data.copy(conversionMode = "Situs Web (URL)")) }, 
                    colors = RadioButtonDefaults.colors(selectedColor = MaterialTheme.colorScheme.primary)
                )
                Text("Situs Web (URL)", color = MaterialTheme.colorScheme.onBackground)
            }
            Row(
                verticalAlignment = Alignment.CenterVertically, 
                modifier = Modifier.fillMaxWidth().clickable { onDataChange(data.copy(conversionMode = "ZIP Offline")) }
            ) {
                RadioButton(
                    selected = data.conversionMode == "ZIP Offline", 
                    onClick = { onDataChange(data.copy(conversionMode = "ZIP Offline")) }, 
                    colors = RadioButtonDefaults.colors(selectedColor = MaterialTheme.colorScheme.primary)
                )
                Text("ZIP Offline", color = MaterialTheme.colorScheme.onBackground)
            }
        }

        Spacer(Modifier.height(16.dp))
        OutlinedTextField(
            value = data.appName,
            onValueChange = { onDataChange(data.copy(appName = it)) },
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
            value = data.htmlCode,
            onValueChange = { onDataChange(data.copy(htmlCode = it)) },
            label = { Text("Kode HTML (Opsional)") },
            leadingIcon = { Icon(Icons.Default.Code, contentDescription = "Code", tint = MaterialTheme.colorScheme.onSurfaceVariant) },
            modifier = Modifier.fillMaxWidth().height(120.dp),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.surfaceVariant,
                focusedLabelColor = MaterialTheme.colorScheme.primary,
                unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        )
    }
}

// --- KOMPONEN STEP 2 ---
@Composable
private fun Step2Appearance(data: WizardData, onDataChange: (WizardData) -> Unit) {
    StepCard(step = "2", title = "Tampilan", icon = Icons.Default.ColorLens) {
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
                checked = data.splashEnabled,
                onCheckedChange = { onDataChange(data.copy(splashEnabled = it)) },
                colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = MaterialTheme.colorScheme.primary)
            )
        }

        Spacer(Modifier.height(24.dp))
        Text("Warna Tema Utama", color = MaterialTheme.colorScheme.onBackground, fontWeight = FontWeight.Medium)
        Spacer(Modifier.height(12.dp))
        
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            val colors = listOf(Color(0xFF5C5CFF), Color(0xFF4CAF50), Color(0xFFF44336), Color(0xFFFF9800))
            colors.forEach { color ->
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(color)
                        .border(
                            width = if (data.themeColor == color) 3.dp else 0.dp,
                            color = if (data.themeColor == color) Color.White else Color.Transparent,
                            shape = CircleShape
                        )
                        .clickable { onDataChange(data.copy(themeColor = color)) }
                )
            }
        }
    }
}

// --- KOMPONEN STEP 3 ---
@Composable
private fun Step3Features(data: WizardData, onDataChange: (WizardData) -> Unit) {
    StepCard(step = "3", title = "Fitur Tambahan", icon = Icons.Default.Extension) {
        FeatureToggle(
            title = "Dukungan Offline",
            subtitle = "Simpan cache halaman web",
            checked = data.offlineSupport,
            onCheckedChange = { onDataChange(data.copy(offlineSupport = it)) }
        )
        Spacer(Modifier.height(16.dp))
        FeatureToggle(
            title = "Pull-to-Refresh",
            subtitle = "Tarik ke bawah untuk memuat ulang",
            checked = data.pullToRefresh,
            onCheckedChange = { onDataChange(data.copy(pullToRefresh = it)) }
        )
    }
}

// --- KOMPONEN STEP 4 ---
@Composable
private fun Step4Permissions(data: WizardData, onDataChange: (WizardData) -> Unit) {
    StepCard(step = "4", title = "Izin Akses", icon = Icons.Default.Lock) {
        Column {
            PermissionCheckbox("Akses Kamera", data.permCamera) { onDataChange(data.copy(permCamera = it)) }
            PermissionCheckbox("Akses Lokasi (GPS)", data.permLocation) { onDataChange(data.copy(permLocation = it)) }
            PermissionCheckbox("Penyimpanan File", data.permStorage) { onDataChange(data.copy(permStorage = it)) }
        }
    }
}

// --- KOMPONEN STEP 5 ---
@Composable
private fun Step5Done(data: WizardData) {
    StepCard(step = "5", title = "Selesai", icon = Icons.Default.CheckCircle) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Ringkasan Aplikasi", color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(12.dp))
                SummaryRow("Nama", if (data.appName.isBlank()) "(Belum diisi)" else data.appName)
                SummaryRow("Mode", data.conversionMode)
                SummaryRow("Offline", if (data.offlineSupport) "Ya" else "Tidak")
                SummaryRow("Izin Storage", if (data.permStorage) "Ya" else "Tidak")
            }
        }
    }
}

// --- KOMPONEN PENDUKUNG ---

@Composable
private fun StepCard(step: String, title: String, icon: ImageVector, content: @Composable () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
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
            Spacer(modifier = Modifier.height(20.dp))
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
private fun FeatureToggle(title: String, subtitle: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
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
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 14.sp)
        Text(value, color = MaterialTheme.colorScheme.onSurface, fontSize = 14.sp, fontWeight = FontWeight.Medium)
    }
}
