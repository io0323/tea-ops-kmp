package com.teaops.shared.domain.usecase

import com.teaops.shared.domain.entity.ChecklistActionLevel
import com.teaops.shared.domain.entity.OperationChecklistItem
import com.teaops.shared.domain.entity.RiskBand
import com.teaops.shared.domain.entity.TemperatureTrend

/**
 * 監視状態から優先アクション一覧を構築するユースケース。
 */
class BuildPriorityChecklistUseCase {
  /**
   * 統合リスクとトレンドをもとに優先項目を最大2件返す。
   */
  operator fun invoke(
    riskBand: RiskBand,
    temperatureTrend: TemperatureTrend,
    isDelayed: Boolean
  ): List<OperationChecklistItem> {
    val items = mutableListOf<OperationChecklistItem>()

    if (riskBand == RiskBand.HIGH) {
      items += OperationChecklistItem(
        title = "温度制御を即時確認",
        detail = "火力・送風・投入量を再点検し、許容範囲へ復帰させる。",
        level = ChecklistActionLevel.CRITICAL
      )
    }

    if (isDelayed) {
      items += OperationChecklistItem(
        title = "工程遅延の要因確認",
        detail = "原料投入量と機器状態を確認し、処理遅延を解消する。",
        level = if (riskBand == RiskBand.HIGH) {
          ChecklistActionLevel.CRITICAL
        } else {
          ChecklistActionLevel.WARNING
        }
      )
    }

    if (temperatureTrend == TemperatureTrend.RISING) {
      items += OperationChecklistItem(
        title = "上昇傾向の緩和",
        detail = "温度上昇が継続中。送風または加熱設定を段階的に調整。",
        level = ChecklistActionLevel.WARNING
      )
    } else if (temperatureTrend == TemperatureTrend.FALLING) {
      items += OperationChecklistItem(
        title = "下降傾向の補正",
        detail = "温度下降が継続中。予熱条件と投入サイクルを再確認。",
        level = ChecklistActionLevel.WARNING
      )
    }

    if (items.isEmpty()) {
      items += OperationChecklistItem(
        title = "現行運転を維持",
        detail = "主要指標は安定。次回チェックまで現行設定を維持。",
        level = ChecklistActionLevel.INFO
      )
    }

    return items.take(2)
  }
}
