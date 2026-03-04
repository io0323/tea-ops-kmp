package com.teaops.shared.domain.entity

/**
 * 品質評価の警告レベルを表す列挙型。
 */
enum class AlertLevel {
  NORMAL,
  CAUTION,
  CRITICAL
}

/**
 * 品質評価結果を表す値オブジェクト。
 */
data class QualityAssessment(
  val score: Int,
  val alertLevel: AlertLevel,
  val message: String
)
