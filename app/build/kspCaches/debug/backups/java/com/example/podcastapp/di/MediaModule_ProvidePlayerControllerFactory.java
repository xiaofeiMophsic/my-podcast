package com.example.podcastapp.di;

import android.content.Context;
import com.example.podcastapp.core.media.PlayerController;
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
public final class MediaModule_ProvidePlayerControllerFactory implements Factory<PlayerController> {
  private final Provider<Context> contextProvider;

  private MediaModule_ProvidePlayerControllerFactory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public PlayerController get() {
    return providePlayerController(contextProvider.get());
  }

  public static MediaModule_ProvidePlayerControllerFactory create(
      Provider<Context> contextProvider) {
    return new MediaModule_ProvidePlayerControllerFactory(contextProvider);
  }

  public static PlayerController providePlayerController(Context context) {
    return Preconditions.checkNotNullFromProvides(MediaModule.INSTANCE.providePlayerController(context));
  }
}
