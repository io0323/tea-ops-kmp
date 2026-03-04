package com.teaops.shared.domain.usecase

import com.teaops.shared.domain.entity.ChecklistActionLevel
import com.teaops.shared.domain.entity.RiskBand
import com.teaops.shared.domain.entity.TemperatureTrend
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * BuildPriorityChecklistUseCase の項目生成を検証するテスト。
 */
class BuildPriorityChecklistUseCaseTest {
  /**
   * 高リスクかつ遅延時に重要項目が先頭で返ることを確認する。
   */
  @Test
  fun returnsCriticalItemsForHighRiskAndDelay() {
    val useCase = BuildPriorityChecklistUseCase()

    val result = useCase(
      riskBand = RiskBand.HIGH,
      temperatureTrend = TemperatureTrend.RISING,
      isDelayed = true
    )

    assertEquals(2, result.size)
    assertEquals(ChecklistActionLevel.CRITICAL, result[0].level)
    assertTrue(result[0].title.contains("温度制御"))
  }

  /**
   * 安定時に維持項目が返ることを確認する。
   */
  @Test
  fun returnsInfoItemForStableState() {
    val useCase = BuildPriorityChecklistUseCase()

    val result = useCase(
      riskBand = RiskBand.LOW,
      temperatureTrend = TemperatureTrend.STABLE,
      isDelayed = false
    )

    assertEquals(1, result.size)
    assertEquals(ChecklistActionLevel.INFO, result[0].level)
    assertTrue(result[0].title.contains("維持"))
  }
}
