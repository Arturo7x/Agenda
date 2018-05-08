package dev.agenda.models

import android.os.Parcel
import android.os.Parcelable


class Contact() : Parcelable {
    var id: String? = null
    var name: String? = null
    var phone: String? = null
    var imageSrc: String? = "@drawable/user_hd"
    var favorite: Boolean = false
    var homePhone: String? = null
    var workPhone: String? = null
    var nickName: String? = null
    var homeEmail: String? = null
    var workEmail: String? = null
    var companyName: String? = null
    var title: String? = null

    constructor(parcel: Parcel) : this() {
        name = parcel.readString()
        id = parcel.readString()
        phone = parcel.readString()
        imageSrc = parcel.readString()
        favorite = parcel.readByte() != 0.toByte()
        homePhone = parcel.readString()
        workPhone = parcel.readString()
        nickName = parcel.readString()
        homeEmail = parcel.readString()
        workEmail = parcel.readString()
        companyName = parcel.readString()
        title = parcel.readString()
    }

    constructor(id: String?, name: String, phone: String?, imageSrc: String?, favorite: Boolean) : this() {
        this.id = id
        this.name = name
        this.phone = phone
        this.imageSrc = imageSrc
        this.favorite = favorite
    }

    constructor(id: String?, name: String, phone: String?, imageSrc: String?, favorite: Boolean,
                homePhone: String?, workPhone: String?, nickName: String?, homeEmail: String?,
                workEmail: String?, companyName: String?, title: String?) : this() {
        this.id = id
        this.name = name
        this.phone = phone
        this.imageSrc = imageSrc
        this.favorite = favorite
        this.homePhone = homePhone
        this.workPhone = workPhone
        this.nickName = nickName
        this.homeEmail = homeEmail
        this.workEmail = workEmail
        this.companyName = companyName
        this.title = title
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeString(id)
        parcel.writeString(phone)
        parcel.writeString(imageSrc)
        parcel.writeByte(if (favorite) 1 else 0)
        parcel.writeString(homePhone)
        parcel.writeString(workPhone)
        parcel.writeString(nickName)
        parcel.writeString(homeEmail)
        parcel.writeString(workEmail)
        parcel.writeString(companyName)
        parcel.writeString(title)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Contact> {
        override fun createFromParcel(parcel: Parcel): Contact {
            return Contact(parcel)
        }

        override fun newArray(size: Int): Array<Contact?> {
            return arrayOfNulls(size)
        }
    }

}