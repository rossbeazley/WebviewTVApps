package uk.co.rossbeazley.webviewtvapps

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.Bitmap.Config.*
import android.graphics.Bitmap.createBitmap
import android.graphics.Color
import android.os.Bundle
import android.view.KeyEvent
import android.view.SurfaceView
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.webkit.*


class MainActivity : Activity() {

    lateinit var webview : WebView
    lateinit var bridge : Bridge

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WebView.setWebContentsDebuggingEnabled(true)
        webview = WebView(this).apply {

            settings.apply {
                userAgentString="smarttv_AFTMM-TCL-TS8011_Build_1234_Chromium_41.0.2250.2"
                javaScriptEnabled=true
                domStorageEnabled=true
                mediaPlaybackRequiresUserGesture = false
                mixedContentMode = 0

                javaScriptCanOpenWindowsAutomatically = true
                builtInZoomControls = true
                displayZoomControls = false
                loadWithOverviewMode = true

                webViewClient =
                    SecuregateWebClient()
                setAppCachePath(cacheDir.absolutePath)
                setAppCacheEnabled(true)

                webChromeClient = object : WebChromeClient() {
                    override fun getDefaultVideoPoster() : Bitmap = createBitmap(100, 100, ARGB_8888)
                                                                        .apply { eraseColor(Color.TRANSPARENT) }


                }
            }

            setBackgroundColor(Color.TRANSPARENT)

            setInitialScale(150)
            loadUrl("https://amazonfire-p06.channel4.com/amazonfire/index.html")
            onResume()
            resumeTimers()


        }

        val surfaceView = SurfaceView(this)
        addContentView(surfaceView,ViewGroup.LayoutParams(MATCH_PARENT,MATCH_PARENT))
        addContentView(webview, ViewGroup.LayoutParams(MATCH_PARENT,MATCH_PARENT))

        bridge = Bridge(webview, surfaceView)
    }




    override fun onBackPressed(){}

    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        return webview.dispatchKeyEvent(event)
    }


}
