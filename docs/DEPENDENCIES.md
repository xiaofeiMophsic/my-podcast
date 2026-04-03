# Dependency Selection

## Core
- Kotlin
- Coroutines
- Kotlinx Serialization (JSON if needed later)

## UI
- Jetpack Compose BOM
- Navigation Compose
- Lifecycle Runtime Compose

## DI
- Hilt (android + compiler)

## Persistence
- Room (runtime, ktx, compiler)
- DataStore Preferences

## Paging
- Paging 3 + Paging Compose

## Media
- Media3 (exoplayer, session, ui)

## Networking
- OkHttp
- XML parsing with XmlPullParser (platform API)

## Why These
- Hilt: modern DI standard on Android
- Room: offline-first data source of truth
- DataStore: modern replacement for SharedPreferences
- Paging: efficient lists for episode feeds and search
- Media3: modern playback stack with notifications and media session
- OkHttp + XmlPullParser: simple, lightweight RSS fetch + parse

## Notes
- RSS parsing is kept lightweight initially. If we need richer parsing later, we can swap in an RSS parsing library behind core:network.
