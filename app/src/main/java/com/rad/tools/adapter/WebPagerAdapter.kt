package com.rad.tools.adapter

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.rad.tools.fragment.WebViewFragment

class WebPagerAdapter(activity: AppCompatActivity, private val fragmentList: MutableList<WebViewFragment>) :
    FragmentStateAdapter(activity) {

    override fun getItemCount(): Int = fragmentList.size

    override fun createFragment(position: Int): Fragment {
        return fragmentList[position]
    }

    fun updateFragments(newFragments: List<WebViewFragment>) {
        fragmentList.clear()
        fragmentList.addAll(newFragments)
        notifyDataSetChanged()
    }
}
