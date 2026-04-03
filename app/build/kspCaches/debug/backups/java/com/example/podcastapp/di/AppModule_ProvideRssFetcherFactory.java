package com.example.podcastapp.di;

import com.example.podcastapp.core.network.RssFetcher;
import com.example.podcastapp.core.network.RssParser;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import okhttp3.OkHttpClient;

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
public final class AppModule_ProvideRssFetcherFactory implements Factory<RssFetcher> {
  private final Provider<OkHttpClient> clientProvider;

  private final Provider<RssParser> parserProvider;

  private AppModule_ProvideRssFetcherFactory(Provider<OkHttpClient> clientProvider,
      Provider<RssParser> parserProvider) {
    this.clientProvider = clientProvider;
    this.parserProvider = parserProvider;
  }

  @Override
  public RssFetcher get() {
    return provideRssFetcher(clientProvider.get(), parserProvider.get());
  }

  public static AppModule_ProvideRssFetcherFactory create(Provider<OkHttpClient> clientProvider,
      Provider<RssParser> parserProvider) {
    return new AppModule_ProvideRssFetcherFactory(clientProvider, parserProvider);
  }

  public static RssFetcher provideRssFetcher(OkHttpClient client, RssParser parser) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideRssFetcher(client, parser));
  }
}
