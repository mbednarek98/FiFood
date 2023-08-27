package inz.mb.pl.app.fitapp.database.weight_change

import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity(tableName = "weight_change")
data class WeightChangeDTO(
    @PrimaryKey(autoGenerate = true)
    var id : Int = 0,
    @ColumnInfo
    var weight : Double = 0.0,
    @ColumnInfo
    var weightChange : Double = 0.0,
    @ColumnInfo
    var dateTime : String = ""
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readDouble(),
        parcel.readDouble(),
        parcel.readString()!!
    )
    override fun describeContents(): Int = 0

    override fun writeToParcel(destination: Parcel?, flags: Int)  = with(destination) {
        this!!.writeInt(id)
        writeDouble(weight)
        writeDouble(weightChange)
        writeString(dateTime)
    }

    companion object CREATOR : Parcelable.Creator<WeightChangeDTO> {
        override fun createFromParcel(parcel: Parcel): WeightChangeDTO {
            return WeightChangeDTO(parcel)
        }

        override fun newArray(size: Int): Array<WeightChangeDTO?> {
            return arrayOfNulls(size)
        }
    }
}