package com.teaops.shared.domain.usecase

import com.teaops.shared.domain.entity.TemperatureTrend

/**
 * 前回温度との差分から温度トレンドを判定するユースケース。
 */
class DetectTemperatureTrendUseCase {
  /**
   * 差分閾値に基づいて上昇・下降・安定を返す。
   */
  operator fun invoke(
    previousTemperature: Double,
    currentTemperature: Double,
    threshold: Double = 1.0
  ): TemperatureTrend {
    val delta = currentTemperature - previousTemperature
    if (delta > threshold) {
      return TemperatureTrend.RISING
    }
    if (delta < -threshold) {
      return TemperatureTrend.FALLING
    }
    return TemperatureTrend.STABLE
  }
}
