package com.example.podcastapp.core.database

import androidx.paging.PagingSource
import androidx.room.EntityDeleteOrUpdateAdapter
import androidx.room.EntityInsertAdapter
import androidx.room.EntityUpsertAdapter
import androidx.room.RoomDatabase
import androidx.room.RoomRawQuery
import androidx.room.paging.LimitOffsetPagingSource
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import javax.`annotation`.processing.Generated
import kotlin.Boolean
import kotlin.Int
import kotlin.Long
import kotlin.String
import kotlin.Suppress
import kotlin.Unit
import kotlin.collections.List
import kotlin.collections.MutableList
import kotlin.collections.mutableListOf
import kotlin.reflect.KClass

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class EpisodeDao_Impl(
  __db: RoomDatabase,
) : EpisodeDao {
  private val __db: RoomDatabase

  private val __updateAdapterOfEpisodeEntity: EntityDeleteOrUpdateAdapter<EpisodeEntity>

  private val __upsertAdapterOfEpisodeEntity: EntityUpsertAdapter<EpisodeEntity>
  init {
    this.__db = __db
    this.__updateAdapterOfEpisodeEntity = object : EntityDeleteOrUpdateAdapter<EpisodeEntity>() {
      protected override fun createQuery(): String = "UPDATE OR ABORT `episodes` SET `id` = ?,`podcastId` = ?,`guid` = ?,`title` = ?,`description` = ?,`audioUrl` = ?,`imageUrl` = ?,`durationSeconds` = ?,`pubDate` = ?,`isPlayed` = ?,`updatedAt` = ? WHERE `id` = ?"

      protected override fun bind(statement: SQLiteStatement, entity: EpisodeEntity) {
        statement.bindLong(1, entity.id)
        statement.bindLong(2, entity.podcastId)
        statement.bindText(3, entity.guid)
        statement.bindText(4, entity.title)
        val _tmpDescription: String? = entity.description
        if (_tmpDescription == null) {
          statement.bindNull(5)
        } else {
          statement.bindText(5, _tmpDescription)
        }
        statement.bindText(6, entity.audioUrl)
        val _tmpImageUrl: String? = entity.imageUrl
        if (_tmpImageUrl == null) {
          statement.bindNull(7)
        } else {
          statement.bindText(7, _tmpImageUrl)
        }
        val _tmpDurationSeconds: Long? = entity.durationSeconds
        if (_tmpDurationSeconds == null) {
          statement.bindNull(8)
        } else {
          statement.bindLong(8, _tmpDurationSeconds)
        }
        val _tmpPubDate: Long? = entity.pubDate
        if (_tmpPubDate == null) {
          statement.bindNull(9)
        } else {
          statement.bindLong(9, _tmpPubDate)
        }
        val _tmp: Int = if (entity.isPlayed) 1 else 0
        statement.bindLong(10, _tmp.toLong())
        statement.bindLong(11, entity.updatedAt)
        statement.bindLong(12, entity.id)
      }
    }
    this.__upsertAdapterOfEpisodeEntity = EntityUpsertAdapter<EpisodeEntity>(object : EntityInsertAdapter<EpisodeEntity>() {
      protected override fun createQuery(): String = "INSERT INTO `episodes` (`id`,`podcastId`,`guid`,`title`,`description`,`audioUrl`,`imageUrl`,`durationSeconds`,`pubDate`,`isPlayed`,`updatedAt`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: EpisodeEntity) {
        statement.bindLong(1, entity.id)
        statement.bindLong(2, entity.podcastId)
        statement.bindText(3, entity.guid)
        statement.bindText(4, entity.title)
        val _tmpDescription: String? = entity.description
        if (_tmpDescription == null) {
          statement.bindNull(5)
        } else {
          statement.bindText(5, _tmpDescription)
        }
        statement.bindText(6, entity.audioUrl)
        val _tmpImageUrl: String? = entity.imageUrl
        if (_tmpImageUrl == null) {
          statement.bindNull(7)
        } else {
          statement.bindText(7, _tmpImageUrl)
        }
        val _tmpDurationSeconds: Long? = entity.durationSeconds
        if (_tmpDurationSeconds == null) {
          statement.bindNull(8)
        } else {
          statement.bindLong(8, _tmpDurationSeconds)
        }
        val _tmpPubDate: Long? = entity.pubDate
        if (_tmpPubDate == null) {
          statement.bindNull(9)
        } else {
          statement.bindLong(9, _tmpPubDate)
        }
        val _tmp: Int = if (entity.isPlayed) 1 else 0
        statement.bindLong(10, _tmp.toLong())
        statement.bindLong(11, entity.updatedAt)
      }
    }, object : EntityDeleteOrUpdateAdapter<EpisodeEntity>() {
      protected override fun createQuery(): String = "UPDATE `episodes` SET `id` = ?,`podcastId` = ?,`guid` = ?,`title` = ?,`description` = ?,`audioUrl` = ?,`imageUrl` = ?,`durationSeconds` = ?,`pubDate` = ?,`isPlayed` = ?,`updatedAt` = ? WHERE `id` = ?"

      protected override fun bind(statement: SQLiteStatement, entity: EpisodeEntity) {
        statement.bindLong(1, entity.id)
        statement.bindLong(2, entity.podcastId)
        statement.bindText(3, entity.guid)
        statement.bindText(4, entity.title)
        val _tmpDescription: String? = entity.description
        if (_tmpDescription == null) {
          statement.bindNull(5)
        } else {
          statement.bindText(5, _tmpDescription)
        }
        statement.bindText(6, entity.audioUrl)
        val _tmpImageUrl: String? = entity.imageUrl
        if (_tmpImageUrl == null) {
          statement.bindNull(7)
        } else {
          statement.bindText(7, _tmpImageUrl)
        }
        val _tmpDurationSeconds: Long? = entity.durationSeconds
        if (_tmpDurationSeconds == null) {
          statement.bindNull(8)
        } else {
          statement.bindLong(8, _tmpDurationSeconds)
        }
        val _tmpPubDate: Long? = entity.pubDate
        if (_tmpPubDate == null) {
          statement.bindNull(9)
        } else {
          statement.bindLong(9, _tmpPubDate)
        }
        val _tmp: Int = if (entity.isPlayed) 1 else 0
        statement.bindLong(10, _tmp.toLong())
        statement.bindLong(11, entity.updatedAt)
        statement.bindLong(12, entity.id)
      }
    })
  }

  public override suspend fun update(item: EpisodeEntity): Unit = performSuspending(__db, false, true) { _connection ->
    __updateAdapterOfEpisodeEntity.handle(_connection, item)
  }

  public override suspend fun upsertAll(items: List<EpisodeEntity>): Unit = performSuspending(__db, false, true) { _connection ->
    __upsertAdapterOfEpisodeEntity.upsert(_connection, items)
  }

  public override fun pagingByPodcast(podcastId: Long): PagingSource<Int, EpisodeEntity> {
    val _sql: String = "SELECT * FROM episodes WHERE podcastId = ? ORDER BY pubDate DESC"
    val _rawQuery: RoomRawQuery = RoomRawQuery(_sql) { _stmt ->
      var _argIndex: Int = 1
      _stmt.bindLong(_argIndex, podcastId)
    }
    return object : LimitOffsetPagingSource<EpisodeEntity>(_rawQuery, __db, "episodes") {
      protected override suspend fun convertRows(limitOffsetQuery: RoomRawQuery, itemCount: Int): List<EpisodeEntity> = performSuspending(__db, true, false) { _connection ->
        val _stmt: SQLiteStatement = _connection.prepare(limitOffsetQuery.sql)
        limitOffsetQuery.getBindingFunction().invoke(_stmt)
        try {
          val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
          val _columnIndexOfPodcastId: Int = getColumnIndexOrThrow(_stmt, "podcastId")
          val _columnIndexOfGuid: Int = getColumnIndexOrThrow(_stmt, "guid")
          val _columnIndexOfTitle: Int = getColumnIndexOrThrow(_stmt, "title")
          val _columnIndexOfDescription: Int = getColumnIndexOrThrow(_stmt, "description")
          val _columnIndexOfAudioUrl: Int = getColumnIndexOrThrow(_stmt, "audioUrl")
          val _columnIndexOfImageUrl: Int = getColumnIndexOrThrow(_stmt, "imageUrl")
          val _columnIndexOfDurationSeconds: Int = getColumnIndexOrThrow(_stmt, "durationSeconds")
          val _columnIndexOfPubDate: Int = getColumnIndexOrThrow(_stmt, "pubDate")
          val _columnIndexOfIsPlayed: Int = getColumnIndexOrThrow(_stmt, "isPlayed")
          val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
          val _result: MutableList<EpisodeEntity> = mutableListOf()
          while (_stmt.step()) {
            val _item: EpisodeEntity
            val _tmpId: Long
            _tmpId = _stmt.getLong(_columnIndexOfId)
            val _tmpPodcastId: Long
            _tmpPodcastId = _stmt.getLong(_columnIndexOfPodcastId)
            val _tmpGuid: String
            _tmpGuid = _stmt.getText(_columnIndexOfGuid)
            val _tmpTitle: String
            _tmpTitle = _stmt.getText(_columnIndexOfTitle)
            val _tmpDescription: String?
            if (_stmt.isNull(_columnIndexOfDescription)) {
              _tmpDescription = null
            } else {
              _tmpDescription = _stmt.getText(_columnIndexOfDescription)
            }
            val _tmpAudioUrl: String
            _tmpAudioUrl = _stmt.getText(_columnIndexOfAudioUrl)
            val _tmpImageUrl: String?
            if (_stmt.isNull(_columnIndexOfImageUrl)) {
              _tmpImageUrl = null
            } else {
              _tmpImageUrl = _stmt.getText(_columnIndexOfImageUrl)
            }
            val _tmpDurationSeconds: Long?
            if (_stmt.isNull(_columnIndexOfDurationSeconds)) {
              _tmpDurationSeconds = null
            } else {
              _tmpDurationSeconds = _stmt.getLong(_columnIndexOfDurationSeconds)
            }
            val _tmpPubDate: Long?
            if (_stmt.isNull(_columnIndexOfPubDate)) {
              _tmpPubDate = null
            } else {
              _tmpPubDate = _stmt.getLong(_columnIndexOfPubDate)
            }
            val _tmpIsPlayed: Boolean
            val _tmp: Int
            _tmp = _stmt.getLong(_columnIndexOfIsPlayed).toInt()
            _tmpIsPlayed = _tmp != 0
            val _tmpUpdatedAt: Long
            _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
            _item = EpisodeEntity(_tmpId,_tmpPodcastId,_tmpGuid,_tmpTitle,_tmpDescription,_tmpAudioUrl,_tmpImageUrl,_tmpDurationSeconds,_tmpPubDate,_tmpIsPlayed,_tmpUpdatedAt)
            _result.add(_item)
          }
          _result
        } finally {
          _stmt.close()
        }
      }
    }
  }

  public override suspend fun getEpisode(episodeId: Long): EpisodeEntity? {
    val _sql: String = "SELECT * FROM episodes WHERE id = ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, episodeId)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfPodcastId: Int = getColumnIndexOrThrow(_stmt, "podcastId")
        val _columnIndexOfGuid: Int = getColumnIndexOrThrow(_stmt, "guid")
        val _columnIndexOfTitle: Int = getColumnIndexOrThrow(_stmt, "title")
        val _columnIndexOfDescription: Int = getColumnIndexOrThrow(_stmt, "description")
        val _columnIndexOfAudioUrl: Int = getColumnIndexOrThrow(_stmt, "audioUrl")
        val _columnIndexOfImageUrl: Int = getColumnIndexOrThrow(_stmt, "imageUrl")
        val _columnIndexOfDurationSeconds: Int = getColumnIndexOrThrow(_stmt, "durationSeconds")
        val _columnIndexOfPubDate: Int = getColumnIndexOrThrow(_stmt, "pubDate")
        val _columnIndexOfIsPlayed: Int = getColumnIndexOrThrow(_stmt, "isPlayed")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _result: EpisodeEntity?
        if (_stmt.step()) {
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpPodcastId: Long
          _tmpPodcastId = _stmt.getLong(_columnIndexOfPodcastId)
          val _tmpGuid: String
          _tmpGuid = _stmt.getText(_columnIndexOfGuid)
          val _tmpTitle: String
          _tmpTitle = _stmt.getText(_columnIndexOfTitle)
          val _tmpDescription: String?
          if (_stmt.isNull(_columnIndexOfDescription)) {
            _tmpDescription = null
          } else {
            _tmpDescription = _stmt.getText(_columnIndexOfDescription)
          }
          val _tmpAudioUrl: String
          _tmpAudioUrl = _stmt.getText(_columnIndexOfAudioUrl)
          val _tmpImageUrl: String?
          if (_stmt.isNull(_columnIndexOfImageUrl)) {
            _tmpImageUrl = null
          } else {
            _tmpImageUrl = _stmt.getText(_columnIndexOfImageUrl)
          }
          val _tmpDurationSeconds: Long?
          if (_stmt.isNull(_columnIndexOfDurationSeconds)) {
            _tmpDurationSeconds = null
          } else {
            _tmpDurationSeconds = _stmt.getLong(_columnIndexOfDurationSeconds)
          }
          val _tmpPubDate: Long?
          if (_stmt.isNull(_columnIndexOfPubDate)) {
            _tmpPubDate = null
          } else {
            _tmpPubDate = _stmt.getLong(_columnIndexOfPubDate)
          }
          val _tmpIsPlayed: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfIsPlayed).toInt()
          _tmpIsPlayed = _tmp != 0
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          _result = EpisodeEntity(_tmpId,_tmpPodcastId,_tmpGuid,_tmpTitle,_tmpDescription,_tmpAudioUrl,_tmpImageUrl,_tmpDurationSeconds,_tmpPubDate,_tmpIsPlayed,_tmpUpdatedAt)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun searchPaging(query: String): PagingSource<Int, EpisodeEntity> {
    val _sql: String = "SELECT * FROM episodes WHERE title LIKE '%' || ? || '%' ORDER BY pubDate DESC"
    val _rawQuery: RoomRawQuery = RoomRawQuery(_sql) { _stmt ->
      var _argIndex: Int = 1
      _stmt.bindText(_argIndex, query)
    }
    return object : LimitOffsetPagingSource<EpisodeEntity>(_rawQuery, __db, "episodes") {
      protected override suspend fun convertRows(limitOffsetQuery: RoomRawQuery, itemCount: Int): List<EpisodeEntity> = performSuspending(__db, true, false) { _connection ->
        val _stmt: SQLiteStatement = _connection.prepare(limitOffsetQuery.sql)
        limitOffsetQuery.getBindingFunction().invoke(_stmt)
        try {
          val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
          val _columnIndexOfPodcastId: Int = getColumnIndexOrThrow(_stmt, "podcastId")
          val _columnIndexOfGuid: Int = getColumnIndexOrThrow(_stmt, "guid")
          val _columnIndexOfTitle: Int = getColumnIndexOrThrow(_stmt, "title")
          val _columnIndexOfDescription: Int = getColumnIndexOrThrow(_stmt, "description")
          val _columnIndexOfAudioUrl: Int = getColumnIndexOrThrow(_stmt, "audioUrl")
          val _columnIndexOfImageUrl: Int = getColumnIndexOrThrow(_stmt, "imageUrl")
          val _columnIndexOfDurationSeconds: Int = getColumnIndexOrThrow(_stmt, "durationSeconds")
          val _columnIndexOfPubDate: Int = getColumnIndexOrThrow(_stmt, "pubDate")
          val _columnIndexOfIsPlayed: Int = getColumnIndexOrThrow(_stmt, "isPlayed")
          val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
          val _result: MutableList<EpisodeEntity> = mutableListOf()
          while (_stmt.step()) {
            val _item: EpisodeEntity
            val _tmpId: Long
            _tmpId = _stmt.getLong(_columnIndexOfId)
            val _tmpPodcastId: Long
            _tmpPodcastId = _stmt.getLong(_columnIndexOfPodcastId)
            val _tmpGuid: String
            _tmpGuid = _stmt.getText(_columnIndexOfGuid)
            val _tmpTitle: String
            _tmpTitle = _stmt.getText(_columnIndexOfTitle)
            val _tmpDescription: String?
            if (_stmt.isNull(_columnIndexOfDescription)) {
              _tmpDescription = null
            } else {
              _tmpDescription = _stmt.getText(_columnIndexOfDescription)
            }
            val _tmpAudioUrl: String
            _tmpAudioUrl = _stmt.getText(_columnIndexOfAudioUrl)
            val _tmpImageUrl: String?
            if (_stmt.isNull(_columnIndexOfImageUrl)) {
              _tmpImageUrl = null
            } else {
              _tmpImageUrl = _stmt.getText(_columnIndexOfImageUrl)
            }
            val _tmpDurationSeconds: Long?
            if (_stmt.isNull(_columnIndexOfDurationSeconds)) {
              _tmpDurationSeconds = null
            } else {
              _tmpDurationSeconds = _stmt.getLong(_columnIndexOfDurationSeconds)
            }
            val _tmpPubDate: Long?
            if (_stmt.isNull(_columnIndexOfPubDate)) {
              _tmpPubDate = null
            } else {
              _tmpPubDate = _stmt.getLong(_columnIndexOfPubDate)
            }
            val _tmpIsPlayed: Boolean
            val _tmp: Int
            _tmp = _stmt.getLong(_columnIndexOfIsPlayed).toInt()
            _tmpIsPlayed = _tmp != 0
            val _tmpUpdatedAt: Long
            _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
            _item = EpisodeEntity(_tmpId,_tmpPodcastId,_tmpGuid,_tmpTitle,_tmpDescription,_tmpAudioUrl,_tmpImageUrl,_tmpDurationSeconds,_tmpPubDate,_tmpIsPlayed,_tmpUpdatedAt)
            _result.add(_item)
          }
          _result
        } finally {
          _stmt.close()
        }
      }
    }
  }

  public companion object {
    public fun getRequiredConverters(): List<KClass<*>> = emptyList()
  }
}
