package org.spray.qmanga.ui.impl.profile

import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.Dispatchers
import org.spray.qmanga.client.models.user.UserData
import org.spray.qmanga.client.source.Source
import org.spray.qmanga.ui.base.BaseViewModel

class ProfileViewModel(val source: Source) : BaseViewModel() {

    val authenticated = MutableLiveData<Boolean>()
    val userData = MutableLiveData<UserData>()

    fun loadData() {
        launchLoadingJob(Dispatchers.Default) {
            val loadUser = source.loadUserData()

            if (loadUser != null) {
                userData.postValue(loadUser!!)
                authenticated.postValue(true)
            } else
                authenticated.postValue(false)
        }
    }

}