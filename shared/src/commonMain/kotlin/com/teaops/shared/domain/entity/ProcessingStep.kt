package com.teaops.shared.domain.entity

/**
 * 茶葉加工の1工程を表すドメインエンティティ。
 */
data class ProcessingStep(
  val id: String,
  val stepName: String,
  val targetTemperature: Double,
  val duration: Long
)
