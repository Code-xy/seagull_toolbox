package com.seagull.toolbox.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.seagull.toolbox.core.Group
import com.seagull.toolbox.core.Tool
import com.seagull.toolbox.core.Tools

@Composable
fun HomeScreen(onOpen: (Tool) -> Unit) {
    var q by remember { mutableStateOf("") }

    val grouped: List<Pair<Group, List<Tool>>> = remember(q) {
        val hit = Tools.search(q)
        Group.entries.mapNotNull { g ->
            val list = hit.filter { it.group == g }
            if (list.isEmpty()) null else g to list
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background),
                title = {
                    Column {
                        Text("海鸥工具箱", fontWeight = FontWeight.SemiBold)
                        Text(
                            "${Tools.ALL.size} 个工具 · 全部离线 · 零权限",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            )
        }
    ) { pad ->
        Column(Modifier.fillMaxSize().padding(pad)) {

            OutlinedTextField(
                value = q,
                onValueChange = { q = it },
                placeholder = { Text("搜索工具", fontSize = 14.sp) },
                leadingIcon = { Icon(Icons.Filled.Search, null, Modifier.size(20.dp)) },
                trailingIcon = {
                    if (q.isNotEmpty()) {
                        IconButton(onClick = { q = "" }) {
                            Icon(Icons.Filled.Close, null, Modifier.size(18.dp))
                        }
                    }
                },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 2.dp)
            )

            if (grouped.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        "没找到「$q」",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 14.sp
                    )
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    contentPadding = PaddingValues(start = 12.dp, end = 12.dp, top = 6.dp, bottom = 28.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    grouped.forEach { (group, list) ->
                        item(span = { GridItemSpan(3) }, key = "h_${group.name}") {
                            Row2(group.label)
                        }
                        items(list, key = { it.id }) { tool ->
                            ToolTile(tool) { onOpen(tool) }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun Row2(label: String) {
    Text(
        label,
        fontSize = 13.sp,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(start = 6.dp, top = 14.dp, bottom = 8.dp)
    )
}

@Composable
private fun ToolTile(tool: Tool, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp, horizontal = 2.dp)
    ) {
        Box(
            Modifier
                .size(56.dp)
                .clip(RoundedCornerShape(18.dp))
                .background(tool.group.color.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                tool.badge,
                fontSize = if (tool.badge.length > 1) 17.sp else 22.sp,
                fontWeight = FontWeight.SemiBold,
                color = tool.group.color
            )
        }
        Spacer(Modifier.height(7.dp))
        Text(
            tool.name,
            fontSize = 12.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
