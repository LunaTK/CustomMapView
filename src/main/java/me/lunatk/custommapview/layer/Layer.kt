package me.lunatk.custommapview.mapview.layer

import android.graphics.Canvas
import me.lunatk.custommapview.mapview.MapView

abstract class Layer {

    var mapView: MapView? = null

    abstract fun drawOnCanvas(canvas: Canvas)

    /**
     * Callback function when MapView is touched by user.
     * x,y coordinates are passed as relative position on the map.
     *
     * @param x The x coordinate of touch event on the map
     * @param y The y coordinate of touch event on the map
     * @param action Action code of touch event
     */
    abstract fun onTouch(x: Float, y: Float, action: Int)
}