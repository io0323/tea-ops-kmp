package com.teaops.shared.domain.repository

import com.teaops.shared.domain.entity.ProcessingStep
import com.teaops.shared.domain.entity.TeaBatch
import kotlinx.coroutines.flow.Flow

/**
 * 茶葉バッチ情報の取得・更新を抽象化するリポジトリ契約。
 */
interface TeaRepository {
  /**
   * 茶葉バッチ一覧を購読する。
   */
  fun getBatches(): Flow<List<TeaBatch>>

  /**
   * 茶葉バッチを登録または更新する。
   */
  suspend fun upsertBatch(batch: TeaBatch)

  /**
   * 指定バッチの加工工程を更新する。
   */
  suspend fun updateStep(batchId: String, step: ProcessingStep)

  /**
   * 指定バッチのリモート同期を試行する。
   */
  suspend fun syncBatch(batchId: String): Result<Unit>
}
