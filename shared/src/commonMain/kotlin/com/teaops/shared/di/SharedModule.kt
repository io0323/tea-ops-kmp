package com.teaops.shared.di

import app.cash.sqldelight.db.SqlDriver
import com.teaops.shared.data.local.TeaLocalDataSource
import com.teaops.shared.data.local.TeaLocalDataSourceImpl
import com.teaops.shared.data.remote.TeaApi
import com.teaops.shared.data.remote.TeaRemoteDataSource
import com.teaops.shared.data.repository.NetworkMonitor
import com.teaops.shared.data.repository.TeaRepositoryImpl
import com.teaops.shared.db.TeaOpsDatabase
import com.teaops.shared.domain.repository.TeaRepository
import com.teaops.shared.domain.usecase.BuildOperationAlertSummaryUseCase
import com.teaops.shared.domain.usecase.BuildMonitoringDigestUseCase
import com.teaops.shared.domain.usecase.BuildOperationalRiskSnapshotUseCase
import com.teaops.shared.domain.usecase.BuildPriorityChecklistUseCase
import com.teaops.shared.domain.usecase.BuildTemperatureActionSuggestionUseCase
import com.teaops.shared.domain.usecase.CalculateTemperatureDeviationIndexUseCase
import com.teaops.shared.domain.usecase.DetectTemperatureTrendUseCase
import com.teaops.shared.domain.usecase.EvaluateTeaQualityUseCase
import com.teaops.shared.domain.usecase.FormatDurationUseCase
import com.teaops.shared.domain.usecase.GetRecommendedStepUseCase
import com.teaops.shared.domain.usecase.SuggestMonitoringIntervalUseCase
import com.teaops.shared.domain.usecase.ValidateProcessDefinitionUseCase
import com.teaops.shared.presenter.production.ProductionMonitorStateFactory
import io.ktor.client.HttpClient
import kotlinx.coroutines.Dispatchers
import org.koin.dsl.module

/**
 * Sharedモジュールで利用するKoin定義。
 */
val sharedModule = module {
  /**
   * SQLDelightドライバとDBを提供する。
   */
  single<SqlDriver> { get<DatabaseDriverFactory>().createDriver() }
  single { TeaOpsDatabase(get()) }

  /**
   * Data層を提供する。
   */
  single<TeaLocalDataSource> { TeaLocalDataSourceImpl(get(), Dispatchers.Default) }
  single<TeaRemoteDataSource> { TeaApi(get(), get()) }
  single<TeaRepository> {
    TeaRepositoryImpl(
      local = get(),
      remote = get(),
      networkMonitor = get<NetworkMonitor>(),
      ioDispatcher = Dispatchers.Default
    )
  }

  /**
   * Domainユースケースを提供する。
   */
  factory { EvaluateTeaQualityUseCase() }
  factory { FormatDurationUseCase() }
  factory { BuildOperationAlertSummaryUseCase() }
  factory { BuildMonitoringDigestUseCase() }
  factory { BuildOperationalRiskSnapshotUseCase() }
  factory { BuildPriorityChecklistUseCase() }
  factory { BuildTemperatureActionSuggestionUseCase() }
  factory { CalculateTemperatureDeviationIndexUseCase() }
  factory { DetectTemperatureTrendUseCase() }
  factory { SuggestMonitoringIntervalUseCase() }
  factory { ValidateProcessDefinitionUseCase() }
  factory {
    GetRecommendedStepUseCase(
      nowProvider = { currentTimeMillis() },
      validateProcessDefinitionUseCase = get()
    )
  }
  factory {
    ProductionMonitorStateFactory(
      get(),
      get(),
      get(),
      get(),
      get(),
      get(),
      get(),
      get(),
      get(),
      get()
    )
  }

  /**
   * 外部注入を想定する必須依存関係。
   */
  single<HttpClient> { error("Provide HttpClient from platform module") }
  single<String> { error("Provide baseUrl from platform module") }
  single<NetworkMonitor> { error("Provide NetworkMonitor from platform module") }
}

/**
 * プラットフォームごとの現在時刻を提供する。
 */
expect fun currentTimeMillis(): Long

/**
 * プラットフォームごとのSQLDelightドライバ作成契約。
 */
interface DatabaseDriverFactory {
  /**
   * SQLDelightのドライバを作成する。
   */
  fun createDriver(): SqlDriver
}
