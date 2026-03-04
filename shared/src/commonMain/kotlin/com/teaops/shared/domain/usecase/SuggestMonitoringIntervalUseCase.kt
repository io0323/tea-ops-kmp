package com.teaops.shared.domain.usecase

import com.teaops.shared.domain.entity.MonitoringCadenceLevel
import com.teaops.shared.domain.entity.MonitoringIntervalSuggestion
import com.teaops.shared.domain.entity.OperationAlertPriority
import com.teaops.shared.domain.entity.TemperatureActionLevel

/**
 * 運用状態から次回チェック間隔を提案するユースケース。
 */
class SuggestMonitoringIntervalUseCase {
  /**
   * 優先度と温度操作レベルから推奨監視間隔を返す。
   */
  operator fun invoke(
    operationPriority: OperationAlertPriority,
    actionLevel: TemperatureActionLevel,
    deviationIndex: Int
  ): MonitoringIntervalSuggestion {
    val safeIndex = deviationIndex.coerceIn(0, 100)

    if (operationPriority == OperationAlertPriority.HIGH ||
      actionLevel == TemperatureActionLevel.URGENT ||
      safeIndex >= 70
    ) {
      return MonitoringIntervalSuggestion(
        seconds = 15,
        label = "即時監視",
        level = MonitoringCadenceLevel.FAST
      )
    }

    if (operationPriority == OperationAlertPriority.MEDIUM ||
      actionLevel == TemperatureActionLevel.ADJUST ||
      safeIndex >= 35
    ) {
      return MonitoringIntervalSuggestion(
        seconds = 30,
        label = "短周期監視",
        level = MonitoringCadenceLevel.NORMAL
      )
    }

    return MonitoringIntervalSuggestion(
      seconds = 60,
      label = "通常監視",
      level = MonitoringCadenceLevel.RELAXED
    )
  }
}
