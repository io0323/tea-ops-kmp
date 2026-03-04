package com.teaops.shared.domain.entity

/**
 * 運用アラート優先度を表す列挙型。
 */
enum class OperationAlertPriority {
  LOW,
  MEDIUM,
  HIGH
}

/**
 * 監視画面向けの運用アラート要約。
 */
data class OperationAlertSummary(
  val priority: OperationAlertPriority,
  val title: String,
  val detail: String
)
