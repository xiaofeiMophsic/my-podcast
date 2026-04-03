package com.example.podcastapp.core.database

import androidx.room.EntityDeleteOrUpdateAdapter
import androidx.room.EntityInsertAdapter
import androidx.room.EntityUpsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import javax.`annotation`.processing.Generated
import kotlin.Int
import kotlin.Long
import kotlin.String
import kotlin.Suppress
import kotlin.Unit
import kotlin.collections.List
import kotlin.collections.MutableList
import kotlin.collections.mutableListOf
import kotlin.reflect.KClass
import kotlinx.coroutines.flow.Flow

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class SubscriptionDao_Impl(
  __db: RoomDatabase,
) : SubscriptionDao {
  private val __db: RoomDatabase

  private val __upsertAdapterOfSubscriptionEntity: EntityUpsertAdapter<SubscriptionEntity>
  init {
    this.__db = __db
    this.__upsertAdapterOfSubscriptionEntity = EntityUpsertAdapter<SubscriptionEntity>(object : EntityInsertAdapter<SubscriptionEntity>() {
      protected override fun createQuery(): String = "INSERT INTO `subscriptions` (`podcastId`,`subscribedAt`) VALUES (?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: SubscriptionEntity) {
        statement.bindLong(1, entity.podcastId)
        statement.bindLong(2, entity.subscribedAt)
      }
    }, object : EntityDeleteOrUpdateAdapter<SubscriptionEntity>() {
      protected override fun createQuery(): String = "UPDATE `subscriptions` SET `podcastId` = ?,`subscribedAt` = ? WHERE `podcastId` = ?"

      protected override fun bind(statement: SQLiteStatement, entity: SubscriptionEntity) {
        statement.bindLong(1, entity.podcastId)
        statement.bindLong(2, entity.subscribedAt)
        statement.bindLong(3, entity.podcastId)
      }
    })
  }

  public override suspend fun upsert(item: SubscriptionEntity): Unit = performSuspending(__db, false, true) { _connection ->
    __upsertAdapterOfSubscriptionEntity.upsert(_connection, item)
  }

  public override fun observeSubscriptions(): Flow<List<SubscriptionEntity>> {
    val _sql: String = "SELECT * FROM subscriptions"
    return createFlow(__db, false, arrayOf("subscriptions")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfPodcastId: Int = getColumnIndexOrThrow(_stmt, "podcastId")
        val _columnIndexOfSubscribedAt: Int = getColumnIndexOrThrow(_stmt, "subscribedAt")
        val _result: MutableList<SubscriptionEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: SubscriptionEntity
          val _tmpPodcastId: Long
          _tmpPodcastId = _stmt.getLong(_columnIndexOfPodcastId)
          val _tmpSubscribedAt: Long
          _tmpSubscribedAt = _stmt.getLong(_columnIndexOfSubscribedAt)
          _item = SubscriptionEntity(_tmpPodcastId,_tmpSubscribedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun delete(podcastId: Long) {
    val _sql: String = "DELETE FROM subscriptions WHERE podcastId = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, podcastId)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public companion object {
    public fun getRequiredConverters(): List<KClass<*>> = emptyList()
  }
}
