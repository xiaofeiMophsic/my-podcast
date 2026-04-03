package com.example.podcastapp.di;

import com.example.podcastapp.core.database.AppDatabase;
import com.example.podcastapp.core.database.EpisodeDao;
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
public final class AppModule_ProvideEpisodeDaoFactory implements Factory<EpisodeDao> {
  private final Provider<AppDatabase> dbProvider;

  private AppModule_ProvideEpisodeDaoFactory(Provider<AppDatabase> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public EpisodeDao get() {
    return provideEpisodeDao(dbProvider.get());
  }

  public static AppModule_ProvideEpisodeDaoFactory create(Provider<AppDatabase> dbProvider) {
    return new AppModule_ProvideEpisodeDaoFactory(dbProvider);
  }

  public static EpisodeDao provideEpisodeDao(AppDatabase db) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideEpisodeDao(db));
  }
}
