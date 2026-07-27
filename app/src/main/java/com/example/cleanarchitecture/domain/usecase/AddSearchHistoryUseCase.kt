package com.example.cleanarchitecture.domain.usecase

import com.example.cleanarchitecture.domain.model.SearchHistoryItem
import com.example.cleanarchitecture.domain.repository.PathFinderRepository

/**
 * 検索履歴を追加するユースケース
 */
class AddSearchHistoryUseCase(
    private val repository: PathFinderRepository
) {
    operator fun invoke(item: SearchHistoryItem) {
        repository.addSearchHistory(item)
    }
}