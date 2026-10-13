package com.seagull.toolbox

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.seagull.toolbox.core.Tool
import com.seagull.toolbox.ui.HomeScreen
import com.seagull.toolbox.ui.ToolPage
import com.seagull.toolbox.ui.ToolboxTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ToolboxTheme {
                var opened by remember { mutableStateOf<Tool?>(null) }
                val current = opened
                if (current == null) {
                    HomeScreen(onOpen = { opened = it })
                } else {
                    BackHandler { opened = null }
                    ToolPage(tool = current, onBack = { opened = null })
                }
            }
        }
    }
}
