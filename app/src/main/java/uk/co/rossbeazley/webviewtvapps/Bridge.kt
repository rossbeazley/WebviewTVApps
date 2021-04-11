package uk.co.rossbeazley.webviewtvapps

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.PorterDuff
import android.net.Uri
import android.view.Surface
import android.view.SurfaceView
import android.view.View
import android.webkit.JavascriptInterface
import android.webkit.WebView
import com.google.android.exoplayer2.ExoPlaybackException
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.analytics.AnalyticsListener
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util


class Bridge(val webview: WebView, val surfaceView: SurfaceView) : Player.EventListener {


    private var exoplayer: SimpleExoPlayer

    init {
        webview.addJavascriptInterface(this, "exobridge")
        exoplayer = SimpleExoPlayer.Builder(webview.context).build()
        exoplayer.setVideoSurfaceView(surfaceView)
    }

    private var src: String = ""
    @JavascriptInterface
    fun setSrc(src: String) {
        this.src = src
    }



    @JavascriptInterface
    fun getSrc() : String = this.src

    @JavascriptInterface
    fun load() {
        if(src.isEmpty()) return
        isEnded = false
        mediaError = null
        videoElementEvent("loadstart")
        prepareExoplayerWith(src)
        maybeSendTimeUpdates()
    }

    private fun prepareExoplayerWith(src: String) {

        val userAgent = Util.getUserAgent(webview.context, "LVT")
        val dataSourceFactory = DefaultDataSourceFactory(webview.context, userAgent)
        val mediaSourceFactory = HlsMediaSource.Factory(dataSourceFactory)
        val videoSource = mediaSourceFactory.createMediaSource(Uri.parse(src))
        exoplayer.prepare(videoSource)

        exoplayer.addListener(this)
        surfaceView.post {
            surfaceView.visibility = View.VISIBLE
        }
    }


    @JavascriptInterface
    fun setAutoplay(value : Boolean) {
        if(exoplayer.playWhenReady == false && value==true) {
            videoElementEvent("play")
        }
        exoplayer.playWhenReady = value
    }
    @JavascriptInterface
    fun getAutoplay() = exoplayer.playWhenReady

    @JavascriptInterface
    fun play() {
        videoElementEvent("play")
        exoplayer.playWhenReady = true
    }

    @JavascriptInterface
    fun pause() {
        videoElementEvent("pause")
        exoplayer.playWhenReady = false
    }


    @JavascriptInterface
    fun getDuration() = exoplayer.duration.millisToSecondsFloat()

    @JavascriptInterface
    fun getCurrentTime() = exoplayer.currentPosition.millisToSecondsFloat()

    fun maybeSendTimeUpdates() {
        when (exoplayer.playbackState) {
            Player.STATE_READY -> videoElementEvent("timeupdate")
        }
        when { src.isNotEmpty() -> webview.postDelayed( ::maybeSendTimeUpdates, 120) }
    }

    @JavascriptInterface
    fun setCurrentTime(seconds : Float) {
        videoElementEvent("seeking")
        isSeeking=true
        val millis = seconds * 1000.0f
        exoplayer.seekTo(millis.toLong())
    }

    var isSeeking = false
    @JavascriptInterface
    fun seeking() = isSeeking

    override fun onSeekProcessed() {
        videoElementEvent("seeked")
        isSeeking = false
    }

    @JavascriptInterface
    fun tearDownExoplayer() {
        exoplayer.stop()
        webview.post {
            surfaceView.visibility = View.GONE
        }
        this.src = ""
    }

    override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
        when (playbackState) {
            Player.STATE_READY -> if(playWhenReady) { videoElementEvent("playing")  }
                                    else { videoElementEvent( "canplay", "durationchange") }
            Player.STATE_ENDED -> {
                videoElementEvent("ended")
                isEnded = true;
            }
            else -> Unit
        }
    }

    var isEnded = false;
    @JavascriptInterface
    fun ended() = isEnded

    private var mediaError : String? = null
    @JavascriptInterface
    fun getMediaError() = mediaError

    override fun onPlayerError(error: ExoPlaybackException) {
        videoElementEvent("error")
        mediaError = "{ \"code\":0, \"message\":\"${error.message}\"}"
    }


    private fun videoElementEvent(vararg evt: String) {
        webview.post {
            evt.forEach {
                val script = "VideoDomBridge.raiseEvent('$it');"
                webview.evaluateJavascript(script) { }
            }
        }
    }

}

fun Long.millisToSecondsFloat() : Float {
    return this.toFloat() / 1000.0f
}