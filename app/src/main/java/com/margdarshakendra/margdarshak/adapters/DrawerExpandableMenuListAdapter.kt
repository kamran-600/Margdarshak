package com.margdarshakendra.margdarshak.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import androidx.appcompat.app.AppCompatActivity
import com.margdarshakendra.margdarshak.databinding.ExpandableListGroupBinding
import com.margdarshakendra.margdarshak.databinding.ExpandableListItemBinding

class DrawerExpandableMenuListAdapter(private val context: Context,
                                      private val groupTitles: MutableList<String>,
                                      private val groupItems: Map<String, List<String>>
) : BaseExpandableListAdapter() {
    override fun getGroupCount(): Int {
        return groupTitles.size
    }

    override fun getChildrenCount(groupPosition: Int): Int {
        val group = getGroup(groupPosition)
        return groupItems[group]?.size ?: 0
    }

    override fun getGroup(groupPosition: Int): String {
        return groupTitles[groupPosition]
    }

    override fun getChild(groupPosition: Int, childPosition: Int): String {
        val group = getGroup(groupPosition)
        return groupItems[group]?.get(childPosition) ?: ""
    }

    override fun getGroupId(groupPosition: Int): Long {
        return groupPosition.toLong()
    }

    override fun getChildId(groupPosition: Int, childPosition: Int): Long {
        return childPosition.toLong()
    }

    override fun hasStableIds(): Boolean {
        return false
    }

    override fun getGroupView(
        groupPosition: Int,
        isExpanded: Boolean,
        convertView: View?,
        parent: ViewGroup?
    ): View {
        val layoutInflater = context
            .getSystemService(AppCompatActivity.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val expandableListGroupBinding =
            ExpandableListGroupBinding.inflate(layoutInflater, parent, false)
        expandableListGroupBinding.groupTitle.text = getGroup(groupPosition)
        return expandableListGroupBinding.root
    }

    override fun getChildView(
        groupPosition: Int,
        childPosition: Int,
        isLastChild: Boolean,
        convertView: View?,
        parent: ViewGroup?
    ): View {

        val layoutInflater = context
            .getSystemService(AppCompatActivity.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val expandableListItemBinding =
            ExpandableListItemBinding.inflate(layoutInflater, parent, false)
        expandableListItemBinding.expandedListItem.text =
            getChild(groupPosition, childPosition)
        return expandableListItemBinding.root
    }

    override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean {
        return true
    }

}
