package com.teaops.shared.domain.entity

/**
 * 温度逸脱の評価結果を表す値オブジェクト。
 */
data class TemperatureDeviationAssessment(
  val deviationIndex: Int,
  val deviationLabel: String
)
