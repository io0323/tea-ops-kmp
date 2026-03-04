package com.teaops.shared.domain.usecase

import com.teaops.shared.domain.entity.TemperatureActionLevel
import com.teaops.shared.domain.entity.TemperatureActionSuggestion
import com.teaops.shared.domain.entity.TemperatureTrend

/**
 * 温度トレンドと目標差分から操作提案を生成するユースケース。
 */
class BuildTemperatureActionSuggestionUseCase {
  /**
   * 現在温度・目標温度・トレンドを使って推奨操作を返す。
   */
  operator fun invoke(
    currentTemperature: Double,
    targetTemperature: Double,
    temperatureTrend: TemperatureTrend
  ): TemperatureActionSuggestion {
    val gap = currentTemperature - targetTemperature
    val absGap = kotlin.math.abs(gap)

    if (absGap >= 8.0) {
      return if (gap > 0) {
        TemperatureActionSuggestion(
          level = TemperatureActionLevel.URGENT,
          title = "加熱抑制が必要",
          detail = "目標より ${formatGap(absGap)}°C高温。投入熱量を即時低減。"
        )
      } else {
        TemperatureActionSuggestion(
          level = TemperatureActionLevel.URGENT,
          title = "加熱強化が必要",
          detail = "目標より ${formatGap(absGap)}°C低温。加熱を即時補正。"
        )
      }
    }

    if (absGap >= 3.0 || temperatureTrend != TemperatureTrend.STABLE) {
      return when {
        gap > 0 && temperatureTrend == TemperatureTrend.RISING ->
          TemperatureActionSuggestion(
            level = TemperatureActionLevel.ADJUST,
            title = "上振れ傾向",
            detail = "温度上昇中。送風または火力を段階的に調整。"
          )
        gap < 0 && temperatureTrend == TemperatureTrend.FALLING ->
          TemperatureActionSuggestion(
            level = TemperatureActionLevel.ADJUST,
            title = "下振れ傾向",
            detail = "温度下降中。予熱と投入量を再確認。"
          )
        else ->
          TemperatureActionSuggestion(
            level = TemperatureActionLevel.ADJUST,
            title = "微調整推奨",
            detail = "目標差 ${formatGap(absGap)}°C。現在設定の微調整を推奨。"
          )
      }
    }

    return TemperatureActionSuggestion(
      level = TemperatureActionLevel.KEEP,
      title = "現状維持",
      detail = "目標付近で安定。現在の操作条件を維持。"
    )
  }

  /**
   * 温度差を表示用に小数1桁へ丸める。
   */
  private fun formatGap(value: Double): String {
    val rounded = (value * 10.0).toInt() / 10.0
    return rounded.toString()
  }
}
