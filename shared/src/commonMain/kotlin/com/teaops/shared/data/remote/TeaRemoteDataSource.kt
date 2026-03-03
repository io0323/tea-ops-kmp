package com.teaops.shared.data.remote

import com.teaops.shared.domain.entity.ProcessingStep
import com.teaops.shared.domain.entity.TeaBatch

/**
 * リモートAPIアクセスの抽象化インターフェース。
 */
interface TeaRemoteDataSource {
  /**
   * サーバー上の茶葉バッチ一覧を取得する。
   */
  suspend fun getBatches(): List<TeaBatch>

  /**
   * 茶葉バッチをサーバーへ反映する。
   */
  suspend fun upsertBatch(batch: TeaBatch)

  /**
   * 工程更新をサーバーへ反映する。
   */
  suspend fun updateStep(batchId: String, step: ProcessingStep)
}
