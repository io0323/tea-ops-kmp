package com.teaops.shared.presenter.production

import com.teaops.shared.domain.entity.AlertLevel
import com.teaops.shared.domain.entity.ProcessingStep
import com.teaops.shared.domain.usecase.EvaluateTeaQualityUseCase
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * ProductionMonitorStateFactory の状態変換を検証するテスト。
 */
class ProductionMonitorStateFactoryTest {
  /**
   * 通常進行中の進捗情報が正しく構築されることを確認する。
   */
  @Test
  fun createBuildsInProgressState() {
    val factory = ProductionMonitorStateFactory(EvaluateTeaQualityUseCase())
    val step = ProcessingStep(
      id = "seisei",
      stepName = "殺青",
      targetTemperature = 180.0,
      duration = 200L
    )

    val state = factory.create(
      currentStep = step,
      currentTemperature = 181.0,
      elapsedSecondsInStep = 80L
    )

    assertEquals(40, state.progressPercent)
    assertEquals("進行中", state.progressLabel)
    assertFalse(state.isDelayed)
    assertEquals(AlertLevel.NORMAL, state.alertLevel)
  }

  /**
   * 工程時間超過時に遅延状態として構築されることを確認する。
   */
  @Test
  fun createBuildsDelayedStateWhenElapsedExceedsDuration() {
    val factory = ProductionMonitorStateFactory(EvaluateTeaQualityUseCase())
    val step = ProcessingStep(
      id = "junen",
      stepName = "揉捻",
      targetTemperature = 95.0,
      duration = 100L
    )

    val state = factory.create(
      currentStep = step,
      currentTemperature = 120.0,
      elapsedSecondsInStep = 140L
    )

    assertEquals(100, state.progressPercent)
    assertEquals("遅延", state.progressLabel)
    assertTrue(state.isDelayed)
    assertTrue(state.qualityScore < 75)
  }
}
