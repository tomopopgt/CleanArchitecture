package com.example.cleanarchitecture.domain.model

data class PathResult(
    val pathNodes: List<GraphNode>,
    val totalDistanceMeters: Double
)