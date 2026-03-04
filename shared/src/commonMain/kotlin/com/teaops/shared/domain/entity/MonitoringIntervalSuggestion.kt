package com.teaops.shared.domain.entity

/**
 * 監視間隔の推奨レベル。
 */
enum class MonitoringCadenceLevel {
  FAST,
  NORMAL,
  RELAXED
}

/**
 * 次回チェック推奨間隔を表す値オブジェクト。
 */
data class MonitoringIntervalSuggestion(
  val seconds: Int,
  val label: String,
  val level: MonitoringCadenceLevel
)
