package org.spray.qmanga.ui.impl.profile

import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.Dispatchers
import org.spray.qmanga.client.models.MangaBookmark
import org.spray.qmanga.client.models.MangaData
import org.spray.qmanga.client.models.user.UserData
import org.spray.qmanga.client.session.SessionManager
import org.spray.qmanga.client.source.Source
import org.spray.qmanga.ui.base.BaseViewModel

class ProfileViewModel(val source: Source) : BaseViewModel() {

    private val reading = MutableLiveData<List<MangaBookmark>>()
    private val willRead = MutableLiveData<List<MangaBookmark>>()
    private val read = MutableLiveData<List<MangaBookmark>>()
    private val postponed = MutableLiveData<List<MangaBookmark>>()
    private val abandoned = MutableLiveData<List<MangaBookmark>>()
    private val notInteresting = MutableLiveData<List<MangaBookmark>>()

    fun loadData() {
        SessionManager.auth(source)
    }

    fun loadBookmarks(type: Int, count: Int, page: Int) {
        launchLoadingJob(Dispatchers.Default) {
            val bookmarks = source.loadBookmarks(
                SessionManager.getCurrentSession()!!.user.id,
                type,
                count,
                page
            )

            getData(type).postValue(bookmarks)
        }
    }

    fun getData(type: Int): MutableLiveData<List<MangaBookmark>> {
        when (type) {
            0 -> return reading
            1 -> return willRead
            2 -> return read
            3 -> return abandoned
            4 -> return postponed
            5 -> return notInteresting
        }
        return reading
    }

}