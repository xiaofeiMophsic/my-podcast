package com.example.podcastapp.di

import android.app.DownloadManager
import android.content.Context
import com.example.podcastapp.core.data.DownloadController
import com.example.podcastapp.core.data.DownloadRepository
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
    fun provideDownloadManager(@ApplicationContext context: Context): DownloadManager {
        return context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
    }

    @Provides
    @Singleton
    fun provideDownloadController(
        @ApplicationContext context: Context,
        downloadManager: DownloadManager,
        downloadRepository: DownloadRepository,
    ): DownloadController {
        return DownloadController(context, downloadManager, downloadRepository)
    }
}
