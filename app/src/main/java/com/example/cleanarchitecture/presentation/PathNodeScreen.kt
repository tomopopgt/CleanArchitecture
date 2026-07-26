package com.example.cleanarchitecture.presentation

import androidx.compose.animation.*
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
import com.example.cleanarchitecture.presentation.components.WarehouseMapCanvas

@Composable
fun PathNodeScreen(
    // 💡 remember を使用して ViewModel を初期化（追加依存関係なしで確実に動作します）
    viewModel: PathNodeViewModel = remember { PathNodeViewModel() }
) {
    val uiState by viewModel.uiState.collectAsState()

    var startNode by remember { mutableStateOf(viewModel.warehouseNodes[0]) } // A
    var targetNode by remember { mutableStateOf(viewModel.warehouseNodes[5]) } // F

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F172A))
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
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

        Card(
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Button(
                    onClick = { viewModel.calculateRoute(startNode, targetNode) },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                    enabled = uiState !is PathUiState.Calculating
                ) {
                    Text("⚡ 経路を探索 (A ➔ F)", fontWeight = FontWeight.Bold)
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

        // 結果テキスト表示
        if (uiState is PathUiState.Success) {
            val result = (uiState as PathUiState.Success).result
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF065F46))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("✅ 最短経路 探索完了", color = Color.White, fontWeight = FontWeight.Bold)
                    Text("総移動距離: ${result.totalDistanceMeters} m", color = Color(0xFFA7F3D0), fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Text("ルート: " + result.pathNodes.joinToString(" ➔ ") { it.id }, color = Color.White, fontSize = 12.sp)
                }
            }
        }
    }
}