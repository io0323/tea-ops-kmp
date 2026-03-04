package com.teaops.shared.presenter.production

import com.teaops.shared.domain.entity.ProcessingStep
import com.teaops.shared.domain.entity.TemperatureTrend
import com.teaops.shared.domain.usecase.BuildOperationAlertSummaryUseCase
import com.teaops.shared.domain.usecase.BuildTemperatureActionSuggestionUseCase
import com.teaops.shared.domain.usecase.DetectTemperatureTrendUseCase
import com.teaops.shared.domain.usecase.EvaluateTeaQualityUseCase
import com.teaops.shared.domain.usecase.FormatDurationUseCase

/**
 * 監視画面向けのUI状態を構築するファクトリ。
 */
class ProductionMonitorStateFactory(
  private val evaluateTeaQualityUseCase: EvaluateTeaQualityUseCase,
  private val formatDurationUseCase: FormatDurationUseCase,
  private val buildOperationAlertSummaryUseCase: BuildOperationAlertSummaryUseCase,
  private val detectTemperatureTrendUseCase: DetectTemperatureTrendUseCase,
  private val buildTemperatureActionSuggestionUseCase: BuildTemperatureActionSuggestionUseCase
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
    val trendLabel = when (temperatureTrend) {
      TemperatureTrend.RISING -> "上昇"
      TemperatureTrend.FALLING -> "下降"
      TemperatureTrend.STABLE -> "安定"
    }
    val actionSuggestion = buildTemperatureActionSuggestionUseCase(
      currentTemperature = currentTemperature,
      targetTemperature = currentStep.targetTemperature,
      temperatureTrend = temperatureTrend
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
      temperatureActionLevel = actionSuggestion.level
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
