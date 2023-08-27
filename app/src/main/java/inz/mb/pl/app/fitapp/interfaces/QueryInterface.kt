package inz.mb.pl.app.fitapp.interfaces

import inz.mb.pl.app.fitapp.database.dish.DishDTO
import inz.mb.pl.app.fitapp.database.exercise.ExerciseDTO
import inz.mb.pl.app.fitapp.database.saved_dish.SavedDishDTO
import inz.mb.pl.app.fitapp.database.water_change.WaterChangeDTO
import inz.mb.pl.app.fitapp.database.weight_change.WeightChangeDTO
import inz.mb.pl.app.fitapp.enums.DBTypeEnum
import inz.mb.pl.app.fitapp.helper.Helper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

interface QueryInterface {

    suspend fun getFromDBWeightChangeByTime() : WeightChangeDTO? = Helper.db?.weightChange?.selectByTime()


    fun insertWeightChange(weightChange: WeightChangeDTO){
        Helper.db?.weightChange?.insert(weightChange)
    }

    suspend fun insertWaterChange(waterChange: WaterChangeDTO){
        Helper.db?.waterChange?.insert(waterChange)
    }

    suspend fun selectWaterChangeByDate(day: Int, month: Int, year: Int) : WaterChangeDTO? = Helper.db?.waterChange?.selectValueByDate(day, month, year)

    suspend fun updateWaterChange(id: Int, waterChange: Int){
        Helper.db?.waterChange?.update(id, waterChange)
    }


    suspend fun getAllExercises() : List<ExerciseDTO>? = Helper.db?.exercise?.getAll()


    suspend fun addNewExercise(exercise : ExerciseDTO) : Long? = Helper.db?.exercise?.insert(exercise)


    suspend fun addNewDish(dishDTO: DishDTO) : Long? = Helper.db?.dish?.insert(dishDTO)


    suspend fun getAllDish() : List<DishDTO>? = Helper.db?.dish?.getAll()

    suspend fun insertAllDish(listOfDishes : List<DishDTO>) : List<Long?>? = Helper.db?.dish?.insertAll(listOfDishes)


    suspend fun removeDish(dishDTO: DishDTO){
        Helper.db?.dish?.delete(dishDTO)
    }


    suspend fun addNewSavedDish(savedDishDTO: SavedDishDTO) : Long?{
        return Helper.db?.savedDish?.insert(savedDishDTO)
    }

    suspend fun getAllSavedDish() : List<SavedDishDTO>? {
        return Helper.db?.savedDish?.getAll()
    }

    suspend fun removeSavedDish(savedDishDTO: SavedDishDTO){
        Helper.db?.savedDish?.delete(savedDishDTO)
    }

    suspend fun updateSavedDish(savedDishDTO: SavedDishDTO){
        Helper.db?.savedDish?.update(savedDishDTO)
    }


    suspend fun removeExercise(exercise: ExerciseDTO){
        Helper.db?.exercise?.delete(exercise)
    }

    suspend fun updateExercise(exercise: ExerciseDTO){
        Helper.db?.exercise?.update(exercise)
    }




    fun selectData(day: Int? = null, month: Int? = null, year: Int? = null, type : DBTypeEnum) : Any? = runBlocking{
        var change : Any?
        withContext(Dispatchers.Default) {
            change = when {
                type == DBTypeEnum.EXERCISE -> getAllExercises()
                type == DBTypeEnum.WEIGHT -> getFromDBWeightChangeByTime()
                type == DBTypeEnum.DISH -> getAllDish()
                type == DBTypeEnum.SAVEDDISH -> getAllSavedDish()
                day != null && month != null && year != null -> {
                    when (type) {
                        DBTypeEnum.WATER -> selectWaterChangeByDate(day, month, year)
                        else -> null
                    }
                }
                else -> null
            }
        }
        return@runBlocking change
    }
}