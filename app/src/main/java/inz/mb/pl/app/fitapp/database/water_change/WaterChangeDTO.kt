package inz.mb.pl.app.fitapp.database.water_change

import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "water_change")
data class WaterChangeDTO (
    @PrimaryKey(autoGenerate = true)
    var id : Int = 0,
    @ColumnInfo
    var waterChange : Int = 0,
    @ColumnInfo
    var dateTime : String = "",
    @ColumnInfo
    var day : Int = 0,
    @ColumnInfo
    var month: Int = 0,
    @ColumnInfo
    var year: Int = 0
    ) : Parcelable {
        constructor(parcel : Parcel) : this(
            parcel.readInt(),
            parcel.readInt(),
            parcel.readString()!!,
            parcel.readInt(),
            parcel.readInt(),
            parcel.readInt()
        )

    override fun describeContents(): Int = 0

    override fun writeToParcel(destination: Parcel?, flags: Int) = with(destination){
        this!!.writeInt(id)
        writeInt(waterChange)
        writeString(dateTime)
        writeInt(day)
        writeInt(month)
        writeInt(year)
    }

    companion object CREATOR : Parcelable.Creator<WaterChangeDTO> {
        override fun createFromParcel(parcel: Parcel): WaterChangeDTO {
            return WaterChangeDTO(parcel)
        }

        override fun newArray(size: Int): Array<WaterChangeDTO?> {
            return arrayOfNulls(size)
        }

    }
    }