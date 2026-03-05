package com.teaops.shared.domain.entity

/**
 * 安定化ガイドの優先度。
 */
enum class StabilizationPriority {
  LOW,
  MEDIUM,
  HIGH
}

/**
 * 状態復帰の判断基準を表す値オブジェクト。
 */
data class StabilizationGuide(
  val title: String,
  val condition: String,
  val priority: StabilizationPriority
)
