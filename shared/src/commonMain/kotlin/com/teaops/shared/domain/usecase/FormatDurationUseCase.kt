package com.teaops.shared.domain.usecase

/**
 * 秒数を運用表示向けの時刻文字列へ変換するユースケース。
 */
class FormatDurationUseCase {
  /**
   * 秒数を `MM:SS` または `HH:MM:SS` 形式に整形する。
   */
  operator fun invoke(seconds: Long): String {
    val safeSeconds = seconds.coerceAtLeast(0L)
    val hours = safeSeconds / 3600L
    val minutes = (safeSeconds % 3600L) / 60L
    val remainSeconds = safeSeconds % 60L

    return if (hours > 0L) {
      "${hours.pad2()}:${minutes.pad2()}:${remainSeconds.pad2()}"
    } else {
      "${minutes.pad2()}:${remainSeconds.pad2()}"
    }
  }
}

/**
 * 2桁ゼロ埋め文字列へ変換する。
 */
private fun Long.pad2(): String {
  return if (this < 10L) {
    "0$this"
  } else {
    this.toString()
  }
}
