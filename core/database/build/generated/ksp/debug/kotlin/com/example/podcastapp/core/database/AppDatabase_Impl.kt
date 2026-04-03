package com.example.podcastapp.core.database

import androidx.room.InvalidationTracker
import androidx.room.RoomOpenDelegate
import androidx.room.migration.AutoMigrationSpec
import androidx.room.migration.Migration
import androidx.room.util.TableInfo
import androidx.room.util.TableInfo.Companion.read
import androidx.room.util.dropFtsSyncTriggers
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL
import javax.`annotation`.processing.Generated
import kotlin.Lazy
import kotlin.String
import kotlin.Suppress
import kotlin.collections.List
import kotlin.collections.Map
import kotlin.collections.MutableList
import kotlin.collections.MutableMap
import kotlin.collections.MutableSet
import kotlin.collections.Set
import kotlin.collections.mutableListOf
import kotlin.collections.mutableMapOf
import kotlin.collections.mutableSetOf
import kotlin.reflect.KClass

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class AppDatabase_Impl : AppDatabase() {
  private val _podcastDao: Lazy<PodcastDao> = lazy {
    PodcastDao_Impl(this)
  }

  private val _episodeDao: Lazy<EpisodeDao> = lazy {
    EpisodeDao_Impl(this)
  }

  private val _subscriptionDao: Lazy<SubscriptionDao> = lazy {
    SubscriptionDao_Impl(this)
  }

  private val _downloadDao: Lazy<DownloadDao> = lazy {
    DownloadDao_Impl(this)
  }

  protected override fun createOpenDelegate(): RoomOpenDelegate {
    val _openDelegate: RoomOpenDelegate = object : RoomOpenDelegate(1, "da12bf3262afec477aeaf291149c3774", "df6dc7aeecd38e26aa29d3e491ff007f") {
      public override fun createAllTables(connection: SQLiteConnection) {
        connection.execSQL("CREATE TABLE IF NOT EXISTS `podcasts` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `feedUrl` TEXT NOT NULL, `title` TEXT NOT NULL, `description` TEXT, `imageUrl` TEXT, `author` TEXT, `language` TEXT, `lastBuildDate` INTEGER, `updatedAt` INTEGER NOT NULL)")
        connection.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_podcasts_feedUrl` ON `podcasts` (`feedUrl`)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `episodes` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `podcastId` INTEGER NOT NULL, `guid` TEXT NOT NULL, `title` TEXT NOT NULL, `description` TEXT, `audioUrl` TEXT NOT NULL, `imageUrl` TEXT, `durationSeconds` INTEGER, `pubDate` INTEGER, `isPlayed` INTEGER NOT NULL, `updatedAt` INTEGER NOT NULL)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_episodes_podcastId` ON `episodes` (`podcastId`)")
        connection.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_episodes_guid` ON `episodes` (`guid`)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_episodes_pubDate` ON `episodes` (`pubDate`)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `subscriptions` (`podcastId` INTEGER NOT NULL, `subscribedAt` INTEGER NOT NULL, PRIMARY KEY(`podcastId`))")
        connection.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_subscriptions_podcastId` ON `subscriptions` (`podcastId`)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `downloads` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `episodeId` INTEGER NOT NULL, `downloadManagerId` INTEGER, `localPath` TEXT, `status` TEXT NOT NULL, `progress` INTEGER NOT NULL, `totalBytes` INTEGER, `downloadedBytes` INTEGER NOT NULL, `createdAt` INTEGER NOT NULL, `updatedAt` INTEGER NOT NULL)")
        connection.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_downloads_episodeId` ON `downloads` (`episodeId`)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)")
        connection.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, 'da12bf3262afec477aeaf291149c3774')")
      }

      public override fun dropAllTables(connection: SQLiteConnection) {
        connection.execSQL("DROP TABLE IF EXISTS `podcasts`")
        connection.execSQL("DROP TABLE IF EXISTS `episodes`")
        connection.execSQL("DROP TABLE IF EXISTS `subscriptions`")
        connection.execSQL("DROP TABLE IF EXISTS `downloads`")
      }

      public override fun onCreate(connection: SQLiteConnection) {
      }

      public override fun onOpen(connection: SQLiteConnection) {
        internalInitInvalidationTracker(connection)
      }

      public override fun onPreMigrate(connection: SQLiteConnection) {
        dropFtsSyncTriggers(connection)
      }

      public override fun onPostMigrate(connection: SQLiteConnection) {
      }

      public override fun onValidateSchema(connection: SQLiteConnection): RoomOpenDelegate.ValidationResult {
        val _columnsPodcasts: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsPodcasts.put("id", TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsPodcasts.put("feedUrl", TableInfo.Column("feedUrl", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsPodcasts.put("title", TableInfo.Column("title", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsPodcasts.put("description", TableInfo.Column("description", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsPodcasts.put("imageUrl", TableInfo.Column("imageUrl", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsPodcasts.put("author", TableInfo.Column("author", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsPodcasts.put("language", TableInfo.Column("language", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsPodcasts.put("lastBuildDate", TableInfo.Column("lastBuildDate", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsPodcasts.put("updatedAt", TableInfo.Column("updatedAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysPodcasts: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesPodcasts: MutableSet<TableInfo.Index> = mutableSetOf()
        _indicesPodcasts.add(TableInfo.Index("index_podcasts_feedUrl", true, listOf("feedUrl"), listOf("ASC")))
        val _infoPodcasts: TableInfo = TableInfo("podcasts", _columnsPodcasts, _foreignKeysPodcasts, _indicesPodcasts)
        val _existingPodcasts: TableInfo = read(connection, "podcasts")
        if (!_infoPodcasts.equals(_existingPodcasts)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |podcasts(com.example.podcastapp.core.database.PodcastEntity).
              | Expected:
              |""".trimMargin() + _infoPodcasts + """
              |
              | Found:
              |""".trimMargin() + _existingPodcasts)
        }
        val _columnsEpisodes: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsEpisodes.put("id", TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsEpisodes.put("podcastId", TableInfo.Column("podcastId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsEpisodes.put("guid", TableInfo.Column("guid", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsEpisodes.put("title", TableInfo.Column("title", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsEpisodes.put("description", TableInfo.Column("description", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsEpisodes.put("audioUrl", TableInfo.Column("audioUrl", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsEpisodes.put("imageUrl", TableInfo.Column("imageUrl", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsEpisodes.put("durationSeconds", TableInfo.Column("durationSeconds", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsEpisodes.put("pubDate", TableInfo.Column("pubDate", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsEpisodes.put("isPlayed", TableInfo.Column("isPlayed", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsEpisodes.put("updatedAt", TableInfo.Column("updatedAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysEpisodes: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesEpisodes: MutableSet<TableInfo.Index> = mutableSetOf()
        _indicesEpisodes.add(TableInfo.Index("index_episodes_podcastId", false, listOf("podcastId"), listOf("ASC")))
        _indicesEpisodes.add(TableInfo.Index("index_episodes_guid", true, listOf("guid"), listOf("ASC")))
        _indicesEpisodes.add(TableInfo.Index("index_episodes_pubDate", false, listOf("pubDate"), listOf("ASC")))
        val _infoEpisodes: TableInfo = TableInfo("episodes", _columnsEpisodes, _foreignKeysEpisodes, _indicesEpisodes)
        val _existingEpisodes: TableInfo = read(connection, "episodes")
        if (!_infoEpisodes.equals(_existingEpisodes)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |episodes(com.example.podcastapp.core.database.EpisodeEntity).
              | Expected:
              |""".trimMargin() + _infoEpisodes + """
              |
              | Found:
              |""".trimMargin() + _existingEpisodes)
        }
        val _columnsSubscriptions: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsSubscriptions.put("podcastId", TableInfo.Column("podcastId", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsSubscriptions.put("subscribedAt", TableInfo.Column("subscribedAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysSubscriptions: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesSubscriptions: MutableSet<TableInfo.Index> = mutableSetOf()
        _indicesSubscriptions.add(TableInfo.Index("index_subscriptions_podcastId", true, listOf("podcastId"), listOf("ASC")))
        val _infoSubscriptions: TableInfo = TableInfo("subscriptions", _columnsSubscriptions, _foreignKeysSubscriptions, _indicesSubscriptions)
        val _existingSubscriptions: TableInfo = read(connection, "subscriptions")
        if (!_infoSubscriptions.equals(_existingSubscriptions)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |subscriptions(com.example.podcastapp.core.database.SubscriptionEntity).
              | Expected:
              |""".trimMargin() + _infoSubscriptions + """
              |
              | Found:
              |""".trimMargin() + _existingSubscriptions)
        }
        val _columnsDownloads: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsDownloads.put("id", TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsDownloads.put("episodeId", TableInfo.Column("episodeId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsDownloads.put("downloadManagerId", TableInfo.Column("downloadManagerId", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsDownloads.put("localPath", TableInfo.Column("localPath", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsDownloads.put("status", TableInfo.Column("status", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsDownloads.put("progress", TableInfo.Column("progress", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsDownloads.put("totalBytes", TableInfo.Column("totalBytes", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsDownloads.put("downloadedBytes", TableInfo.Column("downloadedBytes", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsDownloads.put("createdAt", TableInfo.Column("createdAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsDownloads.put("updatedAt", TableInfo.Column("updatedAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysDownloads: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesDownloads: MutableSet<TableInfo.Index> = mutableSetOf()
        _indicesDownloads.add(TableInfo.Index("index_downloads_episodeId", true, listOf("episodeId"), listOf("ASC")))
        val _infoDownloads: TableInfo = TableInfo("downloads", _columnsDownloads, _foreignKeysDownloads, _indicesDownloads)
        val _existingDownloads: TableInfo = read(connection, "downloads")
        if (!_infoDownloads.equals(_existingDownloads)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |downloads(com.example.podcastapp.core.database.DownloadEntity).
              | Expected:
              |""".trimMargin() + _infoDownloads + """
              |
              | Found:
              |""".trimMargin() + _existingDownloads)
        }
        return RoomOpenDelegate.ValidationResult(true, null)
      }
    }
    return _openDelegate
  }

  protected override fun createInvalidationTracker(): InvalidationTracker {
    val _shadowTablesMap: MutableMap<String, String> = mutableMapOf()
    val _viewTables: MutableMap<String, Set<String>> = mutableMapOf()
    return InvalidationTracker(this, _shadowTablesMap, _viewTables, "podcasts", "episodes", "subscriptions", "downloads")
  }

  public override fun clearAllTables() {
    super.performClear(false, "podcasts", "episodes", "subscriptions", "downloads")
  }

  protected override fun getRequiredTypeConverterClasses(): Map<KClass<*>, List<KClass<*>>> {
    val _typeConvertersMap: MutableMap<KClass<*>, List<KClass<*>>> = mutableMapOf()
    _typeConvertersMap.put(PodcastDao::class, PodcastDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(EpisodeDao::class, EpisodeDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(SubscriptionDao::class, SubscriptionDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(DownloadDao::class, DownloadDao_Impl.getRequiredConverters())
    return _typeConvertersMap
  }

  public override fun getRequiredAutoMigrationSpecClasses(): Set<KClass<out AutoMigrationSpec>> {
    val _autoMigrationSpecsSet: MutableSet<KClass<out AutoMigrationSpec>> = mutableSetOf()
    return _autoMigrationSpecsSet
  }

  public override fun createAutoMigrations(autoMigrationSpecs: Map<KClass<out AutoMigrationSpec>, AutoMigrationSpec>): List<Migration> {
    val _autoMigrations: MutableList<Migration> = mutableListOf()
    return _autoMigrations
  }

  public override fun podcastDao(): PodcastDao = _podcastDao.value

  public override fun episodeDao(): EpisodeDao = _episodeDao.value

  public override fun subscriptionDao(): SubscriptionDao = _subscriptionDao.value

  public override fun downloadDao(): DownloadDao = _downloadDao.value
}
