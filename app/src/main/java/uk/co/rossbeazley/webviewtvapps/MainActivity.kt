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


class MainActivity : Activity() {

    lateinit var webview : WebView
    lateinit var bridge : Bridge

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WebView.setWebContentsDebuggingEnabled(true)
        webview = WebView(this).apply {

            settings.apply {
                userAgentString="Mozilla/5.0 (Linux; U; Android 5.0.1; NVIDIA Shield; smart-tv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Safari/537.36"
                javaScriptEnabled=true
                domStorageEnabled=true
                mediaPlaybackRequiresUserGesture = false
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
            loadUrl("https://www.live.bbctvapps.co.uk/tap/iplayer/?origin=portal")
            onResume()
            resumeTimers()


        }

        val surfaceView = SurfaceView(this)
        addContentView(surfaceView,ViewGroup.LayoutParams(MATCH_PARENT,MATCH_PARENT))
        addContentView(webview, ViewGroup.LayoutParams(MATCH_PARENT,MATCH_PARENT))

        bridge = Bridge(webview, surfaceView)
    }


    override fun dispatchKeyEvent(event: KeyEvent): Boolean {

        val mappedKey = when(event.keyCode) {
            KeyEvent.KEYCODE_DPAD_UP -> 19
            KeyEvent.KEYCODE_DPAD_DOWN -> 20
            KeyEvent.KEYCODE_DPAD_LEFT ->  21
            KeyEvent.KEYCODE_DPAD_RIGHT -> 22
            KeyEvent.KEYCODE_ENTER -> 23
            KeyEvent.KEYCODE_DEL -> 4
            else -> 0
        }

        val eventString = when(event.action) {
            KeyEvent.ACTION_DOWN -> "keydown"
            else -> "keyup"
        }

        val keyEventDispatchJS = """
        
        function press(key, event) {
            var keyDownEvent = document.createEvent("Events");
            keyDownEvent.initEvent(event, true, true);
            keyDownEvent.which = key; 
            keyDownEvent.keyCode = key;
            document.dispatchEvent(keyDownEvent);            
        }
                
        press( ${mappedKey}, "${eventString}" );
        """
        webview.evaluateJavascript(keyEventDispatchJS){}
        return true
    }


}
