package me.lunatk.custommapview.util

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint

fun Canvas.drawBitmapCentered(bitmap: Bitmap, left: Float, top: Float, paint: Paint) {
    drawBitmap(bitmap, left - bitmap.width/2, top - bitmap.height/2, paint)
}