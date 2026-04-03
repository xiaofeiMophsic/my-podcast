package com.example.podcastapp.feature.episode;

import com.example.podcastapp.core.data.EpisodeRepository;
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
public final class SearchViewModel_Factory implements Factory<SearchViewModel> {
  private final Provider<EpisodeRepository> episodeRepositoryProvider;

  private SearchViewModel_Factory(Provider<EpisodeRepository> episodeRepositoryProvider) {
    this.episodeRepositoryProvider = episodeRepositoryProvider;
  }

  @Override
  public SearchViewModel get() {
    return newInstance(episodeRepositoryProvider.get());
  }

  public static SearchViewModel_Factory create(
      Provider<EpisodeRepository> episodeRepositoryProvider) {
    return new SearchViewModel_Factory(episodeRepositoryProvider);
  }

  public static SearchViewModel newInstance(EpisodeRepository episodeRepository) {
    return new SearchViewModel(episodeRepository);
  }
}
