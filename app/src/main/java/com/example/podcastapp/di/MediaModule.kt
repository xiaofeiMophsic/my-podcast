package com.example.podcastapp.di

import android.content.Context
import com.example.podcastapp.core.media.PlayerController
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object MediaModule {

    @Provides
    @Singleton
    fun providePlayerController(@ApplicationContext context: Context): PlayerController {
        return PlayerController(context)
    }
}
