package com.teaops.shared.domain.usecase

import com.teaops.shared.domain.entity.ProcessDefinitionIssue
import com.teaops.shared.domain.entity.ProcessDefinitionIssueType
import com.teaops.shared.domain.entity.ProcessingStep

/**
 * 加工工程定義の整合性を検証するユースケース。
 */
class ValidateProcessDefinitionUseCase {
  /**
   * 工程一覧を評価し、問題があれば一覧で返す。
   */
  operator fun invoke(processDefinition: List<ProcessingStep>): List<ProcessDefinitionIssue> {
    val issues = mutableListOf<ProcessDefinitionIssue>()
    val duplicateIds = processDefinition
      .groupBy { it.id }
      .filterValues { it.size > 1 }
      .keys

    processDefinition.forEach { step ->
      if (step.stepName.isBlank()) {
        issues += ProcessDefinitionIssue(
          stepId = step.id,
          type = ProcessDefinitionIssueType.EMPTY_STEP_NAME,
          message = "工程名が空です"
        )
      }

      if (step.duration <= 0L) {
        issues += ProcessDefinitionIssue(
          stepId = step.id,
          type = ProcessDefinitionIssueType.NON_POSITIVE_DURATION,
          message = "工程時間は1秒以上が必要です"
        )
      }

      if (step.targetTemperature < 0.0 || step.targetTemperature > 350.0) {
        issues += ProcessDefinitionIssue(
          stepId = step.id,
          type = ProcessDefinitionIssueType.INVALID_TEMPERATURE_RANGE,
          message = "目標温度は0〜350°Cの範囲で指定してください"
        )
      }

      if (step.id in duplicateIds) {
        issues += ProcessDefinitionIssue(
          stepId = step.id,
          type = ProcessDefinitionIssueType.DUPLICATE_STEP_ID,
          message = "工程IDが重複しています"
        )
      }
    }

    return issues
  }
}
