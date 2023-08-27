package inz.mb.pl.app.fitapp.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import inz.mb.pl.app.fitapp.MainActivity
import inz.mb.pl.app.fitapp.R
import inz.mb.pl.app.fitapp.SearchFoodActivity
import inz.mb.pl.app.fitapp.database.dish.DishDTO
import inz.mb.pl.app.fitapp.helper.Helper
import java.time.LocalDate

class FoodTileViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
    val nameOfMeal : TextView = itemView.findViewById(R.id.edit_text_meal)
    val nameOfDishes : TextView = itemView.findViewById(R.id.edit_text_detailed)
    val kcalEaten : TextView = itemView.findViewById(R.id.edit_text_kcal_tile)
    val kcalName : TextView = itemView.findViewById(R.id.card_tile_text_kcal)
    val layout : ConstraintLayout = itemView.findViewById(R.id.food_tile_layout)
    val addFoodButton : FloatingActionButton = itemView.findViewById(R.id.food_tile_button)
}

class FoodTileAdapter(private val mainActivity: MainActivity, private var context : Context, private val dishes_count : Int, private val calorieIntake : Int) :
    RecyclerView.Adapter<FoodTileViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodTileViewHolder = FoodTileViewHolder(LayoutInflater.from(parent.context).inflate(
        R.layout.food_tile, parent, false))

    override fun onBindViewHolder(holder: FoodTileViewHolder, position: Int) {
        val newListOfDishes : ArrayList<DishDTO> = Helper.dishItemPerDateAndPosition(LocalDate.now(),position)
        val titleString : String = context.resources.getStringArray(R.array.dishes_name_array)[position]
        val kcalCount = getCount(newListOfDishes)
        setUpKcalLayout(holder,kcalCount,titleString,position)
        val nameOfDishes = newListOfDishes.joinToString { it.name }
        holder.nameOfDishes.text = nameOfDishes
        if(nameOfDishes.isBlank()) setUpDishes(holder, position)
        holder.nameOfMeal.text = titleString
        holder.kcalEaten.text = kcalCount.toString()
        setUpBackground(position,holder)
        checkUserDishCount(position,holder)
    }

    override fun getItemCount(): Int = 5

    private fun getCount( list : ArrayList<DishDTO>) : Int{
        var count = 0
        for(dish in list) count += dish.kcalEaten
        return count
    }

    private fun setUpDishes(holder: FoodTileViewHolder, i : Int){
        when (dishes_count) {
            2 -> {
                when (i) {
                    0 -> holder.nameOfDishes.text = context.resources.getString(R.string.recommended_kcal, (calorieIntake*0.4).toInt().toString())
                    2 -> holder.nameOfDishes.text = context.resources.getString(R.string.recommended_kcal, (calorieIntake*0.6).toInt().toString())
                }
            }
            3 -> {
                when (i) {
                    0 -> holder.nameOfDishes.text = context.resources.getString(R.string.recommended_kcal, (calorieIntake*0.4).toInt().toString())
                    1 -> holder.nameOfDishes.text = context.resources.getString(R.string.recommended_kcal, (calorieIntake*0.05).toInt().toString())
                    2 -> holder.nameOfDishes.text = context.resources.getString(R.string.recommended_kcal, (calorieIntake*0.55).toInt().toString())
                }
            }
            4 -> {
                when (i) {
                    0 -> holder.nameOfDishes.text = context.resources.getString(R.string.recommended_kcal, (calorieIntake*0.25).toInt().toString())
                    1 -> holder.nameOfDishes.text = context.resources.getString(R.string.recommended_kcal, (calorieIntake*0.05).toInt().toString())
                    2 -> holder.nameOfDishes.text = context.resources.getString(R.string.recommended_kcal, (calorieIntake*0.45).toInt().toString())
                    4 -> holder.nameOfDishes.text = context.resources.getString(R.string.recommended_kcal, (calorieIntake*0.25).toInt().toString())
                }
            }
            5 -> {
                when (i) {
                    0 -> holder.nameOfDishes.text = context.resources.getString(R.string.recommended_kcal, (calorieIntake*0.25).toInt().toString())
                    1 -> holder.nameOfDishes.text = context.resources.getString(R.string.recommended_kcal, (calorieIntake*0.05).toInt().toString())
                    2 -> holder.nameOfDishes.text = context.resources.getString(R.string.recommended_kcal, (calorieIntake*0.40).toInt().toString())
                    3 -> holder.nameOfDishes.text = context.resources.getString(R.string.recommended_kcal, (calorieIntake*0.05).toInt().toString())
                    4 -> holder.nameOfDishes.text = context.resources.getString(R.string.recommended_kcal, (calorieIntake*0.25).toInt().toString())
                }
            }
        }
    }


    private fun setUpKcalLayout(holder : FoodTileViewHolder, kcalCount : Int, titleString: String, position: Int) = with(holder){
        when {
            kcalCount != 0 -> {
                addFoodButton.isEnabled = false
                addFoodButton.visibility = View.GONE
                kcalEaten.visibility = View.VISIBLE
                kcalName.visibility = View.VISIBLE
            }
            else -> {
                addFoodButton.isEnabled = true
                addFoodButton.visibility = View.VISIBLE
                kcalEaten.visibility = View.GONE
                kcalName.visibility = View.GONE
            }
        }
        addFoodButton.setOnClickListener {
            goToFoodSearchActivity(titleString, position)
        }
    }

    private fun checkUserDishCount(i : Int, foodTileViewHolder: FoodTileViewHolder){
        when {
            dishes_count == 2 && (i in (3..4) || i == 1) -> {
                foodTileViewHolder.layout.layoutParams = ConstraintLayout.LayoutParams(0,0)
            }
            dishes_count == 3 && i in (3..4) -> {
                foodTileViewHolder.layout.layoutParams = ConstraintLayout.LayoutParams(0,0)
            }
            dishes_count == 4 && i == 3 -> {
                foodTileViewHolder.layout.layoutParams = ConstraintLayout.LayoutParams(0,0)
            }
        }
    }

    private fun setUpBackground(i : Int, holder : FoodTileViewHolder){
        when(i){
            0 -> holder.layout.setBackgroundResource(R.drawable.bg_card_breakfast_one)
            1 -> holder.layout.setBackgroundResource(R.drawable.bg_card_breakfast_two)
            2 -> holder.layout.setBackgroundResource(R.drawable.bg_card_breakfast_five)
            3 -> holder.layout.setBackgroundResource(R.drawable.bg_card_breakfast_four)
            4 -> holder.layout.setBackgroundResource(R.drawable.bg_card_breakfast_three)
        }
    }

    private fun goToFoodSearchActivity(titleString : String, position : Int){
        val newIntent = Intent(context, SearchFoodActivity::class.java)
        newIntent.putExtra("name_of_dish", titleString)
        newIntent.putExtra("position", position)
        context.startActivity(newIntent)
        mainActivity.slideRightToLeft(mainActivity)
    }
}