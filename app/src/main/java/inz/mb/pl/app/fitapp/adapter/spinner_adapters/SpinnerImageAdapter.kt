package inz.mb.pl.app.fitapp.adapter.spinner_adapters

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import inz.mb.pl.app.fitapp.R

class SpinnerImageViewHolder(itemView : View){
    val spinnerTextView: TextView = itemView.findViewById(R.id.spinner_text)
}

class SpinnerImageAdapter(private val context: Context, private var listOfNames: List<String>, private var listOfURLs : List<String>) : SpinnerAdapter() {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val (view: View, holder: Any) = onCreateViewHolder(convertView, parent, R.layout.spinner_image_layout)
        assignViewHolderItems(holder as SpinnerImageViewHolder, position)
        return view
    }

    private fun assignViewHolderItems(holder : SpinnerImageViewHolder, position: Int) = with(holder) {
        spinnerTextView.text = listOfNames[position]
        val drawable = ContextCompat.getDrawable(context, context.resources.getIdentifier(listOfURLs[position], "drawable", context.packageName))
        val size = context.resources.getDimensionPixelSize(R.dimen.spinner_image_size)
        drawable?.setBounds(0, 0, size, size)
        spinnerTextView.setCompoundDrawables(drawable,null, null, null)
    }

    override fun getItem(position: Int): Any = listOfNames[position]
    override fun createNewVH(itemView: View): Any = SpinnerImageViewHolder(itemView)
    override fun getCount(): Int = listOfNames.size
    override fun getItemId(position: Int): Long = position.toLong()

}