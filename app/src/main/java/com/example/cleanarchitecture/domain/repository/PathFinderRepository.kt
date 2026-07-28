package com.example.cleanarchitecture.domain.repository

import com.example.cleanarchitecture.domain.model.GraphEdge
import com.example.cleanarchitecture.domain.model.GraphNode
import com.example.cleanarchitecture.domain.model.PathResult
import com.example.cleanarchitecture.domain.model.SearchHistoryItem
import kotlinx.coroutines.flow.StateFlow

interface PathFinderRepository {
    suspend fun findShortestPath(
        nodes: List<GraphNode>,
        edges: List<GraphEdge>,
        startNode: GraphNode,
        targetNode: GraphNode
    ): Result<PathResult>
    /**
     * 検索履歴一覧をリアルタイム（Flow）で取得する
     */
    fun getSearchHistory(): StateFlow<List<SearchHistoryItem>>

    /**
     * 新しい検索結果を履歴に保存する
     */
    fun addSearchHistory(item: SearchHistoryItem)

    fun deleteSearchHistory(historyId: String)
}