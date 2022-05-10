package org.spray.qmanga.ui.impl.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import org.spray.qmanga.client.source.Source

class ProfileModelFactory(val source: Source) :
    ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
            ProfileViewModel(source) as T
        } else {
            throw IllegalArgumentException("ViewModel Not Found")
        }
    }
}