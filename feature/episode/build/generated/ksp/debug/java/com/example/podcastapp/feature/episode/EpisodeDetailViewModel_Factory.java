package com.example.podcastapp.feature.episode;

import androidx.lifecycle.SavedStateHandle;
import com.example.podcastapp.core.data.DownloadController;
import com.example.podcastapp.core.data.DownloadRepository;
import com.example.podcastapp.core.data.EpisodeRepository;
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
public final class EpisodeDetailViewModel_Factory implements Factory<EpisodeDetailViewModel> {
  private final Provider<SavedStateHandle> savedStateHandleProvider;

  private final Provider<EpisodeRepository> episodeRepositoryProvider;

  private final Provider<DownloadController> downloadControllerProvider;

  private final Provider<DownloadRepository> downloadRepositoryProvider;

  private final Provider<PlayerController> playerControllerProvider;

  private EpisodeDetailViewModel_Factory(Provider<SavedStateHandle> savedStateHandleProvider,
      Provider<EpisodeRepository> episodeRepositoryProvider,
      Provider<DownloadController> downloadControllerProvider,
      Provider<DownloadRepository> downloadRepositoryProvider,
      Provider<PlayerController> playerControllerProvider) {
    this.savedStateHandleProvider = savedStateHandleProvider;
    this.episodeRepositoryProvider = episodeRepositoryProvider;
    this.downloadControllerProvider = downloadControllerProvider;
    this.downloadRepositoryProvider = downloadRepositoryProvider;
    this.playerControllerProvider = playerControllerProvider;
  }

  @Override
  public EpisodeDetailViewModel get() {
    return newInstance(savedStateHandleProvider.get(), episodeRepositoryProvider.get(), downloadControllerProvider.get(), downloadRepositoryProvider.get(), playerControllerProvider.get());
  }

  public static EpisodeDetailViewModel_Factory create(
      Provider<SavedStateHandle> savedStateHandleProvider,
      Provider<EpisodeRepository> episodeRepositoryProvider,
      Provider<DownloadController> downloadControllerProvider,
      Provider<DownloadRepository> downloadRepositoryProvider,
      Provider<PlayerController> playerControllerProvider) {
    return new EpisodeDetailViewModel_Factory(savedStateHandleProvider, episodeRepositoryProvider, downloadControllerProvider, downloadRepositoryProvider, playerControllerProvider);
  }

  public static EpisodeDetailViewModel newInstance(SavedStateHandle savedStateHandle,
      EpisodeRepository episodeRepository, DownloadController downloadController,
      DownloadRepository downloadRepository, PlayerController playerController) {
    return new EpisodeDetailViewModel(savedStateHandle, episodeRepository, downloadController, downloadRepository, playerController);
  }
}
