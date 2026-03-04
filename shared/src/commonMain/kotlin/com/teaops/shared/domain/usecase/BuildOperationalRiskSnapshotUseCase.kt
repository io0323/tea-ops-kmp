package com.teaops.shared.domain.usecase

import com.teaops.shared.domain.entity.OperationalRiskSnapshot
import com.teaops.shared.domain.entity.OperationAlertPriority
import com.teaops.shared.domain.entity.RiskBand

/**
 * 品質・逸脱・運用優先度から統合リスクを生成するユースケース。
 */
class BuildOperationalRiskSnapshotUseCase {
  /**
   * 0〜100の品質値と逸脱指数を統合し、運用向けリスク帯を返す。
   */
  operator fun invoke(
    qualityScore: Int,
    deviationIndex: Int,
    operationPriority: OperationAlertPriority
  ): OperationalRiskSnapshot {
    val safeQuality = qualityScore.coerceIn(0, 100)
    val safeDeviation = deviationIndex.coerceIn(0, 100)
    val weightedRisk = ((100 - safeQuality) * 0.6 + safeDeviation * 0.4).toInt()

    if (operationPriority == OperationAlertPriority.HIGH || weightedRisk >= 65) {
      return OperationalRiskSnapshot(
        band = RiskBand.HIGH,
        label = "高リスク",
        summary = "品質/温度の複合リスクが高い状態。即時の工程調整を推奨。"
      )
    }

    if (operationPriority == OperationAlertPriority.MEDIUM || weightedRisk >= 35) {
      return OperationalRiskSnapshot(
        band = RiskBand.MEDIUM,
        label = "中リスク",
        summary = "監視を継続し、温度と進捗の再確認を推奨。"
      )
    }

    return OperationalRiskSnapshot(
      band = RiskBand.LOW,
      label = "低リスク",
      summary = "状態は安定。現行条件での運転継続が可能。"
    )
  }
}
