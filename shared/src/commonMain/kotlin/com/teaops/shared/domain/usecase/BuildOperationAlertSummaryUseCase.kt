package com.teaops.shared.domain.usecase

import com.teaops.shared.domain.entity.AlertLevel
import com.teaops.shared.domain.entity.OperationAlertPriority
import com.teaops.shared.domain.entity.OperationAlertSummary

/**
 * 運用判断に使うアラート要約を組み立てるユースケース。
 */
class BuildOperationAlertSummaryUseCase {
  /**
   * 品質警告と遅延情報から優先対応内容を生成する。
   */
  operator fun invoke(
    alertLevel: AlertLevel,
    qualityScore: Int,
    isDelayed: Boolean,
    delayLabel: String
  ): OperationAlertSummary {
    val safeScore = qualityScore.coerceIn(0, 100)

    if (alertLevel == AlertLevel.CRITICAL || (isDelayed && safeScore < 70)) {
      return OperationAlertSummary(
        priority = OperationAlertPriority.HIGH,
        title = "最優先対応",
        detail = "品質スコア ${safeScore} / $delayLabel / 工程条件を即時調整"
      )
    }

    if (alertLevel == AlertLevel.CAUTION || isDelayed) {
      return OperationAlertSummary(
        priority = OperationAlertPriority.MEDIUM,
        title = "要監視",
        detail = "品質スコア ${safeScore} / $delayLabel / 温度推移を継続確認"
      )
    }

    return OperationAlertSummary(
      priority = OperationAlertPriority.LOW,
      title = "安定運転",
      detail = "品質スコア ${safeScore} / $delayLabel / 現行設定を維持"
    )
  }
}
