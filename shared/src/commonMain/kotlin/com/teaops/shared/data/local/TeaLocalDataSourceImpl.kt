package com.teaops.shared.data.local

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.teaops.shared.db.TeaBatch as TeaBatchRow
import com.teaops.shared.db.TeaOpsDatabase
import com.teaops.shared.domain.entity.ProcessingStep
import com.teaops.shared.domain.entity.TeaBatch
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

/**
 * SQLDelightを利用したローカルデータソース実装。
 */
class TeaLocalDataSourceImpl(
  database: TeaOpsDatabase,
  private val ioDispatcher: CoroutineDispatcher
) : TeaLocalDataSource {
  private val queries = database.teaBatchQueries

  /**
   * ローカルDBの茶葉バッチ一覧をFlowで返す。
   */
  override fun observeBatches(): Flow<List<TeaBatch>> {
    return queries.selectAllBatches()
      .asFlow()
      .mapToList(ioDispatcher)
      .map { rows -> rows.map { it.toEntity() } }
  }

  /**
   * 茶葉バッチをSQLDelightへ保存する。
   */
  override suspend fun upsertBatch(batch: TeaBatch) = withContext(ioDispatcher) {
    queries.upsertBatch(
      id = batch.id,
      type = batch.type,
      weight = batch.weight,
      harvested_at = batch.harvestedAt,
      current_step_id = null,
      current_step_name = null,
      target_temperature = null,
      duration = null
    )
  }

  /**
   * 指定バッチの工程カラムを更新する。
   */
  override suspend fun updateStep(
    batchId: String,
    step: ProcessingStep
  ) = withContext(ioDispatcher) {
    queries.updateBatchStep(
      current_step_id = step.id,
      current_step_name = step.stepName,
      target_temperature = step.targetTemperature,
      duration = step.duration,
      id = batchId
    )
  }

  /**
   * 同期待ちバッチをPendingSyncへ追加する。
   */
  override suspend fun markPendingSync(batchId: String) = withContext(ioDispatcher) {
    queries.insertPendingSync(batchId)
  }

  /**
   * 同期待ちのバッチID一覧を返す。
   */
  override suspend fun getPendingSyncIds(): List<String> {
    return withContext(ioDispatcher) {
      queries.selectPendingSyncIds().executeAsList()
    }
  }

  /**
   * 同期済みバッチをPendingSyncから削除する。
   */
  override suspend fun clearPendingSync(batchId: String) = withContext(ioDispatcher) {
    queries.deletePendingSync(batchId)
  }
}

/**
 * SQLDelight行モデルをドメインモデルへ変換する。
 */
private fun TeaBatchRow.toEntity(): TeaBatch {
  return TeaBatch(
    id = id,
    type = type,
    weight = weight,
    harvestedAt = harvested_at
  )
}
