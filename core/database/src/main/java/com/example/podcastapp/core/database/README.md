# core:database

Room entities and DAO definitions for the podcast app.

## Entities
- PodcastEntity: RSS feed metadata
- EpisodeEntity: episode details
- SubscriptionEntity: subscribed feeds
- DownloadEntity: download state

## Notes
- Uses Room @Upsert to avoid primary key churn on updates.
