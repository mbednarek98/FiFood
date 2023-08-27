package inz.mb.pl.app.fitapp.database.water_change

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface WaterChangeDAO {
    @Insert
    fun insert(waterChange: WaterChangeDTO) : Long

    @Query("SELECT * FROM water_change;")
    fun selectAll(): List<WaterChangeDTO>

    @Query("SELECT * FROM water_change WHERE day =:day AND month=:month AND year=:year")
    fun selectValueByDate(day: Int, month: Int, year: Int) : WaterChangeDTO

    @Query("UPDATE water_change SET waterChange =:waterChange WHERE id =:id ")
    fun update(id: Int, waterChange: Int)

    @Query("DELETE FROM water_change")
    fun nukeTable()
}