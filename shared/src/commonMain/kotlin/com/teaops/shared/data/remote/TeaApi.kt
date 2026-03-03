package com.teaops.shared.data.remote

import com.teaops.shared.domain.entity.ProcessingStep
import com.teaops.shared.domain.entity.TeaBatch
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody

/**
 * Ktorでリモート同期を行うAPI実装。
 */
class TeaApi(
  private val client: HttpClient,
  private val baseUrl: String
) : TeaRemoteDataSource {
  /**
   * 茶葉バッチ一覧を取得する。
   */
  override suspend fun getBatches(): List<TeaBatch> {
    return client.get("$baseUrl/tea-batches").body()
  }

  /**
   * 茶葉バッチを登録または更新する。
   */
  override suspend fun upsertBatch(batch: TeaBatch) {
    client.post("$baseUrl/tea-batches") {
      setBody(batch)
    }
  }

  /**
   * 指定バッチの工程更新を送信する。
   */
  override suspend fun updateStep(batchId: String, step: ProcessingStep) {
    client.post("$baseUrl/tea-batches/$batchId/step") {
      parameter("stepId", step.id)
      setBody(step)
    }
  }
}
