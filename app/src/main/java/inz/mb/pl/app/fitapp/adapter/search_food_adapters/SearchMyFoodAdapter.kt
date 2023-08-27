package inz.mb.pl.app.fitapp.adapter.search_food_adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import inz.mb.pl.app.fitapp.R
import inz.mb.pl.app.fitapp.SearchFoodActivity
import inz.mb.pl.app.fitapp.adapter.operation_dish_viewholders.NewDishViewHolder
import inz.mb.pl.app.fitapp.adapter.operation_dish_viewholders.NewDishViewHolder.Companion.checkIfIsBlankDouble
import inz.mb.pl.app.fitapp.adapter.operation_dish_viewholders.NewDishViewHolder.Companion.checkIfIsBlankInt
import inz.mb.pl.app.fitapp.helper.Helper
import inz.mb.pl.app.fitapp.database.saved_dish.SavedDishDTO
import inz.mb.pl.app.fitapp.interfaces.MacroInterface
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch


class SearchMyFoodAdapter(private val context : Context, private val activity : SearchFoodActivity, private val listOfItems : List<SavedDishDTO>, private val inflater: LayoutInflater)
    : RecyclerView.Adapter<SearchFoodViewHolder>(), MacroInterface{
    override val job = SupervisorJob()
    override val ioScope by lazy { CoroutineScope(job + Dispatchers.IO) }

    override fun onBindViewHolder(holder: SearchFoodViewHolder, position: Int) {
        assignViewHolderItems(holder,listOfItems[position])
    }

    private fun assignViewHolderItems(holder: SearchFoodViewHolder, item: SavedDishDTO)= with(holder){
        nameOfDishItem.text = item.name
        typeOfDishItem.text = context.resources.getStringArray(R.array.food_type_of_diet)[item.type]
        val newDishWeight = context.resources.getStringArray(R.array.food_units)[item.typeOfUnits]
        weightOfDishItem.text = newDishWeight
        kcalEatenOfDishItem.text = context.resources.getString(R.string.data_kcal, item.kcalEaten.toString())
        carbsOfDishItem.text = context.resources.getString(R.string.g ,item.carbs.toString())
        proteinOfDishItem.text = context.resources.getString(R.string.g ,item.protein.toString())
        fatOfDishItem.text = context.resources.getString(R.string.g ,item.fat.toString())
        layout.setOnClickListener { addNewDish(item,inflater,activity) }
        layout.setOnLongClickListener {
            createNewDish(item)
            return@setOnLongClickListener true
        }
    }

    private fun createNewDish(item : SavedDishDTO){
        val dialog = createNewAlertDialog(context.resources.getString(R.string.new_dish))
        val inflater = inflater
        val dialogView = inflater.inflate(R.layout.add_food_dialog, null)
        val holder = NewDishViewHolder(dialogView)
        with(holder){
            dishCarb.setText(item.carbs.toString())
            dishFat.setText(item.fat.toString())
            dishProtein.setText(item.protein.toString())
            dishKcal.setText(item.kcalEaten.toString())
            mealType.setSelection(item.meal_type)
            dishName.setText(item.name)
            dietType.setSelection(item.type)
            unitType.setSelection(item.typeOfUnits)
        }
        with(dialog){
            setView(dialogView)
            setPositiveButton(context.resources.getString(R.string.done)) { _, _ ->
                val id = Helper.savedDishesList.indexOf(item)
                with(item){
                    carbs = checkIfIsBlankDouble(holder.dishCarb.text.toString())
                    fat =  checkIfIsBlankDouble(holder.dishFat.text.toString())
                    protein =  checkIfIsBlankDouble(holder.dishProtein.text.toString())
                    kcalEaten = checkIfIsBlankInt(holder.dishKcal.text.toString())
                    meal_type = holder.mealType.selectedItemPosition
                    name = holder.dishName.text.toString()
                    name_lowercase = holder.dishName.text.toString().lowercase()
                    type = holder.dietType.selectedItemPosition
                    typeOfUnits = holder.unitType.selectedItemPosition
                }
                ioScope.launch {
                    updateSavedDish(item)
                    Helper.savedDishesList[id] = item
                }.start()
                notifyDataSetChanged()
            }
            setNegativeButton(context.resources.getString(R.string.cancel)) { _, _ -> }
            setNeutralButton(context.resources.getString(R.string.delete)) { _, _ ->
                ioScope.launch {
                    removeSavedDish(item)
                    Helper.savedDishesList.remove(item)
                }.start()
                notifyDataSetChanged()
            }
        }.show()
    }

    override fun getItemCount(): Int = listOfItems.size
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchFoodViewHolder = SearchFoodViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.card_food_template, parent, false))
    override fun getDialogContext(): Context = context
}