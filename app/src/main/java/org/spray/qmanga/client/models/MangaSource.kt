package org.spray.qmanga.client.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Suppress("SpellCheckingInspection")
@Parcelize
enum class MangaSource(
    val title: String,
) : Parcelable {
    REMANGA("Remanga")
}