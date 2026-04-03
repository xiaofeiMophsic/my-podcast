package com.example.podcastapp.feature.podcast;

import com.example.podcastapp.core.data.EpisodeRepository;
import com.example.podcastapp.core.data.PodcastRepository;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
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
public final class PodcastListViewModel_Factory implements Factory<PodcastListViewModel> {
  private final Provider<PodcastRepository> podcastRepositoryProvider;

  private final Provider<EpisodeRepository> episodeRepositoryProvider;

  private PodcastListViewModel_Factory(Provider<PodcastRepository> podcastRepositoryProvider,
      Provider<EpisodeRepository> episodeRepositoryProvider) {
    this.podcastRepositoryProvider = podcastRepositoryProvider;
    this.episodeRepositoryProvider = episodeRepositoryProvider;
  }

  @Override
  public PodcastListViewModel get() {
    return newInstance(podcastRepositoryProvider.get(), episodeRepositoryProvider.get());
  }

  public static PodcastListViewModel_Factory create(
      Provider<PodcastRepository> podcastRepositoryProvider,
      Provider<EpisodeRepository> episodeRepositoryProvider) {
    return new PodcastListViewModel_Factory(podcastRepositoryProvider, episodeRepositoryProvider);
  }

  public static PodcastListViewModel newInstance(PodcastRepository podcastRepository,
      EpisodeRepository episodeRepository) {
    return new PodcastListViewModel(podcastRepository, episodeRepository);
  }
}
