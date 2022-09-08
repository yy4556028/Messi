package com.yuyang.lib_base.browser

import android.content.Context
import android.content.MutableContextWrapper
import android.os.Looper
import android.view.ViewGroup
import android.webkit.WebView

object WebViewManager {

    private val webViewCache:MutableList<WebView> = ArrayList(1)

    private fun create(context: Context):WebView {
        return WebView(context)
    }

    @JvmStatic
    fun prepare(context: Context) {
        if (webViewCache.isEmpty()) {
            Looper.myQueue().addIdleHandler {
                webViewCache.add(create(MutableContextWrapper(context)))
                false
            }
        }
    }

    fun obtain(context: Context) :WebView {
        if (webViewCache.isEmpty()) {
            webViewCache.add(create(MutableContextWrapper(context)))
        }
        val webView = webViewCache.removeFirst()
        val contentWrapper = webView.context as MutableContextWrapper
        contentWrapper.baseContext = context
        webView.clearHistory()
        webView.resumeTimers()
        return webView
    }

    @JvmStatic
    fun recycle(webView: WebView) {
        try {
            webView.stopLoading()
            webView.loadDataWithBaseURL(null, "", "text/html", "utf-8", null)
            webView.clearHistory()
            webView.pauseTimers()
            webView.removeJavascriptInterface("webkit")

            val parent = webView.parent
            if (parent != null) {
                (parent as ViewGroup).removeView(webView)
            }
        } catch (e:Exception) {
            e.printStackTrace()
        } finally {
            if (!webViewCache.contains(webView)) {
                webViewCache.add(webView)
            }
        }
    }

    @JvmStatic
    fun destroy() {
        try {
            webViewCache.forEach {
                it.removeAllViews()
                it.destroy()
                webViewCache.remove(it)
            }
        } catch (e:Exception) {
            e.printStackTrace()
        }
    }
}