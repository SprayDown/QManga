package org.spray.qmanga.utils

import android.content.Context
import android.os.Environment
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineExceptionHandler
import java.io.File


fun RecyclerView.autoFitColumns(columnWidth: Int) {
    val displayMetrics = this.context.resources.displayMetrics
    val noOfColumns = ((displayMetrics.widthPixels / displayMetrics.density) / columnWidth).toInt()
    layoutManager = GridLayoutManager(context, noOfColumns)
    setHasFixedSize(true)
}

fun getRootDirPath(context: Context): String? {
    return if (Environment.MEDIA_MOUNTED == Environment.getExternalStorageState()) {
        val file: File =
            ContextCompat.getExternalFilesDirs(context.applicationContext, null)[0]
        file.absolutePath
    } else {
        context.applicationContext.filesDir.absolutePath
    }
}

fun coroutineExceptionHandler() = CoroutineExceptionHandler{ _, throwable ->
    throwable.printStackTrace()
}
