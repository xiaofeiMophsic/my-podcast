package com.example.podcastapp;

import android.app.Activity;
import android.app.DownloadManager;
import android.app.Service;
import android.view.View;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;
import com.example.podcastapp.core.data.DownloadController;
import com.example.podcastapp.core.data.DownloadRepository;
import com.example.podcastapp.core.data.EpisodeRepository;
import com.example.podcastapp.core.data.PodcastRepository;
import com.example.podcastapp.core.database.AppDatabase;
import com.example.podcastapp.core.database.DownloadDao;
import com.example.podcastapp.core.database.EpisodeDao;
import com.example.podcastapp.core.database.PodcastDao;
import com.example.podcastapp.core.database.SubscriptionDao;
import com.example.podcastapp.core.media.PlaybackService;
import com.example.podcastapp.core.media.PlaybackService_MembersInjector;
import com.example.podcastapp.core.media.PlayerController;
import com.example.podcastapp.core.network.RssFetcher;
import com.example.podcastapp.core.network.RssParser;
import com.example.podcastapp.core.player.PlayerViewModel;
import com.example.podcastapp.core.player.PlayerViewModel_HiltModules;
import com.example.podcastapp.core.player.PlayerViewModel_HiltModules_BindsModule_Binds_LazyMapKey;
import com.example.podcastapp.core.player.PlayerViewModel_HiltModules_KeyModule_Provide_LazyMapKey;
import com.example.podcastapp.di.AppModule_ProvideDatabaseFactory;
import com.example.podcastapp.di.AppModule_ProvideDownloadDaoFactory;
import com.example.podcastapp.di.AppModule_ProvideDownloadRepositoryFactory;
import com.example.podcastapp.di.AppModule_ProvideEpisodeDaoFactory;
import com.example.podcastapp.di.AppModule_ProvideEpisodeRepositoryFactory;
import com.example.podcastapp.di.AppModule_ProvideOkHttpClientFactory;
import com.example.podcastapp.di.AppModule_ProvidePodcastDaoFactory;
import com.example.podcastapp.di.AppModule_ProvidePodcastRepositoryFactory;
import com.example.podcastapp.di.AppModule_ProvideRssFetcherFactory;
import com.example.podcastapp.di.AppModule_ProvideRssParserFactory;
import com.example.podcastapp.di.AppModule_ProvideSubscriptionDaoFactory;
import com.example.podcastapp.di.DownloadModule_ProvideDownloadControllerFactory;
import com.example.podcastapp.di.DownloadModule_ProvideDownloadManagerFactory;
import com.example.podcastapp.di.MediaModule_ProvidePlayerControllerFactory;
import com.example.podcastapp.download.DownloadCompleteReceiver;
import com.example.podcastapp.download.DownloadCompleteReceiver_MembersInjector;
import com.example.podcastapp.feature.download.DownloadViewModel;
import com.example.podcastapp.feature.download.DownloadViewModel_HiltModules;
import com.example.podcastapp.feature.download.DownloadViewModel_HiltModules_BindsModule_Binds_LazyMapKey;
import com.example.podcastapp.feature.download.DownloadViewModel_HiltModules_KeyModule_Provide_LazyMapKey;
import com.example.podcastapp.feature.episode.EpisodeDetailViewModel;
import com.example.podcastapp.feature.episode.EpisodeDetailViewModel_HiltModules;
import com.example.podcastapp.feature.episode.EpisodeDetailViewModel_HiltModules_BindsModule_Binds_LazyMapKey;
import com.example.podcastapp.feature.episode.EpisodeDetailViewModel_HiltModules_KeyModule_Provide_LazyMapKey;
import com.example.podcastapp.feature.episode.EpisodeListViewModel;
import com.example.podcastapp.feature.episode.EpisodeListViewModel_HiltModules;
import com.example.podcastapp.feature.episode.EpisodeListViewModel_HiltModules_BindsModule_Binds_LazyMapKey;
import com.example.podcastapp.feature.episode.EpisodeListViewModel_HiltModules_KeyModule_Provide_LazyMapKey;
import com.example.podcastapp.feature.episode.SearchViewModel;
import com.example.podcastapp.feature.episode.SearchViewModel_HiltModules;
import com.example.podcastapp.feature.episode.SearchViewModel_HiltModules_BindsModule_Binds_LazyMapKey;
import com.example.podcastapp.feature.episode.SearchViewModel_HiltModules_KeyModule_Provide_LazyMapKey;
import com.example.podcastapp.feature.podcast.PodcastListViewModel;
import com.example.podcastapp.feature.podcast.PodcastListViewModel_HiltModules;
import com.example.podcastapp.feature.podcast.PodcastListViewModel_HiltModules_BindsModule_Binds_LazyMapKey;
import com.example.podcastapp.feature.podcast.PodcastListViewModel_HiltModules_KeyModule_Provide_LazyMapKey;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import dagger.hilt.android.ActivityRetainedLifecycle;
import dagger.hilt.android.ViewModelLifecycle;
import dagger.hilt.android.internal.builders.ActivityComponentBuilder;
import dagger.hilt.android.internal.builders.ActivityRetainedComponentBuilder;
import dagger.hilt.android.internal.builders.FragmentComponentBuilder;
import dagger.hilt.android.internal.builders.ServiceComponentBuilder;
import dagger.hilt.android.internal.builders.ViewComponentBuilder;
import dagger.hilt.android.internal.builders.ViewModelComponentBuilder;
import dagger.hilt.android.internal.builders.ViewWithFragmentComponentBuilder;
import dagger.hilt.android.internal.lifecycle.DefaultViewModelFactories;
import dagger.hilt.android.internal.lifecycle.DefaultViewModelFactories_InternalFactoryFactory_Factory;
import dagger.hilt.android.internal.managers.ActivityRetainedComponentManager_LifecycleModule_ProvideActivityRetainedLifecycleFactory;
import dagger.hilt.android.internal.managers.SavedStateHandleHolder;
import dagger.hilt.android.internal.modules.ApplicationContextModule;
import dagger.hilt.android.internal.modules.ApplicationContextModule_ProvideContextFactory;
import dagger.internal.DaggerGenerated;
import dagger.internal.DoubleCheck;
import dagger.internal.LazyClassKeyMap;
import dagger.internal.Preconditions;
import dagger.internal.Provider;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;
import okhttp3.OkHttpClient;

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
public final class DaggerPodcastApplication_HiltComponents_SingletonC {
  private DaggerPodcastApplication_HiltComponents_SingletonC() {
  }

