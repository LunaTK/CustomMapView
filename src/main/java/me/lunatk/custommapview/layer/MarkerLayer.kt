package me.lunatk.custommapview.layer

import android.graphics.*
import android.view.MotionEvent
import androidx.core.graphics.minus
import androidx.core.graphics.scaleMatrix
import androidx.core.graphics.withSave
import me.lunatk.custommapview.R
import me.lunatk.custommapview.data.Marker
import me.lunatk.custommapview.util.logi
import kotlin.math.min

class MarkerLayer: Layer() {

    private val markerImage: Bitmap by lazy {
        val options = BitmapFactory.Options().apply { inScaled = false }
        BitmapFactory.decodeResource(mapView?.resources, R.drawable.marker, options)
    }

    private val popupImage: Bitmap by lazy {
        val options = BitmapFactory.Options().apply { inScaled = true }
        BitmapFactory.decodeResource(mapView?.resources, R.drawable.popup, options)
    }
    private val matrix = Matrix()
    private val paint = Paint().apply {
        textAlign = Paint.Align.CENTER
    }

    private val markers = ArrayList<Marker>()

    val showingDetail: Boolean get() = markerShowingDetail != null
    var markerShowingDetail: Marker? = null
    val popupPos: PointF = PointF()

    var onMarkerClickListener: ((Marker) -> Unit)? = null
    var onPopupClickListener: ((Marker) -> Unit)? = null

    override fun drawOnCanvas(canvas: Canvas) {
        mapView?.let {
            val points = FloatArray(markers.size*2) { if (it % 2 == 0) markers[it/2].x else markers[it/2].y  }
            canvas.matrix.mapPoints(points)

            matrix.reset()
            matrix.postTranslate(- markerImage.width / 2f, - markerImage.height.toFloat())

            canvas.withSave {
                matrix = this@MarkerLayer.matrix
                0.until(markers.size).forEach {
                    drawBitmap(markerImage, points[it*2], points[it*2+1], paint)
                }

                drawMarkerDetail(points, canvas)
            }

        }
    }

    private fun drawMarkerDetail(points: FloatArray, canvas: Canvas) {
        markerShowingDetail?.let {
            val index = markers.indexOf(it)
            val popupX = points[index*2] + markerImage.width / 2f - popupImage.width / 2f
            val popupY = points[index*2 + 1] - popupImage.height
            canvas.drawBitmap(popupImage,
                popupX,
                popupY,
                paint)

            paint.color = Color.WHITE
            paint.textSize = popupImage.height / 5f
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)

            canvas.drawText("Select as Destination",
                popupX + popupImage.width / 2f,
                popupY + popupImage.height / 2f,
                paint)
            popupPos.set(popupX, popupY)
        }
    }

    override fun onTouch(x: Float, y: Float, action: Int) {
        val positionOnMap = mapView!!.toPositionOnMap(x, y)
        if (action == MotionEvent.ACTION_UP) {
            if (markerShowingDetail == null) { // No popup
                getNearestMarker(positionOnMap)?.let {marker ->
                    onMarkerClickListener?.invoke(marker)
                    logi("Marker Touched : ${marker}")
                    markerShowingDetail = marker
                } ?: run {
                    dismissPopup()
                }
            } else { // Popup is shown
                if (isTouchInsidePopup(x, y)) {
                    onPopupClickListener?.invoke(markerShowingDetail!!)
                } else {
                    dismissPopup()
                }
            }
        }
    }

    private fun isTouchInsidePopup(x: Float, y: Float): Boolean {
        logi("x : $x, y : $y")
        logi("Popup : $popupPos")
        val dx = x - popupPos.x
        val dy = y - popupPos.y

        return 0 <= dx && dx <= popupImage.width &&
                0 <= dy && dy <= popupImage.height
    }

    fun addMarker(marker: Marker) {
        markers += marker
        mapView?.invalidate()
    }

    fun dismissPopup() {
        markerShowingDetail = null
    }

    private fun getNearestMarker(point: PointF): Marker? {
        val threshold = markerImage.height * 1.3f / mapView!!.scale
        var marker: Marker? = null
        var minDistance = Float.MAX_VALUE

        for (m in markers) {
            val distance = (m.point - point).length()
            if (distance < min(minDistance, threshold) ) {
                minDistance = distance
                marker = m
            }
        }
        return marker
    }
}