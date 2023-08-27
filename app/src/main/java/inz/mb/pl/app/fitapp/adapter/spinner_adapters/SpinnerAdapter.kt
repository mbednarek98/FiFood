package inz.mb.pl.app.fitapp.adapter.spinner_adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter


abstract class SpinnerAdapter : BaseAdapter() {
    fun onCreateViewHolder(
        convertView: View?,
        parent: ViewGroup?, layoutIndex : Int
    ): Pair<View, Any> {
        val view: View
        val vh: Any
        when {
            convertView != null -> {
                view = convertView
                vh = view.tag as Any
            }
            else -> {
                view = LayoutInflater.from(parent?.context).inflate(layoutIndex, parent, false)
                vh = createNewVH(view)
                view?.tag = vh
            }
        }
        return Pair(view, vh)
    }
    abstract fun createNewVH(itemView: View) : Any
}