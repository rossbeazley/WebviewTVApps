package uk.co.rossbeazley.webviewtvapps

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.Bitmap.Config.*
import android.graphics.Bitmap.createBitmap
import android.graphics.Color
import android.os.Bundle
import android.view.KeyEvent
import android.view.SurfaceView
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.webkit.*
import java.net.CookieHandler
import java.net.CookiePolicy


class MainActivity : Activity() {

    lateinit var webview : WebView
    lateinit var bridge : Bridge

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WebView.setWebContentsDebuggingEnabled(true)
        webview = WebView(this).apply {

            settings.apply {
                userAgentString="Mozilla/5.0 (Linux; Android 8.0.0; SHIELD Android TV Build/OPR6.170623.010; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/69.0.3497.100 Mobile Safari/537.36"
                javaScriptEnabled=true
                domStorageEnabled=true
                settings.useWideViewPort = true
                mediaPlaybackRequiresUserGesture = false
                webViewClient =
                    JSLoadingWebClient()
                setAppCachePath(cacheDir.absolutePath)
                setAppCacheEnabled(true)

                webChromeClient = object : WebChromeClient() {
                    override fun getDefaultVideoPoster() : Bitmap = createBitmap(100, 100, ARGB_8888)
                                                                        .apply { eraseColor(Color.TRANSPARENT) }
                }
            }

            setBackgroundColor(Color.TRANSPARENT)

            loadUrl("https://app.10ft.itv.com/androidtv/")
            onResume()
            resumeTimers()


        }

        val surfaceView = SurfaceView(this)
        addContentView(surfaceView,ViewGroup.LayoutParams(MATCH_PARENT,MATCH_PARENT))
        addContentView(webview, ViewGroup.LayoutParams(MATCH_PARENT,MATCH_PARENT))

        bridge = Bridge(webview, surfaceView)

        enableCookies(webview)

    }

    //Needs cookies for cdn auth
    private fun enableCookies(webview: WebView) {
        val DEFAULT_COOKIE_MANAGER = java.net.CookieManager()
        DEFAULT_COOKIE_MANAGER.setCookiePolicy(CookiePolicy.ACCEPT_ORIGINAL_SERVER)
        CookieHandler.setDefault(DEFAULT_COOKIE_MANAGER)
        android.webkit.CookieManager.getInstance().setAcceptCookie(true)
        android.webkit.CookieManager.getInstance().setAcceptThirdPartyCookies(webview, true)
    }


    override fun onBackPressed(){}

    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        return webview.dispatchKeyEvent(event)
    }


}
