package com.teaops.shared.data.repository

import com.teaops.shared.data.local.TeaLocalDataSource
import com.teaops.shared.data.remote.TeaRemoteDataSource
import com.teaops.shared.domain.entity.ProcessingStep
import com.teaops.shared.domain.entity.TeaBatch
import com.teaops.shared.domain.repository.TeaRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.withContext

/**
 * Offline-first方針の茶葉リポジトリ実装。
 */
class TeaRepositoryImpl(
  private val local: TeaLocalDataSource,
  private val remote: TeaRemoteDataSource,
  private val networkMonitor: NetworkMonitor,
  private val ioDispatcher: CoroutineDispatcher
) : TeaRepository {
  /**
   * ローカルデータを返しつつ、オンライン時にバックグラウンド同期する。
   */
  override fun getBatches(): Flow<List<TeaBatch>> {
    return local.observeBatches().onStart {
      val isOnlineNow = networkMonitor.isOnline.first()
      if (isOnlineNow) {
        runCatching {
          remote.getBatches()
        }.onSuccess { remoteBatches ->
          remoteBatches.forEach { batch ->
            local.upsertBatch(batch)
          }
        }
      }
    }
  }

  /**
   * まずローカルへ反映し、オンライン時のみリモート更新を試行する。
   */
  override suspend fun upsertBatch(batch: TeaBatch) = withContext(ioDispatcher) {
    local.upsertBatch(batch)
    val isOnlineNow = networkMonitor.isOnline.first()
    if (!isOnlineNow) {
      local.markPendingSync(batch.id)
      return@withContext
    }

    runCatching {
      remote.upsertBatch(batch)
    }.onFailure {
      local.markPendingSync(batch.id)
    }
  }

  /**
   * 工程更新をローカル優先で適用し、失敗時は同期待ちに積む。
   */
  override suspend fun updateStep(
    batchId: String,
    step: ProcessingStep
  ) = withContext(ioDispatcher) {
    local.updateStep(batchId, step)

    val isOnlineNow = networkMonitor.isOnline.first()
    if (!isOnlineNow) {
      local.markPendingSync(batchId)
      return@withContext
    }

    runCatching {
      remote.updateStep(batchId, step)
    }.onFailure {
      local.markPendingSync(batchId)
    }
  }

  /**
   * 同期待ちキューの対象も含めてリモート反映を試行する。
   */
  override suspend fun syncBatch(batchId: String): Result<Unit> {
    return withContext(ioDispatcher) {
      runCatching {
        val pendingIds = local.getPendingSyncIds()
        val syncTargets = (pendingIds + batchId).distinct()

        if (!networkMonitor.isOnline.first()) {
          error("Network is offline")
        }

        val currentBatches = local.observeBatches().first().associateBy { it.id }
        syncTargets.forEach { id ->
          val batch = currentBatches[id] ?: return@forEach
          remote.upsertBatch(batch)
          local.clearPendingSync(id)
        }
      }
    }
  }
}
