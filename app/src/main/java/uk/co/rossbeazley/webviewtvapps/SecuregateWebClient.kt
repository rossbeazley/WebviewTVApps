package uk.co.rossbeazley.webviewtvapps

import android.net.http.SslError
import android.webkit.*
import java.net.URL

class SecuregateWebClient : WebViewClient() {

    override fun onPageFinished(view: WebView, url: String) {

        val polyfill = view.context.assets
            .open("all_your_base.js")
            .bufferedReader()
            .use { it.readText() }

        view.evaluateJavascript(polyfill) {}

    }


    override fun shouldInterceptRequest(view: WebView?, request: WebResourceRequest?): WebResourceResponse? {
        if (request != null) {
            val url_string = request.url.toString()
            if (url_string.contains("securegate.iplayer")) {
                return interceptSecureGateRequestAndGoToOpenLive(url_string)
            }
        }
        return super.shouldInterceptRequest(view, request)
    }

    private fun interceptSecureGateRequestAndGoToOpenLive(toString: String): WebResourceResponse {
        val openUrlString = toString.replace("securegate.iplayer", "open.live")
        val msResponseStream = URL(openUrlString).openStream()
        val mimeType = "application/javascript;charset=UTF-8"
        val transferEncoding = "gzip"
        val webResourceResponse = WebResourceResponse(
            mimeType,
            transferEncoding,
            msResponseStream
        )
        return webResourceResponse
    }

    override fun onReceivedSslError(view: WebView, handler: SslErrorHandler, error: SslError) {
        handler.proceed()
    }

}