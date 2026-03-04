package com.teaops.shared.domain.usecase

import com.teaops.shared.domain.entity.AlertLevel
import com.teaops.shared.domain.entity.OperationAlertPriority
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * BuildOperationAlertSummaryUseCase の要約生成を検証するテスト。
 */
class BuildOperationAlertSummaryUseCaseTest {
  /**
   * 重大警告時に高優先度の要約が返ることを確認する。
   */
  @Test
  fun returnsHighPriorityForCriticalAlert() {
    val useCase = BuildOperationAlertSummaryUseCase()

    val result = useCase(
      alertLevel = AlertLevel.CRITICAL,
      qualityScore = 42,
      isDelayed = true,
      delayLabel = "遅延 00:30"
    )

    assertEquals(OperationAlertPriority.HIGH, result.priority)
    assertEquals("最優先対応", result.title)
    assertTrue(result.detail.contains("品質スコア 42"))
  }

  /**
   * 遅延も警告もない場合に低優先度の要約が返ることを確認する。
   */
  @Test
  fun returnsLowPriorityForStableOperation() {
    val useCase = BuildOperationAlertSummaryUseCase()

    val result = useCase(
      alertLevel = AlertLevel.NORMAL,
      qualityScore = 92,
      isDelayed = false,
      delayLabel = "遅延なし"
    )

    assertEquals(OperationAlertPriority.LOW, result.priority)
    assertEquals("安定運転", result.title)
    assertTrue(result.detail.contains("遅延なし"))
  }
}
