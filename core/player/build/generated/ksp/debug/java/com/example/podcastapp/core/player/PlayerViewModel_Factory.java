package com.example.podcastapp.core.player;

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
public final class PlayerViewModel_Factory implements Factory<PlayerViewModel> {
  private final Provider<PlayerController> controllerProvider;

  private PlayerViewModel_Factory(Provider<PlayerController> controllerProvider) {
    this.controllerProvider = controllerProvider;
  }

  @Override
  public PlayerViewModel get() {
    return newInstance(controllerProvider.get());
  }

  public static PlayerViewModel_Factory create(Provider<PlayerController> controllerProvider) {
    return new PlayerViewModel_Factory(controllerProvider);
  }

  public static PlayerViewModel newInstance(PlayerController controller) {
    return new PlayerViewModel(controller);
  }
}