  public static Builder builder() {
    return new Builder();
  }

  public static final class Builder {
    private ApplicationContextModule applicationContextModule;

    private Builder() {
    }

    public Builder applicationContextModule(ApplicationContextModule applicationContextModule) {
      this.applicationContextModule = Preconditions.checkNotNull(applicationContextModule);
      return this;
    }

    public PodcastApplication_HiltComponents.SingletonC build() {
      Preconditions.checkBuilderRequirement(applicationContextModule, ApplicationContextModule.class);
      return new SingletonCImpl(applicationContextModule);
    }
  }

  private static final class ActivityRetainedCBuilder implements PodcastApplication_HiltComponents.ActivityRetainedC.Builder {
    private final SingletonCImpl singletonCImpl;

    private SavedStateHandleHolder savedStateHandleHolder;

    private ActivityRetainedCBuilder(SingletonCImpl singletonCImpl) {
      this.singletonCImpl = singletonCImpl;
    }

    @Override
    public ActivityRetainedCBuilder savedStateHandleHolder(
        SavedStateHandleHolder savedStateHandleHolder) {
      this.savedStateHandleHolder = Preconditions.checkNotNull(savedStateHandleHolder);
      return this;
    }

    @Override
    public PodcastApplication_HiltComponents.ActivityRetainedC build() {
      Preconditions.checkBuilderRequirement(savedStateHandleHolder, SavedStateHandleHolder.class);
      return new ActivityRetainedCImpl(singletonCImpl, savedStateHandleHolder);
    }
  }

