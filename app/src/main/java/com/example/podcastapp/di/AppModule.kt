package com.example.podcastapp.di

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.work.WorkManager
import com.example.podcastapp.core.data.DownloadRepository
import com.example.podcastapp.core.data.DownloadRepositoryImpl
import com.example.podcastapp.core.data.EpisodeRepository
import com.example.podcastapp.core.data.EpisodeRepositoryImpl
import com.example.podcastapp.core.data.PodcastRepository
import com.example.podcastapp.core.data.PodcastRepositoryImpl
import com.example.podcastapp.core.data.SearchHistoryRepository
import com.example.podcastapp.core.data.SearchHistoryRepositoryImpl
import com.example.podcastapp.core.data.WaveformRepository
import com.example.podcastapp.core.data.WaveformRepositoryImpl
import com.example.podcastapp.core.database.AppDatabase
import com.example.podcastapp.core.database.DownloadDao
import com.example.podcastapp.core.database.EpisodeDao
import com.example.podcastapp.core.database.PodcastDao
import com.example.podcastapp.core.database.SearchHistoryDao
import com.example.podcastapp.core.database.SubscriptionDao
import com.example.podcastapp.core.database.WaveformDao
import com.example.podcastapp.core.audioprocessing.WaveformGenerator
import com.example.podcastapp.core.network.RssFetcher
import com.example.podcastapp.core.network.RssParser
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient = OkHttpClient.Builder().build()

    @Provides
    @Singleton
    fun provideRssParser(): RssParser = RssParser()

    @Provides
    @Singleton
    fun provideRssFetcher(
        client: OkHttpClient,
        parser: RssParser,
    ): RssFetcher = RssFetcher(client, parser)

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(context, AppDatabase::class.java, "podcast.db")
            .addMigrations(MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5, MIGRATION_5_6)
            .build()
    }

    @Provides fun providePodcastDao(db: AppDatabase): PodcastDao = db.podcastDao()
    @Provides fun provideEpisodeDao(db: AppDatabase): EpisodeDao = db.episodeDao()
    @Provides fun provideSubscriptionDao(db: AppDatabase): SubscriptionDao = db.subscriptionDao()
    @Provides fun provideDownloadDao(db: AppDatabase): DownloadDao = db.downloadDao()
    @Provides fun provideWaveformDao(db: AppDatabase): WaveformDao = db.waveformDao()
    @Provides fun provideSearchHistoryDao(db: AppDatabase): SearchHistoryDao = db.searchHistoryDao()

    @Provides
    @Singleton
    fun providePodcastRepository(
        podcastDao: PodcastDao,
        subscriptionDao: SubscriptionDao,
        rssFetcher: RssFetcher,
    ): PodcastRepository = PodcastRepositoryImpl(podcastDao, subscriptionDao, rssFetcher)

    @Provides
    @Singleton
    fun provideEpisodeRepository(
        podcastDao: PodcastDao,
        episodeDao: EpisodeDao,
        rssFetcher: RssFetcher,
    ): EpisodeRepository = EpisodeRepositoryImpl(podcastDao, episodeDao, rssFetcher)

    @Provides
    @Singleton
    fun provideDownloadRepository(
        downloadDao: DownloadDao,
    ): DownloadRepository = DownloadRepositoryImpl(downloadDao)

    @Provides
    @Singleton
    fun provideWaveformRepository(
        waveformDao: WaveformDao,
    ): WaveformRepository = WaveformRepositoryImpl(waveformDao)

    @Provides
    @Singleton
    fun provideSearchHistoryRepository(
        searchHistoryDao: SearchHistoryDao,
    ): SearchHistoryRepository = SearchHistoryRepositoryImpl(searchHistoryDao)

    @Provides
    @Singleton
    fun provideWaveformGenerator(
        @ApplicationContext context: Context,
    ): WaveformGenerator = WaveformGenerator(context)

    @Provides
    @Singleton
    fun provideWorkManager(@ApplicationContext context: Context): WorkManager {
        return WorkManager.getInstance(context)
    }

    private val MIGRATION_2_3 = object : Migration(2, 3) {
        override fun migrate(db: SupportSQLiteDatabase) {
            // No schema changes between v2 and v3; version bump only.
        }
    }

    private val MIGRATION_3_4 = object : Migration(3, 4) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(
                "CREATE TABLE IF NOT EXISTS `search_history` (" +
                    "`normalized` TEXT NOT NULL, " +
                    "`query` TEXT NOT NULL, " +
                    "`updatedAt` INTEGER NOT NULL, " +
                    "PRIMARY KEY(`normalized`)" +
                ")"
            )
            db.execSQL(
                "CREATE INDEX IF NOT EXISTS `index_search_history_updatedAt` " +
                    "ON `search_history` (`updatedAt`)"
            )
        }
    }

    private val MIGRATION_4_5 = object : Migration(4, 5) {
        override fun migrate(db: SupportSQLiteDatabase) {
            // Add author column to episodes table
            db.execSQL("ALTER TABLE episodes ADD COLUMN `author` TEXT")
        }
    }

    private val MIGRATION_5_6 = object : Migration(5, 6) {
        override fun migrate(db: SupportSQLiteDatabase) {
            // Drop old table with CSV format and recreate with BLOB
            // This clears all old waveform data as requested
            db.execSQL("DROP TABLE IF EXISTS episode_waveforms")
            db.execSQL(
                "CREATE TABLE episode_waveforms (" +
                    "episodeId INTEGER NOT NULL PRIMARY KEY, " +
                    "barsBlob BLOB NOT NULL, " +
                    "updatedAt INTEGER NOT NULL" +
                    ")"
            )
        }
    }
}
