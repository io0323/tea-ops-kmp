package com.teaops.shared.domain.entity

/**
 * 工程定義の問題種別を表す列挙型。
 */
enum class ProcessDefinitionIssueType {
  EMPTY_STEP_NAME,
  NON_POSITIVE_DURATION,
  INVALID_TEMPERATURE_RANGE,
  DUPLICATE_STEP_ID
}

/**
 * 工程定義の問題内容を表す値オブジェクト。
 */
data class ProcessDefinitionIssue(
  val stepId: String,
  val type: ProcessDefinitionIssueType,
  val message: String
)
