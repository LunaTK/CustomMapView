package me.lunatk.custommapview.mapview

import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewTreeObserver
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import me.lunatk.custommapview.R
import me.lunatk.custommapview.mapview.layer.Layer
import kotlin.math.PI

class MapView: View, ViewTreeObserver.OnGlobalLayoutListener {

    var mapImage: Bitmap? = null
    set(value) {
        field = value
        reset()
//        Log.d("MapView", "(${mapImage?.width}, ${mapImage?.height})")
    }

    private val paint: Paint = Paint()
    private val imageMatrix: Matrix = Matrix()
    private val touchManager = TouchManager(2)

    val position: PointF by lazy {PointF(width/2f, height/2f)}

    var scale: Float = 1.0f
    private set
    var angle: Float = 0.0f
    private set

    var rotatable = true
    var zoomable = true

    private var layers: Set<Layer> = HashSet()

    constructor(context: Context, attrs: AttributeSet): super(context, attrs){
        with(context.obtainStyledAttributes(attrs, R.styleable.MapView)) {
            rotatable = getBoolean(R.styleable.MapView_rotatable, true)
            zoomable = getBoolean(R.styleable.MapView_zoomable, true)
        }
    }

    private fun updateImageMatrix() {
        imageMatrix.reset();
        imageMatrix.postTranslate(-mapImage!!.width / 2.0f, -mapImage!!.height / 2.0f)
        if (rotatable)
            imageMatrix.postRotate(angle.toDegree())
        if (zoomable)
            imageMatrix.postScale(scale, scale)
        imageMatrix.postTranslate(position.x, position.y)
    }

    fun reset() {
        imageMatrix.reset()
        scale = 1.0f
        angle = 0.0f
        viewTreeObserver.addOnGlobalLayoutListener(this)
        invalidate()
    }

    fun toPositionOnMap(x: Float, y: Float): PointF {
        val invertMatrix = Matrix()
        val point = floatArrayOf(x, y)

        imageMatrix.invert(invertMatrix)
        invertMatrix.mapPoints(point)
        return PointF(point[0], point[1])
    }

    fun toPositionOnScreen(x: Float, y: Float): PointF {
        return with (floatArrayOf(x, y)) {
            imageMatrix.mapPoints(this)
            PointF(this[0], this[1])
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        mapImage?.let {
            updateImageMatrix()
            canvas.matrix = imageMatrix
            canvas.drawBitmap(mapImage, 0f,0f, paint)

            layers.forEach { it.drawOnCanvas(canvas) }
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        touchManager.update(event)
        val touchPointOnMap = toPositionOnMap(event.x, event.y)

        with (touchPointOnMap) {
            Log.i(javaClass.simpleName, "(${x}, ${y})")
        }

        when(touchManager.pressCount) {
            0 -> {
                layers.forEach { it.onTouch(touchPointOnMap.x, touchPointOnMap.y, touchManager.actionCode) }
            }
            1 -> {
                val offset = touchManager.getDelta(0)
                if (offset.length() > 2)
                    position.offset(offset.x, offset.y)
                layers.forEach { it.onTouch(touchPointOnMap.x, touchPointOnMap.y, touchManager.actionCode) }
            }
            2 -> {
                val current = touchManager.getVector(0, 1)
                val previous = touchManager.getVectorPrevious(0, 1)

                if (previous.length() != 0f) {
                    scale *= current.length() / previous.length()
                }

                angle -= current.getSignedAngleBetween(previous)
            }

        }
        invalidate()
        return true
    }

    override fun onGlobalLayout() {
        position.set(width / 2f, height / 2f)
        viewTreeObserver.removeOnGlobalLayoutListener(this)
    }

    fun setMapImage(mapImage: Drawable?) {
        this.mapImage = mapImage?.toBitmap(mapImage.intrinsicWidth, mapImage.intrinsicHeight)
    }

    fun setMapImage(@DrawableRes resId: Int) {
        setMapImage(ContextCompat.getDrawable(context, resId))
    }

    fun addLayer(layer: Layer) {
        layers += layer
        layer.mapView = this
        invalidate()
    }

    fun removeLayer(layer: Layer) {
        layers -= layer
        layer.mapView = null
        invalidate()
    }

}

fun Float.toDegree(): Float {
    return this * 180f / PI.toFloat()
}