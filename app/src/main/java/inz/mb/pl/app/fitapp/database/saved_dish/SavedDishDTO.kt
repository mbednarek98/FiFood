package inz.mb.pl.app.fitapp.database.saved_dish

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "saved_dish")
data class SavedDishDTO(
    @PrimaryKey(autoGenerate = true)
    var id : Int = 0,
    @ColumnInfo
    var accessibility : Int = 0,
    @ColumnInfo
    var carbs : Double = 0.0,
    @ColumnInfo
    var easiness : Int = 1,
    @ColumnInfo
    var fat : Double = 0.0,
    @ColumnInfo
    var kcalEaten : Int = 0,
    @ColumnInfo
    var meal_type : Int = 0,
    @ColumnInfo
    var name : String = "",
    @ColumnInfo
    var name_lowercase : String = "",
    @ColumnInfo
    var protein : Double = 0.0,
    @ColumnInfo
    var time : Int = 0,
    @ColumnInfo
    var type : Int = 0,
    @ColumnInfo
    var typeOfUnits : Int = 0,
    @ColumnInfo
    var url : String = ""
)