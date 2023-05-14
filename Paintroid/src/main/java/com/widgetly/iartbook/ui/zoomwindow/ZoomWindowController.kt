package com.widgetly.iartbook.ui.zoomwindow

import android.graphics.Bitmap
import android.graphics.PointF
import com.widgetly.iartbookid.tools.Tool

interface ZoomWindowController {
    fun show(drawingSurfaceCoordinates: PointF, displayCoordinates: PointF)

    fun dismiss()

    fun dismissOnPinch()

    fun onMove(drawingSurfaceCoordinates: PointF, displayCoordinates: PointF)

    fun getBitmap(bitmap: Bitmap?)

    fun checkIfToolCompatibleWithZoomWindow(tool: Tool?): DefaultZoomWindowController.Constants
}
