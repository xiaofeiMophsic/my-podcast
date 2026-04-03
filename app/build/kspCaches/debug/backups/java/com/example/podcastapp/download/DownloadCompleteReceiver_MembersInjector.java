package com.example.podcastapp.download;

import com.example.podcastapp.core.data.DownloadController;
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
public final class DownloadCompleteReceiver_MembersInjector implements MembersInjector<DownloadCompleteReceiver> {
  private final Provider<DownloadController> downloadControllerProvider;

  private DownloadCompleteReceiver_MembersInjector(
      Provider<DownloadController> downloadControllerProvider) {
    this.downloadControllerProvider = downloadControllerProvider;
  }

  @Override
  public void injectMembers(DownloadCompleteReceiver instance) {
    injectDownloadController(instance, downloadControllerProvider.get());
  }

  public static MembersInjector<DownloadCompleteReceiver> create(
      Provider<DownloadController> downloadControllerProvider) {
    return new DownloadCompleteReceiver_MembersInjector(downloadControllerProvider);
  }

  @InjectedFieldSignature("com.example.podcastapp.download.DownloadCompleteReceiver.downloadController")
  public static void injectDownloadController(DownloadCompleteReceiver instance,
      DownloadController downloadController) {
    instance.downloadController = downloadController;
  }
}
