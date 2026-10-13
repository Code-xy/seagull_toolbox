package com.seagull.toolbox.core

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/**
 * 工具分组。颜色同时用于首页色块，保持视觉区分。
 */
enum class Group(val label: String, val color: Color) {
    MEASURE("测量", Color(0xFF1D9A8A)),
    IMAGE("图像", Color(0xFF7C5CFF)),
    TEXT("文本", Color(0xFF3B76DB)),
    CALC("计算", Color(0xFFE08A2E)),
    DEVICE("设备", Color(0xFFC2456B))
}

/**
 * 一个工具 = 一条注册表记录。
 *
 * 加新工具只要两步：
 *   1. 在 tools/ 下写一个 @Composable 屏幕
 *   2. 在 [Tools.ALL] 里加一行
 * 首页网格、搜索、分组全部自动出来。
 *
 * badge 用字符而不是 drawable —— 少一堆图标资源，也天然跟随主题色。
 */
data class Tool(
    val id: String,
    val name: String,
    val badge: String,
    val group: Group,
    val desc: String,
    val screen: @Composable () -> Unit
)

val Tool.tint: Color get() = group.color
