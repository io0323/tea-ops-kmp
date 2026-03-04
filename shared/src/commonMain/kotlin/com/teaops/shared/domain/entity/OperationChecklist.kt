package com.teaops.shared.domain.entity

/**
 * 優先アクションの分類。
 */
enum class ChecklistActionLevel {
  INFO,
  WARNING,
  CRITICAL
}

/**
 * 現場向けチェックリスト項目。
 */
data class OperationChecklistItem(
  val title: String,
  val detail: String,
  val level: ChecklistActionLevel
)
