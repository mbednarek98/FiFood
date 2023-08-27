package inz.mb.pl.app.fitapp.adapter.dish_adapters

import android.content.Context
import android.content.Intent
import android.graphics.*
import android.graphics.drawable.ColorDrawable
import androidx.recyclerview.widget.RecyclerView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatButton
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView.RecycledViewPool
import inz.mb.pl.app.fitapp.MainActivity
import inz.mb.pl.app.fitapp.R
import inz.mb.pl.app.fitapp.SearchFoodActivity
import inz.mb.pl.app.fitapp.callback.DeleteCallback
import inz.mb.pl.app.fitapp.helper.Helper
import inz.mb.pl.app.fitapp.database.dish.DishDTO
import java.time.LocalDate

class DishViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val nameOfDish : TextView = itemView.findViewById(R.id.dish_type_of_text_view)
    val kcalOverallEaten : TextView = itemView.findViewById(R.id.kcal_eaten_text_view)
    val dishRecycleView: RecyclerView = itemView.findViewById(R.id.dish_item_recyclerView)
    val foodButton : AppCompatButton = itemView.findViewById(R.id.button_addFood)
    val constraintLayout: ConstraintLayout = itemView.findViewById(R.id.constraint_layout_dish_card)
}

class DishAdapter (private val context: Context, private val date: LocalDate?, private val dishes_count : Int?, private val mainActivity: MainActivity?) :
    RecyclerView.Adapter<DishViewHolder>() {
    private val viewPool = RecycledViewPool()
    private lateinit var dishItemList : ArrayList<DishDTO>
    override fun onBindViewHolder(holder: DishViewHolder, position: Int) {
        dishItemList = Helper.dishItemPerDateAndPosition(date,position)
        assignViewHolderItems(position, holder)
        setUpAdapter(dishItemList,holder,position,getRunnable(position,holder))
        checkUserDishCount(position, holder)
    }

    private fun assignViewHolderItems(position: Int, holder: DishViewHolder) = with(holder) {
        val titleString = context.resources.getStringArray(R.array.dishes_name_array)[position]
        foodButton.setOnClickListener { goToFoodSearchActivity(titleString, position) }
        nameOfDish.text = titleString
    }

    private fun getRunnable(position: Int, holder: DishViewHolder) : Runnable = Runnable{
        dishItemList = Helper.dishItemPerDateAndPosition(date,position)
        holder.kcalOverallEaten.text = countOverallKcal(dishItemList)
    }


    private fun setUpAdapter(dishDTOList : ArrayList<DishDTO>, holder: DishViewHolder, i : Int, runnable: Runnable)=  with(holder) {
        kcalOverallEaten.text = countOverallKcal(dishDTOList)
        val layoutManager = LinearLayoutManager(dishRecycleView.context)
        layoutManager.initialPrefetchItemCount = dishDTOList.size
        with(dishRecycleView) {
            this.layoutManager = layoutManager
            adapter = DishItemAdapter(context, dishDTOList, date, i, runnable)
            val swipeHandler = object : DeleteCallback(context) {
                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val adapter = dishRecycleView.adapter as DishItemAdapter
                    adapter.removeAt()
                }
            }
            val itemTouchHelper = ItemTouchHelper(swipeHandler)
            itemTouchHelper.attachToRecyclerView(this)
            setRecycledViewPool(viewPool)
        }
    }


    private fun checkUserDishCount(i : Int, dishViewHolder: DishViewHolder){
        when {
            dishes_count == 2 && (i in (3..4) || i == 1) -> {
                dishViewHolder.constraintLayout.layoutParams = ConstraintLayout.LayoutParams(0,0)
            }
            dishes_count == 3 && i in (3..4) -> {
                dishViewHolder.constraintLayout.layoutParams = ConstraintLayout.LayoutParams(0,0)
            }
            dishes_count == 4 && i == 3 -> {
                dishViewHolder.constraintLayout.layoutParams = ConstraintLayout.LayoutParams(0,0)
            }
        }
    }

    private fun goToFoodSearchActivity(titleString : String, position : Int){
        val newIntent = Intent(context,SearchFoodActivity::class.java)
        newIntent.putExtra("name_of_dish", titleString)
        newIntent.putExtra("position", position)
        context.startActivity(newIntent)
        mainActivity?.slideRightToLeft(mainActivity)
    }

    private fun countOverallKcal(dishDTOList : List<DishDTO>) : String = context.resources.getString(R.string.data_kcal, dishDTOList.sumOf{ it.kcalEaten }.toString())
    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): DishViewHolder = DishViewHolder(LayoutInflater.from(viewGroup.context).inflate(R.layout.dish_card, viewGroup, false))
    override fun getItemCount(): Int = 5


}



