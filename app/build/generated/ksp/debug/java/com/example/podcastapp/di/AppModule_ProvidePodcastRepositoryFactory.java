package com.example.podcastapp.di;

import com.example.podcastapp.core.data.PodcastRepository;
import com.example.podcastapp.core.database.PodcastDao;
import com.example.podcastapp.core.database.SubscriptionDao;
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
public final class AppModule_ProvidePodcastRepositoryFactory implements Factory<PodcastRepository> {
  private final Provider<PodcastDao> podcastDaoProvider;

  private final Provider<SubscriptionDao> subscriptionDaoProvider;

  private final Provider<RssFetcher> rssFetcherProvider;

  private AppModule_ProvidePodcastRepositoryFactory(Provider<PodcastDao> podcastDaoProvider,
      Provider<SubscriptionDao> subscriptionDaoProvider, Provider<RssFetcher> rssFetcherProvider) {
    this.podcastDaoProvider = podcastDaoProvider;
    this.subscriptionDaoProvider = subscriptionDaoProvider;
    this.rssFetcherProvider = rssFetcherProvider;
  }

  @Override
  public PodcastRepository get() {
    return providePodcastRepository(podcastDaoProvider.get(), subscriptionDaoProvider.get(), rssFetcherProvider.get());
  }

  public static AppModule_ProvidePodcastRepositoryFactory create(
      Provider<PodcastDao> podcastDaoProvider, Provider<SubscriptionDao> subscriptionDaoProvider,
      Provider<RssFetcher> rssFetcherProvider) {
    return new AppModule_ProvidePodcastRepositoryFactory(podcastDaoProvider, subscriptionDaoProvider, rssFetcherProvider);
  }

  public static PodcastRepository providePodcastRepository(PodcastDao podcastDao,
      SubscriptionDao subscriptionDao, RssFetcher rssFetcher) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.providePodcastRepository(podcastDao, subscriptionDao, rssFetcher));
  }
}
