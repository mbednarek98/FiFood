package inz.mb.pl.app.fitapp.adapter.exercise_adapters

import android.content.Context

import android.widget.TextView

import android.view.LayoutInflater
import android.view.View

import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.ImageView
import inz.mb.pl.app.fitapp.R

class ExerciseTypeGroupViewHolder(val view : View){
    val imageCard : ImageView = view.findViewById(R.id.image_card)
    val groupTextView : TextView = view.findViewById(R.id.group_text_view)
}

class ExerciseTypeChildViewHolder(val view : View){
    val item : TextView = view.findViewById(R.id.exercise_item)
    val description : TextView = view.findViewById(R.id.exercise_description)
}


class ExerciseTypeAdapter(private val context: Context, private var groups: List<String>,private var groupsImgURL : List<String>,private var items: HashMap<String, List<String>>) :
    BaseExpandableListAdapter() {

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


    private fun assignGroupViewHolderItems(holder : ExerciseTypeGroupViewHolder, groupPosition: Int) = with(holder){
        imageCard.setBackgroundResource(context.resources.getIdentifier(groupsImgURL[groupPosition],
            "drawable",
            context.packageName))
        groupTextView.text = groups[groupPosition]
    }


    private fun assignChildViewHolderItems(holder : ExerciseTypeChildViewHolder, groupPosition: Int, childPosition: Int) = with(holder){
        item.text = getGroup(groupPosition)
        description.text = getChild(groupPosition, childPosition)
    }

    private fun onCreateGroupViewHolder(viewGroup : ViewGroup) : ExerciseTypeGroupViewHolder = ExerciseTypeGroupViewHolder(LayoutInflater.from(viewGroup.context).inflate(R.layout.exercise_list_view_group, viewGroup, false))
    private fun onCreateChildViewHolder(viewGroup : ViewGroup) : ExerciseTypeChildViewHolder = ExerciseTypeChildViewHolder(LayoutInflater.from(viewGroup.context).inflate(R.layout.exercise_list_view_child, viewGroup, false))
    override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean = true
    override fun getGroupCount(): Int = groups.size
    override fun getChildrenCount(groupPosition: Int): Int = items[getGroup(groupPosition)]!!.size
    override fun getGroup(groupPosition: Int): String = groups[groupPosition]
    override fun getChild(groupPosition: Int, childPosition: Int): String = items[getGroup(groupPosition)]!![childPosition]
    override fun getGroupId(groupPosition: Int): Long = groupPosition.toLong()
    override fun getChildId(groupPosition: Int, childPosition: Int): Long = childPosition.toLong()
    override fun hasStableIds(): Boolean = false
}