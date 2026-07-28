package com.example.cleanarchitecture.domain.usecase

import com.example.cleanarchitecture.domain.repository.PathFinderRepository

/**
 * 検索履歴を1件削除するユースケース
 */
class DeleteSearchHistoryUseCase(
    private val repository: PathFinderRepository
) {
    operator fun invoke(historyId: String) {
        repository.deleteSearchHistory(historyId)
    }
}