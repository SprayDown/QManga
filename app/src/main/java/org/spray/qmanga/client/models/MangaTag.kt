package org.spray.qmanga.client.models

import android.os.Parcel
import android.os.Parcelable

data class MangaTag(val name: String, val id: Int, val type: TagType? = TagType.CATEGORY) :
    Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString().toString(),
        parcel.readInt(),
        TagType.valueOf(parcel.readString().toString())
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeInt(id)
        parcel.writeString(type?.name)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<MangaTag> {
        override fun createFromParcel(parcel: Parcel): MangaTag {
            return MangaTag(parcel)
        }

        override fun newArray(size: Int): Array<MangaTag?> {
            return arrayOfNulls(size)
        }
    }
}