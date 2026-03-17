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
    val hours = safeSeconds / SECONDS_PER_HOUR
    val minutes = (safeSeconds % SECONDS_PER_HOUR) / SECONDS_PER_MINUTE
    val remainSeconds = safeSeconds % SECONDS_PER_MINUTE

    return if (hours > 0L) {
      "${hours.pad2()}:${minutes.pad2()}:${remainSeconds.pad2()}"
    } else {
      "${minutes.pad2()}:${remainSeconds.pad2()}"
    }
  }

  companion object {
    private const val SECONDS_PER_HOUR = 3600L
    private const val SECONDS_PER_MINUTE = 60L
  }
}

/**
 * 2桁ゼロ埋め文字列へ変換する。
 */
private fun Long.pad2(): String {
  return this.toString().padStart(2, '0')
}
