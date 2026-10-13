package com.seagull.toolbox.tools

import android.graphics.Bitmap
import android.graphics.Color as AColor
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.MultiFormatWriter
import com.seagull.toolbox.ui.ActionRow
import com.seagull.toolbox.ui.Chip
import com.seagull.toolbox.ui.Gap
import com.seagull.toolbox.ui.SectionTitle

private fun makeQr(content: String, size: Int = 640): Bitmap? = try {
    val hints = mapOf(
        EncodeHintType.CHARACTER_SET to "UTF-8",
        EncodeHintType.MARGIN to 1
    )
    val m = MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, size, size, hints)
    val px = IntArray(size * size)
    for (y in 0 until size) {
        val row = y * size
        for (x in 0 until size) {
            px[row + x] = if (m.get(x, y)) AColor.BLACK else AColor.WHITE
        }
    }
    Bitmap.createBitmap(px, size, size, Bitmap.Config.ARGB_8888)
} catch (t: Throwable) {
    null
}

@Composable
fun QrCodeTool() {
    var mode by remember { mutableStateOf(0) }
    var text by remember { mutableStateOf("") }
    var ssid by remember { mutableStateOf("") }
    var pwd by remember { mutableStateOf("") }
    var hidden by remember { mutableStateOf(false) }
    var bmp by remember { mutableStateOf<Bitmap?>(null) }
    var err by remember { mutableStateOf("") }

    fun payload(): String = when (mode) {
        2 -> "WIFI:T:${if (hidden) "HIDDEN" else "WPA"};S:$ssid;P:$pwd;;"
        else -> text.trim()
    }

    fun generate() {
        val p = payload()
        if (p.isEmpty()) { err = "内容为空"; bmp = null; return }
        val b = makeQr(p)
        if (b == null) { err = "生成失败，内容可能过长（二维码上限约 2900 字节）"; bmp = null }
        else { err = ""; bmp = b }
    }

    Column {
        ActionRow(
            "生成" to { generate() },
            "清除" to { bmp = null; err = "" }
        )

        SectionTitle("内容类型")
        androidx.compose.foundation.layout.Row {
            Chip("文本", mode == 0) { mode = 0; bmp = null }
            Gap2()
            Chip("网址", mode == 1) { mode = 1; bmp = null }
            Gap2()
            Chip("WiFi", mode == 2) { mode = 2; bmp = null }
        }
        Gap(8)

        if (mode == 2) {
            OutlinedTextField(
                value = ssid, onValueChange = { ssid = it },
                label = { Text("WiFi 名称 (SSID)", fontSize = 12.sp) },
                singleLine = true, modifier = Modifier.fillMaxWidth()
            )
            Gap(6)
            OutlinedTextField(
                value = pwd, onValueChange = { pwd = it },
                label = { Text("密码", fontSize = 12.sp) },
                singleLine = true, modifier = Modifier.fillMaxWidth()
            )
            Gap(6)
            androidx.compose.material3.TextButton(onClick = { hidden = !hidden }) {
                Text(if (hidden) "隐藏网络：是" else "隐藏网络：否", fontSize = 12.sp)
            }
        } else {
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                placeholder = { Text(if (mode == 1) "https://example.com" else "要编码的文字", fontSize = 13.sp) },
                minLines = 3, maxLines = 8,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Gap(12)
        if (err.isNotEmpty()) {
            Text(err, color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
            Gap(8)
        }

        bmp?.let { b ->
            Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                Box(
                    Modifier
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color.White)
                        .padding(14.dp)
                ) {
                    Image(
                        bitmap = b.asImageBitmap(),
                        contentDescription = null,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.size(268.dp)
                    )
                }
            }
            Gap(10)
            SectionTitle("实际编码内容")
            Text(payload(), fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun Gap2() = androidx.compose.foundation.layout.Spacer(Modifier.size(8.dp))
