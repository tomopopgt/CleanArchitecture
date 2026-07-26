package com.example.cleanarchitecture.domain.repository

import com.example.cleanarchitecture.domain.model.GraphEdge
import com.example.cleanarchitecture.domain.model.GraphNode
import com.example.cleanarchitecture.domain.model.PathResult

interface PathFinderRepository {
    suspend fun findShortestPath(
        nodes: List<GraphNode>,
        edges: List<GraphEdge>,
        startNode: GraphNode,
        targetNode: GraphNode
    ): Result<PathResult>
}