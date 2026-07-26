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
    fun `findShortestPath should return correct shortest route for connected graph`() = runBlocking {
        // Given
        val nodeA = GraphNode("A", "Node A", 0f, 0f)
        val nodeB = GraphNode("B", "Node B", 0f, 0f)
        val nodeC = GraphNode("C", "Node C", 0f, 0f)

        val nodes = listOf(nodeA, nodeB, nodeC)
        val edges = listOf(
            GraphEdge(nodeA, nodeB, 10.0),
            GraphEdge(nodeB, nodeC, 5.0),
            GraphEdge(nodeA, nodeC, 20.0) // 遠回り
        )

        // When (A -> C)
        val result = engine.findShortestPath(nodes, edges, nodeA, nodeC)

        // Then
        assertTrue(result.isSuccess)
        val pathResult = result.getOrThrow()
        assertEquals(15.0, pathResult.totalDistanceMeters, 0.001) // A -> B -> C (10 + 5 = 15)
        assertEquals(listOf(nodeA, nodeB, nodeC), pathResult.pathNodes)
    }

    @Test
    fun `findShortestPath should fail when no path exists between nodes`() = runBlocking {
        // Given (孤立したグラフ)
        val nodeA = GraphNode("A", "Node A", 0f, 0f)
        val nodeB = GraphNode("B", "Node B", 0f, 0f)
        val isolatedNode = GraphNode("X", "Node X", 0f, 0f)

        val nodes = listOf(nodeA, nodeB, isolatedNode)
        val edges = listOf(
            GraphEdge(nodeA, nodeB, 10.0)
        )

        // When (A -> X)
        val result = engine.findShortestPath(nodes, edges, nodeA, isolatedNode)

        // Then
        assertTrue(result.isFailure)
    }
}