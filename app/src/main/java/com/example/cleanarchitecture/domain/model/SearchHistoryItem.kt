package com.example.cleanarchitecture.domain.model

/**
 * 検索履歴1件分を表すドメインモデル
 */
data class SearchHistoryItem(
    val id: String,
    val startNodeName: String,
    val targetNodeName: String,
    val totalDistance: Double,
    val timestamp: Long = System.currentTimeMillis()
)