package com.example.appbuildergo.ui.library

import android.content.Context
import android.content.Intent
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
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Inventory2
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import com.example.appbuildergo.data.BuildRecord
import com.example.appbuildergo.data.BuildRepository
import com.example.appbuildergo.data.BuildStatus
import kotlinx.coroutines.launch
import java.io.File

private enum class LibFilter { ALL, SUCCESS, FAILED }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BuildLibraryScreen(
    tabBar: @Composable () -> Unit = {},
    onOpenWizard: () -> Unit = {},
    onRebuild: (BuildRecord) -> Unit = {}
) {
    val context = LocalContext.current
    val clipboard = LocalClipboardManager.current
    val scope = rememberCoroutineScope()
    val snackbar = remember { SnackbarHostState() }

    val builds by BuildRepository.builds.collectAsState()
    var query by remember { mutableStateOf("") }
    var filter by remember { mutableStateOf(LibFilter.ALL) }
    var pendingDelete by remember { mutableStateOf<BuildRecord?>(null) }

    val filtered = builds.filter { rec ->
        (filter == LibFilter.ALL ||
            (filter == LibFilter.SUCCESS && rec.status == BuildStatus.SUCCESS) ||
            (filter == LibFilter.FAILED && rec.status == BuildStatus.FAILED)) &&
            rec.appName.contains(query, ignoreCase = true)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Build Saya", fontWeight = FontWeight.Bold, fontSize = 19.sp)
                        Text(
                            "Kelola & pasang ulang hasil build Anda",
                            fontSize = 11.5.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        bottomBar = { tabBar() },
        snackbarHost = { SnackbarHost(snackbar) },
        containerColor = MaterialTheme.colorScheme.background
    ) { pv ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(pv)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // STATS + STORAGE
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(20.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.08f))
            ) {
                Column(Modifier.padding(16.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        StatBox("${builds.size}", "TOTAL BUILD", MaterialTheme.colorScheme.onSurface, Modifier.weight(1f))
                        StatBox("${builds.count { it.status == BuildStatus.SUCCESS }}", "SUKSES", Color(0xFF4ADE80), Modifier.weight(1f))
                        StatBox("${builds.count { it.status == BuildStatus.FAILED }}", "GAGAL", Color(0xFFF87171), Modifier.weight(1f))
                    }
                    Spacer(Modifier.height(14.dp))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Penyimpanan build lokal", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(
                            "${"%.1f".format(BuildRepository.totalSizeMb())} MB / ${BuildRepository.QUOTA_MB.toInt()} MB",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Spacer(Modifier.height(6.dp))
                    LinearProgressIndicator(
                        progress = (BuildRepository.totalSizeMb() / BuildRepository.QUOTA_MB).toFloat().coerceIn(0f, 1f),
                        modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = Color.White.copy(alpha = 0.08f)
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            // SEARCH
            Box(
                Modifier
                    .fillMaxWidth()
                    .background(Color.White.copy(alpha = 0.03f), RoundedCornerShape(14.dp))
                    .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(14.dp))
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Rounded.Search, null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(start = 12.dp).size(16.dp))
                    androidx.compose.material3.OutlinedTextField(
                        value = query,
                        onValueChange = { query = it },
                        placeholder = { Text("Cari nama aplikasi...", fontSize = 13.5.sp) },
                        trailingIcon = {
                            if (query.isNotBlank()) {
                                Icon(Icons.Rounded.Close, null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(end = 8.dp).size(16.dp).clickable { query = "" })
                            }
                        },
                        colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent,
                            focusedTextColor = MaterialTheme.colorScheme.onSurface,
                            cursorColor = MaterialTheme.colorScheme.primary
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            }

            Spacer(Modifier.height(10.dp))

            // FILTER CHIPS
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                LibChip("Semua", filter == LibFilter.ALL) { filter = LibFilter.ALL }
                LibChip("Sukses", filter == LibFilter.SUCCESS) { filter = LibFilter.SUCCESS }
                LibChip("Gagal", filter == LibFilter.FAILED) { filter = LibFilter.FAILED }
            }

            Spacer(Modifier.height(14.dp))

            when {
                builds.isEmpty() -> EmptyState(onOpenWizard)
                filtered.isEmpty() -> Box(
                    Modifier.fillMaxWidth().padding(vertical = 30.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Tidak ada build yang cocok dengan pencarian/filter.", fontSize = 12.5.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                else -> filtered.forEach { rec ->
                    BuildCard(
                        record = rec,
                        onInstall = {
                            runCatching {
                                val file = File(rec.apkPath ?: error("no path"))
                                require(file.exists()) { "file missing" }
                                val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
                                context.startActivity(
                                    Intent(Intent.ACTION_VIEW)
                                        .setDataAndType(uri, "application/vnd.android.package-archive")
                                        .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK)
                                )
                            }.onFailure {
                                scope.launch { snackbar.showSnackbar("Hook instalasi siap — butuh FileProvider di Manifest (AI logic)") }
                            }
                        },
                        onShare = {
                            runCatching {
                                val file = File(rec.apkPath ?: error("no path"))
                                require(file.exists()) { "file missing" }
                                val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
                                context.startActivity(
                                    Intent.createChooser(
                                        Intent(Intent.ACTION_SEND)
                                            .setType("application/vnd.android.package-archive")
                                            .putExtra(Intent.EXTRA_STREAM, uri)
                                            .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION),
                                        "Bagikan APK"
                                    )
                                )
                            }.onFailure {
                                scope.launch { snackbar.showSnackbar("Hook share siap — butuh FileProvider di Manifest (AI logic)") }
                            }
                        },
                        onCopyConfig = {
                            clipboard.setText(
                                AnnotatedString(
                                    """{"appName":"${rec.appName}","packageName":"${rec.packageName}","version":"${rec.versionName}","mode":"${rec.modeLabel}"}"""
                                )
                            )
                            scope.launch { snackbar.showSnackbar("📄 Config disalin!") }
                        },
                        onDelete = { pendingDelete = rec },
                        onRebuild = { onRebuild(rec) }
                    )
                    Spacer(Modifier.height(12.dp))
                }
            }
            Spacer(Modifier.height(8.dp))
        }
    }

    pendingDelete?.let { rec ->
        AlertDialog(
            onDismissRequest = { pendingDelete = null },
            title = { Text("Hapus Build?", fontWeight = FontWeight.Bold) },
            text = { Text("Build \"${rec.appName}\" (${rec.apkSizeMb} MB) akan dihapus permanen dari penyimpanan.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        BuildRepository.delete(rec.id)
                        pendingDelete = null
                        scope.launch { snackbar.showSnackbar("🗑 Build dihapus (${rec.apkSizeMb} MB bebas)") }
                    }
                ) { Text("Hapus", color = Color(0xFFF87171), fontWeight = FontWeight.Bold) }
            },
            dismissButton = {
                TextButton(onClick = { pendingDelete = null }) { Text("Batal") }
            },
            containerColor = MaterialTheme.colorScheme.surface
        )
    }
}

