package com.teaops.shared.domain.entity

/**
 * 収穫した茶葉バッチを表すドメインエンティティ。
 */
data class TeaBatch(
  val id: String,
  val type: String,
  val weight: Double,
  val harvestedAt: Long
)
