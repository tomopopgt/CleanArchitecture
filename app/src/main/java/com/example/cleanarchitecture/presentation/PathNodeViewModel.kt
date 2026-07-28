package com.example.cleanarchitecture.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cleanarchitecture.data.algorithm.DijkstraPathEngine
import com.example.cleanarchitecture.domain.model.GraphEdge
import com.example.cleanarchitecture.domain.model.GraphNode
import com.example.cleanarchitecture.domain.model.PathResult
import com.example.cleanarchitecture.domain.usecase.GetShortestPathUseCase
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.example.cleanarchitecture.domain.model.SearchHistoryItem
import com.example.cleanarchitecture.domain.repository.PathFinderRepository
import com.example.cleanarchitecture.domain.usecase.GetSearchHistoryUseCase
import com.example.cleanarchitecture.domain.usecase.AddSearchHistoryUseCase
import java.util.UUID
import com.example.cleanarchitecture.domain.usecase.DeleteSearchHistoryUseCase

sealed interface PathUiState {
    object Idle : PathUiState
    object Calculating : PathUiState
    data class Success(val result: PathResult) : PathUiState
    data class Error(val message: String) : PathUiState
}

class PathNodeViewModel(
    // 1. リポジトリの本体（DijkstraPathEngine）を1つ生成
    private val repository: PathFinderRepository = DijkstraPathEngine(),

    // 2. そのリポジトリを各ユースケースに渡して初期化
    private val getShortestPathUseCase: GetShortestPathUseCase = GetShortestPathUseCase(repository),
    private val getSearchHistoryUseCase: GetSearchHistoryUseCase = GetSearchHistoryUseCase(repository),
    private val addSearchHistoryUseCase: AddSearchHistoryUseCase = AddSearchHistoryUseCase(repository),
    private val deleteSearchHistoryUseCase: DeleteSearchHistoryUseCase = DeleteSearchHistoryUseCase(repository)

) : ViewModel() {

    val searchHistory: StateFlow<List<SearchHistoryItem>> = getSearchHistoryUseCase()

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

            val result = getShortestPathUseCase(
                nodes = warehouseNodes,
                edges = warehouseEdges,
                startNode = start,
                targetNode = target
            )

            delay(timeMillis = 300)

            _uiState.value = result.fold(
                onSuccess = { pathResult ->
                    // 検索が成功したら、履歴を作って保存する
                    val historyItem = SearchHistoryItem(
                        id = UUID.randomUUID().toString(),
                        startNodeName = start.id,
                        targetNodeName = target.id,
                        totalDistance = pathResult.totalDistanceMeters
                    )
                    addSearchHistoryUseCase(historyItem)

                    PathUiState.Success(result = pathResult)
                },
                onFailure = { PathUiState.Error(it.message ?: "経路探索エラー") }
            )
        }
    }
    // 履歴削除関数
    fun deleteSearchHistory(id: String) {
        deleteSearchHistoryUseCase(id)
    }
}