  private static final class ActivityCBuilder implements PodcastApplication_HiltComponents.ActivityC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private Activity activity;

    private ActivityCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
    }

    @Override
    public ActivityCBuilder activity(Activity activity) {
      this.activity = Preconditions.checkNotNull(activity);
      return this;
    }

    @Override
    public PodcastApplication_HiltComponents.ActivityC build() {
      Preconditions.checkBuilderRequirement(activity, Activity.class);
      return new ActivityCImpl(singletonCImpl, activityRetainedCImpl, activity);
    }
  }

  private static final class FragmentCBuilder implements PodcastApplication_HiltComponents.FragmentC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private Fragment fragment;

    private FragmentCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
    }

    @Override
    public FragmentCBuilder fragment(Fragment fragment) {
      this.fragment = Preconditions.checkNotNull(fragment);
      return this;
    }

    @Override
    public PodcastApplication_HiltComponents.FragmentC build() {
      Preconditions.checkBuilderRequirement(fragment, Fragment.class);
      return new FragmentCImpl(singletonCImpl, activityRetainedCImpl, activityCImpl, fragment);
    }
  }

  private static final class ViewWithFragmentCBuilder implements PodcastApplication_HiltComponents.ViewWithFragmentC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final FragmentCImpl fragmentCImpl;

    private View view;

    private ViewWithFragmentCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl,
        FragmentCImpl fragmentCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
      this.fragmentCImpl = fragmentCImpl;
    }

    @Override
    public ViewWithFragmentCBuilder view(View view) {
      this.view = Preconditions.checkNotNull(view);
      return this;
    }

    @Override
    public PodcastApplication_HiltComponents.ViewWithFragmentC build() {
      Preconditions.checkBuilderRequirement(view, View.class);
      return new ViewWithFragmentCImpl(singletonCImpl, activityRetainedCImpl, activityCImpl, fragmentCImpl, view);
    }
  }

  private static final class ViewCBuilder implements PodcastApplication_HiltComponents.ViewC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private View view;

    private ViewCBuilder(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
        ActivityCImpl activityCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
    }

    @Override
    public ViewCBuilder view(View view) {
      this.view = Preconditions.checkNotNull(view);
      return this;
    }

    @Override
    public PodcastApplication_HiltComponents.ViewC build() {
      Preconditions.checkBuilderRequirement(view, View.class);
      return new ViewCImpl(singletonCImpl, activityRetainedCImpl, activityCImpl, view);
    }
  }

  private static final class ViewModelCBuilder implements PodcastApplication_HiltComponents.ViewModelC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private SavedStateHandle savedStateHandle;

    private ViewModelLifecycle viewModelLifecycle;

    private ViewModelCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
    }

    @Override
    public ViewModelCBuilder savedStateHandle(SavedStateHandle handle) {
      this.savedStateHandle = Preconditions.checkNotNull(handle);
      return this;
    }

    @Override
    public ViewModelCBuilder viewModelLifecycle(ViewModelLifecycle viewModelLifecycle) {
      this.viewModelLifecycle = Preconditions.checkNotNull(viewModelLifecycle);
      return this;
    }

    @Override
    public PodcastApplication_HiltComponents.ViewModelC build() {
      Preconditions.checkBuilderRequirement(savedStateHandle, SavedStateHandle.class);
      Preconditions.checkBuilderRequirement(viewModelLifecycle, ViewModelLifecycle.class);
      return new ViewModelCImpl(singletonCImpl, activityRetainedCImpl, savedStateHandle, viewModelLifecycle);
    }
  }

  private static final class ServiceCBuilder implements PodcastApplication_HiltComponents.ServiceC.Builder {
    private final SingletonCImpl singletonCImpl;

    private Service service;

    private ServiceCBuilder(SingletonCImpl singletonCImpl) {
      this.singletonCImpl = singletonCImpl;
    }

    @Override
    public ServiceCBuilder service(Service service) {
      this.service = Preconditions.checkNotNull(service);
      return this;
    }

    @Override
    public PodcastApplication_HiltComponents.ServiceC build() {
      Preconditions.checkBuilderRequirement(service, Service.class);
      return new ServiceCImpl(singletonCImpl, service);
    }
  }

  private static final class ViewWithFragmentCImpl extends PodcastApplication_HiltComponents.ViewWithFragmentC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final FragmentCImpl fragmentCImpl;

    private final ViewWithFragmentCImpl viewWithFragmentCImpl = this;

    ViewWithFragmentCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl,
        FragmentCImpl fragmentCImpl, View viewParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
      this.fragmentCImpl = fragmentCImpl;


    }
  }

  private static final class FragmentCImpl extends PodcastApplication_HiltComponents.FragmentC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final FragmentCImpl fragmentCImpl = this;

    FragmentCImpl(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
        ActivityCImpl activityCImpl, Fragment fragmentParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;


    }

    @Override
    public DefaultViewModelFactories.InternalFactoryFactory getHiltInternalFactoryFactory() {
      return activityCImpl.getHiltInternalFactoryFactory();
    }

    @Override
    public ViewWithFragmentComponentBuilder viewWithFragmentComponentBuilder() {
      return new ViewWithFragmentCBuilder(singletonCImpl, activityRetainedCImpl, activityCImpl, fragmentCImpl);
    }
  }

  private static final class ViewCImpl extends PodcastApplication_HiltComponents.ViewC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final ViewCImpl viewCImpl = this;

    ViewCImpl(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
        ActivityCImpl activityCImpl, View viewParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;


    }
  }

  private static final class ActivityCImpl extends PodcastApplication_HiltComponents.ActivityC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl = this;

    ActivityCImpl(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
        Activity activityParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;


    }

    ImmutableMap keySetMapOfClassOfAndBooleanBuilder() {
      ImmutableMap.Builder mapBuilder = ImmutableMap.<String, Boolean>builderWithExpectedSize(6);
      mapBuilder.put(DownloadViewModel_HiltModules_KeyModule_Provide_LazyMapKey.lazyClassKeyName, DownloadViewModel_HiltModules.KeyModule.provide());
      mapBuilder.put(EpisodeDetailViewModel_HiltModules_KeyModule_Provide_LazyMapKey.lazyClassKeyName, EpisodeDetailViewModel_HiltModules.KeyModule.provide());
      mapBuilder.put(EpisodeListViewModel_HiltModules_KeyModule_Provide_LazyMapKey.lazyClassKeyName, EpisodeListViewModel_HiltModules.KeyModule.provide());
      mapBuilder.put(PlayerViewModel_HiltModules_KeyModule_Provide_LazyMapKey.lazyClassKeyName, PlayerViewModel_HiltModules.KeyModule.provide());
      mapBuilder.put(PodcastListViewModel_HiltModules_KeyModule_Provide_LazyMapKey.lazyClassKeyName, PodcastListViewModel_HiltModules.KeyModule.provide());
      mapBuilder.put(SearchViewModel_HiltModules_KeyModule_Provide_LazyMapKey.lazyClassKeyName, SearchViewModel_HiltModules.KeyModule.provide());
      return mapBuilder.build();
    }

    @Override
    public void injectMainActivity(MainActivity arg0) {
    }

    @Override
    public DefaultViewModelFactories.InternalFactoryFactory getHiltInternalFactoryFactory() {
      return DefaultViewModelFactories_InternalFactoryFactory_Factory.newInstance(getViewModelKeys(), new ViewModelCBuilder(singletonCImpl, activityRetainedCImpl));
    }

    @Override
    public Map<Class<?>, Boolean> getViewModelKeys() {
      return LazyClassKeyMap.<Boolean>of(keySetMapOfClassOfAndBooleanBuilder());
    }

    @Override
    public ViewModelComponentBuilder getViewModelComponentBuilder() {
      return new ViewModelCBuilder(singletonCImpl, activityRetainedCImpl);
    }

    @Override
    public FragmentComponentBuilder fragmentComponentBuilder() {
      return new FragmentCBuilder(singletonCImpl, activityRetainedCImpl, activityCImpl);
    }

    @Override
    public ViewComponentBuilder viewComponentBuilder() {
      return new ViewCBuilder(singletonCImpl, activityRetainedCImpl, activityCImpl);
    }
  }

  private static final class ViewModelCImpl extends PodcastApplication_HiltComponents.ViewModelC {
    private final SavedStateHandle savedStateHandle;

    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ViewModelCImpl viewModelCImpl = this;

    Provider<DownloadViewModel> downloadViewModelProvider;

    Provider<EpisodeDetailViewModel> episodeDetailViewModelProvider;

    Provider<EpisodeListViewModel> episodeListViewModelProvider;

    Provider<PlayerViewModel> playerViewModelProvider;

    Provider<PodcastListViewModel> podcastListViewModelProvider;

    Provider<SearchViewModel> searchViewModelProvider;

    ViewModelCImpl(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
        SavedStateHandle savedStateHandleParam, ViewModelLifecycle viewModelLifecycleParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.savedStateHandle = savedStateHandleParam;
      initialize(savedStateHandleParam, viewModelLifecycleParam);

    }

    ImmutableMap hiltViewModelMapMapOfClassOfAndProviderOfViewModelBuilder() {
      ImmutableMap.Builder mapBuilder = ImmutableMap.<String, javax.inject.Provider<ViewModel>>builderWithExpectedSize(6);
      mapBuilder.put(DownloadViewModel_HiltModules_BindsModule_Binds_LazyMapKey.lazyClassKeyName, ((Provider) (downloadViewModelProvider)));
      mapBuilder.put(EpisodeDetailViewModel_HiltModules_BindsModule_Binds_LazyMapKey.lazyClassKeyName, ((Provider) (episodeDetailViewModelProvider)));
      mapBuilder.put(EpisodeListViewModel_HiltModules_BindsModule_Binds_LazyMapKey.lazyClassKeyName, ((Provider) (episodeListViewModelProvider)));
      mapBuilder.put(PlayerViewModel_HiltModules_BindsModule_Binds_LazyMapKey.lazyClassKeyName, ((Provider) (playerViewModelProvider)));
      mapBuilder.put(PodcastListViewModel_HiltModules_BindsModule_Binds_LazyMapKey.lazyClassKeyName, ((Provider) (podcastListViewModelProvider)));
      mapBuilder.put(SearchViewModel_HiltModules_BindsModule_Binds_LazyMapKey.lazyClassKeyName, ((Provider) (searchViewModelProvider)));
      return mapBuilder.build();
    }

    @SuppressWarnings("unchecked")
    private void initialize(final SavedStateHandle savedStateHandleParam,
        final ViewModelLifecycle viewModelLifecycleParam) {
      this.downloadViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 0);
      this.episodeDetailViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 1);
      this.episodeListViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 2);
      this.playerViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 3);
      this.podcastListViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 4);
      this.searchViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 5);
    }

    @Override
    public Map<Class<?>, javax.inject.Provider<ViewModel>> getHiltViewModelMap() {
      return LazyClassKeyMap.<javax.inject.Provider<ViewModel>>of(hiltViewModelMapMapOfClassOfAndProviderOfViewModelBuilder());
    }

    @Override
    public Map<Class<?>, Object> getHiltViewModelAssistedMap() {
      return ImmutableMap.<Class<?>, Object>of();
    }

    private static final class SwitchingProvider<T> implements Provider<T> {
      private final SingletonCImpl singletonCImpl;

      private final ActivityRetainedCImpl activityRetainedCImpl;

      private final ViewModelCImpl viewModelCImpl;

      private final int id;

      SwitchingProvider(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
          ViewModelCImpl viewModelCImpl, int id) {
        this.singletonCImpl = singletonCImpl;
        this.activityRetainedCImpl = activityRetainedCImpl;
        this.viewModelCImpl = viewModelCImpl;
        this.id = id;
      }

      @Override
      @SuppressWarnings("unchecked")
      public T get() {
        switch (id) {
          case 0: // com.example.podcastapp.feature.download.DownloadViewModel
          return (T) new DownloadViewModel(singletonCImpl.provideDownloadRepositoryProvider.get());

          case 1: // com.example.podcastapp.feature.episode.EpisodeDetailViewModel
          return (T) new EpisodeDetailViewModel(viewModelCImpl.savedStateHandle, singletonCImpl.provideEpisodeRepositoryProvider.get(), singletonCImpl.provideDownloadControllerProvider.get(), singletonCImpl.provideDownloadRepositoryProvider.get(), singletonCImpl.providePlayerControllerProvider.get());

          case 2: // com.example.podcastapp.feature.episode.EpisodeListViewModel
          return (T) new EpisodeListViewModel(viewModelCImpl.savedStateHandle, singletonCImpl.provideEpisodeRepositoryProvider.get(), singletonCImpl.providePodcastRepositoryProvider.get(), singletonCImpl.provideDownloadRepositoryProvider.get(), singletonCImpl.providePlayerControllerProvider.get());

          case 3: // com.example.podcastapp.core.player.PlayerViewModel
          return (T) new PlayerViewModel(singletonCImpl.providePlayerControllerProvider.get());

          case 4: // com.example.podcastapp.feature.podcast.PodcastListViewModel
          return (T) new PodcastListViewModel(singletonCImpl.providePodcastRepositoryProvider.get(), singletonCImpl.provideEpisodeRepositoryProvider.get());

          case 5: // com.example.podcastapp.feature.episode.SearchViewModel
          return (T) new SearchViewModel(singletonCImpl.provideEpisodeRepositoryProvider.get());

          default: throw new AssertionError(id);
        }
      }
    }
  }

  private static final class ActivityRetainedCImpl extends PodcastApplication_HiltComponents.ActivityRetainedC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl = this;

    Provider<ActivityRetainedLifecycle> provideActivityRetainedLifecycleProvider;

    ActivityRetainedCImpl(SingletonCImpl singletonCImpl,
        SavedStateHandleHolder savedStateHandleHolderParam) {
      this.singletonCImpl = singletonCImpl;

      initialize(savedStateHandleHolderParam);

    }

    @SuppressWarnings("unchecked")
    private void initialize(final SavedStateHandleHolder savedStateHandleHolderParam) {
      this.provideActivityRetainedLifecycleProvider = DoubleCheck.provider(new SwitchingProvider<ActivityRetainedLifecycle>(singletonCImpl, activityRetainedCImpl, 0));
    }

    @Override
    public ActivityComponentBuilder activityComponentBuilder() {
      return new ActivityCBuilder(singletonCImpl, activityRetainedCImpl);
    }

    @Override
    public ActivityRetainedLifecycle getActivityRetainedLifecycle() {
      return provideActivityRetainedLifecycleProvider.get();
    }

    private static final class SwitchingProvider<T> implements Provider<T> {
      private final SingletonCImpl singletonCImpl;

      private final ActivityRetainedCImpl activityRetainedCImpl;

      private final int id;

      SwitchingProvider(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
          int id) {
        this.singletonCImpl = singletonCImpl;
        this.activityRetainedCImpl = activityRetainedCImpl;
        this.id = id;
      }

      @Override
      @SuppressWarnings("unchecked")
      public T get() {
        switch (id) {
          case 0: // dagger.hilt.android.ActivityRetainedLifecycle
          return (T) ActivityRetainedComponentManager_LifecycleModule_ProvideActivityRetainedLifecycleFactory.provideActivityRetainedLifecycle();

          default: throw new AssertionError(id);
        }
      }
    }
  }

  private static final class ServiceCImpl extends PodcastApplication_HiltComponents.ServiceC {
    private final SingletonCImpl singletonCImpl;

    private final ServiceCImpl serviceCImpl = this;

    ServiceCImpl(SingletonCImpl singletonCImpl, Service serviceParam) {
      this.singletonCImpl = singletonCImpl;


    }

    @Override
    public void injectPlaybackService(PlaybackService arg0) {
      injectPlaybackService2(arg0);
    }

    @CanIgnoreReturnValue
    private PlaybackService injectPlaybackService2(PlaybackService instance) {
      PlaybackService_MembersInjector.injectPlayerController(instance, singletonCImpl.providePlayerControllerProvider.get());
      return instance;
    }
  }

  private static final class SingletonCImpl extends PodcastApplication_HiltComponents.SingletonC {
    private final ApplicationContextModule applicationContextModule;

    private final SingletonCImpl singletonCImpl = this;

    Provider<DownloadManager> provideDownloadManagerProvider;

    Provider<AppDatabase> provideDatabaseProvider;

    Provider<DownloadRepository> provideDownloadRepositoryProvider;

    Provider<DownloadController> provideDownloadControllerProvider;

    Provider<OkHttpClient> provideOkHttpClientProvider;

    Provider<RssParser> provideRssParserProvider;

    Provider<RssFetcher> provideRssFetcherProvider;

    Provider<EpisodeRepository> provideEpisodeRepositoryProvider;

    Provider<PlayerController> providePlayerControllerProvider;

    Provider<PodcastRepository> providePodcastRepositoryProvider;

    SingletonCImpl(ApplicationContextModule applicationContextModuleParam) {
      this.applicationContextModule = applicationContextModuleParam;
      initialize(applicationContextModuleParam);

    }

    DownloadDao downloadDao() {
      return AppModule_ProvideDownloadDaoFactory.provideDownloadDao(provideDatabaseProvider.get());
    }

    PodcastDao podcastDao() {
      return AppModule_ProvidePodcastDaoFactory.providePodcastDao(provideDatabaseProvider.get());
    }

    EpisodeDao episodeDao() {
      return AppModule_ProvideEpisodeDaoFactory.provideEpisodeDao(provideDatabaseProvider.get());
    }

    SubscriptionDao subscriptionDao() {
      return AppModule_ProvideSubscriptionDaoFactory.provideSubscriptionDao(provideDatabaseProvider.get());
    }

    @SuppressWarnings("unchecked")
    private void initialize(final ApplicationContextModule applicationContextModuleParam) {
      this.provideDownloadManagerProvider = DoubleCheck.provider(new SwitchingProvider<DownloadManager>(singletonCImpl, 1));
      this.provideDatabaseProvider = DoubleCheck.provider(new SwitchingProvider<AppDatabase>(singletonCImpl, 3));
      this.provideDownloadRepositoryProvider = DoubleCheck.provider(new SwitchingProvider<DownloadRepository>(singletonCImpl, 2));
      this.provideDownloadControllerProvider = DoubleCheck.provider(new SwitchingProvider<DownloadController>(singletonCImpl, 0));
      this.provideOkHttpClientProvider = DoubleCheck.provider(new SwitchingProvider<OkHttpClient>(singletonCImpl, 6));
      this.provideRssParserProvider = DoubleCheck.provider(new SwitchingProvider<RssParser>(singletonCImpl, 7));
      this.provideRssFetcherProvider = DoubleCheck.provider(new SwitchingProvider<RssFetcher>(singletonCImpl, 5));
      this.provideEpisodeRepositoryProvider = DoubleCheck.provider(new SwitchingProvider<EpisodeRepository>(singletonCImpl, 4));
      this.providePlayerControllerProvider = DoubleCheck.provider(new SwitchingProvider<PlayerController>(singletonCImpl, 8));
      this.providePodcastRepositoryProvider = DoubleCheck.provider(new SwitchingProvider<PodcastRepository>(singletonCImpl, 9));
    }

    @Override
    public void injectPodcastApplication(PodcastApplication podcastApplication) {
    }

    @Override
    public void injectDownloadCompleteReceiver(DownloadCompleteReceiver arg0) {
      injectDownloadCompleteReceiver2(arg0);
    }

    @Override
    public Set<Boolean> getDisableFragmentGetContextFix() {
      return ImmutableSet.<Boolean>of();
    }

    @Override
    public ActivityRetainedComponentBuilder retainedComponentBuilder() {
      return new ActivityRetainedCBuilder(singletonCImpl);
    }

    @Override
    public ServiceComponentBuilder serviceComponentBuilder() {
      return new ServiceCBuilder(singletonCImpl);
    }

    @CanIgnoreReturnValue
    private DownloadCompleteReceiver injectDownloadCompleteReceiver2(
        DownloadCompleteReceiver instance) {
      DownloadCompleteReceiver_MembersInjector.injectDownloadController(instance, provideDownloadControllerProvider.get());
      return instance;
    }

    private static final class SwitchingProvider<T> implements Provider<T> {
      private final SingletonCImpl singletonCImpl;

      private final int id;

      SwitchingProvider(SingletonCImpl singletonCImpl, int id) {
        this.singletonCImpl = singletonCImpl;
        this.id = id;
      }

      @Override
      @SuppressWarnings("unchecked")
      public T get() {
        switch (id) {
          case 0: // com.example.podcastapp.core.data.DownloadController
          return (T) DownloadModule_ProvideDownloadControllerFactory.provideDownloadController(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule), singletonCImpl.provideDownloadManagerProvider.get(), singletonCImpl.provideDownloadRepositoryProvider.get());

          case 1: // android.app.DownloadManager
          return (T) DownloadModule_ProvideDownloadManagerFactory.provideDownloadManager(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 2: // com.example.podcastapp.core.data.DownloadRepository
          return (T) AppModule_ProvideDownloadRepositoryFactory.provideDownloadRepository(singletonCImpl.downloadDao());

          case 3: // com.example.podcastapp.core.database.AppDatabase
          return (T) AppModule_ProvideDatabaseFactory.provideDatabase(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 4: // com.example.podcastapp.core.data.EpisodeRepository
          return (T) AppModule_ProvideEpisodeRepositoryFactory.provideEpisodeRepository(singletonCImpl.podcastDao(), singletonCImpl.episodeDao(), singletonCImpl.provideRssFetcherProvider.get());

          case 5: // com.example.podcastapp.core.network.RssFetcher
          return (T) AppModule_ProvideRssFetcherFactory.provideRssFetcher(singletonCImpl.provideOkHttpClientProvider.get(), singletonCImpl.provideRssParserProvider.get());

          case 6: // okhttp3.OkHttpClient
          return (T) AppModule_ProvideOkHttpClientFactory.provideOkHttpClient();

          case 7: // com.example.podcastapp.core.network.RssParser
          return (T) AppModule_ProvideRssParserFactory.provideRssParser();

          case 8: // com.example.podcastapp.core.media.PlayerController
          return (T) MediaModule_ProvidePlayerControllerFactory.providePlayerController(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 9: // com.example.podcastapp.core.data.PodcastRepository
          return (T) AppModule_ProvidePodcastRepositoryFactory.providePodcastRepository(singletonCImpl.podcastDao(), singletonCImpl.subscriptionDao(), singletonCImpl.provideRssFetcherProvider.get());

          default: throw new AssertionError(id);
        }
      }
    }
  }
}
