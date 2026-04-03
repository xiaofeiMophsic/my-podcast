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
public class DownloadDao_Impl(
  __db: RoomDatabase,
) : DownloadDao {
  private val __db: RoomDatabase

  private val __updateAdapterOfDownloadEntity: EntityDeleteOrUpdateAdapter<DownloadEntity>

  private val __converters: Converters = Converters()

  private val __upsertAdapterOfDownloadEntity: EntityUpsertAdapter<DownloadEntity>
  init {
    this.__db = __db
    this.__updateAdapterOfDownloadEntity = object : EntityDeleteOrUpdateAdapter<DownloadEntity>() {
      protected override fun createQuery(): String = "UPDATE OR ABORT `downloads` SET `id` = ?,`episodeId` = ?,`downloadManagerId` = ?,`localPath` = ?,`status` = ?,`progress` = ?,`totalBytes` = ?,`downloadedBytes` = ?,`createdAt` = ?,`updatedAt` = ? WHERE `id` = ?"

      protected override fun bind(statement: SQLiteStatement, entity: DownloadEntity) {
        statement.bindLong(1, entity.id)
        statement.bindLong(2, entity.episodeId)
        val _tmpDownloadManagerId: Long? = entity.downloadManagerId
        if (_tmpDownloadManagerId == null) {
          statement.bindNull(3)
        } else {
          statement.bindLong(3, _tmpDownloadManagerId)
        }
        val _tmpLocalPath: String? = entity.localPath
        if (_tmpLocalPath == null) {
          statement.bindNull(4)
        } else {
          statement.bindText(4, _tmpLocalPath)
        }
        val _tmp: String? = __converters.fromDownloadStatus(entity.status)
        if (_tmp == null) {
          statement.bindNull(5)
        } else {
          statement.bindText(5, _tmp)
        }
        statement.bindLong(6, entity.progress.toLong())
        val _tmpTotalBytes: Long? = entity.totalBytes
        if (_tmpTotalBytes == null) {
          statement.bindNull(7)
        } else {
          statement.bindLong(7, _tmpTotalBytes)
        }
        statement.bindLong(8, entity.downloadedBytes)
        statement.bindLong(9, entity.createdAt)
        statement.bindLong(10, entity.updatedAt)
        statement.bindLong(11, entity.id)
      }
    }
    this.__upsertAdapterOfDownloadEntity = EntityUpsertAdapter<DownloadEntity>(object : EntityInsertAdapter<DownloadEntity>() {
      protected override fun createQuery(): String = "INSERT INTO `downloads` (`id`,`episodeId`,`downloadManagerId`,`localPath`,`status`,`progress`,`totalBytes`,`downloadedBytes`,`createdAt`,`updatedAt`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: DownloadEntity) {
        statement.bindLong(1, entity.id)
        statement.bindLong(2, entity.episodeId)
        val _tmpDownloadManagerId: Long? = entity.downloadManagerId
        if (_tmpDownloadManagerId == null) {
          statement.bindNull(3)
        } else {
          statement.bindLong(3, _tmpDownloadManagerId)
        }
        val _tmpLocalPath: String? = entity.localPath
        if (_tmpLocalPath == null) {
          statement.bindNull(4)
        } else {
          statement.bindText(4, _tmpLocalPath)
        }
        val _tmp: String? = __converters.fromDownloadStatus(entity.status)
        if (_tmp == null) {
          statement.bindNull(5)
        } else {
          statement.bindText(5, _tmp)
        }
        statement.bindLong(6, entity.progress.toLong())
        val _tmpTotalBytes: Long? = entity.totalBytes
        if (_tmpTotalBytes == null) {
          statement.bindNull(7)
        } else {
          statement.bindLong(7, _tmpTotalBytes)
        }
        statement.bindLong(8, entity.downloadedBytes)
        statement.bindLong(9, entity.createdAt)
        statement.bindLong(10, entity.updatedAt)
      }
    }, object : EntityDeleteOrUpdateAdapter<DownloadEntity>() {
      protected override fun createQuery(): String = "UPDATE `downloads` SET `id` = ?,`episodeId` = ?,`downloadManagerId` = ?,`localPath` = ?,`status` = ?,`progress` = ?,`totalBytes` = ?,`downloadedBytes` = ?,`createdAt` = ?,`updatedAt` = ? WHERE `id` = ?"

      protected override fun bind(statement: SQLiteStatement, entity: DownloadEntity) {
        statement.bindLong(1, entity.id)
        statement.bindLong(2, entity.episodeId)
        val _tmpDownloadManagerId: Long? = entity.downloadManagerId
        if (_tmpDownloadManagerId == null) {
          statement.bindNull(3)
        } else {
          statement.bindLong(3, _tmpDownloadManagerId)
        }
        val _tmpLocalPath: String? = entity.localPath
        if (_tmpLocalPath == null) {
          statement.bindNull(4)
        } else {
          statement.bindText(4, _tmpLocalPath)
        }
        val _tmp: String? = __converters.fromDownloadStatus(entity.status)
        if (_tmp == null) {
          statement.bindNull(5)
        } else {
          statement.bindText(5, _tmp)
        }
        statement.bindLong(6, entity.progress.toLong())
        val _tmpTotalBytes: Long? = entity.totalBytes
        if (_tmpTotalBytes == null) {
          statement.bindNull(7)
        } else {
          statement.bindLong(7, _tmpTotalBytes)
        }
        statement.bindLong(8, entity.downloadedBytes)
        statement.bindLong(9, entity.createdAt)
        statement.bindLong(10, entity.updatedAt)
        statement.bindLong(11, entity.id)
      }
    })
  }

  public override suspend fun update(item: DownloadEntity): Unit = performSuspending(__db, false, true) { _connection ->
    __updateAdapterOfDownloadEntity.handle(_connection, item)
  }

  public override suspend fun upsert(item: DownloadEntity): Unit = performSuspending(__db, false, true) { _connection ->
    __upsertAdapterOfDownloadEntity.upsert(_connection, item)
  }

  public override suspend fun getByEpisode(episodeId: Long): DownloadEntity? {
    val _sql: String = "SELECT * FROM downloads WHERE episodeId = ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, episodeId)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfEpisodeId: Int = getColumnIndexOrThrow(_stmt, "episodeId")
        val _columnIndexOfDownloadManagerId: Int = getColumnIndexOrThrow(_stmt, "downloadManagerId")
        val _columnIndexOfLocalPath: Int = getColumnIndexOrThrow(_stmt, "localPath")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfProgress: Int = getColumnIndexOrThrow(_stmt, "progress")
        val _columnIndexOfTotalBytes: Int = getColumnIndexOrThrow(_stmt, "totalBytes")
        val _columnIndexOfDownloadedBytes: Int = getColumnIndexOrThrow(_stmt, "downloadedBytes")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _result: DownloadEntity?
        if (_stmt.step()) {
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpEpisodeId: Long
          _tmpEpisodeId = _stmt.getLong(_columnIndexOfEpisodeId)
          val _tmpDownloadManagerId: Long?
          if (_stmt.isNull(_columnIndexOfDownloadManagerId)) {
            _tmpDownloadManagerId = null
          } else {
            _tmpDownloadManagerId = _stmt.getLong(_columnIndexOfDownloadManagerId)
          }
          val _tmpLocalPath: String?
          if (_stmt.isNull(_columnIndexOfLocalPath)) {
            _tmpLocalPath = null
          } else {
            _tmpLocalPath = _stmt.getText(_columnIndexOfLocalPath)
          }
          val _tmpStatus: DownloadStatus
          val _tmp: String?
          if (_stmt.isNull(_columnIndexOfStatus)) {
            _tmp = null
          } else {
            _tmp = _stmt.getText(_columnIndexOfStatus)
          }
          val _tmp_1: DownloadStatus? = __converters.toDownloadStatus(_tmp)
          if (_tmp_1 == null) {
            error("Expected NON-NULL 'com.example.podcastapp.core.database.DownloadStatus', but it was NULL.")
          } else {
            _tmpStatus = _tmp_1
          }
          val _tmpProgress: Int
          _tmpProgress = _stmt.getLong(_columnIndexOfProgress).toInt()
          val _tmpTotalBytes: Long?
          if (_stmt.isNull(_columnIndexOfTotalBytes)) {
            _tmpTotalBytes = null
          } else {
            _tmpTotalBytes = _stmt.getLong(_columnIndexOfTotalBytes)
          }
          val _tmpDownloadedBytes: Long
          _tmpDownloadedBytes = _stmt.getLong(_columnIndexOfDownloadedBytes)
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          _result = DownloadEntity(_tmpId,_tmpEpisodeId,_tmpDownloadManagerId,_tmpLocalPath,_tmpStatus,_tmpProgress,_tmpTotalBytes,_tmpDownloadedBytes,_tmpCreatedAt,_tmpUpdatedAt)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getByDownloadManagerId(downloadManagerId: Long): DownloadEntity? {
    val _sql: String = "SELECT * FROM downloads WHERE downloadManagerId = ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, downloadManagerId)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfEpisodeId: Int = getColumnIndexOrThrow(_stmt, "episodeId")
        val _columnIndexOfDownloadManagerId: Int = getColumnIndexOrThrow(_stmt, "downloadManagerId")
        val _columnIndexOfLocalPath: Int = getColumnIndexOrThrow(_stmt, "localPath")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfProgress: Int = getColumnIndexOrThrow(_stmt, "progress")
        val _columnIndexOfTotalBytes: Int = getColumnIndexOrThrow(_stmt, "totalBytes")
        val _columnIndexOfDownloadedBytes: Int = getColumnIndexOrThrow(_stmt, "downloadedBytes")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _result: DownloadEntity?
        if (_stmt.step()) {
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpEpisodeId: Long
          _tmpEpisodeId = _stmt.getLong(_columnIndexOfEpisodeId)
          val _tmpDownloadManagerId: Long?
          if (_stmt.isNull(_columnIndexOfDownloadManagerId)) {
            _tmpDownloadManagerId = null
          } else {
            _tmpDownloadManagerId = _stmt.getLong(_columnIndexOfDownloadManagerId)
          }
          val _tmpLocalPath: String?
          if (_stmt.isNull(_columnIndexOfLocalPath)) {
            _tmpLocalPath = null
          } else {
            _tmpLocalPath = _stmt.getText(_columnIndexOfLocalPath)
          }
          val _tmpStatus: DownloadStatus
          val _tmp: String?
          if (_stmt.isNull(_columnIndexOfStatus)) {
            _tmp = null
          } else {
            _tmp = _stmt.getText(_columnIndexOfStatus)
          }
          val _tmp_1: DownloadStatus? = __converters.toDownloadStatus(_tmp)
          if (_tmp_1 == null) {
            error("Expected NON-NULL 'com.example.podcastapp.core.database.DownloadStatus', but it was NULL.")
          } else {
            _tmpStatus = _tmp_1
          }
          val _tmpProgress: Int
          _tmpProgress = _stmt.getLong(_columnIndexOfProgress).toInt()
          val _tmpTotalBytes: Long?
          if (_stmt.isNull(_columnIndexOfTotalBytes)) {
            _tmpTotalBytes = null
          } else {
            _tmpTotalBytes = _stmt.getLong(_columnIndexOfTotalBytes)
          }
          val _tmpDownloadedBytes: Long
          _tmpDownloadedBytes = _stmt.getLong(_columnIndexOfDownloadedBytes)
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          _result = DownloadEntity(_tmpId,_tmpEpisodeId,_tmpDownloadManagerId,_tmpLocalPath,_tmpStatus,_tmpProgress,_tmpTotalBytes,_tmpDownloadedBytes,_tmpCreatedAt,_tmpUpdatedAt)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun observeDownloads(): Flow<List<DownloadEntity>> {
    val _sql: String = "SELECT * FROM downloads ORDER BY updatedAt DESC"
    return createFlow(__db, false, arrayOf("downloads")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfEpisodeId: Int = getColumnIndexOrThrow(_stmt, "episodeId")
        val _columnIndexOfDownloadManagerId: Int = getColumnIndexOrThrow(_stmt, "downloadManagerId")
        val _columnIndexOfLocalPath: Int = getColumnIndexOrThrow(_stmt, "localPath")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfProgress: Int = getColumnIndexOrThrow(_stmt, "progress")
        val _columnIndexOfTotalBytes: Int = getColumnIndexOrThrow(_stmt, "totalBytes")
        val _columnIndexOfDownloadedBytes: Int = getColumnIndexOrThrow(_stmt, "downloadedBytes")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _result: MutableList<DownloadEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: DownloadEntity
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpEpisodeId: Long
          _tmpEpisodeId = _stmt.getLong(_columnIndexOfEpisodeId)
          val _tmpDownloadManagerId: Long?
          if (_stmt.isNull(_columnIndexOfDownloadManagerId)) {
            _tmpDownloadManagerId = null
          } else {
            _tmpDownloadManagerId = _stmt.getLong(_columnIndexOfDownloadManagerId)
          }
          val _tmpLocalPath: String?
          if (_stmt.isNull(_columnIndexOfLocalPath)) {
            _tmpLocalPath = null
          } else {
            _tmpLocalPath = _stmt.getText(_columnIndexOfLocalPath)
          }
          val _tmpStatus: DownloadStatus
          val _tmp: String?
          if (_stmt.isNull(_columnIndexOfStatus)) {
            _tmp = null
          } else {
            _tmp = _stmt.getText(_columnIndexOfStatus)
          }
          val _tmp_1: DownloadStatus? = __converters.toDownloadStatus(_tmp)
          if (_tmp_1 == null) {
            error("Expected NON-NULL 'com.example.podcastapp.core.database.DownloadStatus', but it was NULL.")
          } else {
            _tmpStatus = _tmp_1
          }
          val _tmpProgress: Int
          _tmpProgress = _stmt.getLong(_columnIndexOfProgress).toInt()
          val _tmpTotalBytes: Long?
          if (_stmt.isNull(_columnIndexOfTotalBytes)) {
            _tmpTotalBytes = null
          } else {
            _tmpTotalBytes = _stmt.getLong(_columnIndexOfTotalBytes)
          }
          val _tmpDownloadedBytes: Long
          _tmpDownloadedBytes = _stmt.getLong(_columnIndexOfDownloadedBytes)
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          _item = DownloadEntity(_tmpId,_tmpEpisodeId,_tmpDownloadManagerId,_tmpLocalPath,_tmpStatus,_tmpProgress,_tmpTotalBytes,_tmpDownloadedBytes,_tmpCreatedAt,_tmpUpdatedAt)
          _result.add(_item)
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
