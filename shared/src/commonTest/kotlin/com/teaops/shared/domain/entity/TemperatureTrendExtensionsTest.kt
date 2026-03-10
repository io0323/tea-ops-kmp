package com.teaops.shared.domain.entity

import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * TemperatureTrend の日本語ラベル拡張を検証するテスト。
 */
class TemperatureTrendExtensionsTest {
  /**
   * 上昇トレンドが「上昇」としてラベル変換されることを確認する。
   */
  @Test
  fun toJapaneseLabelReturnsRisingLabel() {
    assertEquals("上昇", TemperatureTrend.RISING.toJapaneseLabel())
  }

  /**
   * 下降トレンドが「下降」としてラベル変換されることを確認する。
   */
  @Test
  fun toJapaneseLabelReturnsFallingLabel() {
    assertEquals("下降", TemperatureTrend.FALLING.toJapaneseLabel())
  }

  /**
   * 安定トレンドが「安定」としてラベル変換されることを確認する。
   */
  @Test
  fun toJapaneseLabelReturnsStableLabel() {
    assertEquals("安定", TemperatureTrend.STABLE.toJapaneseLabel())
  }
}

