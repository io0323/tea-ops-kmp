package com.teaops.shared.domain.entity

/**
 * 監視ダイジェストの表示トーン。
 */
enum class MonitoringDigestTone {
  CALM,
  WATCH,
  ALERT
}

/**
 * 監視状態を1行で要約する値オブジェクト。
 */
data class MonitoringDigest(
  val title: String,
  val detail: String,
  val tone: MonitoringDigestTone
)
