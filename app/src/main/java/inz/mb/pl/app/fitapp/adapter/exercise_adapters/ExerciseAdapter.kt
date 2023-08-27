package inz.mb.pl.app.fitapp.adapter.exercise_adapters

import android.content.Context
import android.widget.ArrayAdapter
import inz.mb.pl.app.fitapp.database.exercise.ExerciseDTO

import android.widget.TextView

import android.view.LayoutInflater
import android.view.View

import android.view.ViewGroup
import android.widget.ImageView
import inz.mb.pl.app.fitapp.R

class ExerciseViewHolder(val view: View){
    val exerciseImg: ImageView? = view.findViewById(R.id.image_card)
    val exerciseName: TextView? = view.findViewById(R.id.name_card)
    val exerciseType: TextView? = view.findViewById(R.id.type_card)
    val exerciseTime: TextView? = view.findViewById(R.id.time_card)
    val exerciseKCAL: TextView? = view.findViewById(R.id.kcal_burn_card)
}

class ExerciseAdapter(context: Context, exerciseDTOS: List<ExerciseDTO?>) :
    ArrayAdapter<ExerciseDTO>(context, 0, exerciseDTOS) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val holder = onCreateViewHolder(parent)
        val exerciseDTO: ExerciseDTO? = getItem(position)
        assignViewHolderItems(holder, exerciseDTO)
        return holder.view
    }


    private fun assignViewHolderItems(holder: ExerciseViewHolder, exerciseDTO : ExerciseDTO?) = with(holder){
        exerciseImg?.setBackgroundResource(context.resources.getIdentifier(context.resources.getStringArray(R.array.exercise_group_image_array)[exerciseDTO?.typeOfExercise ?: 0], "drawable", context.packageName))
        exerciseName?.text = context.resources.getStringArray(context.resources.getIdentifier("${context.resources.getStringArray(R.array.exercise_group_image_array)[exerciseDTO?.typeOfExercise ?: 0]}_description_array", "array", context.packageName))[exerciseDTO?.name ?: 0]
        exerciseType?.text = context.resources.getStringArray(R.array.exercise_group_array)[exerciseDTO?.typeOfExercise ?: 0]
        exerciseTime?.text = exerciseDTO?.timeOfExercise
        exerciseKCAL?.text = context.resources.getString(R.string.data_kcal,exerciseDTO?.kcalBurned.toString())

    }

    private fun onCreateViewHolder(viewGroup : ViewGroup) : ExerciseViewHolder = ExerciseViewHolder(LayoutInflater.from(viewGroup.context).inflate(R.layout.exercise_cell, viewGroup, false))

}