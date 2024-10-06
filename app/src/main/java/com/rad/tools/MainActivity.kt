package com.rad.tools

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.rad.tools.adapter.MenuItem
import com.rad.tools.adapter.MenuItemType
import com.rad.tools.adapter.MenuRecyclerAdapter
import com.rad.tools.adapter.WebPagerAdapter
import com.rad.tools.fragment.SettingsDialogFragment
import com.rad.tools.fragment.WebViewFragment


class MainActivity : AppCompatActivity(),
    SettingsDialogFragment.OnSettingsUpdatedListener {

    private lateinit var menuRecyclerView: RecyclerView
    private lateinit var menuAdapter: MenuRecyclerAdapter
    private lateinit var viewPager: ViewPager2
    private lateinit var pagerAdapter: WebPagerAdapter

    private val defaultMenuItems = mutableListOf(
        MenuItem("Google", MenuItemType.WEB_VIEW, "https://www.google.com"),
        MenuItem("CNN", MenuItemType.WEB_VIEW, "https://www.cnn.com"),
        MenuItem("Toyota", MenuItemType.WEB_VIEW, "https://www.toyota.com"),
        MenuItem("Waze", MenuItemType.APP, "com.waze"),
        MenuItem("Google Maps", MenuItemType.APP, "com.google.android.apps.maps")
    )
    private var menuItems = mutableListOf<MenuItem>()
    private val fragmentList = mutableListOf<WebViewFragment>()
    private lateinit var spHelper: SharedPreferencesHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        spHelper = SharedPreferencesHelper(this)
        menuItems = spHelper.getMenuItems().toMutableList()
        if(menuItems.size == 0) menuItems.addAll(defaultMenuItems)
        menuItems.forEach { item -> fragmentList.add(WebViewFragment.newInstance(item)) }

        val settingsButton: ImageButton = findViewById(R.id.buttonSettings)
        settingsButton.setOnClickListener {
            showSettingsDialog()
        }

        // Set up RecyclerView
        menuRecyclerView = findViewById(R.id.menuRecyclerView)
        menuAdapter = MenuRecyclerAdapter(
            menuItems,
            onItemSelected = { position ->
                switchFragment(position)
            },
            onItemSelectedAgain = { position ->
                val currentFragment = fragmentList[position]
                currentFragment.toggleUrlBarWithAnimation()
            }
        )
        menuRecyclerView.adapter = menuAdapter
        menuRecyclerView.layoutManager = LinearLayoutManager(this)

        // Set up ViewPager2
        viewPager = findViewById(R.id.viewPager)
        pagerAdapter = WebPagerAdapter(this, fragmentList)
        viewPager.adapter = pagerAdapter

        // Synchronize RecyclerView and ViewPager2
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                menuAdapter.notifyItemChanged(menuAdapter.selectedPosition)
                menuAdapter.selectedPosition = position
                menuAdapter.notifyItemChanged(position)
            }
        })

        // Load the first fragment by default
        if (savedInstanceState == null) {
            for( index in menuItems.indices) {
                var menuItem = menuItems[index]
                if(menuItem.type == MenuItemType.WEB_VIEW) {
                    switchFragment(index)
                    break
                }
            }
        }

    }

    // Method to launch an installed app by its package name
    private fun launchInstalledApp(packageName: String) {
        val launchIntent: Intent? = packageManager.getLaunchIntentForPackage(packageName)
        if (launchIntent != null) {
            // Start the app if it's installed
            startActivity(launchIntent)
        } else {
            // App not found, handle accordingly
            Toast.makeText(this, "App not installed", Toast.LENGTH_SHORT).show()
        }
    }

    private fun switchFragment(position: Int) {
        val menuItem = menuItems[position]
        if(menuItem.type == MenuItemType.APP) {
            launchInstalledApp(menuItem.data)
        } else {
            viewPager.currentItem = position
        }
    }

    private fun showSettingsDialog() {
        val dialog = SettingsDialogFragment(menuItems, this)
        dialog.show(supportFragmentManager, "SettingsDialog")
    }



    override fun addMenuItem(item: MenuItem) {
        menuAdapter.notifyItemInserted(menuItems.size - 1)
        menuAdapter.notifyItemRangeChanged(0, menuItems.size)

        fragmentList.add(WebViewFragment.newInstance(item))
        pagerAdapter.notifyItemInserted(fragmentList.size - 1)
        pagerAdapter.notifyItemRangeChanged(0, fragmentList.size)

        spHelper.saveMenuItems(menuItems)
    }
    override fun removeMenuItem(index: Int) {
        menuAdapter.notifyItemRemoved(index)
        menuAdapter.notifyItemRangeChanged(index, menuItems.size)

        fragmentList.removeAt(index)
        pagerAdapter.notifyItemRemoved(index)
        pagerAdapter.notifyItemRangeChanged(index, fragmentList.size)

        spHelper.saveMenuItems(menuItems)
    }




}


