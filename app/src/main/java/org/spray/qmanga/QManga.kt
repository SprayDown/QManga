package org.spray.qmanga

import android.app.Application
import org.spray.qmanga.client.source.SourceManager
import org.spray.qmanga.network.NetworkHelper

class QManga : Application() {

    override fun onCreate() {
        super.onCreate()

        SourceManager.init()
        NetworkHelper.init(applicationContext)
    }

}