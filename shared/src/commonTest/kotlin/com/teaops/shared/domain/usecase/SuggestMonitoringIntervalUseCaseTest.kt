package com.teaops.shared.domain.usecase

import com.teaops.shared.domain.entity.MonitoringCadenceLevel
import com.teaops.shared.domain.entity.OperationAlertPriority
import com.teaops.shared.domain.entity.TemperatureActionLevel
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * SuggestMonitoringIntervalUseCase の提案結果を検証するテスト。
 */
class SuggestMonitoringIntervalUseCaseTest {
  /**
   * 高リスク時に即時監視が返ることを確認する。
   */
  @Test
  fun returnsFastCadenceForHighRisk() {
    val useCase = SuggestMonitoringIntervalUseCase()

    val result = useCase(
      operationPriority = OperationAlertPriority.HIGH,
      actionLevel = TemperatureActionLevel.URGENT,
      deviationIndex = 90
    )

    assertEquals(15, result.seconds)
    assertEquals("即時監視", result.label)
    assertEquals(MonitoringCadenceLevel.FAST, result.level)
  }

  /**
   * 安定時に通常監視が返ることを確認する。
   */
  @Test
  fun returnsRelaxedCadenceForStableState() {
    val useCase = SuggestMonitoringIntervalUseCase()

    val result = useCase(
      operationPriority = OperationAlertPriority.LOW,
      actionLevel = TemperatureActionLevel.KEEP,
      deviationIndex = 12
    )

    assertEquals(60, result.seconds)
    assertEquals("通常監視", result.label)
    assertEquals(MonitoringCadenceLevel.RELAXED, result.level)
  }
}
