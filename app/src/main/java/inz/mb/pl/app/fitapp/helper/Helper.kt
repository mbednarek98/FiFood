package inz.mb.pl.app.fitapp.helper

import inz.mb.pl.app.fitapp.MainActivity
import inz.mb.pl.app.fitapp.database.AppDatabase
import inz.mb.pl.app.fitapp.database.exercise.ExerciseDTO
import inz.mb.pl.app.fitapp.database.dish.DishDTO
import inz.mb.pl.app.fitapp.database.saved_dish.SavedDishDTO
import inz.mb.pl.app.fitapp.ui.get_started.GetStartedFragmentOne
import inz.mb.pl.app.fitapp.ui.get_started.GetStartedFragmentTwo
import java.time.LocalDate

object Helper {
    var db: AppDatabase? = null
    var mainActivity : MainActivity? = null
    const val WEIGHT_LOWER_RANGE : Int = 10
    const val WEIGHT_UPPER_RANGE : Int = 600
    const val HEIGHT_LOWER_RANGE : Int = 50
    const val HEIGHT_UPPER_RANGE : Int = 300
    const val DISHES_LOWER_RANGE : Int = 2
    const val DISHES_UPPER_RANGE : Int = 5
    const val AGE_LOWER_RANGE : Int = 15
    const val AGE_UPPER_RANGE : Int = 80
    val WEIGHT_RANGE : IntRange = (WEIGHT_LOWER_RANGE..WEIGHT_UPPER_RANGE)
    val HEIGHT_RANGE : IntRange = (HEIGHT_LOWER_RANGE..HEIGHT_UPPER_RANGE)
    val DISHES_RANGE : IntRange = (DISHES_LOWER_RANGE..DISHES_UPPER_RANGE)
    val AGE_RANGE : IntRange = (AGE_LOWER_RANGE..AGE_UPPER_RANGE)
    var FragmentOne: GetStartedFragmentOne? = null
    var FragmentTwo : GetStartedFragmentTwo? = null
    var selectedDate: LocalDate = LocalDate.now()
    var exerciseDTOList: ArrayList<ExerciseDTO> = ArrayList()
    var dishDTOList: ArrayList<DishDTO> = ArrayList()
    var savedDishesList : ArrayList<SavedDishDTO> = ArrayList()
    fun exercisePerDate(date: LocalDate?): ArrayList<ExerciseDTO> {
        val exerciseDTOS: ArrayList<ExerciseDTO> = ArrayList()
        exerciseDTOList.forEach { exercise ->
            if (exercise.date == date) exerciseDTOS.add(exercise)
        }
        return exerciseDTOS
    }
    fun dishItemPerDate(date: LocalDate?): ArrayList<DishDTO> {
        val dishDTONewList: ArrayList<DishDTO> = ArrayList()
        dishDTOList.forEach { dish ->
            if (dish.date == date) dishDTONewList.add(
                dish
            )
        }
        return dishDTONewList
    }
    fun savedDishItemPerName(query: String): ArrayList<SavedDishDTO> {
        val dishDTONewList: ArrayList<SavedDishDTO> = ArrayList()
        savedDishesList.forEach { dish ->
            if (dish.name_lowercase >= query.lowercase() && dish.name_lowercase <= query.lowercase() + "\uf8ff") {
                dishDTONewList.add(dish)
            }
        }
        return dishDTONewList
    }
    fun dishItemPerDateAndPosition(date: LocalDate?, position: Int?): ArrayList<DishDTO> {
        val dishDTONewList: ArrayList<DishDTO> = ArrayList()
        dishDTOList.forEach { dish ->
            if (dish.date == date && dish.position == position) dishDTONewList.add(
                dish
            )
        }
        return dishDTONewList
    }
}