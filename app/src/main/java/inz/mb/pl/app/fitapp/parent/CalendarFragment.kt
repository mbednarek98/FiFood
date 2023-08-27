package inz.mb.pl.app.fitapp.parent

import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import inz.mb.pl.app.fitapp.adapter.CalendarAdapter
import inz.mb.pl.app.fitapp.helper.Helper
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.ArrayList

abstract class CalendarFragment : BasicFragment(), CalendarAdapter.OnItemListener{
    var monthYearText: TextView? = null
    var calendarRecyclerView: RecyclerView? = null

    fun setupCreateView(){
        initWidgets()
        setWeekView()
        buttonSetUp()
    }

    fun monthYearFromDate(date: LocalDate): String =
        date.format(DateTimeFormatter.ofPattern("MMMM yyyy"))

    fun daysInWeekArray(selectedDate: LocalDate): ArrayList<LocalDate> {
        val days: ArrayList<LocalDate> = ArrayList()
        var current: LocalDate = mondayForDate(selectedDate)!!
        val endDate: LocalDate = current.plusWeeks(1)
        while (current.isBefore(endDate)) {
            days.add(current)
            current = current.plusDays(1)
        }
        return days
    }

    private fun mondayForDate(current: LocalDate): LocalDate? {
        var currentDate: LocalDate = current
        while (current.isAfter(current.minusWeeks(1))) {
            when (currentDate.dayOfWeek) {
                DayOfWeek.MONDAY -> return currentDate
                else -> currentDate = currentDate.minusDays(1)
            }
        }
        return null
    }


    abstract fun initWidgets()
    abstract fun buttonSetUp()
    abstract fun setWeekView()
    fun previousWeekAction() {
        Helper.selectedDate = Helper.selectedDate.minusWeeks(1)
        setWeekView()
    }

    fun nextWeekAction() {
        Helper.selectedDate = Helper.selectedDate.plusWeeks(1)
        setWeekView()
    }

    override fun onItemClick(position: Int, date: LocalDate?) {
        Helper.selectedDate = date!!
        setWeekView()
    }

}