package inz.mb.pl.app.fitapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.LinearLayout
import android.widget.TextView
import inz.mb.pl.app.fitapp.R
import inz.mb.pl.app.fitapp.models.User

class SettingsGroupViewHolder(val view : View){
    val group : TextView = view.findViewById(R.id.settings_group_text_view)
}

class SettingsChildViewHolder(val view : View){
    val layout : LinearLayout = view.findViewById(R.id.settings_layout)
    val item : TextView = view.findViewById(R.id.settings_item)
    val description : TextView = view.findViewById(R.id.settings_description)
}


class SettingsAdapter(private var groups: List<String>, private var items: Map<String, List<Triple<String,String,Runnable>>>) : BaseExpandableListAdapter() {

    override fun getGroupView(
        groupPosition: Int,
        isExpanded: Boolean,
        convertView: View?,
        parent: ViewGroup
    ): View {
        val holder = onCreateGroupViewHolder(parent)
        assignGroupViewHolderItems(holder, groupPosition)
        return holder.view
    }

    override fun getChildView(
        groupPosition: Int,
        childPosition: Int,
        isLastChild: Boolean,
        convertView: View?,
        parent: ViewGroup
    ): View {
        val holder = onCreateChildViewHolder(parent)
        assignChildViewHolderItems(holder, groupPosition, childPosition)
        return holder.view
    }


    private fun assignGroupViewHolderItems(holder : SettingsGroupViewHolder, groupPosition: Int) = with(holder){
        group.text = groups[groupPosition]
    }


    private fun assignChildViewHolderItems(holder : SettingsChildViewHolder, groupPosition: Int, childPosition: Int) = with(holder){
        val (name, desc, runnable) = getChild(groupPosition,childPosition)
        item.text = name
        description.text = desc
        layout.setOnClickListener {
            runnable.run()
        }
    }

    private fun onCreateGroupViewHolder(viewGroup : ViewGroup) : SettingsGroupViewHolder = SettingsGroupViewHolder(
        LayoutInflater.from(viewGroup.context).inflate(R.layout.settings_list_view_group, viewGroup, false))
    private fun onCreateChildViewHolder(viewGroup : ViewGroup) : SettingsChildViewHolder = SettingsChildViewHolder(
        LayoutInflater.from(viewGroup.context).inflate(R.layout.settings_list_view_child, viewGroup, false))
    override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean = true
    override fun getGroupCount(): Int = groups.size
    override fun getChildrenCount(groupPosition: Int): Int = items[getGroup(groupPosition)]!!.size
    override fun getGroup(groupPosition: Int): String = groups[groupPosition]
    override fun getChild(groupPosition: Int, childPosition: Int): Triple<String,String,Runnable> = items[getGroup(groupPosition)]!![childPosition]
    override fun getGroupId(groupPosition: Int): Long = groupPosition.toLong()
    override fun getChildId(groupPosition: Int, childPosition: Int): Long = childPosition.toLong()
    override fun hasStableIds(): Boolean = false
}