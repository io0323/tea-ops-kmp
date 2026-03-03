package com.teaops.shared.data.repository

import kotlinx.coroutines.flow.Flow

/**
 * ネットワーク接続状態の監視インターフェース。
 */
interface NetworkMonitor {
  /**
   * オンライン状態の変化を通知する。
   */
  val isOnline: Flow<Boolean>
}
