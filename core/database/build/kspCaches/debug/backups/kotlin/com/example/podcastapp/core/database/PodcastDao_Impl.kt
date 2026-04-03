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
import kotlin.collections.List
import kotlin.collections.MutableList
import kotlin.collections.mutableListOf
import kotlin.reflect.KClass
import kotlinx.coroutines.flow.Flow

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class PodcastDao_Impl(
  __db: RoomDatabase,
) : PodcastDao {
  private val __db: RoomDatabase

  private val __upsertAdapterOfPodcastEntity: EntityUpsertAdapter<PodcastEntity>
  init {
    this.__db = __db
    this.__upsertAdapterOfPodcastEntity = EntityUpsertAdapter<PodcastEntity>(object : EntityInsertAdapter<PodcastEntity>() {
      protected override fun createQuery(): String = "INSERT INTO `podcasts` (`id`,`feedUrl`,`title`,`description`,`imageUrl`,`author`,`language`,`lastBuildDate`,`updatedAt`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: PodcastEntity) {
        statement.bindLong(1, entity.id)
        statement.bindText(2, entity.feedUrl)
        statement.bindText(3, entity.title)
        val _tmpDescription: String? = entity.description
        if (_tmpDescription == null) {
          statement.bindNull(4)
        } else {
          statement.bindText(4, _tmpDescription)
        }
        val _tmpImageUrl: String? = entity.imageUrl
        if (_tmpImageUrl == null) {
          statement.bindNull(5)
        } else {
          statement.bindText(5, _tmpImageUrl)
        }
        val _tmpAuthor: String? = entity.author
        if (_tmpAuthor == null) {
          statement.bindNull(6)
        } else {
          statement.bindText(6, _tmpAuthor)
        }
        val _tmpLanguage: String? = entity.language
        if (_tmpLanguage == null) {
          statement.bindNull(7)
        } else {
          statement.bindText(7, _tmpLanguage)
        }
        val _tmpLastBuildDate: Long? = entity.lastBuildDate
        if (_tmpLastBuildDate == null) {
          statement.bindNull(8)
        } else {
          statement.bindLong(8, _tmpLastBuildDate)
        }
        statement.bindLong(9, entity.updatedAt)
      }
    }, object : EntityDeleteOrUpdateAdapter<PodcastEntity>() {
      protected override fun createQuery(): String = "UPDATE `podcasts` SET `id` = ?,`feedUrl` = ?,`title` = ?,`description` = ?,`imageUrl` = ?,`author` = ?,`language` = ?,`lastBuildDate` = ?,`updatedAt` = ? WHERE `id` = ?"

      protected override fun bind(statement: SQLiteStatement, entity: PodcastEntity) {
        statement.bindLong(1, entity.id)
        statement.bindText(2, entity.feedUrl)
        statement.bindText(3, entity.title)
        val _tmpDescription: String? = entity.description
        if (_tmpDescription == null) {
          statement.bindNull(4)
        } else {
          statement.bindText(4, _tmpDescription)
        }
        val _tmpImageUrl: String? = entity.imageUrl
        if (_tmpImageUrl == null) {
          statement.bindNull(5)
        } else {
          statement.bindText(5, _tmpImageUrl)
        }
        val _tmpAuthor: String? = entity.author
        if (_tmpAuthor == null) {
          statement.bindNull(6)
        } else {
          statement.bindText(6, _tmpAuthor)
        }
        val _tmpLanguage: String? = entity.language
        if (_tmpLanguage == null) {
          statement.bindNull(7)
        } else {
          statement.bindText(7, _tmpLanguage)
        }
        val _tmpLastBuildDate: Long? = entity.lastBuildDate
        if (_tmpLastBuildDate == null) {
          statement.bindNull(8)
        } else {
          statement.bindLong(8, _tmpLastBuildDate)
        }
        statement.bindLong(9, entity.updatedAt)
        statement.bindLong(10, entity.id)
      }
    })
  }

  public override suspend fun upsert(item: PodcastEntity): Long = performSuspending(__db, false, true) { _connection ->
    val _result: Long = __upsertAdapterOfPodcastEntity.upsertAndReturnId(_connection, item)
    _result
  }

  public override suspend fun upsertAll(items: List<PodcastEntity>): List<Long> = performSuspending(__db, false, true) { _connection ->
    val _result: List<Long> = __upsertAdapterOfPodcastEntity.upsertAndReturnIdsList(_connection, items)
    _result
  }

  public override fun observePodcasts(): Flow<List<PodcastEntity>> {
    val _sql: String = "SELECT * FROM podcasts ORDER BY title"
    return createFlow(__db, false, arrayOf("podcasts")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfFeedUrl: Int = getColumnIndexOrThrow(_stmt, "feedUrl")
        val _columnIndexOfTitle: Int = getColumnIndexOrThrow(_stmt, "title")
        val _columnIndexOfDescription: Int = getColumnIndexOrThrow(_stmt, "description")
        val _columnIndexOfImageUrl: Int = getColumnIndexOrThrow(_stmt, "imageUrl")
        val _columnIndexOfAuthor: Int = getColumnIndexOrThrow(_stmt, "author")
        val _columnIndexOfLanguage: Int = getColumnIndexOrThrow(_stmt, "language")
        val _columnIndexOfLastBuildDate: Int = getColumnIndexOrThrow(_stmt, "lastBuildDate")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _result: MutableList<PodcastEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: PodcastEntity
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpFeedUrl: String
          _tmpFeedUrl = _stmt.getText(_columnIndexOfFeedUrl)
          val _tmpTitle: String
          _tmpTitle = _stmt.getText(_columnIndexOfTitle)
          val _tmpDescription: String?
          if (_stmt.isNull(_columnIndexOfDescription)) {
            _tmpDescription = null
          } else {
            _tmpDescription = _stmt.getText(_columnIndexOfDescription)
          }
          val _tmpImageUrl: String?
          if (_stmt.isNull(_columnIndexOfImageUrl)) {
            _tmpImageUrl = null
          } else {
            _tmpImageUrl = _stmt.getText(_columnIndexOfImageUrl)
          }
          val _tmpAuthor: String?
          if (_stmt.isNull(_columnIndexOfAuthor)) {
            _tmpAuthor = null
          } else {
            _tmpAuthor = _stmt.getText(_columnIndexOfAuthor)
          }
          val _tmpLanguage: String?
          if (_stmt.isNull(_columnIndexOfLanguage)) {
            _tmpLanguage = null
          } else {
            _tmpLanguage = _stmt.getText(_columnIndexOfLanguage)
          }
          val _tmpLastBuildDate: Long?
          if (_stmt.isNull(_columnIndexOfLastBuildDate)) {
            _tmpLastBuildDate = null
          } else {
            _tmpLastBuildDate = _stmt.getLong(_columnIndexOfLastBuildDate)
          }
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          _item = PodcastEntity(_tmpId,_tmpFeedUrl,_tmpTitle,_tmpDescription,_tmpImageUrl,_tmpAuthor,_tmpLanguage,_tmpLastBuildDate,_tmpUpdatedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getPodcast(podcastId: Long): PodcastEntity? {
    val _sql: String = "SELECT * FROM podcasts WHERE id = ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, podcastId)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfFeedUrl: Int = getColumnIndexOrThrow(_stmt, "feedUrl")
        val _columnIndexOfTitle: Int = getColumnIndexOrThrow(_stmt, "title")
        val _columnIndexOfDescription: Int = getColumnIndexOrThrow(_stmt, "description")
        val _columnIndexOfImageUrl: Int = getColumnIndexOrThrow(_stmt, "imageUrl")
        val _columnIndexOfAuthor: Int = getColumnIndexOrThrow(_stmt, "author")
        val _columnIndexOfLanguage: Int = getColumnIndexOrThrow(_stmt, "language")
        val _columnIndexOfLastBuildDate: Int = getColumnIndexOrThrow(_stmt, "lastBuildDate")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _result: PodcastEntity?
        if (_stmt.step()) {
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpFeedUrl: String
          _tmpFeedUrl = _stmt.getText(_columnIndexOfFeedUrl)
          val _tmpTitle: String
          _tmpTitle = _stmt.getText(_columnIndexOfTitle)
          val _tmpDescription: String?
          if (_stmt.isNull(_columnIndexOfDescription)) {
            _tmpDescription = null
          } else {
            _tmpDescription = _stmt.getText(_columnIndexOfDescription)
          }
          val _tmpImageUrl: String?
          if (_stmt.isNull(_columnIndexOfImageUrl)) {
            _tmpImageUrl = null
          } else {
            _tmpImageUrl = _stmt.getText(_columnIndexOfImageUrl)
          }
          val _tmpAuthor: String?
          if (_stmt.isNull(_columnIndexOfAuthor)) {
            _tmpAuthor = null
          } else {
            _tmpAuthor = _stmt.getText(_columnIndexOfAuthor)
          }
          val _tmpLanguage: String?
          if (_stmt.isNull(_columnIndexOfLanguage)) {
            _tmpLanguage = null
          } else {
            _tmpLanguage = _stmt.getText(_columnIndexOfLanguage)
          }
          val _tmpLastBuildDate: Long?
          if (_stmt.isNull(_columnIndexOfLastBuildDate)) {
            _tmpLastBuildDate = null
          } else {
            _tmpLastBuildDate = _stmt.getLong(_columnIndexOfLastBuildDate)
          }
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          _result = PodcastEntity(_tmpId,_tmpFeedUrl,_tmpTitle,_tmpDescription,_tmpImageUrl,_tmpAuthor,_tmpLanguage,_tmpLastBuildDate,_tmpUpdatedAt)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getByFeedUrl(feedUrl: String): PodcastEntity? {
    val _sql: String = "SELECT * FROM podcasts WHERE feedUrl = ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, feedUrl)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfFeedUrl: Int = getColumnIndexOrThrow(_stmt, "feedUrl")
        val _columnIndexOfTitle: Int = getColumnIndexOrThrow(_stmt, "title")
        val _columnIndexOfDescription: Int = getColumnIndexOrThrow(_stmt, "description")
        val _columnIndexOfImageUrl: Int = getColumnIndexOrThrow(_stmt, "imageUrl")
        val _columnIndexOfAuthor: Int = getColumnIndexOrThrow(_stmt, "author")
        val _columnIndexOfLanguage: Int = getColumnIndexOrThrow(_stmt, "language")
        val _columnIndexOfLastBuildDate: Int = getColumnIndexOrThrow(_stmt, "lastBuildDate")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _result: PodcastEntity?
        if (_stmt.step()) {
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpFeedUrl: String
          _tmpFeedUrl = _stmt.getText(_columnIndexOfFeedUrl)
          val _tmpTitle: String
          _tmpTitle = _stmt.getText(_columnIndexOfTitle)
          val _tmpDescription: String?
          if (_stmt.isNull(_columnIndexOfDescription)) {
            _tmpDescription = null
          } else {
            _tmpDescription = _stmt.getText(_columnIndexOfDescription)
          }
          val _tmpImageUrl: String?
          if (_stmt.isNull(_columnIndexOfImageUrl)) {
            _tmpImageUrl = null
          } else {
            _tmpImageUrl = _stmt.getText(_columnIndexOfImageUrl)
          }
          val _tmpAuthor: String?
          if (_stmt.isNull(_columnIndexOfAuthor)) {
            _tmpAuthor = null
          } else {
            _tmpAuthor = _stmt.getText(_columnIndexOfAuthor)
          }
          val _tmpLanguage: String?
          if (_stmt.isNull(_columnIndexOfLanguage)) {
            _tmpLanguage = null
          } else {
            _tmpLanguage = _stmt.getText(_columnIndexOfLanguage)
          }
          val _tmpLastBuildDate: Long?
          if (_stmt.isNull(_columnIndexOfLastBuildDate)) {
            _tmpLastBuildDate = null
          } else {
            _tmpLastBuildDate = _stmt.getLong(_columnIndexOfLastBuildDate)
          }
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          _result = PodcastEntity(_tmpId,_tmpFeedUrl,_tmpTitle,_tmpDescription,_tmpImageUrl,_tmpAuthor,_tmpLanguage,_tmpLastBuildDate,_tmpUpdatedAt)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public companion object {
    public fun getRequiredConverters(): List<KClass<*>> = emptyList()
  }
}
