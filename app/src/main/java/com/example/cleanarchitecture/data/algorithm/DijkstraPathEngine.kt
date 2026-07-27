package com.example.cleanarchitecture.data.algorithm

import com.example.cleanarchitecture.domain.model.GraphEdge
import com.example.cleanarchitecture.domain.model.GraphNode
import com.example.cleanarchitecture.domain.model.PathResult
import com.example.cleanarchitecture.domain.model.SearchHistoryItem
import com.example.cleanarchitecture.domain.repository.PathFinderRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import java.util.PriorityQueue

class DijkstraPathEngine : PathFinderRepository {

    // 👇 1. メモリ上で履歴リストを保持する StateFlow を追加
    private val _searchHistory = MutableStateFlow<List<SearchHistoryItem>>(emptyList())

    // 👇 2. 履歴取得の実装を追加
    override fun getSearchHistory(): StateFlow<List<SearchHistoryItem>> {
        return _searchHistory.asStateFlow()
    }

    // 👇 3. 履歴保存の実装を追加
    override fun addSearchHistory(item: SearchHistoryItem) {
        _searchHistory.value = listOf(item) + _searchHistory.value
    }

    override suspend fun findShortestPath(
        nodes: List<GraphNode>,
        edges: List<GraphEdge>,
        startNode: GraphNode,
        targetNode: GraphNode
    ): Result<PathResult> = withContext(Dispatchers.Default) {
        runCatching {
            val adjacencyMap = mutableMapOf<GraphNode, MutableList<Pair<GraphNode, Double>>>()
            nodes.forEach { adjacencyMap[it] = mutableListOf() }

            edges.forEach { edge ->
                adjacencyMap[edge.from]?.add(edge.to to edge.distanceMeters)
                adjacencyMap[edge.to]?.add(edge.from to edge.distanceMeters)
            }

            val distances = mutableMapOf<GraphNode, Double>().withDefault { Double.MAX_VALUE }
            val previousNodes = mutableMapOf<GraphNode, GraphNode?>()
            val priorityQueue = PriorityQueue<Pair<GraphNode, Double>>(compareBy { it.second })

            distances[startNode] = 0.0
            priorityQueue.add(startNode to 0.0)

            while (priorityQueue.isNotEmpty()) {
                val (current, currentDist) = priorityQueue.poll()!!

                if (current == targetNode) break
                if (currentDist > distances.getValue(current)) continue

                val neighbors = adjacencyMap[current] ?: emptyList()
                for ((neighbor, weight) in neighbors) {
                    val newDist = currentDist + weight
                    if (newDist < distances.getValue(neighbor)) {
                        distances[neighbor] = newDist
                        previousNodes[neighbor] = current
                        priorityQueue.add(neighbor to newDist)
                    }
                }
            }

            val totalDist = distances.getValue(targetNode)
            if (totalDist == Double.MAX_VALUE) {
                throw IllegalStateException("経路が見つかりませんでした")
            }

            val path = mutableListOf<GraphNode>()
            var curr: GraphNode? = targetNode
            while (curr != null) {
                path.add(0, curr)
                curr = previousNodes[curr]
            }

            PathResult(pathNodes = path, totalDistanceMeters = totalDist)
        }
    }
}