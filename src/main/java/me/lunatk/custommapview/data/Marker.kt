package me.lunatk.custommapview.data

import android.graphics.PointF

data class Marker(val x: Float, val y: Float, val name: String) {
    val point: PointF = PointF(x, y)
}