package org.spray.qmanga.client.models.user

import org.spray.qmanga.client.models.team.TeamData

data class OtherUser(
    val id: Int,
    val username: String,
    val avatarUrl: String,
    val sex: Int = 0,
    val count_views: Int = 0,
    val count_votes: Int = 0,
    val count_comments: Int = 0,
    val tagline: String? = null,
    val teams: Set<TeamData> = emptySet(),
    val badges: Set<String> = emptySet()
)