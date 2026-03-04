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

    return ProductionMonitorUiState(
      currentStep = currentStep,
      remainingSeconds = remaining,
      currentTemperature = currentTemperature,
      qualityScore = quality.score,
      warningMessage = quality.message,
      alertLevel = quality.alertLevel
    )
  }
}
