package com.seagull.toolbox.tools

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.seagull.toolbox.ui.Gap
import kotlin.math.atan2
import kotlin.math.sqrt

@Composable
fun LevelTool() {
    val ctx = LocalContext.current
    var pitch by remember { mutableFloatStateOf(0f) }
    var roll by remember { mutableFloatStateOf(0f) }
    var has by remember { mutableStateOf(false) }

    DisposableEffect(Unit) {
        val sm = ctx.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val sensor = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        val l = object : SensorEventListener {
            override fun onSensorChanged(e: SensorEvent) {
                val x = e.values[0]; val y = e.values[1]; val z = e.values[2]
                val norm = sqrt(x * x + y * y + z * z)
                if (norm < 0.1f) return
                pitch = Math.toDegrees(atan2(-y.toDouble(), z.toDouble())).toFloat()
                roll = Math.toDegrees(atan2(-x.toDouble(), z.toDouble())).toFloat()
                has = true
            }
            override fun onAccuracyChanged(s: Sensor?, a: Int) = Unit
        }
        if (sensor != null) sm.registerListener(l, sensor, SensorManager.SENSOR_DELAY_UI)
        onDispose { sm.unregisterListener(l) }
    }

    if (!has) {
        Text("没有检测到加速度计", fontSize = 13.sp, color = MaterialTheme.colorScheme.error)
        return
    }

    val flat = kotlin.math.abs(pitch) < 0.6f && kotlin.math.abs(roll) < 0.6f
    val okColor = Color(0xFF1D9A8A)
    val bubbleColor = if (flat) okColor else MaterialTheme.colorScheme.primary
    val gridColor = MaterialTheme.colorScheme.outlineVariant

    Column {
        Text(
            if (flat) "已水平 ✓" else "未水平",
            fontSize = 15.sp,
            color = if (flat) okColor else MaterialTheme.colorScheme.onSurface
        )
        Gap(10)

        Canvas(
            Modifier
                .fillMaxWidth()
                .height(260.dp)
        ) {
            val cx = size.width / 2f
            val cy = size.height / 2f
            val r = minOf(cx, cy) - 8f

            // 十字参考线
            drawLine(gridColor, Offset(cx - r, cy), Offset(cx + r, cy), strokeWidth = 2f)
            drawLine(gridColor, Offset(cx, cy - r), Offset(cx, cy + r), strokeWidth = 2f)

            // 同心圆
            listOf(1f, 0.66f, 0.33f).forEach { k ->
                drawCircle(gridColor, radius = r * k, center = Offset(cx, cy), style = Stroke(width = 2f))
            }
            drawCircle(okColor.copy(alpha = 0.45f), radius = r * 0.12f, center = Offset(cx, cy), style = Stroke(width = 2f))

            // 气泡：角度映射到半径（±10° 出界）
            val maxAngle = 10f
            val bx = cx + (roll / maxAngle).coerceIn(-1f, 1f) * r
            val by = cy + (pitch / maxAngle).coerceIn(-1f, 1f) * r
            drawCircle(bubbleColor, radius = 16f, center = Offset(bx, by))
        }

        Gap(6)
        Text(
            "倾角 X（左右）：%+.1f°".format(roll),
            fontSize = 14.sp,
            fontFamily = FontFamily.Monospace
        )
        Text(
            "倾角 Y（前后）：%+.1f°".format(pitch),
            fontSize = 14.sp,
            fontFamily = FontFamily.Monospace
        )
        Gap(8)
        Text(
            "气泡在中心圆内即为水平。手机平放桌面校准零点更准。",
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
