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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

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
fun WizardScreen(
    onGenerate: (WizardData) -> Unit = {},
    tabBar: (@Composable () -> Unit)? = null
) {
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
                            fontSize = 20.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Langkah $currentStep dari 5",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            letterSpacing = 0.5.sp
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        bottomBar = {
            Column {
                WizardNavRow(
                    currentStep = currentStep,
                    nextEnabled = currentStep != 1 || wizardData.appName.isNotBlank(),
                    onBack = { if (currentStep > 1) currentStep-- },
                    onNext = { if (currentStep < 5) currentStep++ },
                    onGenerate = { onGenerate(wizardData) }
                )
                tabBar?.invoke()
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            StepIndicator(currentStep = currentStep)
            Spacer(modifier = Modifier.height(32.dp))
            AnimatedContent(
                targetState = currentStep,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "StepTransition"
            ) { step ->
                when (step) {
                    1 -> Step1App(data = wizardData, onDataChange = { wizardData = it })
                    2 -> Step2Appearance(data = wizardData, onDataChange = { wizardData = it })
                    3 -> Step3Features(data = wizardData, onDataChange = { wizardData = it })
                    4 -> Step4Permissions(data = wizardData, onDataChange = { wizardData = it })
                    5 -> Step5Done(data = wizardData)
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun WizardNavRow(
    currentStep: Int,
    nextEnabled: Boolean,
    onBack: () -> Unit,
    onNext: () -> Unit,
    onGenerate: () -> Unit
) {
    Column {
        Box(
            Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(
                    Brush.horizontalGradient(
                        listOf(Color.Transparent, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f), Color.Transparent)
                    )
                )
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.background.copy(alpha = 0.95f))
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (currentStep > 1) {
                OutlinedButton(
                    onClick = onBack,
                    modifier = Modifier.weight(1f).height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.onSurface)
                ) {
                    Text("Kembali", fontWeight = FontWeight.SemiBold)
                }
            }
            Button(
                onClick = { if (currentStep == 5) onGenerate() else onNext() },
                enabled = nextEnabled,
                modifier = Modifier
                    .weight(if (currentStep > 1) 2f else 1f)
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
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
}

@Composable
private fun Step1App(data: WizardData, onDataChange: (WizardData) -> Unit) {
    StepCard(step = "1", title = "Data Aplikasi", icon = Icons.Default.PhoneAndroid) {
        Text("Mode Konversi", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
        Spacer(Modifier.height(12.dp))
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            PremiumRadioButton(
                selected = data.conversionMode == "Situs Web (URL)",
                onClick = { onDataChange(data.copy(conversionMode = "Situs Web (URL)")) },
                text = "Situs Web (URL)"
            )
            PremiumRadioButton(
                selected = data.conversionMode == "ZIP Offline",
                onClick = { onDataChange(data.copy(conversionMode = "ZIP Offline")) },
                text = "ZIP Offline"
            )
        }
        Spacer(Modifier.height(24.dp))
        PremiumTextField(
            value = data.appName,
            onValueChange = { onDataChange(data.copy(appName = it)) },
            label = "Nama Aplikasi"
        )
        Spacer(Modifier.height(16.dp))
        PremiumTextField(
            value = data.htmlCode,
            onValueChange = { onDataChange(data.copy(htmlCode = it)) },
            label = "Kode HTML (Opsional)",
            leadingIcon = Icons.Default.Code,
            minLines = 4,
            maxLines = 6
        )
    }
}

@Composable
private fun Step2Appearance(data: WizardData, onDataChange: (WizardData) -> Unit) {
    StepCard(step = "2", title = "Tampilan", icon = Icons.Default.ColorLens) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Splash Screen", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
                Spacer(modifier = Modifier.height(4.dp))
                Text("Tampilkan saat aplikasi dibuka", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Switch(
                checked = data.splashEnabled,
                onCheckedChange = { onDataChange(data.copy(splashEnabled = it)) },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = MaterialTheme.colorScheme.primary,
                    uncheckedThumbColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    uncheckedTrackColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                )
            )
        }
        Spacer(Modifier.height(28.dp))
        Text("Warna Tema Utama", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
        Spacer(Modifier.height(16.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            val colors = listOf(Color(0xFF5C5CFF), Color(0xFF4CAF50), Color(0xFFF44336), Color(0xFFFF9800), Color(0xFF00BCD4))
            colors.forEach { color ->
                val isSelected = data.themeColor == color
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(color)
                        .border(
                            width = if (isSelected) 3.dp else 0.dp,
                            color = if (isSelected) Color.White else Color.Transparent,
                            shape = CircleShape
                        )
                        .shadow(
                            elevation = if (isSelected) 8.dp else 0.dp,
                            shape = CircleShape,
                            ambientColor = color,
                            spotColor = color
                        )
                        .clickable { onDataChange(data.copy(themeColor = color)) }
                )
            }
        }
    }
}

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

@Composable
private fun Step4Permissions(data: WizardData, onDataChange: (WizardData) -> Unit) {
    StepCard(step = "4", title = "Izin Akses", icon = Icons.Default.Lock) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            PermissionCheckbox("Akses Kamera", data.permCamera) { onDataChange(data.copy(permCamera = it)) }
            PermissionCheckbox("Akses Lokasi (GPS)", data.permLocation) { onDataChange(data.copy(permLocation = it)) }
            PermissionCheckbox("Penyimpanan File", data.permStorage) { onDataChange(data.copy(permStorage = it)) }
        }
    }
}

@Composable
private fun Step5Done(data: WizardData) {
    StepCard(step = "5", title = "Selesai", icon = Icons.Default.CheckCircle) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text("Ringkasan Aplikasi", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
                Spacer(Modifier.height(16.dp))
                SummaryRow("Nama", if (data.appName.isBlank()) "(Belum diisi)" else data.appName)
                SummaryRow("Mode", data.conversionMode)
                SummaryRow("Offline", if (data.offlineSupport) "Ya" else "Tidak")
                SummaryRow("Izin Storage", if (data.permStorage) "Ya" else "Tidak")
            }
        }
    }
}

