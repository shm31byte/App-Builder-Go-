package com.example.appbuildergo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import android.graphics.Color as AndroidColor
import com.example.appbuildergo.data.BuildRepository
import com.example.appbuildergo.ui.AppBuilderGoApp
import com.example.appbuildergo.ui.theme.AppBuilderGoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(AndroidColor.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(AndroidColor.TRANSPARENT)
        )
        super.onCreate(savedInstanceState)
        BuildRepository.init(this)
        setContent {
            AppBuilderGoTheme {
                AppBuilderGoApp()
            }
        }
    }
}