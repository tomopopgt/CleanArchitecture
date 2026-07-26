package com.example.cleanarchitecture.domain.usecase

import com.example.cleanarchitecture.domain.model.GraphEdge
import com.example.cleanarchitecture.domain.model.GraphNode
import com.example.cleanarchitecture.domain.model.PathResult
import com.example.cleanarchitecture.domain.repository.PathFinderRepository
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class GetShortestPathUseCaseTest {

    // 💡 モックライブラリを使わず、純粋なKotlinでテスト用Fakeリポジトリを作成
    private class FakePathFinderRepository : PathFinderRepository {
        var isCalled = false

        override suspend fun findShortestPath(
            nodes: List<GraphNode>,
            edges: List<GraphEdge>,
            startNode: GraphNode,
            targetNode: GraphNode
        ): Result<PathResult> {
            isCalled = true
            return Result.success(PathResult(listOf(startNode, targetNode), 10.0))
        }
    }

    private val fakeRepository = FakePathFinderRepository()
    private val useCase = GetShortestPathUseCase(fakeRepository)

    @Test
    fun `invoke - 未選択のノードがある場合はエラーメッセージ付きでFailureを返すこと`() = runBlocking {
        val nodeA = GraphNode("A", "Node A", 0f, 0f)

        // スタート地点が未選択
        val result1 = useCase(listOf(nodeA), emptyList(), null, nodeA)
        assertTrue(result1.isFailure)
        assertEquals("スタート地点とゴール地点を選択してください。", result1.exceptionOrNull()?.message)

        // ゴール地点が未選択
        val result2 = useCase(listOf(nodeA), emptyList(), nodeA, null)
        assertTrue(result2.isFailure)

        // リポジトリが呼ばれていないことを検証
        assertFalse(fakeRepository.isCalled)
    }

    @Test
    fun `invoke - スタートとゴールが同じ場合、計算エンジンを呼ぶことなく即座に距離0でSuccessを返すこと`() = runBlocking {
        val nodeA = GraphNode("A", "Node A", 0f, 0f)

        val result = useCase(listOf(nodeA), emptyList(), nodeA, nodeA)

        assertTrue(result.isSuccess)
        assertEquals(0.0, result.getOrThrow().totalDistanceMeters, 0.001)

        // 無駄な計算処理をスキップできたか検証
        assertFalse(fakeRepository.isCalled)
    }

    @Test
    fun `invoke - 正常系ではリポジトリを経由して経路計算結果が返されること`() = runBlocking {
        val nodeA = GraphNode("A", "Node A", 0f, 0f)
        val nodeB = GraphNode("B", "Node B", 0f, 0f)

        val result = useCase(listOf(nodeA, nodeB), emptyList(), nodeA, nodeB)

        assertTrue(result.isSuccess)
        assertTrue(fakeRepository.isCalled)
        assertEquals(10.0, result.getOrThrow().totalDistanceMeters, 0.001)
    }
}