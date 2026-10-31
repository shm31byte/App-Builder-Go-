package com.example.appbuildergo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.appbuildergo.ui.theme.AppBuilderGoTheme
import com.example.appbuildergo.ui.wizard.WizardScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppBuilderGoTheme {
                WizardScreen()
            }
        }
    }
}
