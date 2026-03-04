package com.teaops.shared.domain.usecase

import com.teaops.shared.domain.entity.ProcessDefinitionIssue
import com.teaops.shared.domain.entity.ProcessingStep

/**
 * 推奨工程判定の結果を保持する値オブジェクト。
 */
data class RecommendedStepDecision(
  val recommendedStep: ProcessingStep?,
  val elapsedSecondsFromHarvest: Long,
  val validationIssues: List<ProcessDefinitionIssue>
)
