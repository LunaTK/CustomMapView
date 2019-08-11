package me.lunatk.custommapview

import android.graphics.PointF
import android.view.MotionEvent
import me.lunatk.custommapview.layer.Layer
import kotlin.math.atan2

fun PointF.copy(): PointF = PointF(this.x, this.y)

fun MotionEvent.getPoint(i: Int): PointF = PointF(getX(i), getY(i))

fun PointF.getSignedAngleBetween(that: PointF): Float {
    val na = this.getNormalized()
    val nb = that.getNormalized()

    return atan2(nb.y, nb.x) - atan2(na.y, na.x)
}

fun PointF.getNormalized(): PointF  {
    val length = length()
    return if (length == 0f) PointF() else PointF(x / length, y / length)
}

fun Layer.toPositionOnScreen(point: PointF): PointF? = mapView?.toPositionOnScreen(point.x, point.y)

operator fun PointF.div(num: Int): PointF = PointF(x / num, y / num)