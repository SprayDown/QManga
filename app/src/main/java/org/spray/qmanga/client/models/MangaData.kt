package org.spray.qmanga.client.models

import android.os.Parcel
import android.os.Parcelable

open class MangaData(
    val name: String,
    val imageUrl: String,
    val url: String,
    val rating: String,
    val type: String,
    var hashId: Int = url.hashCode()
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readInt()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeString(imageUrl)
        parcel.writeString(url)
        parcel.writeString(rating)
        parcel.writeString(type)
        parcel.writeInt(hashId)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<MangaData> {
        override fun createFromParcel(parcel: Parcel): MangaData {
            return MangaData(parcel)
        }

        override fun newArray(size: Int): Array<MangaData?> {
            return arrayOfNulls(size)
        }
    }
}
