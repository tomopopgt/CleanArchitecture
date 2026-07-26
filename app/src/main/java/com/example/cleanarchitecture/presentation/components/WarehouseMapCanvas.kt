package com.example.cleanarchitecture.presentation.components

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.unit.dp
import com.example.cleanarchitecture.domain.model.GraphEdge
import com.example.cleanarchitecture.domain.model.GraphNode
import com.example.cleanarchitecture.domain.model.PathResult

@Composable
fun WarehouseMapCanvas(
    nodes: List<GraphNode>,
    edges: List<GraphEdge>,
    pathResult: PathResult?,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        // 全エッジ（灰色）
        edges.forEach { edge ->
            drawLine(
                color = Color(0xFF334155),
                start = Offset(edge.from.xRatio * w, edge.from.yRatio * h),
                end = Offset(edge.to.xRatio * w, edge.to.yRatio * h),
                strokeWidth = 2.dp.toPx()
            )
        }

        // 最短経路エッジ（エメラルドグリーン）
        if (pathResult != null) {
            val pNodes = pathResult.pathNodes
            for (i in 0 until pNodes.size - 1) {
                drawLine(
                    color = Color(0xFF34D399),
                    start = Offset(pNodes[i].xRatio * w, pNodes[i].yRatio * h),
                    end = Offset(pNodes[i + 1].xRatio * w, pNodes[i + 1].yRatio * h),
                    strokeWidth = 4.dp.toPx(),
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                )
            }
        }

        // ノード（点）
        nodes.forEach { node ->
            val isPath = pathResult?.pathNodes?.contains(node) == true
            drawCircle(
                color = if (isPath) Color(0xFFFDE047) else Color(0xFF38BDF8),
                radius = if (isPath) 8.dp.toPx() else 5.dp.toPx(),
                center = Offset(node.xRatio * w, node.yRatio * h)
            )
        }
    }
}