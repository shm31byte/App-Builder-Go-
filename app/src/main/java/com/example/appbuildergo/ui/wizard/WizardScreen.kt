package com.example.appbuildergo.ui.wizard

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun WizardScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            text = "Buat Aplikasi Baru",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Ikuti 5 langkah mudah di bawah ini",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(24.dp))

        // Menampilkan semua step secara vertikal sebagai mockup statis
        Step1App()
        SectionDivider()
        Step2Appearance()
        SectionDivider()
        Step3Features()
        SectionDivider()
        Step4Permissions()
        SectionDivider()
        Step5Done()
        
        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun SectionDivider() {
    Spacer(modifier = Modifier.height(32.dp))
    Divider(color = MaterialTheme.colorScheme.surfaceVariant)
    Spacer(modifier = Modifier.height(32.dp))
}

@Composable
private fun StepHeader(stepNumber: String, title: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stepNumber,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
    Spacer(modifier = Modifier.height(16.dp))
}

// --- KOMPONEN STEP 1 ---
@Composable
private fun Step1App() {
    StepHeader("1", "Data Aplikasi")

    Text("Mode Konversi", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 14.sp)
    Spacer(Modifier.height(8.dp))
    
    // Dummy Radio Buttons
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            RadioButton(selected = true, onClick = {}, colors = RadioButtonDefaults.colors(selectedColor = MaterialTheme.colorScheme.primary))
            Text("Situs Web", color = MaterialTheme.colorScheme.onBackground)
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
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

// --- KOMPONEN STEP 2 ---
@Composable
private fun Step2Appearance() {
    StepHeader("2", "Tampilan")

    Text("Splash Screen", color = MaterialTheme.colorScheme.onBackground, fontWeight = FontWeight.SemiBold)
    Spacer(Modifier.height(8.dp))
    
    var splashEnabled by remember { mutableStateOf(true) }
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("Tampilkan Splash Screen saat dibuka", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 14.sp)
        Switch(
            checked = splashEnabled,
            onCheckedChange = { splashEnabled = it },
            colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = MaterialTheme.colorScheme.primary)
        )
    }

    Spacer(Modifier.height(16.dp))
    Text("Warna Tema Utama", color = MaterialTheme.colorScheme.onBackground, fontWeight = FontWeight.SemiBold)
    Spacer(Modifier.height(8.dp))
    
    // Dummy Color Picker (barisan kotak warna)
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Box(modifier = Modifier.size(40.dp).clip(CircleShape).background(Color(0xFF5C5CFF)).border(2.dp, Color.White, CircleShape))
        Box(modifier = Modifier.size(40.dp).clip(CircleShape).background(Color(0xFF4CAF50)))
        Box(modifier = Modifier.size(40.dp).clip(CircleShape).background(Color(0xFFF44336)))
        Box(modifier = Modifier.size(40.dp).clip(CircleShape).background(Color(0xFFFF9800)))
    }
}

// --- KOMPONEN STEP 3 ---
@Composable
private fun Step3Features() {
    StepHeader("3", "Fitur Tambahan")

    var offline by remember { mutableStateOf(false) }
    var pullRefresh by remember { mutableStateOf(true) }

    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("Dukungan Offline", color = MaterialTheme.colorScheme.onBackground)
        Switch(
            checked = offline, onCheckedChange = { offline = it },
            colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = MaterialTheme.colorScheme.primary)
        )
    }
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("Pull-to-Refresh", color = MaterialTheme.colorScheme.onBackground)
        Switch(
            checked = pullRefresh, onCheckedChange = { pullRefresh = it },
            colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = MaterialTheme.colorScheme.primary)
        )
    }
}

// --- KOMPONEN STEP 4 ---
@Composable
private fun Step4Permissions() {
    StepHeader("4", "Izin Akses")

    var izinKamera by remember { mutableStateOf(false) }
    var izinLokasi by remember { mutableStateOf(false) }
    var izinStorage by remember { mutableStateOf(true) }

    Column {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 4.dp)) {
            Checkbox(
                checked = izinKamera, onCheckedChange = { izinKamera = it },
                colors = CheckboxDefaults.colors(checkedColor = MaterialTheme.colorScheme.primary)
            )
            Text("Akses Kamera", color = MaterialTheme.colorScheme.onBackground)
        }
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 4.dp)) {
            Checkbox(
                checked = izinLokasi, onCheckedChange = { izinLokasi = it },
                colors = CheckboxDefaults.colors(checkedColor = MaterialTheme.colorScheme.primary)
            )
            Text("Akses Lokasi (GPS)", color = MaterialTheme.colorScheme.onBackground)
        }
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 4.dp)) {
            Checkbox(
                checked = izinStorage, onCheckedChange = { izinStorage = it },
                colors = CheckboxDefaults.colors(checkedColor = MaterialTheme.colorScheme.primary)
            )
            Text("Penyimpanan File", color = MaterialTheme.colorScheme.onBackground)
        }
    }
}

// --- KOMPONEN STEP 5 ---
@Composable
private fun Step5Done() {
    StepHeader("5", "Selesai")

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Ringkasan", color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            Text("Nama: (Belum diisi)", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 14.sp)
            Text("Mode: Situs Web", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 14.sp)
            Text("Offline: Tidak", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 14.sp)
        }
    }

    Spacer(Modifier.height(24.dp))
    Button(
        onClick = { /* Aksi generate nanti di sini */ },
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
    ) {
        Text("Generate Aplikasi", fontSize = 16.sp, fontWeight = FontWeight.Bold)
    }
}
