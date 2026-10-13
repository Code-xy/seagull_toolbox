package com.seagull.toolbox.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.seagull.toolbox.core.copyText

@Composable
fun SectionTitle(text: String) {
    Text(
        text,
        fontSize = 13.sp,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(top = 4.dp, bottom = 4.dp)
    )
}

@Composable
fun ToolInput(
    value: String,
    onChange: (String) -> Unit,
    placeholder: String = "",
    minLines: Int = 3,
    maxLines: Int = 10,
    modifier: Modifier = Modifier,
    mono: Boolean = true
) {
    OutlinedTextField(
        value = value,
        onValueChange = onChange,
        placeholder = { Text(placeholder, fontSize = 14.sp) },
        minLines = minLines,
        maxLines = maxLines,
        textStyle = if (mono) MaterialTheme.typography.bodyMedium.copy(fontFamily = FontFamily.Monospace)
                    else MaterialTheme.typography.bodyMedium,
        modifier = modifier.fillMaxWidth()
    )
}

@Composable
fun ActionRow(vararg actions: Pair<String, () -> Unit>) {
    Row(
        Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        actions.forEach { (label, fn) ->
            TextButton(onClick = fn) { Text(label, fontSize = 13.sp) }
        }
    }
}

/** 结果展示块：可滚动 + 一键复制 */
@Composable
fun ResultBox(
    text: String,
    label: String = "结果",
    mono: Boolean = true,
    maxHeight: Int = 320
) {
    if (text.isEmpty()) return
    val ctx = LocalContext.current
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    label,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Medium
                )
                Spacer(Modifier.weight(1f))
                TextButton(onClick = { copyText(ctx, text) }) {
                    Text("复制", fontSize = 12.sp)
                }
            }
            Text(
                text,
                fontSize = 13.sp,
                lineHeight = 19.sp,
                fontFamily = if (mono) FontFamily.Monospace else FontFamily.Default,
                modifier = Modifier
                    .heightIn(max = maxHeight.dp)
                    .verticalScroll(rememberScrollState())
            )
        }
    }
}

@Composable
fun KV(key: String, value: String) {
    Row(Modifier.fillMaxWidth().padding(vertical = 3.dp)) {
        Text(
            key,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.width(112.dp)
        )
        Text(value, fontSize = 12.sp, fontFamily = FontFamily.Monospace)
    }
}

@Composable
fun Chip(text: String, selected: Boolean, onClick: () -> Unit) {
    androidx.compose.material3.FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(text, fontSize = 13.sp) }
    )
}

@Composable
fun Tag(text: String) {
    Text(
        text,
        fontSize = 11.sp,
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(MaterialTheme.colorScheme.primaryContainer)
            .padding(horizontal = 7.dp, vertical = 2.dp),
        color = MaterialTheme.colorScheme.onPrimaryContainer
    )
}

@Composable
fun Gap(dp: Int) = Spacer(Modifier.height(dp.dp))
