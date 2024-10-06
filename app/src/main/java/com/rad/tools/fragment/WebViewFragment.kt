package com.rad.tools.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.rad.tools.R
import com.rad.tools.adapter.MenuItem
import com.rad.tools.adapter.MenuItemType


class WebViewFragment : Fragment() {
    private lateinit var menuItem: MenuItem
    private lateinit var webView: WebView
    private var webViewState: Bundle? = null

    private lateinit var urlBarLayout: LinearLayout
    private lateinit var urlTextView: TextView
    private var isUrlBarVisible = false  // Track the visibility of the URL bar


    companion object {
        fun newInstance(item: MenuItem): WebViewFragment {
            val fragment = WebViewFragment()
            fragment.menuItem = item
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_webview, container, false)

        urlBarLayout = rootView.findViewById(R.id.urlBarLayout)
        urlTextView = rootView.findViewById(R.id.urlTextView)

        // Set a placeholder URL for display
        urlTextView.text = menuItem.data

        return rootView

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if(menuItem.type == MenuItemType.WEB_VIEW) loadWebView(view, menuItem.data)
    }

    // Method to load the WebView with the provided URL
    private fun loadWebView(view: View, url: String) {
        webView = view.findViewById(R.id.webView)
        webView.settings.javaScriptEnabled = true
        webView.webViewClient = WebViewClient()

        // Restore the webview's state if it's not null
        if (webViewState != null) {
            webView.restoreState(webViewState!!)
        } else {
            webView.loadUrl(url)
        }
    }

    // Save the WebView state in a fragment field
    override fun onPause() {
        super.onPause()
        if(menuItem.type == MenuItemType.WEB_VIEW) {
            webViewState = Bundle()
            webView.saveState(webViewState!!)
        }

    }

    // Save WebView state before the fragment is destroyed
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        if(menuItem.type == MenuItemType.WEB_VIEW) {
            webView.saveState(outState)
        }
    }

    // Toggle the URL bar with slide up/down animation
    fun toggleUrlBarWithAnimation() {
        if (isUrlBarVisible) {
            hideUrlBar()
        } else {
            showUrlBar()
        }
        isUrlBarVisible = !isUrlBarVisible
    }

    private fun showUrlBar() {
        val slideDown = AnimationUtils.loadAnimation(requireContext(), R.anim.slide_down)
        urlBarLayout.visibility = View.VISIBLE
        urlBarLayout.startAnimation(slideDown)
    }

    private fun hideUrlBar() {
        val slideUp = AnimationUtils.loadAnimation(requireContext(), R.anim.slide_up)
        slideUp.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {}

            override fun onAnimationEnd(animation: Animation) {
                // Set visibility to GONE after animation finishes
                urlBarLayout.visibility = View.GONE
            }

            override fun onAnimationRepeat(animation: Animation) {}
        })
        urlBarLayout.startAnimation(slideUp)
    }


}

