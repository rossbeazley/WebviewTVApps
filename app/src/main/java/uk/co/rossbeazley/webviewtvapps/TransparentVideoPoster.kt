package uk.co.rossbeazley.webviewtvapps

import android.graphics.Bitmap
import android.view.View
import android.webkit.WebChromeClient

class TransparentVideoPoster : WebChromeClient() {

    override fun getDefaultVideoPoster() : Bitmap {
        val blank = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)
        blank.eraseColor(View.MEASURED_STATE_MASK)
        return blank
    }
}