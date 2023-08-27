package inz.mb.pl.app.fitapp.database.dish

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "dish")
data class DishDTO(
    @PrimaryKey(autoGenerate = true)
    var id : Int = 0,
    @ColumnInfo
    var name : String = "Unknown",
    @ColumnInfo
    var type : Int = 0,
    @ColumnInfo
    var typeOfUnits : Int = 0,
    @ColumnInfo
    var kcalEaten : Int = 0,
    @ColumnInfo
    var weight : Int = 0,
    @ColumnInfo
    var carbs : Double = 0.0,
    @ColumnInfo
    var protein : Double = 0.0,
    @ColumnInfo
    var fat : Double = 0.0,
    @ColumnInfo
    var position : Int = 0,
    @ColumnInfo
    var date : LocalDate = LocalDate.now(),
    @ColumnInfo
    var url : String = ""
)