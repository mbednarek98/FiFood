package inz.mb.pl.app.fitapp.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import inz.mb.pl.app.fitapp.R
import inz.mb.pl.app.fitapp.helper.Helper
import java.time.LocalDate
import java.util.ArrayList


class CalendarViewHolder(
    itemView: View,
    private val onItemListener: CalendarAdapter.OnItemListener,
    private val days: ArrayList<LocalDate>
) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
    val parentView: View = itemView.findViewById(R.id.parentView)
    val dayOfMonth: TextView = itemView.findViewById(R.id.cellDayText)
    override fun onClick(view: View) {
        onItemListener.onItemClick(adapterPosition, days[adapterPosition])
    }

    init {
        itemView.setOnClickListener(this)
    }
}

class CalendarAdapter(
    private val days: ArrayList<LocalDate>,
    private val onItemListener: OnItemListener
) :
    RecyclerView.Adapter<CalendarViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.calendar_cell, parent, false)
        val layoutParams = view.layoutParams
        layoutParams.height = parent.height
        return CalendarViewHolder(view, onItemListener, days)
    }

    override fun onBindViewHolder(holder: CalendarViewHolder, position: Int) {
        val date = days[position]
        holder.dayOfMonth.text = date.dayOfMonth.toString()
        if (date == Helper.selectedDate) holder.parentView.setBackgroundColor(Color.LTGRAY)
    }

    override fun getItemCount(): Int = days.size

    interface OnItemListener {
        fun onItemClick(position: Int, date: LocalDate?)
    }
}

