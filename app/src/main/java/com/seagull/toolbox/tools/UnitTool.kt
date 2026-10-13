package com.seagull.toolbox.tools

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.seagull.toolbox.ui.Chip
import com.seagull.toolbox.ui.Gap
import com.seagull.toolbox.ui.ResultBox

private data class Unit(val name: String, val factor: Double)

private data class Cat(val name: String, val units: List<Unit>, val temperature: Boolean = false)

private val CATS = listOf(
    Cat("长度", listOf(
        Unit("毫米 mm", 0.001), Unit("厘米 cm", 0.01), Unit("米 m", 1.0),
        Unit("千米 km", 1000.0), Unit("英寸 in", 0.0254), Unit("英尺 ft", 0.3048),
        Unit("码 yd", 0.9144), Unit("英里 mi", 1609.344), Unit("海里 nmi", 1852.0),
        Unit("市尺", 1.0 / 3), Unit("台尺", 0.30303)
    )),
    Cat("重量", listOf(
        Unit("毫克 mg", 1e-6), Unit("克 g", 0.001), Unit("千克 kg", 1.0),
        Unit("吨 t", 1000.0), Unit("盎司 oz", 0.0283495), Unit("磅 lb", 0.4535924),
        Unit("斤(市斤)", 0.5), Unit("两", 0.05)
    )),
    Cat("温度", listOf(
        Unit("摄氏度 °C", 0.0), Unit("华氏度 °F", 0.0), Unit("开尔文 K", 0.0)
    ), temperature = true),
    Cat("面积", listOf(
        Unit("平方毫米 mm²", 1e-6), Unit("平方厘米 cm²", 1e-4), Unit("平方米 m²", 1.0),
        Unit("平方千米 km²", 1e6), Unit("公顷 ha", 10000.0), Unit("亩", 666.6667),
        Unit("平方英尺 ft²", 0.09290304), Unit("英亩 ac", 4046.8564)
    )),
    Cat("体积", listOf(
        Unit("毫升 mL", 0.001), Unit("升 L", 1.0), Unit("立方米 m³", 1000.0),
        Unit("立方厘米 cm³", 0.001), Unit("加仑(US)", 3.7854118), Unit("夸脱(US)", 0.9463529),
        Unit("品脱(US)", 0.4731765), Unit("液盎司(US)", 0.0295735)
    )),
    Cat("数据", listOf(
        Unit("位 bit", 1.0), Unit("字节 B", 8.0), Unit("KB", 8 * 1024.0),
        Unit("MB", 8 * 1024.0 * 1024), Unit("GB", 8 * 1024.0 * 1024 * 1024),
        Unit("TB", 8 * 1024.0 * 1024 * 1024 * 1024)
    )),
    Cat("速度", listOf(
        Unit("米/秒 m/s", 1.0), Unit("千米/时 km/h", 1.0 / 3.6),
        Unit("英里/时 mph", 0.44704), Unit("节 kn", 0.5144444), Unit("英尺/秒 ft/s", 0.3048)
    )),
    Cat("时间", listOf(
        Unit("毫秒 ms", 0.001), Unit("秒 s", 1.0), Unit("分钟 min", 60.0),
        Unit("小时 h", 3600.0), Unit("天 d", 86400.0), Unit("周 w", 604800.0), Unit("年 y", 31557600.0)
    ))
)

private fun convertTemp(v: Double, from: Int, to: Int): Double {
    val c = when (from) {
        0 -> v
        1 -> (v - 32) * 5 / 9
        else -> v - 273.15
    }
    return when (to) {
        0 -> c
        1 -> c * 9 / 5 + 32
        else -> c + 273.15
    }
}

@Composable
fun UnitTool() {
    var catIdx by remember { mutableIntStateOf(0) }
    var fromIdx by remember { mutableIntStateOf(0) }
    var input by remember { mutableStateOf("1") }

    val cat = CATS[catIdx]
    val v = input.trim().toDoubleOrNull()

    Column {
        Row(
            Modifier.horizontalScroll(rememberScrollState())
        ) {
            CATS.forEachIndexed { i, c ->
                Chip(c.name, catIdx == i) {
                    catIdx = i
                    fromIdx = 0
                }
                androidx.compose.foundation.layout.Spacer(Modifier.padding(4.dp))
            }
        }
        Gap(10)

        OutlinedTextField(
            value = input,
            onValueChange = { input = it },
            label = { Text("数值", fontSize = 12.sp) },
            singleLine = true,
            textStyle = androidx.compose.ui.text.TextStyle(fontFamily = FontFamily.Monospace)
        )

        Gap(8)
        Row(Modifier.horizontalScroll(rememberScrollState())) {
            cat.units.forEachIndexed { i, u ->
                Chip(u.name, fromIdx == i) { fromIdx = i }
                androidx.compose.foundation.layout.Spacer(Modifier.padding(4.dp))
            }
        }

        Gap(12)
        if (v == null) {
            Text("请输入数字", color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
            return@Column
        }

        val base = if (cat.temperature) v else v * cat.units[fromIdx].factor

        Text(
            "${v} ${cat.units[fromIdx].name}  =",
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Gap(6)

        cat.units.forEachIndexed { i, u ->
            val r = if (cat.temperature) convertTemp(v, fromIdx, i) else base / u.factor
            Row(Modifier.padding(vertical = 3.dp)) {
                Text(
                    u.name,
                    fontSize = 12.sp,
                    color = if (i == fromIdx) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.width(120.dp)
                )
                Text(
                    formatNum(r),
                    fontSize = 13.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = if (i == fromIdx) FontWeight.SemiBold else FontWeight.Normal
                )
            }
        }

        Gap(10)
        val all = buildString {
            cat.units.forEachIndexed { i, u ->
                val r = if (cat.temperature) convertTemp(v, fromIdx, i) else base / u.factor
                appendLine("${u.name} = ${formatNum(r)}")
            }
        }
        ResultBox(all, label = "全部结果")
    }
}

private fun formatNum(d: Double): String {
    if (d == 0.0) return "0"
    val a = kotlin.math.abs(d)
    return when {
        a >= 1e15 || a < 1e-6 -> String.format("%.6e", d)
        a >= 1000 -> String.format("%,.4f", d).trimEnd('0').trimEnd('.')
        else -> String.format("%.6f", d).trimEnd('0').trimEnd('.')
    }
}
