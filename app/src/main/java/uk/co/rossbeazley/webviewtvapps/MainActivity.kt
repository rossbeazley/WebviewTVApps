package uk.co.rossbeazley.webviewtvapps

import LVT.uk.co.rossbeazley.webviewtvapps.R
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
import android.widget.ImageView


class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val view = ImageView(this)
        view.setImageResource(R.drawable.lvt)

        addContentView(view,ViewGroup.LayoutParams(MATCH_PARENT,MATCH_PARENT))
    }


}
