package com.teaops.shared.presenter.production

import com.teaops.shared.domain.entity.ChecklistActionLevel
import com.teaops.shared.domain.entity.ProcessingStep
import com.teaops.shared.domain.entity.StabilizationPriority
import com.teaops.shared.domain.entity.TemperatureTrend
import com.teaops.shared.domain.entity.toJapaneseLabel
import com.teaops.shared.domain.usecase.BuildOperationAlertSummaryUseCase
import com.teaops.shared.domain.usecase.BuildMonitoringDigestUseCase
import com.teaops.shared.domain.usecase.BuildOperationalRiskSnapshotUseCase
import com.teaops.shared.domain.usecase.BuildPriorityChecklistUseCase
import com.teaops.shared.domain.usecase.BuildStabilizationGuideUseCase
import com.teaops.shared.domain.usecase.BuildTemperatureActionSuggestionUseCase
import com.teaops.shared.domain.usecase.CalculateTemperatureDeviationIndexUseCase
import com.teaops.shared.domain.usecase.DetectTemperatureTrendUseCase
import com.teaops.shared.domain.usecase.EvaluateTeaQualityUseCase
import com.teaops.shared.domain.usecase.FormatDurationUseCase
import com.teaops.shared.domain.usecase.SuggestMonitoringIntervalUseCase

/**
 * 監視画面向けのUI状態を構築するファクトリ。
 */
