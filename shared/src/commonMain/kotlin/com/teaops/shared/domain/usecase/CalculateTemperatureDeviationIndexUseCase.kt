package com.teaops.shared.domain.usecase

import com.teaops.shared.domain.entity.TemperatureDeviationAssessment

/**
 * 目標温度との乖離から温度逸脱指数を算出するユースケース。
 */
class CalculateTemperatureDeviationIndexUseCase {
  /**
   * 0〜100の逸脱指数と評価ラベルを返す。
   */
  operator fun invoke(
    currentTemperature: Double,
    targetTemperature: Double
  ): TemperatureDeviationAssessment {
    val gap = kotlin.math.abs(currentTemperature - targetTemperature)
    val index = ((gap / 20.0) * 100.0).toInt().coerceIn(0, 100)
    val label = when {
      index >= 60 -> "危険"
      index >= 30 -> "注意"
      else -> "安定"
    }

    return TemperatureDeviationAssessment(
      deviationIndex = index,
      deviationLabel = label
    )
  }
}
