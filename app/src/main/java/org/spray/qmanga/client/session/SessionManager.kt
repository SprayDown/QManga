package org.spray.qmanga.client.session

import org.spray.qmanga.client.models.MangaSource
import org.spray.qmanga.client.models.user.User

class SessionManager {

    companion object {

        private var currentSession: Session? = null

        fun updateSession(user: User, source: MangaSource) {
            if (currentSession == null || currentSession?.source != source)
                currentSession = Session(user, source)
        }

        fun getCurrentSession(): Session? {
            return currentSession
        }

        fun isAuthenticated(): Boolean = currentSession != null
    }

}