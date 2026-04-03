package com.example.podcastapp.feature.download;

import com.example.podcastapp.core.data.DownloadRepository;
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
public final class DownloadViewModel_Factory implements Factory<DownloadViewModel> {
  private final Provider<DownloadRepository> downloadRepositoryProvider;

  private DownloadViewModel_Factory(Provider<DownloadRepository> downloadRepositoryProvider) {
    this.downloadRepositoryProvider = downloadRepositoryProvider;
  }

  @Override
  public DownloadViewModel get() {
    return newInstance(downloadRepositoryProvider.get());
  }

  public static DownloadViewModel_Factory create(
      Provider<DownloadRepository> downloadRepositoryProvider) {
    return new DownloadViewModel_Factory(downloadRepositoryProvider);
  }

  public static DownloadViewModel newInstance(DownloadRepository downloadRepository) {
    return new DownloadViewModel(downloadRepository);
  }
}
