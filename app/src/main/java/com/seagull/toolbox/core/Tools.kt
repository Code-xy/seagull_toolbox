package com.seagull.toolbox.core

import com.seagull.toolbox.tools.ColorPickerTool
import com.seagull.toolbox.tools.DeadPixelTool
import com.seagull.toolbox.tools.DeviceInfoTool
import com.seagull.toolbox.tools.HashTool
import com.seagull.toolbox.tools.JsonTool
import com.seagull.toolbox.tools.LevelTool
import com.seagull.toolbox.tools.PasswordTool
import com.seagull.toolbox.tools.QrCodeTool
import com.seagull.toolbox.tools.RulerTool
import com.seagull.toolbox.tools.TimestampTool
import com.seagull.toolbox.tools.TotpTool
import com.seagull.toolbox.tools.UnitTool

/**
 * 工具注册表。
 *
 * 加新工具 = 写一个工具文件 + 在下面加一行。首页网格、分组、搜索全自动。
 */
object Tools {

    val ALL: List<Tool> = listOf(
        Tool("ruler", "尺子", "尺", Group.MEASURE, "屏幕物理尺子，银行卡校准精度") { RulerTool() },
        Tool("level", "水平仪", "平", Group.MEASURE, "加速度计气泡水平仪，实时倾角") { LevelTool() },

        Tool("color", "取色器", "色", Group.IMAGE, "相册取色，输出 HEX / RGB / HSB") { ColorPickerTool() },
        Tool("qr", "二维码", "码", Group.IMAGE, "生成文本 / 网址 / WiFi 二维码") { QrCodeTool() },

        Tool("json", "JSON", "{}", Group.TEXT, "格式化 / 压缩 / 校验") { JsonTool() },
        Tool("hash", "哈希", "#", Group.TEXT, "MD5 / SHA-1 / SHA-256 / SHA-512") { HashTool() },

        Tool("timestamp", "时间戳", "时", Group.CALC, "时间戳与日期互转，实时刷新") { TimestampTool() },
        Tool("unit", "单位换算", "换", Group.CALC, "长度 重量 温度 面积 体积 数据 速度 时间") { UnitTool() },
        Tool("totp", "动态令牌", "盾", Group.CALC, "TOTP 双因素验证码，纯本地") { TotpTool() },
        Tool("password", "密码生成", "密", Group.CALC, "SecureRandom 强密码，带熵评估") { PasswordTool() },

        Tool("device", "设备信息", "机", Group.DEVICE, "硬件 / 系统 / 传感器 / 存储") { DeviceInfoTool() },
        Tool("deadpixel", "坏点检测", "点", Group.DEVICE, "全屏逐色检查屏幕坏点") { DeadPixelTool() }
    )

    fun search(q: String): List<Tool> {
        val s = q.trim()
        if (s.isEmpty()) return ALL
        return ALL.filter {
            it.name.contains(s, true) ||
                it.desc.contains(s, true) ||
                it.group.label.contains(s, true) ||
                it.id.contains(s, true) ||
                it.badge.contains(s, true)
        }
    }
}
