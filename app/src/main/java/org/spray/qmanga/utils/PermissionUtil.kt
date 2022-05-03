package org.spray.qmanga.utils

import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import org.spray.qmanga.QManga

fun checkPermission(permission: String): Boolean {
    return ContextCompat.checkSelfPermission(
        QManga.appContext,
        permission
    ) == PackageManager.PERMISSION_GRANTED
}