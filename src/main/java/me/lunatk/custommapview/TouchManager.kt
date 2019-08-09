package me.lunatk.custommapview.mapview

import android.graphics.PointF
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

    fun update(event: MotionEvent) {
        actionCode = event.action and MotionEvent.ACTION_MASK

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