package com.example.cleanarchitecture.domain.usecase

import com.example.cleanarchitecture.domain.model.SearchHistoryItem
import com.example.cleanarchitecture.domain.repository.PathFinderRepository
import kotlinx.coroutines.flow.StateFlow

/**
 * 検索履歴一覧を取得するユースケース
 */
class GetSearchHistoryUseCase(
    private val repository: PathFinderRepository
) {
    operator fun invoke(): StateFlow<List<SearchHistoryItem>> {
        return repository.getSearchHistory()
    }
}