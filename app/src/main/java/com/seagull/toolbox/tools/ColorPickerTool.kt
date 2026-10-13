package com.seagull.toolbox.tools

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.seagull.toolbox.ui.ActionRow
import com.seagull.toolbox.ui.Gap
import com.seagull.toolbox.ui.ResultBox
import java.io.InputStream

private fun loadBitmap(ctx: Context, uri: Uri, maxSide: Int = 1600): Bitmap? = try {
    ctx.contentResolver.openInputStream(uri).use { ins: InputStream? ->
        val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        ctx.contentResolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, bounds) }
        var scale = 1
        while (bounds.outWidth / scale > maxSide || bounds.outHeight / scale > maxSide) scale *= 2
        val opts = BitmapFactory.Options().apply { inSampleSize = scale }
        ins?.let { BitmapFactory.decodeStream(it, null, opts) }
    }
} catch (t: Throwable) {
    null
}

@Composable
fun ColorPickerTool() {
    val ctx = LocalContext.current
    var bmp by remember { mutableStateOf<Bitmap?>(null) }
    var picked by remember { mutableStateOf<Color?>(null) }
    var tap by remember { mutableStateOf<Offset?>(null) }
    var viewSize by remember { mutableStateOf(IntSize.Zero) }

    val picker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri ?: return@rememberLauncherForActivityResult
        bmp = loadBitmap(ctx, uri)
        picked = null
        tap = null
    }

    Column {
        ActionRow(
            "选择图片" to { picker.launch("image/*") },
            "清除" to { bmp = null; picked = null; tap = null }
        )

        val b = bmp
        if (b == null) {
            Text(
                "选一张图，点任意位置取色。图片只在本机解码，不会上传。",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            return@Column
        }

        Gap(6)
        Box(
            Modifier
                .fillMaxWidth()
                .heightIn(max = 380.dp)
                .clip(RoundedCornerShape(12.dp))
                .onSizeChanged { viewSize = it }
                .pointerInput(b) {
                    detectTapGestures { off ->
                        tap = off
                        val vw = viewSize.width.toFloat()
                        val vh = viewSize.height.toFloat()
                        if (vw <= 0f || vh <= 0f) return@detectTapGestures
                        // 图片按 Fit 居中，需换算回原图坐标
                        val scale = minOf(vw / b.width, vh / b.height)
                        val dw = b.width * scale
                        val dh = b.height * scale
                        val ox = (vw - dw) / 2f
                        val oy = (vh - dh) / 2f
                        val px = ((off.x - ox) / scale).toInt()
                        val py = ((off.y - oy) / scale).toInt()
                        if (px in 0 until b.width && py in 0 until b.height) {
                            picked = Color(b.getPixel(px, py))
                        }
                    }
                }
        ) {
            Image(
                bitmap = b.asImageBitmap(),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier.fillMaxWidth()
            )
            Canvas(Modifier.fillMaxWidth().height(viewSize.height.dp)) {
                tap?.let { o ->
                    drawCircle(Color.White, radius = 13f, center = o, style = Stroke(width = 3f))
                    drawCircle(Color.Black, radius = 15f, center = o, style = Stroke(width = 2f))
                }
            }
        }

        picked?.let { c ->
            Gap(12)
            val r = (c.red * 255f).toInt()
            val g = (c.green * 255f).toInt()
            val bl = (c.blue * 255f).toInt()
            val hex = "#%02X%02X%02X".format(r, g, bl)
            val hsl = rgbToHsl(r, g, bl)

            Column {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(64.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .padding(0.dp)
                ) {
                    Canvas(Modifier.fillMaxWidth().height(64.dp)) { drawRect(c) }
                }
                Gap(10)
                Text("HEX   $hex", fontSize = 13.sp, fontFamily = FontFamily.Monospace)
                Text("RGB   $r, $g, $bl", fontSize = 13.sp, fontFamily = FontFamily.Monospace)
                Text("HSB   %.0f°, %.0f%%, %.0f%%".format(hsl[0], hsl[1] * 100, hsl[2] * 100), fontSize = 13.sp, fontFamily = FontFamily.Monospace)
                Text("Android 0xFF%02X%02X%02X".format(r, g, bl), fontSize = 13.sp, fontFamily = FontFamily.Monospace)
                Gap(8)
                ResultBox("$hex\n$r, $g, $bl\n%.0f°, %.0f%%, %.0f%%".format(hsl[0], hsl[1] * 100, hsl[2] * 100), label = "打包复制")
            }
        } ?: run {
            Gap(8)
            Text("点击图片任意位置取色", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

private fun rgbToHsl(r: Int, g: Int, b: Int): FloatArray {
    val rf = r / 255f; val gf = g / 255f; val bf = b / 255f
    val max = maxOf(rf, gf, bf); val min = minOf(rf, gf, bf)
    val l = (max + min) / 2f
    var h = 0f; var s = 0f
    val d = max - min
    if (d > 1e-6f) {
        s = if (l > 0.5f) d / (2f - max - min) else d / (max + min)
        h = when (max) {
            rf -> ((gf - bf) / d + if (gf < bf) 6f else 0f)
            gf -> (bf - rf) / d + 2f
            else -> (rf - gf) / d + 4f
        } * 60f
    }
    return floatArrayOf(h, s, l)
}
