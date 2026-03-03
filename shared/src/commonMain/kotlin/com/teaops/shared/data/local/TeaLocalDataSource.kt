package com.teaops.shared.data.local

import com.teaops.shared.domain.entity.ProcessingStep
import com.teaops.shared.domain.entity.TeaBatch
import kotlinx.coroutines.flow.Flow

/**
 * ローカルDBアクセスの抽象化インターフェース。
 */
interface TeaLocalDataSource {
  /**
   * ローカルに保存された茶葉バッチ一覧を取得する。
   */
  fun observeBatches(): Flow<List<TeaBatch>>

  /**
   * 茶葉バッチを保存する。
   */
  suspend fun upsertBatch(batch: TeaBatch)

  /**
   * バッチの現在工程を更新する。
   */
  suspend fun updateStep(batchId: String, step: ProcessingStep)

  /**
   * サーバー同期が必要なバッチIDを記録する。
   */
  suspend fun markPendingSync(batchId: String)

  /**
   * サーバー同期待ちのバッチID一覧を返す。
   */
  suspend fun getPendingSyncIds(): List<String>

  /**
   * 同期済みとしてフラグを削除する。
   */
  suspend fun clearPendingSync(batchId: String)
}
