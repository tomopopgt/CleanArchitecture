package com.example.cleanarchitecture

import com.example.cleanarchitecture.data.algorithm.DijkstraPathEngine
import com.example.cleanarchitecture.domain.model.GraphEdge
import com.example.cleanarchitecture.domain.model.GraphNode
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class DijkstraPathEngineTest {

    private val engine = DijkstraPathEngine()

    @Test
    fun `findShortestPath - 正常系 - 接続されたグラフで正確な最短経路を計算できること`() = runBlocking {
        // Given
        val nodeA = GraphNode("A", "Node A", 0f, 0f)
        val nodeB = GraphNode("B", "Node B", 0f, 0f)
        val nodeC = GraphNode("C", "Node C", 0f, 0f)

        val nodes = listOf(nodeA, nodeB, nodeC)
        val edges = listOf(
            GraphEdge(nodeA, nodeB, 10.0),
            GraphEdge(nodeB, nodeC, 5.0),
            GraphEdge(nodeA, nodeC, 20.0) // A -> C 直通は 20 だが、A -> B -> C は 15
        )

        // When (A -> C)
        val result = engine.findShortestPath(nodes, edges, nodeA, nodeC)

        // Then
        assertTrue(result.isSuccess)
        val pathResult = result.getOrThrow()
        assertEquals(15.0, pathResult.totalDistanceMeters, 0.001)
        assertEquals(listOf(nodeA, nodeB, nodeC), pathResult.pathNodes)
    }

    @Test
    fun `findShortestPath - 境界値 - スタートとターゲットが同一ノードの場合、距離0を返すこと`() = runBlocking {
        // Given
        val nodeA = GraphNode("A", "Node A", 0f, 0f)
        val nodes = listOf(nodeA)
        val edges = emptyList<GraphEdge>()

        // When (A -> A)
        val result = engine.findShortestPath(nodes, edges, nodeA, nodeA)

        // Then
        assertTrue(result.isSuccess)
        val pathResult = result.getOrThrow()
        assertEquals(0.0, pathResult.totalDistanceMeters, 0.001)
        assertEquals(listOf(nodeA), pathResult.pathNodes)
    }

    @Test
    fun `findShortestPath - 異常系 - 経路が存在しない独立したノードへの探索は失敗すること`() = runBlocking {
        // Given (A-B は繋がっているが、X は独立)
        val nodeA = GraphNode("A", "Node A", 0f, 0f)
        val nodeB = GraphNode("B", "Node B", 0f, 0f)
        val nodeX = GraphNode("X", "Node X", 0f, 0f)

        val nodes = listOf(nodeA, nodeB, nodeX)
        val edges = listOf(GraphEdge(nodeA, nodeB, 10.0))

        // When (A -> X)
        val result = engine.findShortestPath(nodes, edges, nodeA, nodeX)

        // Then
        assertTrue(result.isFailure)
    }
}