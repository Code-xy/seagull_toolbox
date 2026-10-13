package com.seagull.toolbox.tools

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.seagull.toolbox.core.bytesToHex
import com.seagull.toolbox.ui.ActionRow
import com.seagull.toolbox.ui.Chip
import com.seagull.toolbox.ui.Gap
import com.seagull.toolbox.ui.ResultBox
import com.seagull.toolbox.ui.ToolInput
import java.security.MessageDigest

private val ALGOS = listOf("MD5", "SHA-1", "SHA-256", "SHA-512")

@Composable
fun HashTool() {
    var src by remember { mutableStateOf("") }
    var algo by remember { mutableStateOf("SHA-256") }
    var upper by remember { mutableStateOf(false) }
    var out by remember { mutableStateOf("") }
    var err by remember { mutableStateOf("") }

    fun compute() {
        if (src.isEmpty()) { out = ""; err = ""; return }
        try {
            val d = MessageDigest.getInstance(algo).digest(src.toByteArray(Charsets.UTF_8))
            var hex = bytesToHex(d)
            if (upper) hex = hex.uppercase()
            out = buildString {
                appendLine("${algo.padEnd(8)} $hex")
                appendLine()
                appendLine("输入长度   ${src.toByteArray(Charsets.UTF_8).size} 字节")
                appendLine("输入字符   ${src.length}")
                append("摘要长度   ${d.size * 8} 位")
            }
            err = ""
        } catch (t: Throwable) {
            err = t.message ?: "计算失败"
            out = ""
        }
    }

    Column {
        ToolInput(src, { src = it }, placeholder = "输入要计算哈希的文本…", minLines = 4, maxLines = 10)
        Gap(6)
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            ALGOS.forEach { Chip(it, algo == it) { algo = it; compute() } }
        }
        ActionRow(
            "计算" to { compute() },
            if (upper) "转小写" to { upper = false; compute() } else "转大写" to { upper = true; compute() },
            "清空" to { src = ""; out = ""; err = "" }
        )
        if (err.isNotEmpty()) {
            Text(err, color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
            Gap(6)
        }
        ResultBox(out, label = "哈希")
        Gap(8)
        Text(
            "用 JDK 内置 MessageDigest，支持中文（UTF-8 编码）。",
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
