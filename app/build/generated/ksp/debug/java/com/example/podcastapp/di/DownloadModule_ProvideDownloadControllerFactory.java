package com.example.podcastapp.di;

import android.app.DownloadManager;
import android.content.Context;
import com.example.podcastapp.core.data.DownloadController;
import com.example.podcastapp.core.data.DownloadRepository;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata("dagger.hilt.android.qualifiers.ApplicationContext")
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
public final class DownloadModule_ProvideDownloadControllerFactory implements Factory<DownloadController> {
  private final Provider<Context> contextProvider;

  private final Provider<DownloadManager> downloadManagerProvider;

  private final Provider<DownloadRepository> downloadRepositoryProvider;

  private DownloadModule_ProvideDownloadControllerFactory(Provider<Context> contextProvider,
      Provider<DownloadManager> downloadManagerProvider,
      Provider<DownloadRepository> downloadRepositoryProvider) {
    this.contextProvider = contextProvider;
    this.downloadManagerProvider = downloadManagerProvider;
    this.downloadRepositoryProvider = downloadRepositoryProvider;
  }

  @Override
  public DownloadController get() {
    return provideDownloadController(contextProvider.get(), downloadManagerProvider.get(), downloadRepositoryProvider.get());
  }

  public static DownloadModule_ProvideDownloadControllerFactory create(
      Provider<Context> contextProvider, Provider<DownloadManager> downloadManagerProvider,
      Provider<DownloadRepository> downloadRepositoryProvider) {
    return new DownloadModule_ProvideDownloadControllerFactory(contextProvider, downloadManagerProvider, downloadRepositoryProvider);
  }

  public static DownloadController provideDownloadController(Context context,
      DownloadManager downloadManager, DownloadRepository downloadRepository) {
    return Preconditions.checkNotNullFromProvides(DownloadModule.INSTANCE.provideDownloadController(context, downloadManager, downloadRepository));
  }
}
