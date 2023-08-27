package inz.mb.pl.app.fitapp.database.dish

import androidx.room.*
import androidx.room.OnConflictStrategy





@Dao
interface DishDAO {
    @Insert
    fun insert(dishDTO : DishDTO): Long

    @Insert
    fun insertAll(order: List<DishDTO?>?) : List<Long>

    @Update
    fun update(dishDTO: DishDTO)

    @Query("SELECT * FROM dish")
    fun getAll(): List<DishDTO>

    @Delete
    fun delete(dishDTO: DishDTO)

    @Query("DELETE FROM dish")
    fun nukeTable()
}