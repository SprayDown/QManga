package org.spray.qmanga.client.models

import android.os.Parcel
import android.os.Parcelable

data class QueryData(
    val query: String? = null,
    val status: List<MangaTag>? = null,
    val limit: List<MangaTag>? = null,
    val genres: List<MangaTag>? = null,
    val categories: List<MangaTag>? = null
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.createTypedArrayList(MangaTag),
        parcel.createTypedArrayList(MangaTag),
        parcel.createTypedArrayList(MangaTag),
        parcel.createTypedArrayList(MangaTag)
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(query)
        parcel.writeTypedList(status)
        parcel.writeTypedList(limit)
        parcel.writeTypedList(genres)
        parcel.writeTypedList(categories)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<QueryData> {
        override fun createFromParcel(parcel: Parcel): QueryData {
            return QueryData(parcel)
        }

        override fun newArray(size: Int): Array<QueryData?> {
            return arrayOfNulls(size)
        }
    }
}