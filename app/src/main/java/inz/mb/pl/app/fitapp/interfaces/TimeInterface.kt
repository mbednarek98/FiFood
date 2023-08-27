package inz.mb.pl.app.fitapp.interfaces

import android.content.res.Resources
import inz.mb.pl.app.fitapp.R
import inz.mb.pl.app.fitapp.helper.Helper
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList

interface TimeInterface {
    fun parseStringTLocalDateTime(text : String) : LocalDateTime = LocalDateTime.parse(text)
    fun parseLocalDateTimeToTimeString(text : String) : String = parseStringTLocalDateTime(text).format(
        DateTimeFormatter.ofPattern("HH:mm"))
    fun parseLocalDateTimeToLocalDate(text : String) : LocalDate = parseStringTLocalDateTime(text).toLocalDate()
    fun parseLocalDateToString(text: String) : String = parseLocalDateTimeToLocalDate(text).format(
        DateTimeFormatter.ofPattern("dd/MM/yyyy"))
    fun parseLocalDateTimeToDateString(text : String) : String {
        val dateTime : LocalDateTime = parseStringTLocalDateTime(text)
        val dateNow : LocalDateTime = LocalDateTime.now()
        return when {
            dateTime.year == dateNow.year && dateTime.month == dateNow.month -> {
                when (dateTime.dayOfMonth) {
                    dateNow.dayOfMonth -> return getInterfaceResources().getString(R.string.today_day)
                    dateNow.dayOfMonth - 1 -> return getInterfaceResources().getString(R.string.yesterday_day)
                    else -> parseLocalDateToString(text)
                }
            }
            else -> parseLocalDateToString(text)
        }
    }

    fun getListOfDaysAndExerciseCount(date : LocalDate) : Map<String, Int> {
        val mapOfValues : MutableMap<String, Int> = mutableMapOf()
        for(i in 0..6){
            val newDate = date.minusDays(i.toLong())
            val newListOfExercise = Helper.exercisePerDate(newDate)
            var count = 0
            newListOfExercise.forEach { exercise -> count += exercise.kcalBurned }
            mapOfValues[SimpleDateFormat("EEE").format(Date.from(newDate.atStartOfDay(ZoneId.systemDefault()).toInstant()))] = count
        }
        return mapOfValues
    }

    fun getListOfDishCount(date : LocalDate) : ArrayList<Int> {
        val listOfValues : ArrayList<Int> = ArrayList()
        for(i in 0..6){
            val newDate = date.minusDays(i.toLong())
            val newListOfExercise = Helper.dishItemPerDate(newDate)
            var count = 0
            newListOfExercise.forEach { dish -> count += dish.kcalEaten }
            listOfValues.add(count)
        }
        return listOfValues
    }

    fun getInterfaceResources() : Resources

}