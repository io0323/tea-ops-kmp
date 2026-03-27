package com.teaops.shared.domain.usecase

import com.teaops.shared.domain.util.pad2
import kotlin.time.Duration.Companion.seconds

/**
 * 秒数を運用表示向けの時刻文字列へ変換するユースケース。
 */
class FormatDurationUseCase {
  /**
   * 秒数を `MM:SS` または `HH:MM:SS` 形式に整形する。
   */
  operator fun invoke(seconds: Long): String {
    val duration = seconds.coerceAtLeast(0L).seconds
    val hours = duration.inWholeHours
    val minutes = duration.inWholeMinutes % 60
    val remainSeconds = duration.inWholeSeconds % 60

    return if (hours > 0L) {
      "${hours.pad2()}:${minutes.pad2()}:${remainSeconds.pad2()}"
    } else {
      "${minutes.pad2()}:${remainSeconds.pad2()}"
    }
  }
}
