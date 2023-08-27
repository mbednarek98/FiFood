package inz.mb.pl.app.fitapp.models

import android.os.Parcel
import android.os.Parcelable

data class User(
    var id: String = "",
    var email: String = "",
    var age: Int = 0,
    var gender: Int = 0,
    var height: Int = 0,
    var weight: Double = 0.0,
    var activity : Int = 0,
    var progress : Int = 0,
    var dishesCount : Int = 0,
    var dishesType : Int = 0
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readDouble(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt()
    )

    override fun describeContents(): Int = 0

    override fun writeToParcel(destination: Parcel, flags: Int) = with(destination) {
        writeString(id)
        writeString(email)
        writeInt(age)
        writeInt(gender)
        writeInt(height)
        writeDouble(weight)
        writeInt(activity)
        writeInt(progress)
        writeInt(dishesCount)
        writeInt(dishesType)
    }

    companion object CREATOR : Parcelable.Creator<User> {
        override fun createFromParcel(parcel: Parcel): User {
            return User(parcel)
        }

        override fun newArray(size: Int): Array<User?> {
            return arrayOfNulls(size)
        }
    }
}


