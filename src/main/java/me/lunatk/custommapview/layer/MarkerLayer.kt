package me.lunatk.custommapview.layer

import android.graphics.*
import android.view.MotionEvent
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.graphics.minus
import androidx.core.graphics.withMatrix
import androidx.core.graphics.withSave
import me.lunatk.custommapview.R
import me.lunatk.custommapview.mapview.toDegree
import me.lunatk.custommapview.mapview.toPositionOnScreen
import kotlin.math.min

class MarkerLayer: Layer() {

    private val markerImage: Bitmap by lazy {
        val options = BitmapFactory.Options()
        options.inScaled = false
        BitmapFactory.decodeResource(mapView?.resources, R.drawable.marker, options)
    }
    private val matrix = Matrix()
    private val paint = Paint()

    private val markers = ArrayList<Marker>()

    var onMarkerClickListener: ((Marker) -> Unit)? = null

    override fun drawOnCanvas(canvas: Canvas) {
        mapView?.let {
            val points = FloatArray(markers.size*2) { if (it % 2 == 0) markers[it/2].x else markers[it/2].y  }
            canvas.matrix.mapPoints(points)

            matrix.reset()
            matrix.postTranslate(- markerImage.width / 2f, - markerImage.height.toFloat())

            canvas.withSave {
                matrix.reset()
                matrix = this@MarkerLayer.matrix
                0.until(markers.size).forEach {
                    drawBitmap(markerImage, points[it*2], points[it*2+1], paint)
                }
            }
        }
    }

    override fun onTouch(x: Float, y: Float, action: Int) {
//        Log.i(simpleName, "onTouch(${x}, ${y}), action : ${action}")
        if (action == MotionEvent.ACTION_UP) {
            val marker = getNearestMarker(x, y)
            marker?.let { onMarkerClickListener?.invoke(it) }
        }
    }

    fun addMarker(marker: Marker) {
        markers += marker
        mapView?.invalidate()
    }

    fun addMarker(x: Float, y: Float) {
        addMarker(Marker(x, y))
    }

    private fun getNearestMarker(x: Float, y: Float): Marker? {
        val point = PointF(x, y)
        val threshold = markerImage.height * 1.5f
        var marker: Marker? = null
        var minDistance = Float.MAX_VALUE

        for (m in markers) {
            val distance = (m.point - point).length()
            if (distance < min(minDistance, threshold) ) {
                minDistance = distance
                marker = m
            }
        }
//        Log.i(simpleName, "nearestMarker : ${marker}")
        return marker
    }
}