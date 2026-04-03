package com.example.podcastapp.di;

import com.example.podcastapp.core.database.AppDatabase;
import com.example.podcastapp.core.database.PodcastDao;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

@ScopeMetadata
@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava",
    "cast",
    "deprecation",
    "nullness:initialization.field.uninitialized"
})
public final class AppModule_ProvidePodcastDaoFactory implements Factory<PodcastDao> {
  private final Provider<AppDatabase> dbProvider;

  private AppModule_ProvidePodcastDaoFactory(Provider<AppDatabase> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public PodcastDao get() {
    return providePodcastDao(dbProvider.get());
  }

  public static AppModule_ProvidePodcastDaoFactory create(Provider<AppDatabase> dbProvider) {
    return new AppModule_ProvidePodcastDaoFactory(dbProvider);
  }

  public static PodcastDao providePodcastDao(AppDatabase db) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.providePodcastDao(db));
  }
}