class ProductionMonitorStateFactory(
  private val evaluateTeaQualityUseCase: EvaluateTeaQualityUseCase,
  private val formatDurationUseCase: FormatDurationUseCase,
  private val buildOperationAlertSummaryUseCase: BuildOperationAlertSummaryUseCase,
  private val detectTemperatureTrendUseCase: DetectTemperatureTrendUseCase,
  private val buildTemperatureActionSuggestionUseCase: BuildTemperatureActionSuggestionUseCase,
  private val calculateTemperatureDeviationIndexUseCase:
    CalculateTemperatureDeviationIndexUseCase,
  private val suggestMonitoringIntervalUseCase: SuggestMonitoringIntervalUseCase,
  private val buildOperationalRiskSnapshotUseCase: BuildOperationalRiskSnapshotUseCase,
  private val buildPriorityChecklistUseCase: BuildPriorityChecklistUseCase,
  private val buildMonitoringDigestUseCase: BuildMonitoringDigestUseCase,
  private val buildStabilizationGuideUseCase: BuildStabilizationGuideUseCase
) {
  /**
   * ドメイン情報を画面描画用の状態へ変換する。
   */
  fun create(
    currentStep: ProcessingStep,
    currentTemperature: Double,
    elapsedSecondsInStep: Long,
    previousTemperature: Double = currentTemperature
  ): ProductionMonitorUiState {
    val quality = evaluateTeaQualityUseCase(
      step = currentStep,
      currentTemperature = currentTemperature,
      elapsedSecondsInStep = elapsedSecondsInStep
    )
    val duration = currentStep.duration.coerceAtLeast(0L)
    val boundedElapsed = elapsedSecondsInStep.coerceAtLeast(0L)
    val remaining = (duration - boundedElapsed).coerceAtLeast(0L)
    val progressPercent = calculateProgressPercent(
      elapsedSeconds = boundedElapsed,
      durationSeconds = duration
    )
    val isDelayed = boundedElapsed > duration
    val delaySeconds = (boundedElapsed - duration).coerceAtLeast(0L)
    val progressLabel = buildProgressLabel(progressPercent, isDelayed)
    val remainingTimeLabel = formatDurationUseCase(remaining)
    val delayLabel = if (isDelayed) {
      "遅延 ${formatDurationUseCase(delaySeconds)}"
    } else {
      "遅延なし"
    }
    val operationSummary = buildOperationAlertSummaryUseCase(
      alertLevel = quality.alertLevel,
      qualityScore = quality.score,
      isDelayed = isDelayed,
      delayLabel = delayLabel
    )
    val temperatureTrend = detectTemperatureTrendUseCase(
      previousTemperature = previousTemperature,
      currentTemperature = currentTemperature
    )
    val trendLabel = temperatureTrend.toJapaneseLabel()
    val actionSuggestion = buildTemperatureActionSuggestionUseCase(
      currentTemperature = currentTemperature,
      targetTemperature = currentStep.targetTemperature,
      temperatureTrend = temperatureTrend
    )
    val deviationAssessment = calculateTemperatureDeviationIndexUseCase(
      currentTemperature = currentTemperature,
      targetTemperature = currentStep.targetTemperature
    )
    val intervalSuggestion = suggestMonitoringIntervalUseCase(
      operationPriority = operationSummary.priority,
      actionLevel = actionSuggestion.level,
      deviationIndex = deviationAssessment.deviationIndex
    )
    val riskSnapshot = buildOperationalRiskSnapshotUseCase(
      qualityScore = quality.score,
      deviationIndex = deviationAssessment.deviationIndex,
      operationPriority = operationSummary.priority
    )
    val checklistItems = buildPriorityChecklistUseCase(
      riskBand = riskSnapshot.band,
      temperatureTrend = temperatureTrend,
      isDelayed = isDelayed
    )
    val primaryChecklist = checklistItems.firstOrNull()
    val secondaryChecklist = checklistItems.getOrNull(1)
    val monitoringDigest = buildMonitoringDigestUseCase(
      riskBand = riskSnapshot.band,
      nextCheckLevel = intervalSuggestion.level,
      temperatureTrend = temperatureTrend,
      isDelayed = isDelayed
    )
    val stabilizationGuide = buildStabilizationGuideUseCase(
      riskBand = riskSnapshot.band,
      temperatureTrend = temperatureTrend,
      nextCheckLevel = intervalSuggestion.level
    )

    return ProductionMonitorUiState(
      currentStep = currentStep,
      remainingSeconds = remaining,
      remainingTimeLabel = remainingTimeLabel,
      currentTemperature = currentTemperature,
      qualityScore = quality.score,
      warningMessage = quality.message,
      alertLevel = quality.alertLevel,
      progressPercent = progressPercent,
      progressLabel = progressLabel,
      isDelayed = isDelayed,
      delaySeconds = delaySeconds,
      delayLabel = delayLabel,
      operationAlertTitle = operationSummary.title,
      operationAlertDetail = operationSummary.detail,
      operationAlertPriority = operationSummary.priority,
      temperatureTrend = temperatureTrend,
      temperatureTrendLabel = trendLabel,
      temperatureActionTitle = actionSuggestion.title,
      temperatureActionDetail = actionSuggestion.detail,
      temperatureActionLevel = actionSuggestion.level,
      temperatureDeviationIndex = deviationAssessment.deviationIndex,
      temperatureDeviationLabel = deviationAssessment.deviationLabel,
      nextCheckInSeconds = intervalSuggestion.seconds,
      nextCheckLabel = intervalSuggestion.label,
      nextCheckLevel = intervalSuggestion.level,
      riskBand = riskSnapshot.band,
      riskLabel = riskSnapshot.label,
      riskSummary = riskSnapshot.summary,
      checklistPrimaryTitle = primaryChecklist?.title.orEmpty(),
      checklistPrimaryDetail = primaryChecklist?.detail.orEmpty(),
      checklistPrimaryLevel = primaryChecklist?.level ?: ChecklistActionLevel.INFO,
      checklistSecondaryTitle = secondaryChecklist?.title.orEmpty(),
      checklistSecondaryDetail = secondaryChecklist?.detail.orEmpty(),
      checklistSecondaryLevel = secondaryChecklist?.level ?: ChecklistActionLevel.INFO,
      monitoringDigestTitle = monitoringDigest.title,
      monitoringDigestDetail = monitoringDigest.detail,
      monitoringDigestTone = monitoringDigest.tone,
      stabilizationGuideTitle = stabilizationGuide.title,
      stabilizationGuideCondition = stabilizationGuide.condition,
      stabilizationGuidePriority = stabilizationGuide.priority
    )
  }

  /**
   * 工程経過秒数から進捗率を算出する。
   */
  private fun calculateProgressPercent(
    elapsedSeconds: Long,
    durationSeconds: Long
  ): Int {
    if (durationSeconds <= 0L) {
      return 100
    }
    val ratio = elapsedSeconds.toDouble() / durationSeconds.toDouble()
    return (ratio * 100.0).toInt().coerceIn(0, 100)
  }

  /**
   * 進捗率と遅延状態からステータス文言を返す。
   */
  private fun buildProgressLabel(
    progressPercent: Int,
    isDelayed: Boolean
  ): String {
    if (isDelayed) {
      return "遅延"
    }
    return when {
      progressPercent < 25 -> "準備"
      progressPercent < 60 -> "進行中"
      progressPercent < 100 -> "終盤"
      else -> "完了目安"
    }
  }
}
