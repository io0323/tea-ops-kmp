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

  /**
   * 中程度の乖離では「注意」ラベルが返ることを確認する。
   */
  @Test
  fun returnsMediumIndexAndCautionLabelForModerateGap() {
    val useCase = CalculateTemperatureDeviationIndexUseCase()

    val result = useCase(
      currentTemperature = 190.0,
      targetTemperature = 180.0
    )

    assertEquals(50, result.deviationIndex)
    assertEquals("注意", result.deviationLabel)
  }

  /**
   * 目標より低温側の乖離でも絶対値ベースで同じ指数になることを確認する。
   */
  @Test
  fun returnsSameIndexForSymmetricBelowTargetGap() {
    val useCase = CalculateTemperatureDeviationIndexUseCase()

    val result = useCase(
      currentTemperature = 170.0,
      targetTemperature = 180.0
    )

    assertEquals(50, result.deviationIndex)
    assertEquals("注意", result.deviationLabel)
  }

  /**
   * 非現実的な大きな乖離でも指数が100にクリップされることを確認する。
   */
  @Test
  fun clipsIndexToHundredForExtremeGap() {
    val useCase = CalculateTemperatureDeviationIndexUseCase()

    val result = useCase(
      currentTemperature = 280.0,
      targetTemperature = 180.0
    )

    assertEquals(100, result.deviationIndex)
    assertEquals("危険", result.deviationLabel)
  }
}
