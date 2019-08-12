package me.lunatk.custommapview.layer

import android.graphics.*
import androidx.core.graphics.withSave
import me.lunatk.custommapview.R
import me.lunatk.custommapview.data.CurrentLocationProvider
import me.lunatk.custommapview.data.Location

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

                matrix.reset()
                matrix.postTranslate(- locationImage.width / 2f, - locationImage.height / 2f)

                canvas.withSave {
                    matrix.reset()
                    matrix = this@CurrentLocationLayer.matrix

                    canvas.drawBitmap(locationImage, point[0], point[1], paint)
                }
            }
        }
    }

    override fun onTouch(x: Float, y: Float, action: Int) {}

}