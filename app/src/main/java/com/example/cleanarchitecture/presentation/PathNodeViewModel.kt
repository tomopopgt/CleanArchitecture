package com.example.cleanarchitecture.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cleanarchitecture.data.algorithm.DijkstraPathEngine
import com.example.cleanarchitecture.domain.model.GraphEdge
import com.example.cleanarchitecture.domain.model.GraphNode
import com.example.cleanarchitecture.domain.model.PathResult
import com.example.cleanarchitecture.domain.repository.PathFinderRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface PathUiState {
    object Idle : PathUiState
    object Calculating : PathUiState
    data class Success(val result: PathResult) : PathUiState
    data class Error(val message: String) : PathUiState
}

class PathNodeViewModel(
    private val repository: PathFinderRepository = DijkstraPathEngine()
) : ViewModel() {

    private val _uiState = MutableStateFlow<PathUiState>(PathUiState.Idle)
    val uiState: StateFlow<PathUiState> = _uiState.asStateFlow()

    val warehouseNodes = listOf(
        GraphNode("A", "入荷場 A", 0.15f, 0.20f),
        GraphNode("B", "保管棚 B", 0.50f, 0.15f),
        GraphNode("C", "保管棚 C", 0.85f, 0.25f),
        GraphNode("D", "検品台 D", 0.30f, 0.55f),
        GraphNode("E", "梱包エリア E", 0.70f, 0.60f),
        GraphNode("F", "出荷口 F", 0.50f, 0.85f)
    )

    val warehouseEdges = listOf(
        GraphEdge(warehouseNodes[0], warehouseNodes[1], 15.0),
        GraphEdge(warehouseNodes[0], warehouseNodes[3], 25.0),
        GraphEdge(warehouseNodes[1], warehouseNodes[2], 20.0),
        GraphEdge(warehouseNodes[1], warehouseNodes[3], 18.0),
        GraphEdge(warehouseNodes[2], warehouseNodes[4], 12.0),
        GraphEdge(warehouseNodes[3], warehouseNodes[4], 22.0),
        GraphEdge(warehouseNodes[3], warehouseNodes[5], 16.0),
        GraphEdge(warehouseNodes[4], warehouseNodes[5], 10.0)
    )

    fun calculateRoute(start: GraphNode, target: GraphNode) {
        viewModelScope.launch {
            _uiState.value = PathUiState.Calculating

            val result = repository.findShortestPath(
                nodes = warehouseNodes,
                edges = warehouseEdges,
                startNode = start,
                targetNode = target
            )

            delay(300)

            _uiState.value = result.fold(
                onSuccess = { PathUiState.Success(it) },
                onFailure = { PathUiState.Error(it.message ?: "経路探索エラー") }
            )
        }
    }
}