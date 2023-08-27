package inz.mb.pl.app.fitapp.database.exercise

import androidx.room.*

@Dao
interface ExerciseDAO {

    @Insert
    fun insert(exerciseDTO: ExerciseDTO): Long

    @Update
    fun update(exerciseDTO: ExerciseDTO)

    @Query("SELECT * FROM exercise")
    fun getAll(): List<ExerciseDTO>

    @Delete
    fun delete(exerciseDTO: ExerciseDTO)

    @Query("DELETE FROM exercise")
    fun nukeTable()
}