package inz.mb.pl.app.fitapp.adapter.search_food_adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import inz.mb.pl.app.fitapp.R
import inz.mb.pl.app.fitapp.SearchFoodActivity
import inz.mb.pl.app.fitapp.database.saved_dish.SavedDishDTO
import inz.mb.pl.app.fitapp.interfaces.MacroInterface
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import android.content.Intent
import android.net.Uri


class SearchFoodViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
    val nameOfDishItem : TextView = itemView.findViewById(R.id.dish_item_name_text_view)
    val typeOfDishItem : TextView = itemView.findViewById(R.id.dish_item_type_of_food)
    val weightOfDishItem : TextView = itemView.findViewById(R.id.dish_item_weight)
    val kcalEatenOfDishItem: TextView = itemView.findViewById(R.id.dish_item_kcal_eaten_text_view)
    val carbsOfDishItem : TextView = itemView.findViewById(R.id.dish_item_carbs)
    val proteinOfDishItem : TextView = itemView.findViewById(R.id.dish_item_protein)
    val fatOfDishItem : TextView = itemView.findViewById(R.id.dish_item_fat)
    val layout : LinearLayout = itemView.findViewById(R.id.card_food_template_layout)
}

class SearchFoodFragmentAdapter(private val context : Context, private val activity : SearchFoodActivity, private val listOfItems : List<SavedDishDTO>, private val inflater: LayoutInflater) : RecyclerView.Adapter<SearchFoodViewHolder>(), MacroInterface{
    override val job = SupervisorJob()
    override val ioScope by lazy { CoroutineScope(job + Dispatchers.IO) }

    override fun onBindViewHolder(holder: SearchFoodViewHolder, position: Int) {
        assignViewHolderItems(holder, listOfItems[position])
    }


    private fun assignViewHolderItems(holder : SearchFoodViewHolder, item: SavedDishDTO) = with(holder){
        nameOfDishItem.text = item.name
        typeOfDishItem.text = context.resources.getStringArray(R.array.food_type_of_diet)[item.type]
        weightOfDishItem.text = context.resources.getStringArray(R.array.food_units)[item.typeOfUnits]
        kcalEatenOfDishItem.text = context.resources.getString(R.string.data_kcal, item.kcalEaten.toString())
        carbsOfDishItem.text = context.resources.getString(R.string.g ,item.carbs.toString())
        proteinOfDishItem.text = context.resources.getString(R.string.g ,item.protein.toString())
        fatOfDishItem.text = context.resources.getString(R.string.g ,item.fat.toString())
        layout.setOnClickListener { addNewDish(item,inflater,activity) }
        if(item.url.isNotBlank()) {
            layout.setOnLongClickListener {
                val browse = Intent(Intent.ACTION_VIEW, Uri.parse(item.url))
                context.startActivity(browse)
                return@setOnLongClickListener true
            }
        }
    }



    override fun getItemCount(): Int = listOfItems.size
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchFoodViewHolder = SearchFoodViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.card_food_template, parent, false))
    override fun getDialogContext(): Context = context
}