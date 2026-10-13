package com.seagull.toolbox.tools

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.seagull.toolbox.ui.ActionRow
import com.seagull.toolbox.ui.Chip
import com.seagull.toolbox.ui.Gap
import com.seagull.toolbox.ui.ResultBox
import com.seagull.toolbox.ui.ToolInput
import org.json.JSONArray
import org.json.JSONObject

private sealed interface JsonResult {
    data class Ok(val out: String, val kind: String) : JsonResult
    data class Bad(val msg: String) : JsonResult
}

private fun runJson(src: String, indent: Int): JsonResult {
    val t = src.trim()
    if (t.isEmpty()) return JsonResult.Ok("", "")
    return try {
        if (t.startsWith("[")) {
            val a = JSONArray(t)
            JsonResult.Ok(if (indent > 0) a.toString(indent) else a.toString(), "数组 · ${a.length()} 项")
        } else {
            val o = JSONObject(t)
            JsonResult.Ok(if (indent > 0) o.toString(indent) else o.toString(), "对象 · ${o.length()} 个键")
        }
    } catch (e: Exception) {
        JsonResult.Bad(e.message ?: "解析失败")
    }
}

@Composable
fun JsonTool() {
    var src by remember { mutableStateOf("") }
    var indent by remember { mutableIntStateOf(2) }
    var out by remember { mutableStateOf("") }
    var kind by remember { mutableStateOf("") }
    var err by remember { mutableStateOf("") }

    fun apply(i: Int) {
        indent = i
        when (val r = runJson(src, i)) {
            is JsonResult.Ok -> { out = r.out; kind = r.kind; err = "" }
            is JsonResult.Bad -> { out = ""; err = r.msg }
        }
    }

    Column {
        ToolInput(
            src, { src = it },
            placeholder = "粘贴 JSON…",
            minLines = 6, maxLines = 14
        )
        Gap(6)
        Row {
            Chip("格式化 2", indent == 2) { apply(2) }
            Gap4()
            Chip("格式化 4", indent == 4) { apply(4) }
            Gap4()
            Chip("压缩", indent == 0) { apply(0) }
        }
        ActionRow(
            "转换" to { apply(indent) },
            "清空" to { src = ""; out = ""; err = ""; kind = "" }
        )

        if (err.isNotEmpty()) {
            Text("✗ $err", color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
            Gap(6)
        }
        if (kind.isNotEmpty()) {
            Text(kind + " · ${out.length} 字符", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Gap(6)
        }
        ResultBox(out, label = "输出")
        Gap(10)
        Text(
            "解析用 Android 内置 org.json，不上传、不联网。",
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun Gap4() = androidx.compose.foundation.layout.Spacer(Modifier.size(8.dp))
