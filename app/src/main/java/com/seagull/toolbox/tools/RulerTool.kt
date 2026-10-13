package com.seagull.toolbox.tools

import android.content.Context
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.text.drawText
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.seagull.toolbox.ui.ActionRow
import com.seagull.toolbox.ui.Gap
import com.seagull.toolbox.ui.SectionTitle

/**
 * 屏幕物理尺子。
 *
 * 显示尺寸依赖 DisplayMetrics.xdpi，但大量机型的这个值是错的（误差可达 5%），
 * 所以提供校准：银行卡是 ISO/IEC 7810 ID-1 标准，全世界都是 85.60mm 宽，
 * 拖到对齐即可把像素/毫米比值定死，之后刻度就是真的。
 */
@Composable
fun RulerTool() {
    val ctx = LocalContext.current
    val dm = ctx.resources.displayMetrics
    val prefs = remember { ctx.getSharedPreferences("toolbox", Context.MODE_PRIVATE) }
    val fallback = (dm.xdpi / 25.4f).takeIf { it > 1f } ?: (dm.density * 160f / 25.4f)

    var ppmm by remember { mutableFloatStateOf(prefs.getFloat("ppmm", fallback)) }
    var calibrating by remember { mutableStateOf(false) }

    val onColor = MaterialTheme.colorScheme.onSurface
    val primary = MaterialTheme.colorScheme.primary
    val tm = rememberTextMeasurer()

    fun persist(v: Float) {
        ppmm = v
        prefs.edit().putFloat("ppmm", v).apply()
    }

    Column {
        Text(
            "%.3f px/mm  ·  %.2f 毫米/英寸".format(ppmm, ppmm * 25.4f),
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        ActionRow(
            (if (calibrating) "收起校准" else "校准") to { calibrating = !calibrating },
            "恢复默认" to { persist(fallback) }
        )

        if (calibrating) {
            SectionTitle("银行卡（85.60mm）参照线")
            Canvas(Modifier.fillMaxWidth().height(64.dp)) {
                val target = 85.6f * ppmm
                val y = size.height / 2f
                val w = target.coerceAtMost(size.width - 4f)
                drawLine(primary, Offset(0f, y), Offset(w, y), strokeWidth = 3f)
                drawLine(primary, Offset(0f, y - 22f), Offset(0f, y + 22f), strokeWidth = 3f)
                drawLine(primary, Offset(w, y - 22f), Offset(w, y + 22f), strokeWidth = 3f)
                drawText(
                    tm,
                    "85.60 mm",
                    topLeft = Offset(6f, y - 40f),
                    style = TextStyle(color = primary, fontSize = 12.sp)
                )
            }
            Text(
                "把银行卡贴屏幕上，拖滑块让线两端刚好对准卡片宽度",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Slider(
                value = ppmm,
                onValueChange = { persist(it) },
                valueRange = (fallback * 0.80f)..(fallback * 1.25f)
            )
            Gap(8)
        }

        SectionTitle("刻度尺")
        Canvas(Modifier.fillMaxWidth().height(120.dp)) {
            var i = 0
            while (true) {
                val x = i * ppmm
                if (x > size.width) break
                val isCm = i % 10 == 0
                val isHalf = i % 5 == 0
                val h = when {
                    isCm -> size.height * 0.62f
                    isHalf -> size.height * 0.42f
                    else -> size.height * 0.24f
                }
                drawLine(
                    onColor,
                    Offset(x, 0f),
                    Offset(x, h),
                    strokeWidth = if (isCm) 3f else 1.2f
                )
                if (isCm && i > 0) {
                    drawText(
                        tm,
                        "${i / 10}",
                        topLeft = Offset(x + 3f, h + 4f),
                        style = TextStyle(color = onColor, fontSize = 13.sp)
                    )
                }
                i++
            }
        }
        Text(
            "每格 1mm，长线每 5mm，带数字每 10mm（1cm）",
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}
