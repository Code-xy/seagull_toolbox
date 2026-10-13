package com.seagull.toolbox.core

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun copyText(ctx: Context, text: String, label: String = "toolbox") {
    val cm = ctx.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager ?: return
    runCatching { cm.setPrimaryClip(ClipData.newPlainText(label, text)) }
}

fun bytesToHex(b: ByteArray): String {
    val sb = StringBuilder(b.size * 2)
    for (x in b) sb.append("%02x".format(x))
    return sb.toString()
}

object Fmt {
    private val TIME = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    private val DATE = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val TIME_ONLY = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
    private val ISO = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.getDefault())

    fun full(ms: Long): String = TIME.format(Date(ms))
    fun date(ms: Long): String = DATE.format(Date(ms))
    fun time(ms: Long): String = TIME_ONLY.format(Date(ms))
    fun iso(ms: Long): String = ISO.format(Date(ms))
}

/** 人类可读的字节数 */
fun humanBytes(n: Long): String {
    if (n < 1024) return "$n B"
    val units = arrayOf("KB", "MB", "GB", "TB")
    var v = n.toDouble() / 1024
    var i = 0
    while (v >= 1024 && i < units.size - 1) { v /= 1024; i++ }
    return String.format(Locale.US, "%.2f %s", v, units[i])
}
