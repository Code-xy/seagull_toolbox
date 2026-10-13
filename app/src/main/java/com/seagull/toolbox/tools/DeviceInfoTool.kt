package com.seagull.toolbox.tools

import android.app.ActivityManager
import android.content.Context
import android.hardware.SensorManager
import android.os.Build
import android.os.Environment
import android.os.StatFs
import android.util.DisplayMetrics
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.sp
import com.seagull.toolbox.core.humanBytes
import com.seagull.toolbox.ui.Gap
import com.seagull.toolbox.ui.KV
import com.seagull.toolbox.ui.ResultBox
import com.seagull.toolbox.ui.SectionTitle

@Composable
fun DeviceInfoTool() {
    val ctx = LocalContext.current
    val dm: DisplayMetrics = ctx.resources.displayMetrics

    val am = ctx.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    val mi = ActivityManager.MemoryInfo().also { am.getMemoryInfo(it) }

    val dataStat = StatFs(Environment.getDataDirectory().path)
    val totalData = dataStat.blockCountLong * dataStat.blockSizeLong
    val freeData = dataStat.availableBlocksLong * dataStat.blockSizeLong

    val sm = ctx.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    val sensors = sm.getSensorList(android.hardware.Sensor.TYPE_ALL)

    val report = buildString {
        appendLine("厂商      ${Build.MANUFACTURER}")
        appendLine("品牌      ${Build.BRAND}")
        appendLine("型号      ${Build.MODEL}")
        appendLine("设备      ${Build.DEVICE}")
        appendLine("产品      ${Build.PRODUCT}")
        appendLine("主板      ${Build.BOARD}")
        appendLine("硬件      ${Build.HARDWARE}")
        appendLine("Android   ${Build.VERSION.RELEASE} (API ${Build.VERSION.SDK_INT})")
        appendLine("安全补丁  ${if (Build.VERSION.SDK_INT >= 23) Build.VERSION.SECURITY_PATCH else "-"}")
        appendLine("内核      ${System.getProperty("os.version")}")
        appendLine("CPU ABI   ${Build.SUPPORTED_ABIS.joinToString(", ")}")
        appendLine("CPU 核数  ${Runtime.getRuntime().availableProcessors()}")
        appendLine("Heap 上限 ${humanBytes(Runtime.getRuntime().maxMemory())}")
        appendLine("内存总量  ${humanBytes(mi.totalMem)}")
        appendLine("内存可用  ${humanBytes(mi.availMem)}")
        appendLine("存储总量  ${humanBytes(totalData)}")
        appendLine("存储可用  ${humanBytes(freeData)}")
        appendLine("屏幕      ${dm.widthPixels} x ${dm.heightPixels} px")
        appendLine("密度      ${dm.densityDpi} dpi (${"%.2f".format(dm.density)}x)")
        appendLine("物理 dpi  x=${dm.xdpi} y=${dm.ydpi}")
        appendLine("传感器数  ${sensors.size}")
        appendLine("指纹")
        append("  ${Build.FINGERPRINT}")
    }

    Column {
        SectionTitle("系统")
        KV("型号", "${Build.MANUFACTURER} ${Build.MODEL}")
        KV("Android", "${Build.VERSION.RELEASE} (API ${Build.VERSION.SDK_INT})")
        KV("内核", System.getProperty("os.version") ?: "-")
        KV("ABI", Build.SUPPORTED_ABIS.firstOrNull() ?: "-")
        KV("CPU 核心", "${Runtime.getRuntime().availableProcessors()}")

        Gap(12)
        SectionTitle("内存 / 存储")
        KV("内存总量", humanBytes(mi.totalMem))
        KV("内存可用", humanBytes(mi.availMem))
        KV("存储总量", humanBytes(totalData))
        KV("存储可用", humanBytes(freeData))
        KV("Heap 上限", humanBytes(Runtime.getRuntime().maxMemory()))

        Gap(12)
        SectionTitle("显示")
        KV("分辨率", "${dm.widthPixels} × ${dm.heightPixels}")
        KV("密度", "${dm.densityDpi} dpi")
        KV("物理 dpi", "x=${"%.1f".format(dm.xdpi)} y=${"%.1f".format(dm.ydpi)}")
        Text(
            "提示：尺子工具的精度就取自上面的 xdpi，多数机型不准，建议用银行卡校准一次。",
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Gap(12)
        SectionTitle("传感器（${sensors.size}）")
        sensors.take(24).forEach { s ->
            Text("· ${s.name}  (${s.vendor})", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        if (sensors.size > 24) {
            Text("… 其余 ${sensors.size - 24} 个", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }

        Gap(14)
        SectionTitle("完整报告")
        ResultBox(report, label = "可复制")
    }
}
