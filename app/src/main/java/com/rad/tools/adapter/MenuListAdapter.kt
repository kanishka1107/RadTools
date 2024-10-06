package com.rad.tools.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.TextView
import com.rad.tools.R


class MenuListAdapter(
    private val context: Context,
    private val menuItems: MutableList<MenuItem>,
    private val listener: OnMenuItemRemoveClickListener
) : ArrayAdapter<MenuItem>(context, 0, menuItems) {

    interface OnMenuItemRemoveClickListener {
        fun onClick(index: Int)
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val inflater = LayoutInflater.from(context)
        val view = convertView ?: inflater.inflate(R.layout.list_item_menu, parent, false)

        // Get the title text for the current item
        val title = menuItems[position].title

        // Set the title to the TextView
        val textViewTitle = view.findViewById<TextView>(R.id.textViewTitle)
        textViewTitle.text = title

        // Set up the remove button
        val buttonRemove = view.findViewById<ImageButton>(R.id.buttonRemove)
        buttonRemove.setOnClickListener {
            // Remove the item from the list and notify the adapter
            menuItems.removeAt(position)
            notifyDataSetChanged()

            listener.onClick(position)
        }
        return view
    }
}




