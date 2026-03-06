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
   * 収穫時刻より現在時刻が過去でも経過秒数が0として扱われることを確認する。
   */
  @Test
  fun treatsNegativeElapsedAsZeroAndReturnsFirstStep() {
    val batch = TeaBatch(
      id = "batch-3",
      type = "kabuse",
      weight = 8.0,
      harvestedAt = 2_000L
    )
    val useCase = GetRecommendedStepUseCase(
      nowProvider = { 1_500L },
      validateProcessDefinitionUseCase = ValidateProcessDefinitionUseCase()
    )
    val processDefinition = listOf(
      ProcessingStep("s1", "蒸し", 100.0, 60L),
      ProcessingStep("s2", "冷却", 40.0, 120L)
    )

    val decision = useCase.evaluate(batch, processDefinition)

    assertEquals(0L, decision.elapsedSecondsFromHarvest)
    assertNotNull(decision.recommendedStep)
    assertEquals("s1", decision.recommendedStep.id)
  }

  /**
   * 全工程時間を超過した場合は最後の工程が推奨されることを確認する。
   */
  @Test
  fun returnsLastStepWhenElapsedExceedsAllDurations() {
    val batch = TeaBatch(
      id = "batch-4",
      type = "sencha",
      weight = 15.0,
      harvestedAt = 1_000L
    )
    val useCase = GetRecommendedStepUseCase(
      nowProvider = { 1_600L },
      validateProcessDefinitionUseCase = ValidateProcessDefinitionUseCase()
    )
    val processDefinition = listOf(
      ProcessingStep("s1", "殺青", 180.0, 120L),
      ProcessingStep("s2", "揉捻", 95.0, 180L)
    )

    val decision = useCase.evaluate(batch, processDefinition)

    assertEquals(600L, decision.elapsedSecondsFromHarvest)
    assertNotNull(decision.recommendedStep)
    assertEquals("s2", decision.recommendedStep.id)
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
