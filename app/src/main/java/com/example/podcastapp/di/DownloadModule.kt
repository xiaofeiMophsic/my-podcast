package com.example.podcastapp.di

import android.app.DownloadManager
import android.content.Context
import androidx.work.WorkManager
import com.example.podcastapp.core.data.DownloadRepository
import com.example.podcastapp.core.data.WaveformRepository
import com.example.podcastapp.core.data.download.DownloadController2
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DownloadModule {

    @Provides
    @Singleton
    fun provideDownloadController2(
        workManager: WorkManager,
        downloadRepository: DownloadRepository,
    ): DownloadController2 {
        return DownloadController2(workManager, downloadRepository)
    }
}
