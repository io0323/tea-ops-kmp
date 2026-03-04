package com.teaops.shared.presenter.production

import com.teaops.shared.domain.entity.AlertLevel
import com.teaops.shared.domain.entity.MonitoringCadenceLevel
import com.teaops.shared.domain.entity.OperationAlertPriority
import com.teaops.shared.domain.entity.ProcessingStep
import com.teaops.shared.domain.entity.RiskBand
import com.teaops.shared.domain.entity.TemperatureActionLevel
import com.teaops.shared.domain.entity.TemperatureTrend
import com.teaops.shared.domain.usecase.BuildOperationAlertSummaryUseCase
import com.teaops.shared.domain.usecase.BuildOperationalRiskSnapshotUseCase
import com.teaops.shared.domain.usecase.BuildTemperatureActionSuggestionUseCase
import com.teaops.shared.domain.usecase.CalculateTemperatureDeviationIndexUseCase
import com.teaops.shared.domain.usecase.DetectTemperatureTrendUseCase
import com.teaops.shared.domain.usecase.EvaluateTeaQualityUseCase
import com.teaops.shared.domain.usecase.FormatDurationUseCase
import com.teaops.shared.domain.usecase.SuggestMonitoringIntervalUseCase
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * ProductionMonitorStateFactory の状態変換を検証するテスト。
 */
class ProductionMonitorStateFactoryTest {
  /**
   * 通常進行中の進捗情報が正しく構築されることを確認する。
   */
  @Test
  fun createBuildsInProgressState() {
    val factory = ProductionMonitorStateFactory(
      evaluateTeaQualityUseCase = EvaluateTeaQualityUseCase(),
      formatDurationUseCase = FormatDurationUseCase(),
      buildOperationAlertSummaryUseCase = BuildOperationAlertSummaryUseCase(),
      detectTemperatureTrendUseCase = DetectTemperatureTrendUseCase(),
      buildTemperatureActionSuggestionUseCase = BuildTemperatureActionSuggestionUseCase(),
      calculateTemperatureDeviationIndexUseCase =
        CalculateTemperatureDeviationIndexUseCase(),
      suggestMonitoringIntervalUseCase = SuggestMonitoringIntervalUseCase(),
      buildOperationalRiskSnapshotUseCase = BuildOperationalRiskSnapshotUseCase()
    )
    val step = ProcessingStep(
      id = "seisei",
      stepName = "殺青",
      targetTemperature = 180.0,
      duration = 200L
    )

    val state = factory.create(
      currentStep = step,
      currentTemperature = 181.0,
      elapsedSecondsInStep = 80L
    )

    assertEquals(40, state.progressPercent)
    assertEquals("進行中", state.progressLabel)
    assertFalse(state.isDelayed)
    assertEquals("02:00", state.remainingTimeLabel)
    assertEquals("遅延なし", state.delayLabel)
    assertEquals(0L, state.delaySeconds)
    assertEquals(AlertLevel.NORMAL, state.alertLevel)
    assertEquals(OperationAlertPriority.LOW, state.operationAlertPriority)
    assertEquals("安定運転", state.operationAlertTitle)
    assertEquals(TemperatureTrend.STABLE, state.temperatureTrend)
    assertEquals("安定", state.temperatureTrendLabel)
    assertEquals(TemperatureActionLevel.KEEP, state.temperatureActionLevel)
    assertEquals("現状維持", state.temperatureActionTitle)
    assertEquals(5, state.temperatureDeviationIndex)
    assertEquals("安定", state.temperatureDeviationLabel)
    assertEquals(60, state.nextCheckInSeconds)
    assertEquals("通常監視", state.nextCheckLabel)
    assertEquals(MonitoringCadenceLevel.RELAXED, state.nextCheckLevel)
    assertEquals(RiskBand.LOW, state.riskBand)
    assertEquals("低リスク", state.riskLabel)
  }

  /**
   * 工程時間超過時に遅延状態として構築されることを確認する。
   */
  @Test
  fun createBuildsDelayedStateWhenElapsedExceedsDuration() {
    val factory = ProductionMonitorStateFactory(
      evaluateTeaQualityUseCase = EvaluateTeaQualityUseCase(),
      formatDurationUseCase = FormatDurationUseCase(),
      buildOperationAlertSummaryUseCase = BuildOperationAlertSummaryUseCase(),
      detectTemperatureTrendUseCase = DetectTemperatureTrendUseCase(),
      buildTemperatureActionSuggestionUseCase = BuildTemperatureActionSuggestionUseCase(),
      calculateTemperatureDeviationIndexUseCase =
        CalculateTemperatureDeviationIndexUseCase(),
      suggestMonitoringIntervalUseCase = SuggestMonitoringIntervalUseCase(),
      buildOperationalRiskSnapshotUseCase = BuildOperationalRiskSnapshotUseCase()
    )
    val step = ProcessingStep(
      id = "junen",
      stepName = "揉捻",
      targetTemperature = 95.0,
      duration = 100L
    )

    val state = factory.create(
      currentStep = step,
      currentTemperature = 120.0,
      elapsedSecondsInStep = 140L,
      previousTemperature = 110.0
    )

    assertEquals(100, state.progressPercent)
    assertEquals("遅延", state.progressLabel)
    assertTrue(state.isDelayed)
    assertEquals("00:00", state.remainingTimeLabel)
    assertEquals(40L, state.delaySeconds)
    assertEquals("遅延 00:40", state.delayLabel)
    assertTrue(state.qualityScore < 75)
    assertEquals(OperationAlertPriority.HIGH, state.operationAlertPriority)
    assertEquals("最優先対応", state.operationAlertTitle)
    assertEquals(TemperatureTrend.RISING, state.temperatureTrend)
    assertEquals("上昇", state.temperatureTrendLabel)
    assertEquals(TemperatureActionLevel.URGENT, state.temperatureActionLevel)
    assertEquals("加熱抑制が必要", state.temperatureActionTitle)
    assertEquals(100, state.temperatureDeviationIndex)
    assertEquals("危険", state.temperatureDeviationLabel)
    assertEquals(15, state.nextCheckInSeconds)
    assertEquals("即時監視", state.nextCheckLabel)
    assertEquals(MonitoringCadenceLevel.FAST, state.nextCheckLevel)
    assertEquals(RiskBand.HIGH, state.riskBand)
    assertEquals("高リスク", state.riskLabel)
  }
}
