package com.example.appbuildergo.ui.build

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Error
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
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
import com.example.appbuildergo.buildlog.BuildLogger
import com.example.appbuildergo.buildlog.LogLevel
import com.example.appbuildergo.data.BuildRecord
import com.example.appbuildergo.data.BuildRepository
import com.example.appbuildergo.data.BuildStatus
import com.example.appbuildergo.data.appInitials
import com.example.appbuildergo.data.packageSlug
import com.example.appbuildergo.ui.wizard.WizardData
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale

enum class PackPhase { RUNNING, SUCCESS, FAILED }

private enum class StageState { PENDING, ACTIVE, DONE }

private val STAGE_NAMES = listOf(
    "Validasi Config",
    "Preparasi Aset",
    "Bundling Konten",
    "Kompilasi & Signing",
    "Finalisasi APK"
)

private data class ScriptLine(
    val delayMs: Long,
    val level: LogLevel,
    val message: String,
    val progress: Float,
    val subLabel: String? = null,
    val stageActive: Int = -1,
    val stageDone: Int = -1
)

private fun buildScript(config: WizardData): List<ScriptLine> {
    val sourceLine = if (config.htmlCode.isNotBlank())
        "Kode HTML custom terdeteksi (${config.htmlCode.length} karakter)"
    else "Menggunakan template wrapper default"
    return listOf(
        ScriptLine(400, LogLevel.INFO, "Memulai proses pembungkusan (build #${System.currentTimeMillis() % 997})...", 0.05f, "Memuat config wizard...", stageActive = 0),
        ScriptLine(900, LogLevel.INFO, "Config wizard diterima: mode=${config.conversionMode}", 0.10f),
        ScriptLine(800, LogLevel.OK, "Config valid — package: com.example.${packageSlug(config.appName)}", 0.16f),
        ScriptLine(900, LogLevel.INFO, "Menyiapkan workspace /build/${packageSlug(config.appName)}", 0.22f, "Menyiapkan workspace & aset...", stageDone = 0, stageActive = 1),
        ScriptLine(700, LogLevel.DEBUG, "cache dir dibersihkan: /data/local/tmp/abg-cache", 0.27f),
        ScriptLine(900, LogLevel.INFO, sourceLine, 0.34f),
        ScriptLine(1000, LogLevel.INFO, "Injeksi WebView wrapper + JavaScript bridge", 0.42f, "Bundling konten web...", stageDone = 1, stageActive = 2),
        ScriptLine(800, LogLevel.DEBUG, "javaScript=true, domStorage=true, splash=${config.splashEnabled}", 0.48f),
        ScriptLine(900, LogLevel.OK, "24 aset berhasil disalin (1.2 MB)", 0.56f),
        ScriptLine(1100, LogLevel.INFO, "Kompilasi resource & AndroidManifest", 0.64f, "Kompilasi & signing package...", stageDone = 2, stageActive = 3),
        ScriptLine(900, LogLevel.WARN, "minSdk 24: splash V2 dinonaktifkan → fallback splash legacy", 0.71f),
        ScriptLine(1000, LogLevel.INFO, "Signing package via debug keystore...", 0.78f),
        ScriptLine(800, LogLevel.DEBUG, "zipalign 4-byte alignment: OK", 0.83f),
        ScriptLine(1100, LogLevel.OK, "APK tersign: app-release.apk", 0.91f, "Finalisasi & verifikasi...", stageDone = 3, stageActive = 4),
        ScriptLine(800, LogLevel.DEBUG, "sha256 terverifikasi", 0.96f),
        ScriptLine(700, LogLevel.INFO, "Menyimpan riwayat ke menu Build Saya...", 1.0f, "Menyimpan riwayat...", stageDone = 4)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PackagingScreen(
    config: WizardData,
    onExit: () -> Unit
) {
    val context = LocalContext.current
    val clipboard = LocalClipboardManager.current
    val scope = rememberCoroutineScope()
    val snackbar = remember { SnackbarHostState() }

    val allEntries by BuildLogger.entries.collectAsState()
    var verbose by remember { mutableStateOf(false) }
    var phase by remember { mutableStateOf(PackPhase.RUNNING) }
    var progress by remember { mutableFloatStateOf(0f) }
    var subLabel by remember { mutableStateOf("Menginisialisasi build engine...") }
    var stages by remember { mutableStateOf(List(STAGE_NAMES.size) { StageState.PENDING }) }
    var runToken by remember { mutableIntStateOf(0) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var resultSizeMb by remember { mutableFloatStateOf(0f) }
    val listState = rememberLazyListState()

    val visibleEntries = allEntries.filter { verbose || it.level != LogLevel.DEBUG }

    LaunchedEffect(runToken) {
        BuildLogger.reset()
        phase = PackPhase.RUNNING
        progress = 0f
        errorMessage = null
        subLabel = "Menginisialisasi build engine..."
        stages = List(STAGE_NAMES.size) { StageState.PENDING }
        val started = System.currentTimeMillis()

        if (config.appName.isBlank()) {
            BuildLogger.e("Validasi gagal: nama aplikasi kosong")
            errorMessage = "Nama aplikasi kosong. Kembali ke wizard dan isi nama aplikasi."
            phase = PackPhase.FAILED
            BuildRepository.save(
                BuildRecord(
                    appName = "(Tanpa Nama)",
                    packageName = "com.example.app",
                    modeLabel = config.conversionMode,
                    apkSizeMb = 0.0,
                    durationLabel = "-",
                    status = BuildStatus.FAILED,
                    errorMessage = errorMessage,
                    logLines = BuildLogger.entries.value.map { it.render() },
                    configSnapshot = config
                )
            )
            return@LaunchedEffect
        }

        // TODO(AI logic): ganti simulasi ini dengan panggilan engine build asli.
        for (line in buildScript(config)) {
            delay(line.delayMs)
            if (line.stageActive >= 0) stages = stages.toMutableList().also { it[line.stageActive] = StageState.ACTIVE }
            if (line.stageDone >= 0) stages = stages.toMutableList().also { it[line.stageDone] = StageState.DONE }
            progress = line.progress
            line.subLabel?.let { subLabel = it }
            when (line.level) {
                LogLevel.INFO -> BuildLogger.i(line.message)
                LogLevel.OK -> BuildLogger.ok(line.message)
                LogLevel.WARN -> BuildLogger.w(line.message)
                LogLevel.ERROR -> BuildLogger.e(line.message)
                LogLevel.DEBUG -> BuildLogger.d(line.message)
                LogLevel.DONE -> BuildLogger.done(line.message)
            }
        }

        val durationSec = (System.currentTimeMillis() - started) / 1000.0
        val sizeMb = 2.0 + config.htmlCode.length / 2048.0 + if (config.offlineSupport) 1.2 else 0.0
        resultSizeMb = sizeMb.toFloat()
        BuildLogger.done("Pembungkusan selesai dalam ${String.format(Locale.US, "%.1f", durationSec)} detik 🎉")
        phase = PackPhase.SUCCESS
        BuildRepository.save(
            BuildRecord(
                appName = config.appName,
                packageName = "com.example.${packageSlug(config.appName)}",
                themeColorHex = String.format(
                    "#%08X",
                    android.graphics.Color.argb(
                        255,
                        (config.themeColor.red * 255).toInt(),
                        (config.themeColor.green * 255).toInt(),
                        (config.themeColor.blue * 255).toInt()
                    )
                ),
                modeLabel = config.conversionMode,
                apkSizeMb = Math.round(sizeMb * 10) / 10.0,
                durationLabel = "${String.format(Locale.US, "%.1f", durationSec)}s",
                status = BuildStatus.SUCCESS,
                logLines = BuildLogger.entries.value.map { it.render() },
                apkPath = context.filesDir.absolutePath + "/apks/${packageSlug(config.appName)}-release.apk",
                configSnapshot = config
            )
        )
    }

    LaunchedEffect(visibleEntries.size) {
        if (visibleEntries.isNotEmpty()) listState.animateScrollToItem(visibleEntries.size - 1)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Proses Pembungkusan", fontWeight = FontWeight.Bold, fontSize = 17.sp)
                        Text(
                            "${config.appName.ifBlank { "(Tanpa Nama)" }} • v1.0.0",
                            fontSize = 11.5.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    if (phase != PackPhase.RUNNING) {
                        Icon(
                            Icons.Rounded.ArrowBack,
                            contentDescription = "Kembali",
                            tint = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier
                                .padding(start = 12.dp)
                                .size(24.dp)
                                .clickable { onExit() }
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        bottomBar = {
            Column {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(
                            Brush.horizontalGradient(
                                listOf(Color.Transparent, MaterialTheme.colorScheme.primary.copy(alpha = 0.35f), Color.Transparent)
                            )
                        )
                )
                Row(
                    Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.background.copy(alpha = 0.96f))
                        .windowInsetsPadding(WindowInsets.navigationBars)
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            clipboard.setText(AnnotatedString(allEntries.joinToString("\n") { it.render() }))
                            scope.launch { snackbar.showSnackbar("📋 Log disalin ke clipboard") }
                        },
                        modifier = Modifier.weight(1f).height(54.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) { Text("Salin Log", fontWeight = FontWeight.SemiBold) }
                    Button(
                        onClick = {
                            when (phase) {
                                PackPhase.RUNNING -> Unit
                                PackPhase.SUCCESS -> onExit()
                                PackPhase.FAILED -> runToken++
                            }
                        },
                        enabled = phase != PackPhase.RUNNING,
                        modifier = Modifier.weight(1.6f).height(54.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (phase == PackPhase.FAILED) Color(0xFFF87171)
                            else MaterialTheme.colorScheme.primary
                        ),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
                    ) {
                        Text(
                            text = when (phase) {
                                PackPhase.RUNNING -> "Sedang Membungkus…"
                                PackPhase.SUCCESS -> "Selesai ✓"
                                PackPhase.FAILED -> "Coba Lagi"
                            },
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        },
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
            // HERO
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(20.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.08f))
            ) {
                Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(Brush.linearGradient(listOf(config.themeColor, config.themeColor.copy(alpha = 0.6f)))),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(appInitials(config.appName), color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 20.sp)
                    }
                    Spacer(Modifier.width(14.dp))
                    Column(Modifier.weight(1f)) {
                        Text(config.appName.ifBlank { "(Tanpa Nama)" }, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Text(
                            "com.example.${packageSlug(config.appName)} • v1.0.0",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Box(
                        Modifier
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f), RoundedCornerShape(999.dp))
                            .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.35f), RoundedCornerShape(999.dp))
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Text(
                            text = config.conversionMode.replace("Situs Web (URL)", "URL").replace("ZIP Offline", "ZIP").replace("Kode HTML", "HTML"),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            // PROGRESS
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(20.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.08f))
            ) {
                Column(Modifier.padding(16.dp)) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = when (phase) {
                                PackPhase.RUNNING -> "Membungkus..."
                                PackPhase.SUCCESS -> "Selesai"
                                PackPhase.FAILED -> "Gagal"
                            },
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text("${(progress * 100).toInt()}%", fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary)
                    }
                    Spacer(Modifier.height(10.dp))
                    LinearProgressIndicator(
                        progress = progress,
                        modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = Color.White.copy(alpha = 0.08f)
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(subLabel, fontSize = 11.5.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            Spacer(Modifier.height(12.dp))

            // STAGES
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(20.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.08f))
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text("Tahapan Pembungkusan", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(10.dp))
                    stages.forEachIndexed { index, state ->
                        Row(
                            Modifier.fillMaxWidth().padding(vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val dotColor = when (state) {
                                StageState.DONE -> Color(0xFF4ADE80)
                                StageState.ACTIVE -> MaterialTheme.colorScheme.primary
                                StageState.PENDING -> Color.White.copy(alpha = 0.12f)
                            }
                            Box(Modifier.size(10.dp).clip(CircleShape).background(dotColor))
                            Spacer(Modifier.width(10.dp))
                            Text(
                                text = STAGE_NAMES[index],
                                fontSize = 13.sp,
                                fontWeight = if (state == StageState.ACTIVE) FontWeight.SemiBold else FontWeight.Normal,
                                color = if (state == StageState.PENDING) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                text = when (state) {
                                    StageState.DONE -> "SELESAI"
                                    StageState.ACTIVE -> "BERJALAN"
                                    StageState.PENDING -> "MENUNGGU"
                                },
                                fontSize = 10.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = when (state) {
                                    StageState.DONE -> Color(0xFF4ADE80)
                                    StageState.ACTIVE -> MaterialTheme.colorScheme.primary
                                    StageState.PENDING -> MaterialTheme.colorScheme.onSurfaceVariant
                                }
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            // CONSOLE
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0B0B12)),
                shape = RoundedCornerShape(20.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.08f))
            ) {
                Column {
                    Row(
                        Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(if (phase == PackPhase.RUNNING) Color(0xFFF87171) else Color(0xFF4ADE80))
                        )
                        Spacer(Modifier.width(8.dp))
                        Text("BUILD LOG", fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 1.2.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(Modifier.weight(1f))
                        Box(
                            Modifier
                                .clip(RoundedCornerShape(999.dp))
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f))
                                .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.35f), RoundedCornerShape(999.dp))
                                .clickable { verbose = !verbose }
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = if (verbose) "VERBOSE ON" else "VERBOSE OFF",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.primary,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                    Box(Modifier.fillMaxWidth().height(1.dp).background(Color.White.copy(alpha = 0.06f)))
                    LazyColumn(
                        state = listState,
                        modifier = Modifier.fillMaxWidth().height(240.dp).padding(horizontal = 14.dp, vertical = 12.dp)
                    ) {
                        items(visibleEntries) { entry ->
                            Row(Modifier.padding(vertical = 1.dp)) {
                                Text(entry.timestamp, fontSize = 10.5.sp, fontFamily = FontFamily.Monospace, color = Color(0xFF44445C))
                                Spacer(Modifier.width(8.dp))
                                Text(entry.level.name.padEnd(5), fontSize = 10.5.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, color = levelColor(entry.level))
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    text = entry.message,
                                    fontSize = 10.5.sp,
                                    fontFamily = FontFamily.Monospace,
                                    color = if (entry.level == LogLevel.ERROR) Color(0xFFFCA5A5)
                                    else if (entry.level == LogLevel.WARN) Color(0xFFFDE68A)
                                    else Color(0xFFC9C9E0)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            // BANNERS
            if (phase == PackPhase.SUCCESS) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0x144ADE80)),
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x594ADE80))
                ) {
                    Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Rounded.CheckCircle, null, tint = Color(0xFF4ADE80), modifier = Modifier.size(22.dp))
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text("Pembungkusan Berhasil!", fontWeight = FontWeight.Bold, fontSize = 13.5.sp, color = Color(0xFF4ADE80))
                            Text("app-release.apk ($resultSizeMb MB) tersign & tersimpan di Build Saya.", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
            if (phase == PackPhase.FAILED) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0x14F87171)),
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x66F87171))
                ) {
                    Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Rounded.Error, null, tint = Color(0xFFF87171), modifier = Modifier.size(22.dp))
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text("Pembungkusan Gagal", fontWeight = FontWeight.Bold, fontSize = 13.5.sp, color = Color(0xFFF87171))
                            Text(errorMessage ?: "Periksa log untuk detail.", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
            Spacer(Modifier.height(8.dp))
        }
    }
}

private fun levelColor(level: LogLevel): Color = when (level) {
    LogLevel.INFO -> Color(0xFF8F9BFF)
    LogLevel.OK -> Color(0xFF4ADE80)
    LogLevel.WARN -> Color(0xFFFBBF24)
    LogLevel.ERROR -> Color(0xFFF87171)
    LogLevel.DEBUG -> Color(0xFF6B7280)
    LogLevel.DONE -> Color(0xFF5C5CFF)
}
