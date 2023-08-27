package inz.mb.pl.app.fitapp.interfaces

import android.view.LayoutInflater
import android.widget.NumberPicker
import inz.mb.pl.app.fitapp.R
import inz.mb.pl.app.fitapp.SearchFoodActivity
import inz.mb.pl.app.fitapp.adapter.operation_dish_viewholders.SelectDishViewHolder
import inz.mb.pl.app.fitapp.database.dish.DishDTO
import inz.mb.pl.app.fitapp.database.saved_dish.SavedDishDTO
import kotlinx.coroutines.*

interface MacroInterface : DialogInterface, QueryInterface {
    val job : CompletableJob
    val ioScope : CoroutineScope


    fun addNewDish(item : SavedDishDTO, inflater : LayoutInflater, activity : SearchFoodActivity) {
        val dialog = createNewAlertDialog(item.name)
        val dialogView = inflater.inflate(R.layout.select_food_dialog, null)
        val holder = SelectDishViewHolder(dialogView)
        holder.weightName.text = getDialogContext().resources.getString(R.string.weight_data,
            getDialogContext().resources.getStringArray(R.array.food_only_units)[item.typeOfUnits])
        when (item.typeOfUnits) {
            2 -> setUpDigits(holder.beforeNumberPicker, 0, 2000, false, 1)
            else -> setUpDigits(holder.beforeNumberPicker, 0, 2000, false, 100)
        }
        setUpDigits(holder.afterNumberPicker)
        calculateKcalWithChange(holder, item)
        holder.beforeNumberPicker.setOnValueChangedListener { _, _, _ ->
            calculateKcalWithChange(holder, item)
        }
        holder.afterNumberPicker.setOnValueChangedListener { _, _, _ ->
            calculateKcalWithChange(holder, item)
        }
        with(dialog) {
            setView(dialogView)
            setPositiveButton(context.resources.getString(R.string.done)) { _, _ ->
                if (!inz.mb.pl.app.fitapp.helper.Helper.savedDishesList.contains(item)) {
                    ioScope.launch {
                        val id = addNewSavedDish(item)
                        item.id = id?.toInt() ?: 0
                        inz.mb.pl.app.fitapp.helper.Helper.savedDishesList.add(item)
                    }.start()
                }
                val newDish =
                    DishDTO(weight = "${holder.beforeNumberPicker.value}.${holder.afterNumberPicker.value}".toDouble()
                        .toInt(),
                        name = item.name,
                        type = item.type,
                        typeOfUnits = item.typeOfUnits,
                        kcalEaten = item.kcalEaten,
                        carbs = item.carbs,
                        protein = item.protein,
                        fat = item.fat,
                        position = activity.position,
                        date = inz.mb.pl.app.fitapp.helper.Helper.selectedDate,
                        url = item.url)
                ioScope.launch {
                    val id = addNewDish(newDish)
                    newDish.id = id?.toInt() ?: 0
                    inz.mb.pl.app.fitapp.helper.Helper.dishDTOList.add(newDish)
                }.start()
                activity.finish()
            }
            setNegativeButton(context.resources.getString(R.string.cancel)) { _, _ -> }
        }.show()
    }

    private fun setUpDigits(numberPicker: NumberPicker, lowerRange : Int = 0, upperRange : Int = 9, ifWheel : Boolean = true, number: Int = 0){
        numberPicker.maxValue = upperRange
        numberPicker.minValue = lowerRange
        numberPicker.value = number
        numberPicker.wrapSelectorWheel = ifWheel
    }

    fun getFromNumberPickerDouble (firstNumber : Int, secondNumber : Int) : Double = "$firstNumber.$secondNumber".toDouble()

    fun calculateKcalWithChange(holder : SelectDishViewHolder, item : SavedDishDTO){
        with(holder){
            val number = getFromNumberPickerDouble(beforeNumberPicker.value, afterNumberPicker.value)
            val kcal = (item.kcalEaten*number)
            val (carbs, protein, fat) = getMacrosValues(item, number)
            when (item.typeOfUnits) {
                2 -> changeViewHolderItems(kcal, carbs, protein, fat)
                else -> changeViewHolderItems(kcal / 100, carbs / 100, protein / 100, fat / 100)
            }
        }
    }
    fun SelectDishViewHolder.changeViewHolderItems(kcal: Double, carbs: Double, protein: Double, fat: Double) {
        kcalName.text = getDialogContext().resources.getString(R.string.data_kcal, kcal.toInt().toString())
        dishCarbs.text = getDialogContext().resources.getString(R.string.g, carbs.toInt().toString())
        dishProtein.text = getDialogContext().resources.getString(R.string.g, protein.toInt().toString())
        dishFat.text = getDialogContext().resources.getString(R.string.g, fat.toInt().toString())
    }

    fun getMacrosValues(item: SavedDishDTO, number: Double): Triple<Double, Double, Double> {
        val carbs = (item.carbs * number)
        val protein = (item.protein * number)
        val fat = (item.fat * number)
        return Triple(carbs, protein, fat)
    }
}