package com.rad.tools

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.rad.tools.adapter.MenuItem

class SharedPreferencesHelper(context: Context) {
    companion object {
        const val KEY_MENU_ITEMS = "menu_items_key"
        const val NAME_MENU_ITEMS = "menu_items"
    }

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(NAME_MENU_ITEMS, Context.MODE_PRIVATE)


    fun saveMenuItems(itemList: List<MenuItem>) {
        val editor = sharedPreferences.edit()

        // Convert list to JSON string
        val gson = Gson()
        val jsonString = gson.toJson(itemList)

        // Save to SharedPreferences
        editor.putString(KEY_MENU_ITEMS, jsonString)
        editor.apply() // or editor.commit()
    }

    fun getMenuItems(): List<MenuItem> {
        // Retrieve JSON string
        val jsonString = sharedPreferences.getString("menu_items_key", null)

        // Convert JSON string back to list
        val gson = Gson()
        val type = object : TypeToken<List<MenuItem>>() {}.type
        return gson.fromJson(jsonString, type) ?: emptyList() // Return an empty list if null
    }

    // Clear saved data (optional if needed)
    fun clearTabbedMenuItems() {
        val editor = sharedPreferences.edit()
        editor.remove(KEY_MENU_ITEMS)
        editor.apply()
    }
}
