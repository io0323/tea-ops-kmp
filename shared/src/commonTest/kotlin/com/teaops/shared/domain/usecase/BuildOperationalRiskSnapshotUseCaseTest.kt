package com.teaops.shared.domain.usecase

import com.teaops.shared.domain.entity.OperationAlertPriority
import com.teaops.shared.domain.entity.RiskBand
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * BuildOperationalRiskSnapshotUseCase の統合判定を検証するテスト。
 */
class BuildOperationalRiskSnapshotUseCaseTest {
  /**
   * 高優先度または高リスク点数で高リスク判定になることを確認する。
   */
  @Test
  fun returnsHighBandForHighPrioritySignal() {
    val useCase = BuildOperationalRiskSnapshotUseCase()

    val result = useCase(
      qualityScore = 45,
      deviationIndex = 80,
      operationPriority = OperationAlertPriority.HIGH
    )

    assertEquals(RiskBand.HIGH, result.band)
    assertEquals("高リスク", result.label)
  }

  /**
   * 低負荷条件では低リスク判定になることを確認する。
   */
  @Test
  fun returnsLowBandForStableSignal() {
    val useCase = BuildOperationalRiskSnapshotUseCase()

    val result = useCase(
      qualityScore = 92,
      deviationIndex = 10,
      operationPriority = OperationAlertPriority.LOW
    )

    assertEquals(RiskBand.LOW, result.band)
    assertEquals("低リスク", result.label)
  }
}
