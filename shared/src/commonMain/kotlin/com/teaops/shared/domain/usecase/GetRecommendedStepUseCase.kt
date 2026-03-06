package com.teaops.shared.domain.usecase

import com.teaops.shared.domain.entity.ProcessingStep
import com.teaops.shared.domain.entity.TeaBatch

/**
 * 経過時間に応じて推奨加工ステップを返すユースケース。
 */
class GetRecommendedStepUseCase(
  private val nowProvider: () -> Long,
  private val validateProcessDefinitionUseCase: ValidateProcessDefinitionUseCase
) {
  /**
   * 収穫時刻からの経過時間を使って次工程を判定する。
   */
  operator fun invoke(
    batch: TeaBatch,
    processDefinition: List<ProcessingStep>
  ): ProcessingStep? {
    return evaluate(batch, processDefinition).recommendedStep
  }

  /**
   * 推奨工程と定義の検証結果をまとめて返す。
   *
   * - 収穫時刻より現在時刻が過去の場合でも、経過秒数は0として扱う。
   * - 無効な工程（名称空・非正の時間・温度異常）は内部で除外してから判定する。
   */
  fun evaluate(
    batch: TeaBatch,
    processDefinition: List<ProcessingStep>
  ): RecommendedStepDecision {
    val elapsed = (nowProvider() - batch.harvestedAt).coerceAtLeast(0L)
    val issues = validateProcessDefinitionUseCase(processDefinition)
    val validSteps = processDefinition.filter { step ->
      step.duration > 0L &&
        step.stepName.isNotBlank() &&
        step.targetTemperature in 0.0..350.0
    }

    if (validSteps.isEmpty()) {
      return RecommendedStepDecision(
        recommendedStep = null,
        elapsedSecondsFromHarvest = elapsed,
        validationIssues = issues
      )
    }

    var cumulativeDuration = 0L
    for (step in validSteps) {
      cumulativeDuration += step.duration
      if (elapsed <= cumulativeDuration) {
        return RecommendedStepDecision(
          recommendedStep = step,
          elapsedSecondsFromHarvest = elapsed,
          validationIssues = issues
        )
      }
    }

    return RecommendedStepDecision(
      recommendedStep = validSteps.last(),
      elapsedSecondsFromHarvest = elapsed,
      validationIssues = issues
    )
  }
}
