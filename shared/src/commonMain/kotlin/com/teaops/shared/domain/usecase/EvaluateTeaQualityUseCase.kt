package com.teaops.shared.domain.usecase

import com.teaops.shared.domain.entity.AlertLevel
import com.teaops.shared.domain.util.roundToOneDecimal
import com.teaops.shared.domain.entity.ProcessingStep
import com.teaops.shared.domain.entity.QualityAssessment
import kotlin.math.round

/**
 * 温度と進捗から品質スコアを算出するユースケース。
 */
class EvaluateTeaQualityUseCase {
  /**
   * 現在温度と経過時間を評価して品質状態を返す。
   */
  operator fun invoke(
    step: ProcessingStep,
    currentTemperature: Double,
    elapsedSecondsInStep: Long
  ): QualityAssessment {
    val boundedElapsed = elapsedSecondsInStep.coerceAtLeast(0L)
    val duration = step.duration.coerceAtLeast(1L)
    val progressRatio = boundedElapsed.toDouble() / duration.toDouble()
    val tempGap = kotlin.math.abs(currentTemperature - step.targetTemperature)

    val tempPenalty = (tempGap * TEMP_GAP_PENALTY_MULTIPLIER).toInt().coerceAtMost(MAX_TEMP_PENALTY)
    val progressPenalty = when {
      progressRatio < PROGRESS_RATIO_LOW -> PROGRESS_PENALTY_LOW
      progressRatio > PROGRESS_RATIO_HIGH -> PROGRESS_PENALTY_HIGH
      progressRatio > PROGRESS_RATIO_MEDIUM -> PROGRESS_PENALTY_MEDIUM
      else -> 0
    }

    val rawScore = MAX_SCORE - tempPenalty - progressPenalty
    val score = rawScore.coerceIn(0, MAX_SCORE)
    val alertLevel = when {
      score < CRITICAL_SCORE_THRESHOLD -> AlertLevel.CRITICAL
      score < CAUTION_SCORE_THRESHOLD -> AlertLevel.CAUTION
      else -> AlertLevel.NORMAL
    }

    return QualityAssessment(
      score = score,
      alertLevel = alertLevel,
      message = buildMessage(alertLevel, step.stepName, tempGap)
    )
  }

  /**
   * 警告レベルに応じた運用メッセージを組み立てる。
   */
  private fun buildMessage(
    level: AlertLevel,
    stepName: String,
    tempGap: Double
  ): String {
    val roundedGap = tempGap.roundToOneDecimal()
    val gapText = roundedGap.toString()
    return when (level) {
      AlertLevel.NORMAL -> "$stepName は安定中（温度差 ${gapText}°C）"
      AlertLevel.CAUTION -> "$stepName の温度調整推奨: 目標との差 ${gapText}°C"
      AlertLevel.CRITICAL -> "要対応: $stepName の品質低下リスク高"
    }
  }

  companion object {
    private const val TEMP_GAP_PENALTY_MULTIPLIER = 2.2
    private const val MAX_TEMP_PENALTY = 45
    private const val PROGRESS_RATIO_LOW = 0.15
    private const val PROGRESS_RATIO_MEDIUM = 1.0
    private const val PROGRESS_RATIO_HIGH = 1.25
    private const val PROGRESS_PENALTY_LOW = 8
    private const val PROGRESS_PENALTY_MEDIUM = 10
    private const val PROGRESS_PENALTY_HIGH = 20
    private const val MAX_SCORE = 100
    private const val CRITICAL_SCORE_THRESHOLD = 55
    private const val CAUTION_SCORE_THRESHOLD = 75
  }
}
