package uk.co.rossbeazley.webviewtvapps

import android.net.http.SslError
import android.webkit.*
import java.net.URL

class SecuregateWebClient : WebViewClient() {

    override fun onReceivedSslError(view: WebView, handler: SslErrorHandler, error: SslError) {
        handler.proceed()
    }

    override fun onPageFinished(view: WebView, url: String) {

        val somebodySetUpUsTheDOM = view.context.assets
                                    .open("all_your_base.js")
                                    .bufferedReader()
                                    .use { it.readText() }

        view.evaluateJavascript(somebodySetUpUsTheDOM) {}

        val fixes_js =
            view.context.assets.open("fixes.js").bufferedReader().use { it.readText() }
        view.evaluateJavascript(fixes_js) {}
    }
}