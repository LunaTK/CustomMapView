package me.lunatk.custommapview.mapview

import android.graphics.PointF
import android.util.Log
import android.view.MotionEvent
import androidx.core.graphics.minus
import androidx.core.graphics.plus

class TouchManager {
    private val maxNumOfTouch: Int
    private val points: Array<PointF?>
    private val previousPoints: Array<PointF?>
    val middlePoint: PointF get() = (points.reduce { acc, p -> (acc ?: PointF()) + (p ?: PointF()) } ?: PointF()) / pressCount

    val pressCount get() = points.count { it != null }
    var actionCode: Int = 0
    private set

    var isLongTouch = false
    var lastTouchTime: Long? = null
    private set
    private var touchDownPoint: PointF? = null

    var onLongTouch: ((event: MotionEvent) -> Unit)? = null

    constructor(maxNumOfTouch: Int) {
        this.maxNumOfTouch = maxNumOfTouch
        this.points = Array(maxNumOfTouch) {null}
        this.previousPoints = Array(maxNumOfTouch) {null}
    }

    constructor(): this(2)

    fun isPressed(index: Int): Boolean = points[index] != null

    fun getDelta(index: Int): PointF {
        return if (isPressed(index) && previousPoints[index] != null) {
            points[index]!! - previousPoints[index]!!
        } else {
            PointF()
        }
    }

    fun getPoint(index: Int): PointF {
        return points[index] ?: PointF()
    }

    fun getPreviousPoint(index: Int): PointF {
        return previousPoints[index] ?: PointF()
    }

    fun getVector(indexA: Int, indexB: Int): PointF = points[indexB]!! - points[indexA]!!

    fun getVectorPrevious(indexA: Int, indexB: Int): PointF {
        return if (previousPoints[indexA] == null || previousPoints[indexB] == null) {
            return getVector(indexA, indexB)
        } else {
            previousPoints[indexB]!! - previousPoints[indexA]!!
        }
    }

    private fun checkLongTouch(event: MotionEvent) {
        isLongTouch = false
        when(actionCode) {
            MotionEvent.ACTION_DOWN -> {
                lastTouchTime = System.currentTimeMillis()
                touchDownPoint = PointF(event.x, event.y)
            }
            MotionEvent.ACTION_MOVE -> {
                touchDownPoint?.let {
                    if ((it - PointF(event.x, event.y)).length() > 20) {
                        lastTouchTime = null
                        touchDownPoint = null
                    } else if (elapsedTouchTime > 500) { // Long Touch
                        onLongTouch?.invoke(event)
                        lastTouchTime = null
                        touchDownPoint = null
                        isLongTouch = true
                    }
                }
            }
            else -> {
                lastTouchTime = null
                touchDownPoint = null
            }
        }
    }

    private val elapsedTouchTime: Long get() = System.currentTimeMillis() - (lastTouchTime ?: 0L)

    fun update(event: MotionEvent) {
        actionCode = event.action and MotionEvent.ACTION_MASK
        checkLongTouch(event)

        if (actionCode == MotionEvent.ACTION_POINTER_UP || actionCode == MotionEvent.ACTION_UP) {
            val index = event.action shr MotionEvent.ACTION_POINTER_ID_SHIFT
            previousPoints[index] = null
            points[index] = null
        } else {
            for (i in 0.until(maxNumOfTouch)) {
                if (i < event.pointerCount) {
                    val index = event.getPointerId(i)
                    val newPoint = event.getPoint(i)

                    if (points[index] == null) {
                        points[index] = newPoint
                    } else {
                        previousPoints[index] = previousPoints[index]?.apply {
                            set(points[index])
                        } ?: newPoint.copy()

                        points[index]!!.set(newPoint)
                    }
                } else {
                    previousPoints[i] = null
                    points[i] = null
                }
            }
        }
    }

}