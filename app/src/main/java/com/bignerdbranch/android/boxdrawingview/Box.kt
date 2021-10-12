package com.bignerdbranch.android.boxdrawingview

import android.graphics.PointF
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * [Box] is used to store co-ordinates of a touch event.
 */
@Parcelize
class Box(val start: PointF, var end: PointF = start) : Parcelable {

   // var end: PointF = start

    val left: Float
        get() = Math.min(start.x, end.x)
    val right: Float
        get() = Math.max(start.x, end.x)
    val top: Float
        get() = Math.min(start.y, end.y)
    val bottom: Float
        get() = Math.max(start.y, end.y)

}