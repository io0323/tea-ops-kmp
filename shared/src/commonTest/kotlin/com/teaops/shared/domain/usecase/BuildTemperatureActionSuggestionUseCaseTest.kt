package com.teaops.shared.domain.usecase

import com.teaops.shared.domain.entity.TemperatureActionLevel
import com.teaops.shared.domain.entity.TemperatureTrend
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * BuildTemperatureActionSuggestionUseCase の提案生成を検証するテスト。
 */
class BuildTemperatureActionSuggestionUseCaseTest {
  /**
   * 大きな上振れ時に緊急レベル提案となることを確認する。
   */
  @Test
  fun returnsUrgentWhenTemperatureIsFarAboveTarget() {
    val useCase = BuildTemperatureActionSuggestionUseCase()

    val result = useCase(
      currentTemperature = 190.0,
      targetTemperature = 180.0,
      temperatureTrend = TemperatureTrend.RISING
    )

    assertEquals(TemperatureActionLevel.URGENT, result.level)
    assertEquals("加熱抑制が必要", result.title)
  }

  /**
   * 差分が小さく安定時は維持提案になることを確認する。
   */
  @Test
  fun returnsKeepWhenTemperatureIsStableAroundTarget() {
    val useCase = BuildTemperatureActionSuggestionUseCase()

    val result = useCase(
      currentTemperature = 180.8,
      targetTemperature = 180.0,
      temperatureTrend = TemperatureTrend.STABLE
    )

    assertEquals(TemperatureActionLevel.KEEP, result.level)
    assertEquals("現状維持", result.title)
    assertTrue(result.detail.contains("維持"))
  }
}
