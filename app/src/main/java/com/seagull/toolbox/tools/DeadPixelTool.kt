package com.seagull.toolbox.tools

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.seagull.toolbox.ui.ActionRow
import com.seagull.toolbox.ui.Gap
import com.seagull.toolbox.ui.SectionTitle

private val COLORS = listOf(
    "红" to Color(0xFFFF0000),
    "绿" to Color(0xFF00FF00),
    "蓝" to Color(0xFF0000FF),
    "白" to Color(0xFFFFFFFF),
    "黑" to Color(0xFF000000),
    "青" to Color(0xFF00FFFF),
    "品红" to Color(0xFFFF00FF),
    "黄" to Color(0xFFFFFF00),
    "灰" to Color(0xFF808080)
)

@Composable
fun DeadPixelTool() {
    var full by remember { mutableStateOf(false) }
    var idx by remember { mutableIntStateOf(0) }

    Column {
        Text(
            "逐色全屏显示，检查屏幕坏点、亮点、暗斑和色偏。",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Gap(10)
        ActionRow("进入全屏检测" to { idx = 0; full = true })

        Gap(8)
        SectionTitle("检测顺序")
        COLORS.forEachIndexed { i, (name, c) ->
            Row(
                Modifier.padding(vertical = 3.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    Modifier
                        .background(c)
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {}
                Text("${i + 1}. $name", fontSize = 12.sp)
            }
        }
        Gap(10)
        Text(
            "全屏后：点屏幕任意处切换下一色，长按退出。建议调高亮度、关灯看更明显。",
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }

    if (full) {
        Dialog(
            onDismissRequest = { full = false },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            val (name, color) = COLORS[idx]
            Box(
                Modifier
                    .fillMaxSize()
                    .background(color)
                    .clickable {
                        idx++
                        if (idx >= COLORS.size) full = false
                    },
                contentAlignment = Alignment.BottomCenter
            ) {
                Text(
                    "$name    ${idx + 1}/${COLORS.size}   ·   点击下一色",
                    color = if (color == Color(0xFF000000)) Color.White else Color.Black.copy(alpha = 0.55f),
                    fontSize = 12.sp,
                    modifier = Modifier.padding(bottom = 42.dp)
                )
            }
        }
    }
}
