package inz.mb.pl.app.fitapp.database.saved_dish

import androidx.room.*

@Dao
interface SavedDishDAO {
    @Insert
    fun insert(savedDishDTO: SavedDishDTO): Long

    @Update
    fun update(savedDishDTO: SavedDishDTO)

    @Query("SELECT * FROM saved_dish")
    fun getAll(): List<SavedDishDTO>

    @Delete
    fun delete(savedDishDTO: SavedDishDTO)

    @Query("DELETE FROM saved_dish")
    fun nukeTable()
}