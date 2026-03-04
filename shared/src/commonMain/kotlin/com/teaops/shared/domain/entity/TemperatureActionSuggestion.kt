package com.teaops.shared.domain.entity

/**
 * 温度制御アクションの重要度を表す列挙型。
 */
enum class TemperatureActionLevel {
  KEEP,
  ADJUST,
  URGENT
}

/**
 * 温度トレンドに応じた操作提案を表す値オブジェクト。
 */
data class TemperatureActionSuggestion(
  val level: TemperatureActionLevel,
  val title: String,
  val detail: String
)
