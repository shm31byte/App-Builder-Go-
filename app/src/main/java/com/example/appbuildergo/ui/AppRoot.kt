package com.example.appbuildergo.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Construction
import androidx.compose.material.icons.rounded.Inventory2
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.appbuildergo.data.BuildRepository
import com.example.appbuildergo.ui.build.PackagingScreen
import com.example.appbuildergo.ui.library.BuildLibraryScreen
import com.example.appbuildergo.ui.wizard.WizardData
import com.example.appbuildergo.ui.wizard.WizardScreen

enum class MainTab { BUILDER, LIBRARY }

@Composable
fun AppBuilderGoApp() {
    var tab by remember { mutableStateOf(MainTab.BUILDER) }
    var packagingConfig by remember { mutableStateOf<WizardData?>(null) }
    val builds by BuildRepository.builds.collectAsState()

    val tabBar: @Composable () -> Unit = {
        MainTabBar(
            selected = tab,
            libraryCount = builds.size,
            onSelect = { tab = it }
        )
    }

    val config = packagingConfig
    when {
        config != null -> PackagingScreen(
            config = config,
            onExit = {
                packagingConfig = null
                tab = MainTab.LIBRARY
            }
        )
        tab == MainTab.BUILDER -> WizardScreen(
            onGenerate = { data -> packagingConfig = data },
            tabBar = tabBar
        )
        else -> BuildLibraryScreen(
            tabBar = tabBar,
            onOpenWizard = { tab = MainTab.BUILDER },
            onRebuild = { rec ->
                packagingConfig = rec.configSnapshot
                    ?: WizardData(appName = rec.appName, conversionMode = rec.modeLabel)
            }
        )
    }
}

@Composable
fun MainTabBar(
    selected: MainTab,
    libraryCount: Int,
    onSelect: (MainTab) -> Unit
) {
    Column {
        Box(
            Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(
                    Brush.horizontalGradient(
                        listOf(
                            Color.Transparent,
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.35f),
                            Color.Transparent
                        )
                    )
                )
        )
        Row(
            Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.background.copy(alpha = 0.96f))
                .windowInsetsPadding(WindowInsets.navigationBars)
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TabItem(
                icon = Icons.Rounded.Construction,
                label = "Buat App",
                selected = selected == MainTab.BUILDER,
                badge = 0,
                onClick = { onSelect(MainTab.BUILDER) },
                modifier = Modifier.weight(1f)
            )
            TabItem(
                icon = Icons.Rounded.Inventory2,
                label = "Build Saya",
                selected = selected == MainTab.LIBRARY,
                badge = libraryCount,
                onClick = { onSelect(MainTab.LIBRARY) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun TabItem(
    icon: ImageVector,
    label: String,
    selected: Boolean,
    badge: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(
                if (selected) MaterialTheme.colorScheme.primary.copy(alpha = 0.14f)
                else Color.Transparent
            )
            .border(
                width = 1.dp,
                color = if (selected) MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
                else Color.Transparent,
                shape = RoundedCornerShape(14.dp)
            )
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            icon,
            contentDescription = label,
            tint = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(18.dp)
        )
        androidx.compose.foundation.layout.Spacer(Modifier.size(8.dp))
        Text(
            text = label,
            fontSize = 13.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
            color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
        )
        if (badge > 0) {
            androidx.compose.foundation.layout.Spacer(Modifier.size(6.dp))
            Box(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(9.dp))
                    .padding(horizontal = 5.dp, vertical = 1.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "$badge",
                    color = Color.White,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }
        }
    }
}
