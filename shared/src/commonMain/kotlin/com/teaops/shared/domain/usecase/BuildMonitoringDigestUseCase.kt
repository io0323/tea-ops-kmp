package com.teaops.shared.domain.usecase

import com.teaops.shared.domain.entity.MonitoringCadenceLevel
import com.teaops.shared.domain.entity.MonitoringDigest
import com.teaops.shared.domain.entity.MonitoringDigestTone
import com.teaops.shared.domain.entity.RiskBand
import com.teaops.shared.domain.entity.TemperatureTrend

/**
 * 現在の運用状態を短文で要約するユースケース。
 */
class BuildMonitoringDigestUseCase {
  /**
   * 統合リスク・監視間隔・温度推移からダイジェストを返す。
   */
  operator fun invoke(
    riskBand: RiskBand,
    nextCheckLevel: MonitoringCadenceLevel,
    temperatureTrend: TemperatureTrend,
    isDelayed: Boolean
  ): MonitoringDigest {
    if (riskBand == RiskBand.HIGH || nextCheckLevel == MonitoringCadenceLevel.FAST) {
      return MonitoringDigest(
        title = "即応モード",
        detail = "高リスク状態。温度${trendText(temperatureTrend)} / 遅延=${flagText(isDelayed)}",
        tone = MonitoringDigestTone.ALERT
      )
    }

    if (riskBand == RiskBand.MEDIUM || nextCheckLevel == MonitoringCadenceLevel.NORMAL) {
      return MonitoringDigest(
        title = "監視強化モード",
        detail = "状態変動あり。温度${trendText(temperatureTrend)} / 遅延=${flagText(isDelayed)}を継続監視。",
        tone = MonitoringDigestTone.WATCH
      )
    }

    return MonitoringDigest(
      title = "安定モード",
      detail = "主要指標は安定。通常監視を継続。",
      tone = MonitoringDigestTone.CALM
    )
  }

  /**
   * 温度トレンドを文言に変換する。
   */
  private fun trendText(trend: TemperatureTrend): String {
    return when (trend) {
      TemperatureTrend.RISING -> "上昇"
      TemperatureTrend.FALLING -> "下降"
      TemperatureTrend.STABLE -> "安定"
    }
  }

  /**
   * 真偽値を運用表示向けに変換する。
   */
  private fun flagText(flag: Boolean): String {
    return if (flag) "あり" else "なし"
  }
}
