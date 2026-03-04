package com.teaops.shared.domain.usecase

import com.teaops.shared.domain.entity.ProcessDefinitionIssueType
import com.teaops.shared.domain.entity.ProcessingStep
import com.teaops.shared.domain.entity.TeaBatch
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * GetRecommendedStepUseCase の判定結果を検証するテスト。
 */
class GetRecommendedStepUseCaseTest {
  /**
   * 有効工程から経過時間に応じた推奨工程を返すことを確認する。
   */
  @Test
  fun returnsRecommendedStepFromElapsedTime() {
    val batch = TeaBatch(
      id = "batch-1",
      type = "sencha",
      weight = 12.0,
      harvestedAt = 1000L
    )
    val useCase = GetRecommendedStepUseCase(
      nowProvider = { 1240L },
      validateProcessDefinitionUseCase = ValidateProcessDefinitionUseCase()
    )
    val processDefinition = listOf(
      ProcessingStep("s1", "殺青", 180.0, 120L),
      ProcessingStep("s2", "揉捻", 95.0, 180L)
    )

    val decision = useCase.evaluate(batch, processDefinition)

    assertNotNull(decision.recommendedStep)
    assertEquals("s2", decision.recommendedStep.id)
    assertEquals(240L, decision.elapsedSecondsFromHarvest)
    assertTrue(decision.validationIssues.isEmpty())
  }

  /**
   * 無効工程しかない場合は推奨工程がnullになることを確認する。
   */
  @Test
  fun returnsNullWhenAllStepsAreInvalid() {
    val batch = TeaBatch(
      id = "batch-2",
      type = "gyokuro",
      weight = 10.0,
      harvestedAt = 0L
    )
    val useCase = GetRecommendedStepUseCase(
      nowProvider = { 200L },
      validateProcessDefinitionUseCase = ValidateProcessDefinitionUseCase()
    )
    val processDefinition = listOf(
      ProcessingStep("x", "", -10.0, 0L)
    )

    val decision = useCase.evaluate(batch, processDefinition)

    assertNull(decision.recommendedStep)
    assertTrue(
      decision.validationIssues.any {
        it.type == ProcessDefinitionIssueType.NON_POSITIVE_DURATION
      }
    )
  }
}
