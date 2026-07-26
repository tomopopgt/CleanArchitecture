package com.example.cleanarchitecture.domain.usecase

import com.example.cleanarchitecture.domain.model.GraphEdge
import com.example.cleanarchitecture.domain.model.GraphNode
import com.example.cleanarchitecture.domain.model.PathResult
import com.example.cleanarchitecture.domain.repository.PathFinderRepository

/**
 * 💡 経路探索ビジネスロジックおよびバリデーションのカプセル化
 */
class GetShortestPathUseCase(
    private val repository: PathFinderRepository
) {
    suspend operator fun invoke(
        nodes: List<GraphNode>,
        edges: List<GraphEdge>,
        startNode: GraphNode?,
        targetNode: GraphNode?
    ): Result<PathResult> {
        // 1. 未選択チェック
        if (startNode == null || targetNode == null) {
            return Result.failure(IllegalArgumentException("スタート地点とゴール地点を選択してください。"))
        }

        // 2. 同一ノードチェック（無駄な計算をガードするドメインルール）
        if (startNode.id == targetNode.id) {
            return Result.success(PathResult(listOf(startNode), 0.0))
        }

        // 3. リポジトリを経由してダイクストラ法を実行
        return repository.findShortestPath(
            nodes = nodes,
            edges = edges,
            startNode = startNode,
            targetNode = targetNode
        )
    }
}