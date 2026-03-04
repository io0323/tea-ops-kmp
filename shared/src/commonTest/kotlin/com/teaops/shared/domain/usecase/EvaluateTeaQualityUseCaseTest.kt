package com.teaops.shared.domain.usecase

import com.teaops.shared.domain.entity.AlertLevel
import com.teaops.shared.domain.entity.ProcessingStep
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * EvaluateTeaQualityUseCase の振る舞いを検証するテスト。
 */
class EvaluateTeaQualityUseCaseTest {
  /**
   * 目標温度付近かつ適正時間帯なら正常判定になることを確認する。
   */
  @Test
  fun returnsNormalWhenTemperatureAndTimeAreInRange() {
    val useCase = EvaluateTeaQualityUseCase()
    val step = ProcessingStep(
      id = "1",
      stepName = "殺青",
      targetTemperature = 180.0,
      duration = 120
    )

    val result = useCase(
      step = step,
      currentTemperature = 181.0,
      elapsedSecondsInStep = 60
    )

    assertEquals(AlertLevel.NORMAL, result.alertLevel)
    assertTrue(result.score in 75..100)
  }

  /**
   * 温度差と遅延が大きい場合に重大警告になることを確認する。
   */
  @Test
  fun returnsCriticalWhenTemperatureGapAndDelayAreLarge() {
    val useCase = EvaluateTeaQualityUseCase()
    val step = ProcessingStep(
      id = "2",
      stepName = "揉捻",
      targetTemperature = 90.0,
      duration = 90
    )

    val result = useCase(
      step = step,
      currentTemperature = 112.0,
      elapsedSecondsInStep = 140
    )

    assertEquals(AlertLevel.CRITICAL, result.alertLevel)
    assertTrue(result.score < 55)
  }
}
