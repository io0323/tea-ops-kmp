package com.teaops.shared.presenter.production

import com.teaops.shared.domain.entity.ProcessingStep
import com.teaops.shared.domain.usecase.EvaluateTeaQualityUseCase

/**
 * 監視画面向けのUI状態を構築するファクトリ。
 */
class ProductionMonitorStateFactory(
  private val evaluateTeaQualityUseCase: EvaluateTeaQualityUseCase
) {
  /**
   * ドメイン情報を画面描画用の状態へ変換する。
   */
  fun create(
    currentStep: ProcessingStep,
    currentTemperature: Double,
    elapsedSecondsInStep: Long
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
    val progressLabel = buildProgressLabel(progressPercent, isDelayed)

    return ProductionMonitorUiState(
      currentStep = currentStep,
      remainingSeconds = remaining,
      currentTemperature = currentTemperature,
      qualityScore = quality.score,
      warningMessage = quality.message,
      alertLevel = quality.alertLevel,
      progressPercent = progressPercent,
      progressLabel = progressLabel,
      isDelayed = isDelayed
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
