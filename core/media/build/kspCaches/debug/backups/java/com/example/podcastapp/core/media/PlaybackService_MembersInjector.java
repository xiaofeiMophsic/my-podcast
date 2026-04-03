package com.example.podcastapp.core.media;

import dagger.MembersInjector;
import dagger.internal.DaggerGenerated;
import dagger.internal.InjectedFieldSignature;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import javax.annotation.processing.Generated;

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
public final class PlaybackService_MembersInjector implements MembersInjector<PlaybackService> {
  private final Provider<PlayerController> playerControllerProvider;

  private PlaybackService_MembersInjector(Provider<PlayerController> playerControllerProvider) {
    this.playerControllerProvider = playerControllerProvider;
  }

  @Override
  public void injectMembers(PlaybackService instance) {
    injectPlayerController(instance, playerControllerProvider.get());
  }

  public static MembersInjector<PlaybackService> create(
      Provider<PlayerController> playerControllerProvider) {
    return new PlaybackService_MembersInjector(playerControllerProvider);
  }

  @InjectedFieldSignature("com.example.podcastapp.core.media.PlaybackService.playerController")
  public static void injectPlayerController(PlaybackService instance,
      PlayerController playerController) {
    instance.playerController = playerController;
  }
}
