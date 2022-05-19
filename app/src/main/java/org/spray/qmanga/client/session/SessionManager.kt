package org.spray.qmanga.client.session

import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.spray.qmanga.client.models.MangaSource
import org.spray.qmanga.client.models.user.User
import org.spray.qmanga.client.models.user.UserData
import org.spray.qmanga.client.source.Source

class SessionManager {

    companion object {

        val authenticated = MutableLiveData<Boolean>()
        val userData = MutableLiveData<UserData>()

        private var currentSession: Session? = null

        fun updateSession(user: User, source: MangaSource) {
            if (currentSession == null || currentSession?.source != source)
                currentSession = Session(user, source)
        }

        fun auth(source: Source) {
            CoroutineScope(Dispatchers.Default).launch {
                val loadUser = source.loadUserData()

                if (loadUser != null) {
                    userData.postValue(loadUser!!)
                    authenticated.postValue(true)
                } else
                    authenticated.postValue(false)
            }
        }

        fun getCurrentSession(): Session? {
            return currentSession
        }

        fun isAuthenticated(): Boolean = currentSession != null
    }

}