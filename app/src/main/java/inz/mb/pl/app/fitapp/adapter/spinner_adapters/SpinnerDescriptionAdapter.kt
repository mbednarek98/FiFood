package inz.mb.pl.app.fitapp.adapter.spinner_adapters

import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import inz.mb.pl.app.fitapp.R

class SpinnerDescriptionVH(itemView : View){
    val spinnerNameTextView: TextView = itemView.findViewById(R.id.spinner_title)
    val spinnerDescriptionTextView: TextView = itemView.findViewById(R.id.spinner_description)
}

class SpinnerDescriptionAdapter(private var dataName: List<String>,private var dataURL: List<String>) : SpinnerAdapter() {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val (view: View, holder: Any) = onCreateViewHolder(convertView, parent, R.layout.spinner_description_layout)
        assignViewHolderItems(holder as SpinnerDescriptionVH, position)
        return view
    }
    private fun assignViewHolderItems(holder : SpinnerDescriptionVH, position: Int) = with(holder) {
        spinnerNameTextView.text = dataName[position]
        spinnerDescriptionTextView.text = dataURL[position]
    }

    override fun getItem(position: Int): Any = dataName[position]
    override fun createNewVH(itemView: View): Any = SpinnerDescriptionVH(itemView)
    override fun getCount(): Int = dataName.size
    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

}