package com.bignerdbranch.android.boxdrawingview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PointF
import android.os.Bundle
import android.os.Parcelable
import android.util.AttributeSet
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import androidx.core.view.GestureDetectorCompat

private const val TAG = "BoxDrawingView"
private const val BOXES="BOXES"
private const val VIEW_STATE="VIEW_STATE"
/**
 * [BoxDrawingView] is a simple custom view for graphically drawing shapes based on the how the user drags his touch on the [View]
 *  Even if you do not plan on using both constructors, it is good practice to include them.
 */
class BoxDrawingView(context: Context, attrs: AttributeSet? = null) :
        View(context, attrs), GestureDetector.OnGestureListener {
    /**
     * variable for GestureDetectorCompat object
     */
    private var detector: GestureDetectorCompat = GestureDetectorCompat(context, this)

    private var currentBox: Box? = null
    private var boxen = mutableListOf<Box>()
    private val boxPaint = Paint().apply {
        color = 0x22ff0000.toInt()
    }
    private val backgroundPaint = Paint().apply {
        color = 0xfff8efe0.toInt()
    }

    /**
     *See superclass method for description [android.view.View.onTouchEvent]
     *
     * @param event:  MotionEvent, a class that describes the touch event, including itslocation and its action
     * @see [android.view.View.onTouchEvent]
     */
    override fun onTouchEvent(event: MotionEvent): Boolean {
        //region when using GestureDetector
        /*    return if(detector.onTouchEvent(event)){
                true
            }
            else{
                super.onTouchEvent(event)
            }*/
        //endregion
        val current = PointF(event.x, event.y)
        var action = ""

        //index can change but the id doesn't change
        val index1 = event.actionIndex
        val id1=event.getPointerId(index1)


        when (event.action) {
            MotionEvent.ACTION_DOWN -> {//note: ACTION_DOWN is the start of all touch event
                action = "ACTION_DOWN"
                // Reset drawing state
                currentBox = Box(current).also {
                    boxen.add(it)
                }
            }
            MotionEvent.ACTION_MOVE -> {
                action = "ACTION_MOVE"
                updateCurrentBox(current)
            }
            MotionEvent.ACTION_UP -> {
                action = "ACTION_UP"
                updateCurrentBox(current)
                currentBox = null
            }
            MotionEvent.ACTION_CANCEL -> {
                action = "ACTION_CANCEL"
                currentBox = null
            }
            MotionEvent.ACTION_OUTSIDE -> {
                Log.d(TAG, "Movement occurred outside bounds of current screen element")
            }
            else -> super.onTouchEvent(event)
        }

        Log.i(TAG, "$action at x=${current.x}, y=${current.y}---with an index:$index1 and id: ${event.getPointerId(index1)}")
        return true
    }

    private fun updateCurrentBox(current: PointF) {
        currentBox?.let {
            it.end = current
            //force the view to update itself.  This causes BoxDrawing.kt to redraw itself and will cause
            //onDraw(Canvas) to be called again.
            invalidate()
        }
    }

    override fun onDraw(canvas: Canvas) {
        // Fill the background
        canvas.drawPaint(backgroundPaint)
        boxen.forEach { box ->
            canvas.drawRect(box.left, box.top, box.right, box.bottom, boxPaint)
        }
    }

    override fun onDown(e: MotionEvent?): Boolean {

        Log.i(TAG, "Performed action ONDOWN:::   ${e!!.action}")
        return true
    }

    override fun onShowPress(e: MotionEvent?) {
        Log.i(TAG, "Performed action ONSHOWPRESS:::   ${e!!.action}")
    }

    override fun onSingleTapUp(e: MotionEvent?): Boolean {
        Log.i(TAG, "Performed action ONSINGLETAPUP:::   ${e!!.action}")
        return true
    }

    override fun onScroll(e1: MotionEvent?, e2: MotionEvent?, distanceX: Float, distanceY: Float): Boolean {
        Log.i(TAG, "Performed action ONSCROLL:::   for e1${e1!!.action}::: for e2${e2!!.action}")
        return true
    }

    override fun onLongPress(e: MotionEvent?) {
        Log.i(TAG, "Performed action ONLONGPRESS:::   ${e!!.action}")
    }

    override fun onFling(e1: MotionEvent?, e2: MotionEvent?, velocityX: Float, velocityY: Float): Boolean {
        Log.i(TAG, "Performed action ONFLING:::   for e1${e1!!.action}::: for e2${e2!!.action}")
        return true
    }

    override fun onSaveInstanceState(): Parcelable {
        val state = super.onSaveInstanceState()
        //persist date
        val bundle = Bundle()
        bundle.putParcelableArrayList(BOXES,ArrayList<Parcelable>(boxen))
        bundle.putParcelable(VIEW_STATE,state)
        return bundle
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        if(state is Bundle){
            //restore data
            boxen = state.getParcelableArrayList<Box>(BOXES)?.toMutableList()?: mutableListOf()
            super.onRestoreInstanceState(state.getParcelable(VIEW_STATE))
        }
    }
}