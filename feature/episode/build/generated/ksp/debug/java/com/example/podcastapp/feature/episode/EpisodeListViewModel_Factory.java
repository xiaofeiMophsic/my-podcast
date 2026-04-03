package com.example.podcastapp.feature.episode;

import androidx.lifecycle.SavedStateHandle;
import com.example.podcastapp.core.data.DownloadRepository;
import com.example.podcastapp.core.data.EpisodeRepository;
import com.example.podcastapp.core.data.PodcastRepository;
import com.example.podcastapp.core.media.PlayerController;
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
public final class EpisodeListViewModel_Factory implements Factory<EpisodeListViewModel> {
  private final Provider<SavedStateHandle> savedStateHandleProvider;

  private final Provider<EpisodeRepository> episodeRepositoryProvider;

  private final Provider<PodcastRepository> podcastRepositoryProvider;

  private final Provider<DownloadRepository> downloadRepositoryProvider;

  private final Provider<PlayerController> playerControllerProvider;

  private EpisodeListViewModel_Factory(Provider<SavedStateHandle> savedStateHandleProvider,
      Provider<EpisodeRepository> episodeRepositoryProvider,
      Provider<PodcastRepository> podcastRepositoryProvider,
      Provider<DownloadRepository> downloadRepositoryProvider,
      Provider<PlayerController> playerControllerProvider) {
    this.savedStateHandleProvider = savedStateHandleProvider;
    this.episodeRepositoryProvider = episodeRepositoryProvider;
    this.podcastRepositoryProvider = podcastRepositoryProvider;
    this.downloadRepositoryProvider = downloadRepositoryProvider;
    this.playerControllerProvider = playerControllerProvider;
  }

  @Override
  public EpisodeListViewModel get() {
    return newInstance(savedStateHandleProvider.get(), episodeRepositoryProvider.get(), podcastRepositoryProvider.get(), downloadRepositoryProvider.get(), playerControllerProvider.get());
  }

  public static EpisodeListViewModel_Factory create(
      Provider<SavedStateHandle> savedStateHandleProvider,
      Provider<EpisodeRepository> episodeRepositoryProvider,
      Provider<PodcastRepository> podcastRepositoryProvider,
      Provider<DownloadRepository> downloadRepositoryProvider,
      Provider<PlayerController> playerControllerProvider) {
    return new EpisodeListViewModel_Factory(savedStateHandleProvider, episodeRepositoryProvider, podcastRepositoryProvider, downloadRepositoryProvider, playerControllerProvider);
  }

  public static EpisodeListViewModel newInstance(SavedStateHandle savedStateHandle,
      EpisodeRepository episodeRepository, PodcastRepository podcastRepository,
      DownloadRepository downloadRepository, PlayerController playerController) {
    return new EpisodeListViewModel(savedStateHandle, episodeRepository, podcastRepository, downloadRepository, playerController);
  }
}
