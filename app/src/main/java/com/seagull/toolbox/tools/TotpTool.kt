package com.seagull.toolbox.tools

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.seagull.toolbox.ui.Gap
import com.seagull.toolbox.ui.ResultBox
import com.seagull.toolbox.ui.ToolInput
import kotlinx.coroutines.delay
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

private const val B32 = "ABCDEFGHIJKLMNOPQRSTUVWXYZ234567"

private fun base32Decode(s: String): ByteArray {
    val clean = s.trim().replace(" ", "").replace("-", "").replace("=", "").uppercase()
    val out = ByteArray(clean.length * 5 / 8)
    var buf = 0
    var bits = 0
    var idx = 0
    for (c in clean) {
        val v = B32.indexOf(c)
        if (v < 0) continue
        buf = (buf shl 5) or v
        bits += 5
        if (bits >= 8) {
            out[idx++] = ((buf shr (bits - 8)) and 0xFF).toByte()
            bits -= 8
        }
    }
    return out
}

private fun totpAt(key: ByteArray, counter: Long, digits: Int = 6): String {
    val data = ByteArray(8)
    var c = counter
    for (i in 7 downTo 0) {
        data[i] = (c and 0xFF).toByte()
        c = c shr 8
    }
    val mac = Mac.getInstance("HmacSHA1")
    mac.init(SecretKeySpec(key, "HmacSHA1"))
    val h = mac.doFinal(data)
    val off = h[h.size - 1].toInt() and 0x0F
    val code = ((h[off].toInt() and 0x7F) shl 24) or
            ((h[off + 1].toInt() and 0xFF) shl 16) or
            ((h[off + 2].toInt() and 0xFF) shl 8) or
            (h[off + 3].toInt() and 0xFF)
    val mod = when (digits) {
        8 -> 100_000_000
        else -> 1_000_000
    }
    return (code % mod).toString().padStart(digits, '0')
}

@Composable
fun TotpTool() {
    var secret by remember { mutableStateOf("") }
    var tick by remember { mutableLongStateOf(System.currentTimeMillis()) }

    LaunchedEffect(Unit) {
        while (true) {
            tick = System.currentTimeMillis()
            delay(500)
        }
    }

    Column {
        ToolInput(
            secret, { secret = it },
            placeholder = "粘贴 Base32 密钥（如 JBSWY3DPEHPK3PXP）",
            minLines = 2, maxLines = 4
        )
        Gap(12)

        if (secret.isBlank()) {
            Text(
                "输入密钥后显示 6 位动态验证码（TOTP / RFC 6238，30 秒一换）。" +
                    "计算全在本机，密钥不落盘、不出网。",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            return@Column
        }

        val key = remember(secret) {
            runCatching { base32Decode(secret) }.getOrNull()
        }
        if (key == null || key.isEmpty()) {
            Text("密钥解析失败，检查是否有非法字符", color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
            return@Column
        }

        val sec = tick / 1000
        val period = 30L
        val remain = period - (sec % period)
        val code = runCatching { totpAt(key, sec / period) }.getOrDefault("------")

        Text(
            code.chunked(3).joinToString(" "),
            fontSize = 42.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Gap(8)
        LinearProgressIndicator(
            progress = { remain.toFloat() / period },
            modifier = androidx.compose.ui.Modifier.fillMaxWidth()
        )
        Gap(4)
        Text("$remain 秒后刷新", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

        Gap(14)
        val list = (0 until 3).joinToString("\n") { i ->
            val c = sec / period + i
            val t = period - (sec % period) + i * period
            "${if (i == 0) "当前" else "+$i"}  ${totpAt(key, c)}   (${t}s)"
        }
        ResultBox(list, label = "当前与后续验证码", maxHeight = 140)

        Gap(8)
        Text(
            "算法 HMAC-SHA1 / 6 位 / 30 秒，兼容 Google Authenticator、微软验证器、各类 2FA。",
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
