package com.teaops.shared.domain.usecase

import com.teaops.shared.domain.entity.AlertLevel
import com.teaops.shared.domain.entity.ProcessingStep
import com.teaops.shared.domain.entity.QualityAssessment

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

    val tempPenalty = (tempGap * 2.2).toInt().coerceAtMost(45)
    val progressPenalty = when {
      progressRatio < 0.15 -> 8
      progressRatio > 1.25 -> 20
      progressRatio > 1.0 -> 10
      else -> 0
    }

    val rawScore = 100 - tempPenalty - progressPenalty
    val score = rawScore.coerceIn(0, 100)
    val alertLevel = when {
      score < 55 -> AlertLevel.CRITICAL
      score < 75 -> AlertLevel.CAUTION
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
    val roundedGap = (tempGap * 10.0).toInt() / 10.0
    val gapText = roundedGap.toString()
    return when (level) {
      AlertLevel.NORMAL -> "$stepName は安定中（温度差 ${gapText}°C）"
      AlertLevel.CAUTION -> "$stepName の温度調整推奨: 目標との差 ${gapText}°C"
      AlertLevel.CRITICAL -> "要対応: $stepName の品質低下リスク高"
    }
  }
}
