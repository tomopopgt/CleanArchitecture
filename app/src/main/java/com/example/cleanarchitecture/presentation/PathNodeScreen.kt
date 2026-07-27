package com.example.cleanarchitecture.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cleanarchitecture.domain.model.GraphNode
import com.example.cleanarchitecture.presentation.components.WarehouseMapCanvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PathNodeScreen(
    viewModel: PathNodeViewModel = remember { PathNodeViewModel() }
) {
    val uiState by viewModel.uiState.collectAsState()
    val searchHistory by viewModel.searchHistory.collectAsState()

    var startNode by remember { mutableStateOf(viewModel.warehouseNodes.firstOrNull()) }
    var targetNode by remember { mutableStateOf(viewModel.warehouseNodes.lastOrNull()) }

    var startExpanded by remember { mutableStateOf(false) }
    var targetExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F172A))
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // ヘッダーカード
        Card(
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text("🚚 PathNode Pro (Clean Arch)", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Text("ダイクストラ法 最短経路ナビ ＆ MVVMアーキテクチャ", color = Color(0xFF94A3B8), fontSize = 11.sp)
            }
        }

        // ノード選択 ＆ 探索実行エリア
        Card(
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // スタート地点 選択ボックス
                    ExposedDropdownMenuBox(
                        expanded = startExpanded,
                        onExpandedChange = { startExpanded = !startExpanded },
                        modifier = Modifier.weight(1f)
                    ) {
                        OutlinedTextField(
                            value = startNode?.label ?: "選択",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("出発地", color = Color(0xFF38BDF8)) },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = startExpanded) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = Color(0xFF38BDF8),
                                unfocusedBorderColor = Color(0xFF475569)
                            ),
                            modifier = Modifier.menuAnchor()
                        )
                        ExposedDropdownMenu(
                            expanded = startExpanded,
                            onDismissRequest = { startExpanded = false }
                        ) {
                            viewModel.warehouseNodes.forEach { node ->
                                DropdownMenuItem(
                                    text = { Text(node.label) },
                                    onClick = {
                                        startNode = node
                                        startExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    // 目的地 選択ボックス
                    ExposedDropdownMenuBox(
                        expanded = targetExpanded,
                        onExpandedChange = { targetExpanded = !targetExpanded },
                        modifier = Modifier.weight(1f)
                    ) {
                        OutlinedTextField(
                            value = targetNode?.label ?: "選択",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("目的地", color = Color(0xFFF43F5E)) },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = targetExpanded) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = Color(0xFFF43F5E),
                                unfocusedBorderColor = Color(0xFF475569)
                            ),
                            modifier = Modifier.menuAnchor()
                        )
                        ExposedDropdownMenu(
                            expanded = targetExpanded,
                            onDismissRequest = { targetExpanded = false }
                        ) {
                            viewModel.warehouseNodes.forEach { node ->
                                DropdownMenuItem(
                                    text = { Text(node.label) },
                                    onClick = {
                                        targetNode = node
                                        targetExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                Button(
                    onClick = {
                        val start = startNode
                        val target = targetNode
                        if (start != null && target != null) {
                            viewModel.calculateRoute(start, target)
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                    enabled = uiState !is PathUiState.Calculating
                ) {
                    Text("⚡ 最短経路を探索", fontWeight = FontWeight.Bold)
                }
            }
        }

        // マップ描画 Canvas
        Card(
            modifier = Modifier.fillMaxWidth().height(320.dp).padding(bottom = 16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF020617)),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155))
        ) {
            val activeResult = (uiState as? PathUiState.Success)?.result
            WarehouseMapCanvas(
                nodes = viewModel.warehouseNodes,
                edges = viewModel.warehouseEdges,
                pathResult = activeResult,
                modifier = Modifier.fillMaxSize().padding(16.dp)
            )
        }

        // 結果表示エリア
        when (val state = uiState) {
            is PathUiState.Success -> {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF065F46))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("✅ 最短経路 探索完了", color = Color.White, fontWeight = FontWeight.Bold)
                        Text("総移動距離: ${state.result.totalDistanceMeters} m", color = Color(0xFFA7F3D0), fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        Text("ルート: " + state.result.pathNodes.joinToString(" ➔ ") { it.label }, color = Color.White, fontSize = 12.sp)
                    }
                }
            }
            is PathUiState.Error -> {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF991B1B))
                ) {
                    Text(state.message, color = Color.White, modifier = Modifier.padding(16.dp))
                }
            }
            else -> {}
        }

        if (searchHistory.isNotEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "📜 最近の検索履歴",
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 180.dp)
            ) {
                items(searchHistory) { item ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "${item.startNodeName} ➔ ${item.targetNodeName}",
                                color = Color.White,
                                fontSize = 12.sp
                            )
                            Text(
                                text = "${item.totalDistance} m",
                                color = Color(0xFFA7F3D0),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}