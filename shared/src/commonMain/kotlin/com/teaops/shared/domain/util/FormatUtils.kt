package com.teaops.shared.domain.util

import kotlin.math.round

/**
 * 2桁ゼロ埋め文字列へ変換する。
 */
fun Long.pad2(): String {
  return this.toString().padStart(2, '0')
}

/**
 * 誤差丸めのため小数第1位までに丸める。
 */
fun Double.roundToOneDecimal(): Double {
  return round(this * 10.0) / 10.0
}
