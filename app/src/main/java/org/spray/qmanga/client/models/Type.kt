package org.spray.qmanga.client.models

import org.spray.qmanga.R

enum class SortType(val id: Int) {
    NEWEST(R.string.newest),
    LAST_UPDATES(R.string.last_updates),
    POPULAR(R.string.popular),
    LIKES(R.string.likes),
    VIEWS(R.string.views),
    CHAPTER_COUNT(R.string.chapter_count),
    RANDOM(R.string.random);
}

enum class TagType(val id: Int) {
    GENRE(R.string.genres),
    CATEGORY(R.string.categories),
    LIMIT(R.string.limit),
    STATUS(R.string.status)
}

enum class ListType(val id: Int) {
    DESCENDING(R.string.descending),
    ASCENDING(R.string.ascending)
}

