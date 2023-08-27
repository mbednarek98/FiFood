package inz.mb.pl.app.fitapp.adapter.spinner_adapters

import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import inz.mb.pl.app.fitapp.R

class SpinnerTextVH(itemView : View){
    val spinnerNameTextView: TextView = itemView.findViewById(R.id.spinner_only_text)
}

class SpinnerTextAdapter(private var dataName: List<String>) : SpinnerAdapter() {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val (view: View, holder: Any) = onCreateViewHolder(convertView, parent, R.layout.spinner_text_layout)
        assignViewHolderItems(holder as SpinnerTextVH, position)
        return view
    }
    private fun assignViewHolderItems(holder : SpinnerTextVH, position: Int) = with(holder) {
        spinnerNameTextView.text = dataName[position]
    }

    override fun getItem(position: Int): Any = dataName[position]
    override fun createNewVH(itemView: View): Any = SpinnerTextVH(itemView)
    override fun getCount(): Int = dataName.size
    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

}