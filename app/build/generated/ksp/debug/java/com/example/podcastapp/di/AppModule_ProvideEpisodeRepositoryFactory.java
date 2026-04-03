package com.example.podcastapp.di;

import com.example.podcastapp.core.data.EpisodeRepository;
import com.example.podcastapp.core.database.EpisodeDao;
import com.example.podcastapp.core.database.PodcastDao;
import com.example.podcastapp.core.network.RssFetcher;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

@ScopeMetadata("javax.inject.Singleton")
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
public final class AppModule_ProvideEpisodeRepositoryFactory implements Factory<EpisodeRepository> {
  private final Provider<PodcastDao> podcastDaoProvider;

  private final Provider<EpisodeDao> episodeDaoProvider;

  private final Provider<RssFetcher> rssFetcherProvider;

  private AppModule_ProvideEpisodeRepositoryFactory(Provider<PodcastDao> podcastDaoProvider,
      Provider<EpisodeDao> episodeDaoProvider, Provider<RssFetcher> rssFetcherProvider) {
    this.podcastDaoProvider = podcastDaoProvider;
    this.episodeDaoProvider = episodeDaoProvider;
    this.rssFetcherProvider = rssFetcherProvider;
  }

  @Override
  public EpisodeRepository get() {
    return provideEpisodeRepository(podcastDaoProvider.get(), episodeDaoProvider.get(), rssFetcherProvider.get());
  }

  public static AppModule_ProvideEpisodeRepositoryFactory create(
      Provider<PodcastDao> podcastDaoProvider, Provider<EpisodeDao> episodeDaoProvider,
      Provider<RssFetcher> rssFetcherProvider) {
    return new AppModule_ProvideEpisodeRepositoryFactory(podcastDaoProvider, episodeDaoProvider, rssFetcherProvider);
  }

  public static EpisodeRepository provideEpisodeRepository(PodcastDao podcastDao,
      EpisodeDao episodeDao, RssFetcher rssFetcher) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideEpisodeRepository(podcastDao, episodeDao, rssFetcher));
  }
}
