package com.seagull.toolbox.tools

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.sp
import com.seagull.toolbox.ui.ActionRow
import com.seagull.toolbox.ui.Gap
import com.seagull.toolbox.ui.ResultBox
import java.security.SecureRandom

private const val LOWER = "abcdefghijkmnopqrstuvwxyz"
private const val UPPER = "ABCDEFGHJKLMNPQRSTUVWXYZ"
private const val DIGIT = "23456789"
private const val SYM = "!@#$%^&*()-_=+[]{};:,.?/"

@Composable
fun PasswordTool() {
    var len by remember { mutableIntStateOf(20) }
    var useLower by remember { mutableStateOf(true) }
    var useUpper by remember { mutableStateOf(true) }
    var useDigit by remember { mutableStateOf(true) }
    var useSym by remember { mutableStateOf(true) }
    var noAmbig by remember { mutableStateOf(true) }
    var out by remember { mutableStateOf("") }

    val rnd = remember { SecureRandom() }

    fun gen() {
        val pool = buildString {
            if (useLower) append(if (noAmbig) LOWER else LOWER + "l")
            if (useUpper) append(if (noAmbig) UPPER else UPPER + "IO")
            if (useDigit) append(if (noAmbig) DIGIT else DIGIT + "01")
            if (useSym) append(SYM)
        }
        if (pool.isEmpty()) { out = ""; return }
        out = (1..len).map { pool[rnd.nextInt(pool.length)] }.joinToString("")
    }

    Column {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text("长度", fontSize = 13.sp, modifier = Modifier.fillMaxWidth(0.25f))
            Slider(
                value = len.toFloat(),
                onValueChange = { len = it.toInt() },
                valueRange = 6f..64f,
                modifier = Modifier.fillMaxWidth(0.75f)
            )
        }
        Text("$len 位", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

        Gap(8)
        SwitchRow("小写字母 a-z", useLower) { useLower = it }
        SwitchRow("大写字母 A-Z", useUpper) { useUpper = it }
        SwitchRow("数字 0-9", useDigit) { useDigit = it }
        SwitchRow("符号 !@#…", useSym) { useSym = it }
        SwitchRow("排除易混字符（0O1lI）", noAmbig) { noAmbig = it }

        Gap(6)
        ActionRow(
            "生成" to { gen() },
            "生成 5 个" to {
                val pool = buildString {
                    if (useLower) append(LOWER)
                    if (useUpper) append(UPPER)
                    if (useDigit) append(DIGIT)
                    if (useSym) append(SYM)
                }
                if (pool.isNotEmpty()) {
                    out = (1..5).joinToString("\n") {
                        (1..len).map { _ -> pool[rnd.nextInt(pool.length)] }.joinToString("")
                    }
                }
            },
            "清空" to { out = "" }
        )

        if (out.isNotEmpty()) {
            val entropy = estimateEntropy(len, useLower, useUpper, useDigit, useSym)
            Gap(4)
            Text(
                "熵 ≈ ${"%.0f".format(entropy)} 位  ·  ${strength(entropy)}",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.primary
            )
            Gap(6)
            ResultBox(out, label = "密码", mono = true)
            Gap(8)
            Text(
                "使用 SecureRandom（系统级 CSPRNG），不在任何地方保存，也不联网。",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun SwitchRow(label: String, checked: Boolean, onChange: (Boolean) -> Unit) {
    Row(
        Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, fontSize = 13.sp, modifier = Modifier.fillMaxWidth(0.75f))
        Switch(checked = checked, onCheckedChange = onChange)
    }
}

private fun estimateEntropy(len: Int, l: Boolean, u: Boolean, d: Boolean, s: Boolean): Double {
    var pool = 0
    if (l) pool += 24
    if (u) pool += 24
    if (d) pool += 8
    if (s) pool += 24
    if (pool <= 1) return 0.0
    return len * (kotlin.math.ln(pool.toDouble()) / kotlin.math.ln(2.0))
}

private fun strength(bits: Double): String = when {
    bits < 40 -> "弱"
    bits < 60 -> "一般"
    bits < 90 -> "强"
    else -> "非常强"
}
