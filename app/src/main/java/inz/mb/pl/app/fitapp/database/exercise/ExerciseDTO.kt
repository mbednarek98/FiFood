package inz.mb.pl.app.fitapp.database.exercise


import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate


@Entity(tableName = "exercise")
data class ExerciseDTO(
    @PrimaryKey(autoGenerate = true)
    var id : Int = 0,
    @ColumnInfo
    var typeOfExercise : Int,
    @ColumnInfo
    var name: Int,
    @ColumnInfo
    var timeOfExercise : String,
    @ColumnInfo
    var kcalBurned : Int,
    @ColumnInfo
    var date: LocalDate)