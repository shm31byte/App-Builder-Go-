package com.example.appbuildergo.data

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.ui.graphics.toArgb
import com.example.appbuildergo.ui.wizard.WizardData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONArray
import org.json.JSONObject

object BuildRepository {
    private const val PREFS_NAME = "abg_builds"
    private const val KEY_RECORDS = "build_records_json"
    const val QUOTA_MB: Double = 100.0

    private val _builds = MutableStateFlow<List<BuildRecord>>(emptyList())
    val builds: StateFlow<List<BuildRecord>> = _builds.asStateFlow()

    private var prefs: SharedPreferences? = null

    fun init(context: Context) {
        if (prefs != null) return
        prefs = context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        _builds.value = load()
    }

    fun save(record: BuildRecord) {
        _builds.value = (listOf(record) + _builds.value).distinctBy { it.id }
        persist()
    }

    fun delete(id: Long) {
        _builds.value = _builds.value.filterNot { it.id == id }
        persist()
    }

    fun totalSizeMb(): Double = _builds.value.sumOf { it.apkSizeMb }

    private fun persist() = runCatching {
        val arr = JSONArray()
        _builds.value.forEach { arr.put(serialize(it)) }
        prefs?.edit()?.putString(KEY_RECORDS, arr.toString())?.apply()
    }

    private fun load(): List<BuildRecord> = runCatching {
        val raw = prefs?.getString(KEY_RECORDS, null) ?: return emptyList()
        val arr = JSONArray(raw)
        (0 until arr.length()).mapNotNull { deserialize(arr.optJSONObject(it) ?: return@mapNotNull null) }
    }.getOrDefault(emptyList())

    private fun serialize(r: BuildRecord): JSONObject = JSONObject().apply {
        put("id", r.id)
        put("appName", r.appName)
        put("packageName", r.packageName)
        put("versionName", r.versionName)
        put("themeColorHex", r.themeColorHex)
        put("modeLabel", r.modeLabel)
        put("apkSizeMb", r.apkSizeMb)
        put("durationLabel", r.durationLabel)
        put("createdAtMillis", r.createdAtMillis)
        put("status", r.status.name)
        put("errorMessage", r.errorMessage ?: JSONObject.NULL)
        put("apkPath", r.apkPath ?: JSONObject.NULL)
        put("logLines", JSONArray(r.logLines))
        r.configSnapshot?.let { c ->
            put("config", JSONObject().apply {
                put("appName", c.appName)
                put("conversionMode", c.conversionMode)
                put("htmlCode", c.htmlCode)
                put("splashEnabled", c.splashEnabled)
                put("themeColorHex", String.format("#%08X", c.themeColor.toArgb()))
                put("offlineSupport", c.offlineSupport)
                put("pullToRefresh", c.pullToRefresh)
                put("permCamera", c.permCamera)
                put("permLocation", c.permLocation)
                put("permStorage", c.permStorage)
            })
        }
    }

    private fun deserialize(o: JSONObject): BuildRecord? = runCatching {
        val logArr = o.optJSONArray("logLines")
        BuildRecord(
            id = o.getLong("id"),
            appName = o.getString("appName"),
            packageName = o.getString("packageName"),
            versionName = o.optString("versionName", "1.0.0"),
            themeColorHex = o.optString("themeColorHex", "#FF5C5CFF"),
            modeLabel = o.getString("modeLabel"),
            apkSizeMb = o.optDouble("apkSizeMb", 0.0),
            durationLabel = o.optString("durationLabel", "-"),
            createdAtMillis = o.optLong("createdAtMillis", System.currentTimeMillis()),
            status = runCatching { BuildStatus.valueOf(o.optString("status", "SUCCESS")) }.getOrDefault(BuildStatus.SUCCESS),
            errorMessage = if (o.isNull("errorMessage")) null else o.optString("errorMessage"),
            apkPath = if (o.isNull("apkPath")) null else o.optString("apkPath"),
            logLines = if (logArr == null) emptyList() else (0 until logArr.length()).map { logArr.getString(it) },
            configSnapshot = o.optJSONObject("config")?.let { c ->
                WizardData(
                    appName = c.optString("appName", ""),
                    conversionMode = c.optString("conversionMode", "Situs Web (URL)"),
                    htmlCode = c.optString("htmlCode", ""),
                    splashEnabled = c.optBoolean("splashEnabled", true),
                    themeColor = hexToColor(c.optString("themeColorHex", "#FF5C5CFF")),
                    offlineSupport = c.optBoolean("offlineSupport", false),
                    pullToRefresh = c.optBoolean("pullToRefresh", true),
                    permCamera = c.optBoolean("permCamera", false),
                    permLocation = c.optBoolean("permLocation", false),
                    permStorage = c.optBoolean("permStorage", true)
                )
            }
        )
    }.getOrNull()
}