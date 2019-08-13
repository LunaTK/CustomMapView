package me.lunatk.custommapview.layer

import android.graphics.*
import androidx.core.graphics.withMatrix
import androidx.core.graphics.withSave
import me.lunatk.custommapview.R
import me.lunatk.custommapview.data.CurrentLocationProvider
import me.lunatk.custommapview.util.drawBitmapCentered

class CurrentLocationLayer: Layer() {

    private val paint = Paint()
    private val matrix = Matrix()

    private val locationImage: Bitmap by lazy {
        val options = BitmapFactory.Options()
        options.inScaled = false
        BitmapFactory.decodeResource(mapView?.resources, R.drawable.currenticon, options)
    }

    var locationProvider: CurrentLocationProvider? = null

    override fun drawOnCanvas(canvas: Canvas) {
        mapView?.let {
            locationProvider?.let { lp ->
                val point = floatArrayOf(lp.getLocation().x, lp.getLocation().y)
                canvas.matrix.mapPoints(point)

                canvas.withSave {
                    matrix = this@CurrentLocationLayer.matrix
                    canvas.drawBitmapCentered(locationImage, point[0], point[1], paint)
                }
            }
        }
    }

    override fun onTouch(x: Float, y: Float, action: Int) {}

}