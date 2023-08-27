package inz.mb.pl.app.fitapp.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import inz.mb.pl.app.fitapp.database.dish.DishDAO
import inz.mb.pl.app.fitapp.database.dish.DishDTO
import inz.mb.pl.app.fitapp.database.exercise.ExerciseDAO
import inz.mb.pl.app.fitapp.database.exercise.ExerciseDTO
import inz.mb.pl.app.fitapp.database.saved_dish.SavedDishDAO
import inz.mb.pl.app.fitapp.database.saved_dish.SavedDishDTO
import inz.mb.pl.app.fitapp.database.water_change.WaterChangeDAO
import inz.mb.pl.app.fitapp.database.water_change.WaterChangeDTO
import inz.mb.pl.app.fitapp.database.weight_change.WeightChangeDAO
import inz.mb.pl.app.fitapp.database.weight_change.WeightChangeDTO

@Database(
    entities = [WeightChangeDTO::class,
               WaterChangeDTO::class,
               ExerciseDTO::class,
               DishDTO::class,
               SavedDishDTO::class],
    version = 17
)
@TypeConverters(DateConverters::class)
abstract class AppDatabase : RoomDatabase(){
    abstract val weightChange: WeightChangeDAO
    abstract val waterChange: WaterChangeDAO
    abstract val dish: DishDAO
    abstract val savedDish : SavedDishDAO
    abstract val exercise: ExerciseDAO
    companion object {
        fun open(context: Context) = Room.databaseBuilder(
            context, AppDatabase::class.java, "fitappdb"
        ).fallbackToDestructiveMigration()
            .allowMainThreadQueries().build()
    }
}