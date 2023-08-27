package inz.mb.pl.app.fitapp.adapter.dish_adapters

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import inz.mb.pl.app.fitapp.R
import inz.mb.pl.app.fitapp.helper.Helper
import inz.mb.pl.app.fitapp.database.dish.DishDTO
import inz.mb.pl.app.fitapp.interfaces.DeleteDialogInterface
import inz.mb.pl.app.fitapp.interfaces.QueryInterface
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.time.LocalDate

class DishItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val nameOfDishItem : TextView = itemView.findViewById(R.id.dish_item_name_text_view)
    val typeOfDishItem : TextView = itemView.findViewById(R.id.dish_item_type_of_food)
    val weightOfDishItem : TextView = itemView.findViewById(R.id.dish_item_weight)
    val kcalEatenOfDishItem: TextView = itemView.findViewById(R.id.dish_item_kcal_eaten_text_view)
    val carbsOfDishItem : TextView = itemView.findViewById(R.id.dish_item_carbs)
    val proteinOfDishItem : TextView = itemView.findViewById(R.id.dish_item_protein)
    val fatOfDishItem : TextView = itemView.findViewById(R.id.dish_item_fat)
}

class DishItemAdapter (private val context : Context, private var dishDTOList: ArrayList<DishDTO>, private val date : LocalDate?, private val mealPosition : Int, private val runnable: Runnable) :
    RecyclerView.Adapter<DishItemViewHolder>(), QueryInterface{
    private val job = SupervisorJob()
    private val ioScope by lazy { CoroutineScope(job + Dispatchers.IO) }
    private lateinit var dishItem : DishDTO
    override fun onBindViewHolder(holder: DishItemViewHolder, position: Int) {
        dishItem = dishDTOList[position]
        assignViewHolderItems(dishItem,holder)
    }

    private fun assignViewHolderItems(item : DishDTO, holder : DishItemViewHolder) = with(holder) {
        nameOfDishItem.text = item.name
        val unit = context.resources.getStringArray(R.array.food_only_units)[item.typeOfUnits]
        val newDishWeight = "${item.weight} $unit"
        weightOfDishItem.text = newDishWeight
        typeOfDishItem.text = context.resources.getStringArray(R.array.food_type_of_diet)[item.type]
        kcalEatenOfDishItem.text = context.resources.getString(R.string.data_kcal, item.kcalEaten.toString())
        carbsOfDishItem.text = context.resources.getString(R.string.g, item.carbs.toString())
        proteinOfDishItem.text = context.resources.getString(R.string.g,item.protein.toString())
        fatOfDishItem.text = context.resources.getString(R.string.g,item.fat.toString())
        if(item.url.isNotBlank()){
            itemView.setOnLongClickListener {
                val browse = Intent(Intent.ACTION_VIEW, Uri.parse(item.url))
                context.startActivity(browse)
                return@setOnLongClickListener true
            }
        }
    }


    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): DishItemViewHolder = DishItemViewHolder(LayoutInflater.from(viewGroup.context).inflate(R.layout.card_food_template, viewGroup, false))
    override fun getItemCount(): Int = dishDTOList.size


    fun removeAt() {
        val id = Helper.dishDTOList.indexOf(dishItem)
        Helper.dishDTOList.removeAt(id)
        dishDTOList = Helper.dishItemPerDateAndPosition(date,mealPosition)
        ioScope.launch {
            removeDish(dishItem)
        }
        notifyDataSetChanged()
        runnable.run()
    }

}
