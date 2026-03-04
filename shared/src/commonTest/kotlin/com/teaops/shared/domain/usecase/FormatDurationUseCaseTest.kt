package com.teaops.shared.domain.usecase

import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * FormatDurationUseCase の整形結果を検証するテスト。
 */
class FormatDurationUseCaseTest {
  /**
   * 1時間未満の値が `MM:SS` で整形されることを確認する。
   */
  @Test
  fun formatsUnderOneHourAsMinuteSecond() {
    val useCase = FormatDurationUseCase()

    assertEquals("00:00", useCase(0L))
    assertEquals("01:05", useCase(65L))
    assertEquals("59:59", useCase(3599L))
  }

  /**
   * 1時間以上の値が `HH:MM:SS` で整形されることを確認する。
   */
  @Test
  fun formatsOneHourOrMoreAsHourMinuteSecond() {
    val useCase = FormatDurationUseCase()

    assertEquals("01:00:00", useCase(3600L))
    assertEquals("02:01:05", useCase(7265L))
  }
}