@Composable
private fun StatBox(value: String, label: String, valueColor: Color, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .background(Color.White.copy(alpha = 0.03f), RoundedCornerShape(14.dp))
            .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(14.dp))
            .padding(vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(value, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = valueColor)
        Text(label, fontSize = 10.sp, letterSpacing = 0.6.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun LibChip(label: String, selected: Boolean, onClick: () -> Unit) {
    Box(
        Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(if (selected) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f) else Color.Transparent)
            .border(
                1.dp,
                if (selected) MaterialTheme.colorScheme.primary.copy(alpha = 0.45f) else Color.White.copy(alpha = 0.08f),
                RoundedCornerShape(999.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 7.dp)
    ) {
        Text(
            label,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun EmptyState(onOpenWizard: () -> Unit) {
    Column(
        Modifier.fillMaxWidth().padding(vertical = 46.dp, horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(86.dp)
                .clip(CircleShape)
                .background(Brush.radialGradient(listOf(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f), Color.Transparent)))
                .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.4f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Rounded.Inventory2, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(34.dp))
        }
        Spacer(Modifier.height(18.dp))
        Text("Belum Ada Build", fontSize = 17.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        Text(
            "Hasil pembungkusan aplikasi Anda akan tersimpan rapi di sini — siap dipasang ulang kapan saja tanpa mengulang wizard.",
            fontSize = 12.5.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
        Spacer(Modifier.height(18.dp))
        Button(
            onClick = onOpenWizard,
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
        ) {
            Text("＋ Buat Aplikasi Pertama", fontWeight = FontWeight.Bold, color = Color.White)
        }
    }
}

@Composable
private fun BuildCard(
    record: BuildRecord,
    onInstall: () -> Unit,
    onShare: () -> Unit,
    onCopyConfig: () -> Unit,
    onDelete: () -> Unit,
    onRebuild: () -> Unit
) {
    var logExpanded by remember { mutableStateOf(false) }
    var menuOpen by remember { mutableStateOf(false) }
    val isSuccess = record.status == BuildStatus.SUCCESS

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(20.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.08f))
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Brush.linearGradient(listOf(record.themeColor(), record.themeColor().copy(alpha = 0.6f)))),
                    contentAlignment = Alignment.Center
                ) {
                    Text(record.initials(), color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
                }
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text(
                        text = record.appName,
                        fontSize = 14.5.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1
                    )
                    Text(
                        text = "${record.packageName} • ${record.dateLabel()}",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1
                    )
                }
                Box(
                    Modifier
                        .background(
                            if (isSuccess) Color(0x1F4ADE80) else Color(0x1FF87171),
                            RoundedCornerShape(999.dp)
                        )
                        .border(1.dp, if (isSuccess) Color(0x594ADE80) else Color(0x66F87171), RoundedCornerShape(999.dp))
                        .padding(horizontal = 9.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = if (isSuccess) "SIGNED" else "GAGAL",
                        fontSize = 9.5.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 0.8.sp,
                        color = if (isSuccess) Color(0xFF4ADE80) else Color(0xFFF87171)
                    )
                }
            }

            Spacer(Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                Text("📦 ${record.apkSizeMb} MB", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("🔧 Mode ${record.modeLabel.replace("Situs Web (URL)", "URL").replace("ZIP Offline", "ZIP").replace("Kode HTML", "HTML")}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("⏱ ${record.durationLabel}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            if (!isSuccess && record.errorMessage != null) {
                Spacer(Modifier.height(10.dp))
                Box(
                    Modifier
                        .fillMaxWidth()
                        .background(Color(0x14F87171), RoundedCornerShape(12.dp))
                        .border(1.dp, Color(0x4DF87171), RoundedCornerShape(12.dp))
                        .padding(10.dp)
                ) {
                    Text("⛔ ${record.errorMessage}", fontSize = 11.5.sp, color = Color(0xFFFCA5A5))
                }
            }

            Spacer(Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = if (isSuccess) onInstall else onRebuild,
                    modifier = Modifier.weight(1.4f).height(38.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isSuccess) MaterialTheme.colorScheme.primary else Color(0xFFF87171)
                    )
                ) {
                    Text(if (isSuccess) "Pasang" else "Coba Lagi", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
                Box(
                    Modifier
                        .weight(1f)
                        .height(38.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White.copy(alpha = 0.03f))
                        .border(1.dp, if (logExpanded) MaterialTheme.colorScheme.primary.copy(alpha = 0.5f) else Color.White.copy(alpha = 0.08f), RoundedCornerShape(12.dp))
                        .clickable { logExpanded = !logExpanded },
                    contentAlignment = Alignment.Center
                ) {
                    Text("Log", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = if (logExpanded) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Box(
                    Modifier
                        .weight(1f)
                        .height(38.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White.copy(alpha = 0.03f))
                        .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(12.dp))
                        .clickable(onClick = onShare),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Bagikan", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Box {
                    Box(
                        Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.White.copy(alpha = 0.03f))
                            .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(12.dp))
                            .clickable { menuOpen = true },
                        contentAlignment = Alignment.Center
                    ) {
                        Text("⋮", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    DropdownMenu(expanded = menuOpen, onDismissRequest = { menuOpen = false }) {
                        DropdownMenuItem(text = { Text("🔄 Bangun Ulang") }, onClick = { menuOpen = false; onRebuild() })
                        DropdownMenuItem(text = { Text("📄 Salin Config") }, onClick = { menuOpen = false; onCopyConfig() })
                        DropdownMenuItem(text = { Text("🗑 Hapus Build", color = Color(0xFFF87171)) }, onClick = { menuOpen = false; onDelete() })
                    }
                }
            }

            if (logExpanded) {
                Spacer(Modifier.height(10.dp))
                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(110.dp)
                        .background(Color(0xFF0B0B12), RoundedCornerShape(12.dp))
                        .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(12.dp))
                        .verticalScroll(rememberScrollState())
                        .padding(10.dp)
                ) {
                    Text(
                        text = record.logLines.joinToString("\n"),
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace,
                        color = Color(0xFF8F9BFF),
                        lineHeight = 16.sp
                    )
                }
            }
        }
    }
}
