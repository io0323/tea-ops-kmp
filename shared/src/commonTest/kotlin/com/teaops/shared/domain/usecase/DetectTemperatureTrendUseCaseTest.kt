package com.teaops.shared.domain.usecase

import com.teaops.shared.domain.entity.TemperatureTrend
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * DetectTemperatureTrendUseCase の判定結果を検証するテスト。
 */
class DetectTemperatureTrendUseCaseTest {
  /**
   * 上昇差分が閾値を超える場合に上昇判定となることを確認する。
   */
  @Test
  fun returnsRisingWhenDeltaIsPositiveAboveThreshold() {
    val useCase = DetectTemperatureTrendUseCase()

    val result = useCase(
      previousTemperature = 100.0,
      currentTemperature = 102.5
    )

    assertEquals(TemperatureTrend.RISING, result)
  }

  /**
   * 下降差分が閾値を超える場合に下降判定となることを確認する。
   */
  @Test
  fun returnsFallingWhenDeltaIsNegativeAboveThreshold() {
    val useCase = DetectTemperatureTrendUseCase()

    val result = useCase(
      previousTemperature = 100.0,
      currentTemperature = 98.0
    )

    assertEquals(TemperatureTrend.FALLING, result)
  }

  /**
   * 差分が閾値以内なら安定判定となることを確認する。
   */
  @Test
  fun returnsStableWhenDeltaIsWithinThreshold() {
    val useCase = DetectTemperatureTrendUseCase()

    val result = useCase(
      previousTemperature = 100.0,
      currentTemperature = 100.7
    )

    assertEquals(TemperatureTrend.STABLE, result)
  }
}
