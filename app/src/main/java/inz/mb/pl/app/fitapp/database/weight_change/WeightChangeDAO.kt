package inz.mb.pl.app.fitapp.database.weight_change

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface WeightChangeDAO {
    @Insert
    fun insert(weightChange: WeightChangeDTO): Long

    @Query("SELECT * FROM weight_change;")
    fun selectAll(): List<WeightChangeDTO>

    @Query("SELECT * FROM weight_change ORDER BY dateTime DESC LIMIT 1;")
    fun selectByTime(): WeightChangeDTO

    @Query("DELETE FROM weight_change")
    fun nukeTable()

}