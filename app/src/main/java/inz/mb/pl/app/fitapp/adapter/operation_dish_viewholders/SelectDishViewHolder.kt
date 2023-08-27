package inz.mb.pl.app.fitapp.adapter.operation_dish_viewholders

import android.view.View
import android.widget.NumberPicker
import android.widget.TextView
import inz.mb.pl.app.fitapp.R

class SelectDishViewHolder (dialogView : View){
    val weightName : TextView = dialogView.findViewById(R.id.add_food_weight_text)
    val beforeNumberPicker : NumberPicker = dialogView.findViewById(R.id.select_food_number_before_coma)
    val afterNumberPicker : NumberPicker = dialogView.findViewById(R.id.select_food_number_after_coma)
    val kcalName : TextView = dialogView.findViewById(R.id.select_dish_dialog_kcal)
    val dishCarbs : TextView = dialogView.findViewById(R.id.select_dish_dialog_carbs_text)
    val dishProtein : TextView = dialogView.findViewById(R.id.select_dish_dialog_protein_text)
    val dishFat : TextView = dialogView.findViewById(R.id.select_dish_dialog_fat_text)

}