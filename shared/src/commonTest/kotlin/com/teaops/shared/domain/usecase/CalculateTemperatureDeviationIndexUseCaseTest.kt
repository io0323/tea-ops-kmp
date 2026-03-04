package com.teaops.shared.domain.usecase

import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * CalculateTemperatureDeviationIndexUseCase の算出結果を検証するテスト。
 */
class CalculateTemperatureDeviationIndexUseCaseTest {
  /**
   * 乖離が小さい場合に低い逸脱指数となることを確認する。
   */
  @Test
  fun returnsLowIndexForNearTargetTemperature() {
    val useCase = CalculateTemperatureDeviationIndexUseCase()

    val result = useCase(
      currentTemperature = 180.8,
      targetTemperature = 180.0
    )

    assertEquals(4, result.deviationIndex)
    assertEquals("安定", result.deviationLabel)
  }

  /**
   * 乖離が大きい場合に高い逸脱指数となることを確認する。
   */
  @Test
  fun returnsHighIndexForLargeGapTemperature() {
    val useCase = CalculateTemperatureDeviationIndexUseCase()

    val result = useCase(
      currentTemperature = 196.0,
      targetTemperature = 180.0
    )

    assertEquals(80, result.deviationIndex)
    assertEquals("危険", result.deviationLabel)
  }
}
