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

  /**
   * 中程度リスク時に短周期監視が返ることを確認する。
   */
  @Test
  fun returnsNormalCadenceForMediumRisk() {
    val useCase = SuggestMonitoringIntervalUseCase()

    val result = useCase(
      operationPriority = OperationAlertPriority.MEDIUM,
      actionLevel = TemperatureActionLevel.ADJUST,
      deviationIndex = 50
    )

    assertEquals(30, result.seconds)
    assertEquals("短周期監視", result.label)
    assertEquals(MonitoringCadenceLevel.NORMAL, result.level)
  }

  /**
   * 逸脱指数の閾値境界で正しい監視レベルが返ることを確認する。
   */
  @Test
  fun respectsDeviationIndexThresholds() {
    val useCase = SuggestMonitoringIntervalUseCase()

    val fastByIndex = useCase(
      operationPriority = OperationAlertPriority.LOW,
      actionLevel = TemperatureActionLevel.KEEP,
      deviationIndex = 70
    )
    val normalByIndex = useCase(
      operationPriority = OperationAlertPriority.LOW,
      actionLevel = TemperatureActionLevel.KEEP,
      deviationIndex = 35
    )

    assertEquals(MonitoringCadenceLevel.FAST, fastByIndex.level)
    assertEquals(MonitoringCadenceLevel.NORMAL, normalByIndex.level)
  }

  /**
   * 逸脱指数は 0〜100 の範囲に丸められることを確認する。
   */
  @Test
  fun clampsDeviationIndexIntoValidRange() {
    val useCase = SuggestMonitoringIntervalUseCase()

    val belowRange = useCase(
      operationPriority = OperationAlertPriority.LOW,
      actionLevel = TemperatureActionLevel.KEEP,
      deviationIndex = -10
    )
    val aboveRange = useCase(
      operationPriority = OperationAlertPriority.HIGH,
      actionLevel = TemperatureActionLevel.URGENT,
      deviationIndex = 120
    )

    assertEquals(MonitoringCadenceLevel.RELAXED, belowRange.level)
    assertEquals(MonitoringCadenceLevel.FAST, aboveRange.level)
  }
}
