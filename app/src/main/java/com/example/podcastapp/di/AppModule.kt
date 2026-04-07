package com.example.podcastapp.di

import android.content.Context
import androidx.room.Room
import com.example.podcastapp.core.data.DownloadRepository
import com.example.podcastapp.core.data.DownloadRepositoryImpl
import com.example.podcastapp.core.data.EpisodeRepository
import com.example.podcastapp.core.data.EpisodeRepositoryImpl
import com.example.podcastapp.core.data.PodcastRepository
import com.example.podcastapp.core.data.PodcastRepositoryImpl
import com.example.podcastapp.core.data.WaveformRepository
import com.example.podcastapp.core.data.WaveformRepositoryImpl
import com.example.podcastapp.core.database.AppDatabase
import com.example.podcastapp.core.database.DownloadDao
import com.example.podcastapp.core.database.EpisodeDao
import com.example.podcastapp.core.database.PodcastDao
import com.example.podcastapp.core.database.SubscriptionDao
import com.example.podcastapp.core.database.WaveformDao
import com.example.podcastapp.core.media.WaveformGenerator
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
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides fun providePodcastDao(db: AppDatabase): PodcastDao = db.podcastDao()
    @Provides fun provideEpisodeDao(db: AppDatabase): EpisodeDao = db.episodeDao()
    @Provides fun provideSubscriptionDao(db: AppDatabase): SubscriptionDao = db.subscriptionDao()
    @Provides fun provideDownloadDao(db: AppDatabase): DownloadDao = db.downloadDao()
    @Provides fun provideWaveformDao(db: AppDatabase): WaveformDao = db.waveformDao()

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
    fun provideWaveformGenerator(
        @ApplicationContext context: Context,
    ): WaveformGenerator = WaveformGenerator(context)
}
