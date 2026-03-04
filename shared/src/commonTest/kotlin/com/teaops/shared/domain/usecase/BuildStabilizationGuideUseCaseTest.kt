package com.teaops.shared.domain.usecase

import com.teaops.shared.domain.entity.MonitoringCadenceLevel
import com.teaops.shared.domain.entity.RiskBand
import com.teaops.shared.domain.entity.StabilizationPriority
import com.teaops.shared.domain.entity.TemperatureTrend
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * BuildStabilizationGuideUseCase の復帰条件生成を検証するテスト。
 */
class BuildStabilizationGuideUseCaseTest {
  /**
   * 高リスク時に厳格な復帰条件が返ることを確認する。
   */
  @Test
  fun returnsHighPriorityGuideForHighRisk() {
    val useCase = BuildStabilizationGuideUseCase()

    val result = useCase(
      riskBand = RiskBand.HIGH,
      temperatureTrend = TemperatureTrend.RISING,
      nextCheckLevel = MonitoringCadenceLevel.FAST
    )

    assertEquals(StabilizationPriority.HIGH, result.priority)
    assertTrue(result.title.contains("厳格"))
  }

  /**
   * 安定時に通常ガイドが返ることを確認する。
   */
  @Test
  fun returnsLowPriorityGuideForStableState() {
    val useCase = BuildStabilizationGuideUseCase()

    val result = useCase(
      riskBand = RiskBand.LOW,
      temperatureTrend = TemperatureTrend.STABLE,
      nextCheckLevel = MonitoringCadenceLevel.RELAXED
    )

    assertEquals(StabilizationPriority.LOW, result.priority)
    assertTrue(result.title.contains("通常"))
  }
}
