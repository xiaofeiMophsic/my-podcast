package com.example.podcastapp.di;

import android.app.DownloadManager;
import android.content.Context;
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
public final class DownloadModule_ProvideDownloadManagerFactory implements Factory<DownloadManager> {
  private final Provider<Context> contextProvider;

  private DownloadModule_ProvideDownloadManagerFactory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public DownloadManager get() {
    return provideDownloadManager(contextProvider.get());
  }

  public static DownloadModule_ProvideDownloadManagerFactory create(
      Provider<Context> contextProvider) {
    return new DownloadModule_ProvideDownloadManagerFactory(contextProvider);
  }

  public static DownloadManager provideDownloadManager(Context context) {
    return Preconditions.checkNotNullFromProvides(DownloadModule.INSTANCE.provideDownloadManager(context));
  }
}
