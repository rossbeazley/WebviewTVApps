package uk.co.rossbeazley.webviewtvapps

import android.net.http.SslError
import android.webkit.*
import java.net.URL

class JSLoadingWebClient : WebViewClient() {

    override fun onReceivedSslError(view: WebView, handler: SslErrorHandler, error: SslError) {
        handler.proceed()
    }

    override fun onPageFinished(view: WebView, url: String) {

        val somebodySetUpUsTheDOM = view.context.assets
                                    .open("all_your_base.js")
                                    .bufferedReader()
                                    .use { it.readText() }

        view.evaluateJavascript(somebodySetUpUsTheDOM) {}

    }
}