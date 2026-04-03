package com.example.podcastapp.di;

import com.example.podcastapp.core.data.DownloadRepository;
import com.example.podcastapp.core.database.DownloadDao;
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
public final class AppModule_ProvideDownloadRepositoryFactory implements Factory<DownloadRepository> {
  private final Provider<DownloadDao> downloadDaoProvider;

  private AppModule_ProvideDownloadRepositoryFactory(Provider<DownloadDao> downloadDaoProvider) {
    this.downloadDaoProvider = downloadDaoProvider;
  }

  @Override
  public DownloadRepository get() {
    return provideDownloadRepository(downloadDaoProvider.get());
  }

  public static AppModule_ProvideDownloadRepositoryFactory create(
      Provider<DownloadDao> downloadDaoProvider) {
    return new AppModule_ProvideDownloadRepositoryFactory(downloadDaoProvider);
  }

  public static DownloadRepository provideDownloadRepository(DownloadDao downloadDao) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideDownloadRepository(downloadDao));
  }
}
