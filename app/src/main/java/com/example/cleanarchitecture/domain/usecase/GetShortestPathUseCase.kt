package com.example.cleanarchitecture.domain.usecase

import com.example.cleanarchitecture.domain.model.GraphEdge
import com.example.cleanarchitecture.domain.model.GraphNode
import com.example.cleanarchitecture.domain.model.PathResult
import com.example.cleanarchitecture.domain.repository.PathFinderRepository

/**
 * 💡 経路探索ビジネスロジックのカプセル化 (UseCase)
 */
class GetShortestPathUseCase(
    private val repository: PathFinderRepository
) {
    suspend operator fun invoke(
        nodes: List<GraphNode>,
        edges: List<GraphEdge>,
        startNode: GraphNode,
        targetNode: GraphNode
    ): Result<PathResult> {
        // スタートとゴールが同じ場合は無駄な計算をスキップするビジネスルール
        if (startNode == targetNode) {
            return Result.success(PathResult(listOf(startNode), 0.0))
        }

        return repository.findShortestPath(
            nodes = nodes,
            edges = edges,
            startNode = startNode,
            targetNode = targetNode
        )
    }
}