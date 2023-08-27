package inz.mb.pl.app.fitapp.interfaces

import android.content.res.Resources
import inz.mb.pl.app.fitapp.models.User

interface CalorieCalcInterface {
    fun calculateDailyCaloricIntake(user : User):Int = with(user){
        val equation : Double = (10*weight) +(6.25*height) + (- 5*age) + weightProgressCalc(progress)
        return when(gender){
            0 -> ((equation + 5)*activityCalc(activity)).toInt()
            1 -> ((equation -161)*activityCalc(activity)).toInt()
            else -> ((equation -78)*activityCalc(activity)).toInt()
        }
    }

    fun weightProgressCalc(progress : Int): Int = when (progress) {
            0 -> 1000
            1 -> 500
            2 -> 250
            3 -> -250
            4 -> -500
            5 -> -1000
            else -> 0
        }


    fun activityCalc(activity : Int): Double = when (activity) {
            0 -> 1.375
            1 -> 1.55
            2 -> 1.6375
            3 -> 1.725
            4 -> 1.9
            else -> 1.2
        }



    fun getResources() : Resources
}