package com.rad.tools.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.rad.tools.R

class MenuRecyclerAdapter(
    private val menuItems: MutableList<MenuItem>, // List of items with either URL or package name
    private val onItemSelected: (Int) -> Unit,
    private val onItemSelectedAgain: (Int) -> Unit
) : RecyclerView.Adapter<MenuRecyclerAdapter.TabViewHolder>() {

    var selectedPosition = RecyclerView.NO_POSITION

    // Function to update selection
    private fun selectMenuItem(position: Int) {
        if(selectedPosition == position) {
            onItemSelectedAgain(position);
        } else {
            val previousPosition = selectedPosition
            selectedPosition = position

            // Notify the adapter of changes in both old and new positions
            if (previousPosition != RecyclerView.NO_POSITION) {
                notifyItemChanged(previousPosition)
            }
            notifyItemChanged(selectedPosition)
            onItemSelected(position)
        }
    }

    inner class TabViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tabTitle: TextView = itemView.findViewById(R.id.tab_title)
        fun bind(item: MenuItem, position: Int) {
            tabTitle.text = item.title
            itemView.isSelected = (position == selectedPosition)
            itemView.setOnClickListener {
                selectMenuItem(position)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TabViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_item_menu, parent, false)
        return TabViewHolder(view)
    }

    override fun onBindViewHolder(holder: TabViewHolder, position: Int) {
        holder.bind(menuItems[position], position)
    }

    override fun getItemCount(): Int = menuItems.size

}


data class MenuItem(
    val title: String,
    val type: MenuItemType,
    val data: String // URL for WebView or package name for the installed app
)

enum class MenuItemType {
    WEB_VIEW,
    APP
}



