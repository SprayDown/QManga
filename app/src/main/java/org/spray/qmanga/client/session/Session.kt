package org.spray.qmanga.client.session

import org.spray.qmanga.client.models.MangaSource
import org.spray.qmanga.client.models.user.User

data class Session(val user: User, val source: MangaSource)