@Composable
private fun StepCard(step: String, title: String, icon: ImageVector, content: @Composable () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = Color.White.copy(alpha = 0.08f),
                shape = RoundedCornerShape(24.dp)
            ),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(MaterialTheme.colorScheme.primary.copy(alpha = 0.25f), Color.Transparent)
                            )
                        )
                        .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.4f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(24.dp))
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = "LANGKAH $step",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary,
                        letterSpacing = 1.sp
                    )
                    Text(text = title, style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onSurface)
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
            content()
        }
    }
}

@Composable
private fun StepIndicator(currentStep: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (i in 1..5) {
            val isActive = i <= currentStep
            val isCurrent = i == currentStep
            Box(
                modifier = Modifier
                    .padding(horizontal = 4.dp)
                    .size(if (isCurrent) 12.dp else 8.dp)
                    .clip(CircleShape)
                    .background(
                        if (isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                    )
                    .then(
                        if (isCurrent) Modifier.border(2.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.4f), CircleShape) else Modifier
                    )
            )
            if (i < 5) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(2.dp)
                        .background(
                            if (i < currentStep) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                        )
                )
            }
        }
    }
}

@Composable
private fun PremiumTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    leadingIcon: ImageVector? = null,
    maxLines: Int = 1,
    minLines: Int = 1
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        leadingIcon = leadingIcon?.let { { Icon(it, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant) } },
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        maxLines = maxLines,
        minLines = minLines,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
            focusedLabelColor = MaterialTheme.colorScheme.primary,
            unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
            focusedTextColor = MaterialTheme.colorScheme.onSurface,
            unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
            cursorColor = MaterialTheme.colorScheme.primary
        )
    )
}

@Composable
private fun PremiumRadioButton(selected: Boolean, onClick: () -> Unit, text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(if (selected) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f) else Color.Transparent)
            .border(
                width = 1.dp,
                color = if (selected) MaterialTheme.colorScheme.primary.copy(alpha = 0.5f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.1f),
                shape = RoundedCornerShape(16.dp)
            )
            .clickable { onClick() }
            .padding(16.dp)
    ) {
        RadioButton(
            selected = selected,
            onClick = onClick,
            colors = RadioButtonDefaults.colors(
                selectedColor = MaterialTheme.colorScheme.primary,
                unselectedColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text,
            style = MaterialTheme.typography.bodyLarge,
            color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal
        )
    }
}

@Composable
private fun FeatureToggle(title: String, subtitle: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
            Spacer(modifier = Modifier.height(4.dp))
            Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Spacer(modifier = Modifier.width(16.dp))
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = MaterialTheme.colorScheme.primary,
                uncheckedThumbColor = MaterialTheme.colorScheme.onSurfaceVariant,
                uncheckedTrackColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
            )
        )
    }
}

@Composable
private fun PermissionCheckbox(title: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable { onCheckedChange(!checked) }
            .padding(vertical = 8.dp, horizontal = 4.dp)
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = CheckboxDefaults.colors(
                checkedColor = MaterialTheme.colorScheme.primary,
                checkmarkColor = Color.White,
                uncheckedColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
            )
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(title, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurface)
    }
}

@Composable
private fun SummaryRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.SemiBold)
    }
}
