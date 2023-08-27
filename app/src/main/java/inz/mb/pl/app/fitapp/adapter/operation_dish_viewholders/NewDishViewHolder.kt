package inz.mb.pl.app.fitapp.adapter.operation_dish_viewholders

import android.view.View
import android.widget.EditText
import androidx.appcompat.widget.AppCompatSpinner
import inz.mb.pl.app.fitapp.R

class NewDishViewHolder(dialogView : View){
    companion object {
        fun checkIfIsBlankDouble(text : String) : Double = when {
            text.isBlank() -> 0.0
            else -> text.toDouble()
        }

        fun checkIfIsBlankInt(text : String) : Int = when {
            text.isBlank() -> 0
            else -> text.toInt()
        }

    }
    val dishName : EditText = dialogView.findViewById(R.id.dialog_dish_name_edit_text)
    val dietType : AppCompatSpinner = dialogView.findViewById(R.id.dialog_dish_type_of_diet_spinner)
    val mealType : AppCompatSpinner = dialogView.findViewById(R.id.dialog_dish_type_of_meal_spinner)
    val unitType : AppCompatSpinner = dialogView.findViewById(R.id.dialog_dish_type_of_unit_spinner)
    val dishKcal : EditText = dialogView.findViewById(R.id.dish_dialog_kcal_edit_text)
    val dishCarb : EditText = dialogView.findViewById(R.id.dish_dialog_carbs_edit_text)
    val dishProtein : EditText = dialogView.findViewById(R.id.dish_dialog_protein_edit_text)
    val dishFat : EditText = dialogView.findViewById(R.id.dish_dialog_fat_edit_text)
}