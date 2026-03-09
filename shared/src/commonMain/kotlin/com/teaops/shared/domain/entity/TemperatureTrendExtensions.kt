package com.teaops.shared.domain.entity

/**
 * 温度トレンドを日本語のラベル文字列へ変換する拡張。
 */
fun TemperatureTrend.toJapaneseLabel(): String {
  return when (this) {
    TemperatureTrend.RISING -> "上昇"
    TemperatureTrend.FALLING -> "下降"
    TemperatureTrend.STABLE -> "安定"
  }
}

