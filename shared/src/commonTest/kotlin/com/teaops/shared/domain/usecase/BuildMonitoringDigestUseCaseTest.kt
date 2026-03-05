package com.teaops.shared.domain.usecase

import com.teaops.shared.domain.entity.MonitoringCadenceLevel
import com.teaops.shared.domain.entity.MonitoringDigestTone
import com.teaops.shared.domain.entity.RiskBand
import com.teaops.shared.domain.entity.TemperatureTrend
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * BuildMonitoringDigestUseCase の要約生成を検証するテスト。
 */
class BuildMonitoringDigestUseCaseTest {
  /**
   * 高リスク状態でアラートトーンになることを確認する。
   */
  @Test
  fun returnsAlertDigestForHighRisk() {
    val useCase = BuildMonitoringDigestUseCase()

    val result = useCase(
      riskBand = RiskBand.HIGH,
      nextCheckLevel = MonitoringCadenceLevel.FAST,
      temperatureTrend = TemperatureTrend.RISING,
      isDelayed = true
    )

    assertEquals("即応モード", result.title)
    assertEquals(MonitoringDigestTone.ALERT, result.tone)
    assertTrue(result.detail.contains("上昇"))
  }

  /**
   * 中リスク状態で監視強化モードとして遅延フラグを含むことを確認する。
   */
  @Test
  fun returnsWatchDigestForMediumRiskWithDelayFlag() {
    val useCase = BuildMonitoringDigestUseCase()

    val result = useCase(
      riskBand = RiskBand.MEDIUM,
      nextCheckLevel = MonitoringCadenceLevel.NORMAL,
      temperatureTrend = TemperatureTrend.FALLING,
      isDelayed = true
    )

    assertEquals("監視強化モード", result.title)
    assertEquals(MonitoringDigestTone.WATCH, result.tone)
    assertTrue(result.detail.contains("下降"))
    assertTrue(result.detail.contains("遅延=あり"))
  }

  /**
   * 安定状態で穏やかなトーンになることを確認する。
   */
  @Test
  fun returnsCalmDigestForStableState() {
    val useCase = BuildMonitoringDigestUseCase()

    val result = useCase(
      riskBand = RiskBand.LOW,
      nextCheckLevel = MonitoringCadenceLevel.RELAXED,
      temperatureTrend = TemperatureTrend.STABLE,
      isDelayed = false
    )

    assertEquals("安定モード", result.title)
    assertEquals(MonitoringDigestTone.CALM, result.tone)
  }
}
