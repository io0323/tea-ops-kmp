package com.teaops.shared.domain.usecase

import com.teaops.shared.domain.entity.ProcessingStep
import com.teaops.shared.domain.entity.TeaBatch

/**
 * 経過時間に応じて推奨加工ステップを返すユースケース。
 */
class GetRecommendedStepUseCase(
  private val nowProvider: () -> Long
) {
  /**
   * 収穫時刻からの経過時間を使って次工程を判定する。
   */
  operator fun invoke(
    batch: TeaBatch,
    processDefinition: List<ProcessingStep>
  ): ProcessingStep? {
    if (processDefinition.isEmpty()) {
      return null
    }

    val elapsed = (nowProvider() - batch.harvestedAt).coerceAtLeast(0L)
    var cumulativeDuration = 0L

    for (step in processDefinition) {
      cumulativeDuration += step.duration
      if (elapsed <= cumulativeDuration) {
        return step
      }
    }

    return processDefinition.last()
  }
}
