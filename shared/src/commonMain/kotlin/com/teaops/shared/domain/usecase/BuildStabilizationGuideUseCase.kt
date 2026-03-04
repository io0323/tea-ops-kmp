package com.teaops.shared.domain.usecase

import com.teaops.shared.domain.entity.MonitoringCadenceLevel
import com.teaops.shared.domain.entity.RiskBand
import com.teaops.shared.domain.entity.StabilizationGuide
import com.teaops.shared.domain.entity.StabilizationPriority
import com.teaops.shared.domain.entity.TemperatureTrend

/**
 * 現場の安定復帰判断に使うガイドを生成するユースケース。
 */
class BuildStabilizationGuideUseCase {
  /**
   * リスク帯と温度傾向から安定化条件を返す。
   */
  operator fun invoke(
    riskBand: RiskBand,
    temperatureTrend: TemperatureTrend,
    nextCheckLevel: MonitoringCadenceLevel
  ): StabilizationGuide {
    if (riskBand == RiskBand.HIGH || nextCheckLevel == MonitoringCadenceLevel.FAST) {
      return StabilizationGuide(
        title = "復帰条件: 厳格",
        condition = "温度トレンドが安定に遷移し、逸脱指数が30未満を2回連続で維持。",
        priority = StabilizationPriority.HIGH
      )
    }

    if (riskBand == RiskBand.MEDIUM || temperatureTrend != TemperatureTrend.STABLE) {
      return StabilizationGuide(
        title = "復帰条件: 監視継続",
        condition = "温度トレンドが安定になり、次回チェックまで悪化しないこと。",
        priority = StabilizationPriority.MEDIUM
      )
    }

    return StabilizationGuide(
      title = "復帰条件: 通常",
      condition = "主要指標が許容範囲内のため通常運転を継続。",
      priority = StabilizationPriority.LOW
    )
  }
}
