package com.seagull.toolbox.tools

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.sp
import com.seagull.toolbox.ui.ActionRow
import com.seagull.toolbox.ui.Gap
import com.seagull.toolbox.ui.ResultBox
import com.seagull.toolbox.ui.SectionTitle
import com.seagull.toolbox.ui.ToolInput
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val PATTERNS = listOf(
    "yyyy-MM-dd HH:mm:ss",
    "yyyy-MM-dd HH:mm",
    "yyyy-MM-dd",
    "yyyy/MM/dd HH:mm:ss",
    "yyyy/MM/dd",
    "yyyyMMddHHmmss",
    "yyyyMMdd",
    "HH:mm:ss"
)

private fun parseDate(s: String): Long? {
    for (p in PATTERNS) {
        try {
            val f = SimpleDateFormat(p, Locale.getDefault()).apply { isLenient = false }
            val d = f.parse(s) ?: continue
            return d.time
        } catch (_: Exception) {
        }
    }
    return null
}

@Composable
fun TimestampTool() {
    var input by remember { mutableStateOf("") }
    var now by remember { mutableLongStateOf(System.currentTimeMillis()) }

    LaunchedEffect(Unit) {
        while (true) {
            now = System.currentTimeMillis()
            delay(1000)
        }
    }

    Column {
        SectionTitle("当前时间戳")
        Text("$now", fontSize = 20.sp, fontFamily = FontFamily.Monospace)
        Text("${now / 1000}", fontSize = 14.sp, fontFamily = FontFamily.Monospace, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Gap(4)
        Text(
            com.seagull.toolbox.core.Fmt.full(now) + "  ·  " + com.seagull.toolbox.core.Fmt.time(now),
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Gap(16)
        SectionTitle("转换")
        ToolInput(
            input, { input = it },
            placeholder = "输入时间戳（10 位秒 / 13 位毫秒）或日期字符串",
            minLines = 2, maxLines = 4
        )
        ActionRow(
            "用当前时间" to { input = now.toString() },
            "清空" to { input = "" }
        )

        val s = input.trim()
        if (s.isEmpty()) return@Column

        val ms: Long? = when {
            s.matches(Regex("^\\d{1,10}$")) -> s.toLongOrNull()?.times(1000)
            s.matches(Regex("^\\d{11,13}$")) -> s.toLongOrNull()
            s.matches(Regex("^\\d{14,}$")) -> s.toLongOrNull()?.div(1000)
            else -> parseDate(s)
        }

        Gap(4)
        if (ms == null) {
            Text("认不出来 —— 支持 10/13 位时间戳，或 yyyy-MM-dd [HH:mm[:ss]] 等格式", fontSize = 12.sp, color = MaterialTheme.colorScheme.error)
            return@Column
        }

        val diff = ms - now
        val relTxt = when {
            diff > 0 -> "还有 ${humanDuration(diff)}"
            diff < 0 -> "${humanDuration(-diff)} 前"
            else -> "就是现在"
        }

        val utc = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).apply {
            timeZone = java.util.TimeZone.getTimeZone("UTC")
        }.format(Date(ms))

        val body = buildString {
            appendLine("时间戳(秒)   ${ms / 1000}")
            appendLine("时间戳(毫秒) $ms")
            appendLine("本地时间     ${com.seagull.toolbox.core.Fmt.full(ms)}")
            appendLine("UTC          $utc")
            appendLine("ISO 8601     ${com.seagull.toolbox.core.Fmt.iso(ms)}")
            appendLine("日期         ${com.seagull.toolbox.core.Fmt.date(ms)}")
            appendLine("星期         ${weekday(ms)}")
            append("相对         $relTxt")
        }
        ResultBox(body, label = "结果", maxHeight = 260)
    }
}

private fun weekday(ms: Long): String {
    val w = SimpleDateFormat("EEEE", Locale.getDefault()).format(Date(ms))
    return w
}

private fun humanDuration(d: Long): String {
    val s = d / 1000
    return when {
        s < 60 -> "$s 秒"
        s < 3600 -> "${s / 60} 分钟"
        s < 86400 -> "${s / 3600} 小时 ${s % 3600 / 60} 分"
        else -> "${s / 86400} 天 ${s % 86400 / 3600} 小时"
    }
}
