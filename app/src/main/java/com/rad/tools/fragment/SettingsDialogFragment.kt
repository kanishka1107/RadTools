package com.rad.tools.fragment

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.content.pm.ResolveInfo
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ListView
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.rad.tools.R
import com.rad.tools.adapter.MenuItem
import com.rad.tools.adapter.MenuItemType
import com.rad.tools.adapter.MenuListAdapter


class SettingsDialogFragment(
    private val menuItems: MutableList<MenuItem>,
    private val listener: OnSettingsUpdatedListener
) : DialogFragment(), MenuListAdapter.OnMenuItemRemoveClickListener {

    interface OnSettingsUpdatedListener {
        fun addMenuItem(item: MenuItem)
        fun removeMenuItem(index: Int)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireActivity())
        val inflater = requireActivity().layoutInflater

        val view = inflater.inflate(R.layout.dialog_settings, null)
        builder.setView(view)

        val editTextTitle = view.findViewById<EditText>(R.id.editTextTitle)
        val checkBoxIsApp = view.findViewById<CheckBox>(R.id.checkBoxIsApp)
        val editTextUrl = view.findViewById<EditText>(R.id.editTextUrl)
        val spinnerInstalledApps = view.findViewById<Spinner>(R.id.spinnerInstalledApps)
        val listViewMenuItems = view.findViewById<ListView>(R.id.listViewMenuItems)
        val buttonAddItem = view.findViewById<Button>(R.id.buttonAddItem)

        // Handle showing spinner when the checkbox is checked
        checkBoxIsApp.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                editTextUrl.visibility = View.GONE
                spinnerInstalledApps.visibility = View.VISIBLE
                loadLaunchableApps(spinnerInstalledApps) // Populate installed apps
            } else {
                editTextUrl.visibility = View.VISIBLE
                spinnerInstalledApps.visibility = View.GONE
            }
        }

        // Adapter for displaying the list of added items
        val itemListAdapter = MenuListAdapter(requireContext(), menuItems, this)
        listViewMenuItems.adapter = itemListAdapter

        // Handle adding new items
        buttonAddItem.setOnClickListener {
            val title = editTextTitle.text.toString()
            if (title.isNotEmpty()) {
                val menuItem: MenuItem
                if (checkBoxIsApp.isChecked) {
                    val selectedAppIndex = spinnerInstalledApps.selectedItemPosition
                    val packageName = getLaunchableApps()[selectedAppIndex].activityInfo.packageName
                    menuItem = MenuItem(title, MenuItemType.APP, packageName)
                } else {
                    val url = editTextUrl.text.toString()
                    menuItem = MenuItem(title, MenuItemType.WEB_VIEW, url)
                }
                itemListAdapter.add(menuItem)
                listener.addMenuItem(menuItem)

                editTextTitle.text.clear()
                editTextUrl.setText("https://")
                editTextTitle.requestFocus()

            } else {
                Toast.makeText(requireContext(), "Title is required", Toast.LENGTH_SHORT).show()
            }
        }

        return builder.create()
    }

    private fun loadLaunchableApps(spinner: Spinner) {
        val packageManager = requireContext().packageManager
        val installedApps = getLaunchableApps() // Your list of apps or items
        val appNames = installedApps.map { it.loadLabel(packageManager).toString() }

        // Create an ArrayAdapter using the custom spinner item layout
        val adapter = ArrayAdapter(requireContext(), R.layout.spinner_item, appNames)

        // Apply the custom layout to both the dropdown and the spinner view
        adapter.setDropDownViewResource(R.layout.spinner_item)
        spinner.adapter = adapter
    }

    private fun getLaunchableApps(): List<ResolveInfo> {
        val packageManager = requireContext().packageManager

        // Create an intent that matches launchable activities (apps with launcher icons)
        val intent = Intent(Intent.ACTION_MAIN, null).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }

        // Query the package manager for apps that can be launched
        return packageManager.queryIntentActivities(intent, 0)

    }

    override fun onClick(index: Int) {
        listener.removeMenuItem(index)
    }
}


