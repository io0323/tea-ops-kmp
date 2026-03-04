package com.teaops.shared.domain.usecase

import com.teaops.shared.domain.entity.ProcessDefinitionIssueType
import com.teaops.shared.domain.entity.ProcessingStep
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * ValidateProcessDefinitionUseCase の検証テスト。
 */
class ValidateProcessDefinitionUseCaseTest {
  /**
   * 不正な工程定義を検出できることを確認する。
   */
  @Test
  fun returnsIssuesForInvalidProcessDefinition() {
    val useCase = ValidateProcessDefinitionUseCase()
    val definition = listOf(
      ProcessingStep(
        id = "dup",
        stepName = "",
        targetTemperature = -5.0,
        duration = 0L
      ),
      ProcessingStep(
        id = "dup",
        stepName = "乾燥",
        targetTemperature = 360.0,
        duration = 120L
      )
    )

    val result = useCase(definition)

    assertTrue(result.any { it.type == ProcessDefinitionIssueType.EMPTY_STEP_NAME })
    assertTrue(result.any { it.type == ProcessDefinitionIssueType.NON_POSITIVE_DURATION })
    assertTrue(
      result.any { it.type == ProcessDefinitionIssueType.INVALID_TEMPERATURE_RANGE }
    )
    assertTrue(result.any { it.type == ProcessDefinitionIssueType.DUPLICATE_STEP_ID })
  }

  /**
   * 有効な工程定義では問題が返らないことを確認する。
   */
  @Test
  fun returnsEmptyWhenDefinitionIsValid() {
    val useCase = ValidateProcessDefinitionUseCase()
    val definition = listOf(
      ProcessingStep("step-1", "殺青", 180.0, 90L),
      ProcessingStep("step-2", "揉捻", 95.0, 180L)
    )

    val result = useCase(definition)

    assertEquals(0, result.size)
  }
}
