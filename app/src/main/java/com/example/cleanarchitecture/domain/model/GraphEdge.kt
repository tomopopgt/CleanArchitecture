package com.example.cleanarchitecture.domain.model

data class GraphEdge(
    val from: GraphNode,
    val to: GraphNode,
    val distanceMeters: Double
)