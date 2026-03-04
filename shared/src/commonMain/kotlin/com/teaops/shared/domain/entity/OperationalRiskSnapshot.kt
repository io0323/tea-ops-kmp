package com.teaops.shared.domain.entity

/**
 * 統合リスク帯の分類。
 */
enum class RiskBand {
  LOW,
  MEDIUM,
  HIGH
}

/**
 * 複数シグナルを統合した運用リスクの要約。
 */
data class OperationalRiskSnapshot(
  val band: RiskBand,
  val label: String,
  val summary: String
)
