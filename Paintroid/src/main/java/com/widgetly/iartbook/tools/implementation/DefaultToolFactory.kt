/*
 * Paintroid: An image manipulation application for Android.
 *  Copyright (C) 2010-2022 The Catrobat Team
 * (<http://developer.catrobat.org/credits>)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.widgetly.iartbook.tools.implementation

import androidx.test.espresso.idling.CountingIdlingResource
import com.widgetly.iartbookid.MainActivity
import com.widgetly.iartbookid.colorpicker.OnColorPickedListener
import com.widgetly.iartbookid.command.CommandManager
import com.widgetly.iartbookid.tools.ContextCallback
import com.widgetly.iartbookid.tools.Tool
import com.widgetly.iartbookid.tools.ToolFactory
import com.widgetly.iartbookid.tools.ToolPaint
import com.widgetly.iartbookid.tools.ToolType
import com.widgetly.iartbookid.tools.Workspace
import com.widgetly.iartbookid.tools.options.ToolOptionsViewController
import com.widgetly.iartbookid.ui.tools.DefaultBrushToolOptionsView
import com.widgetly.iartbookid.ui.tools.DefaultFillToolOptionsView
import com.widgetly.iartbookid.ui.tools.DefaultShapeToolOptionsView
import com.widgetly.iartbookid.ui.tools.DefaultSprayToolOptionsView
import com.widgetly.iartbookid.ui.tools.DefaultClipboardToolOptionsView
import com.widgetly.iartbookid.ui.tools.DefaultTextToolOptionsView
import com.widgetly.iartbookid.ui.tools.DefaultTransformToolOptionsView
import com.widgetly.iartbookid.ui.tools.DefaultSmudgeToolOptionsView

private const val DRAW_TIME_INIT: Long = 30_000_000

@SuppressWarnings("LongMethod")
class DefaultToolFactory(mainActivity: MainActivity) : ToolFactory {
    var mainActivity: MainActivity = mainActivity
    override fun createTool(
        toolType: ToolType,
        toolOptionsViewController: ToolOptionsViewController,
        commandManager: CommandManager,
        workspace: Workspace,
        idlingResource: CountingIdlingResource,
        toolPaint: ToolPaint,
        contextCallback: ContextCallback,
        onColorPickedListener: OnColorPickedListener
    ): Tool {
        val toolLayout = toolOptionsViewController.toolSpecificOptionsLayout
        return when (toolType) {
            ToolType.CURSOR -> CursorTool(
                DefaultBrushToolOptionsView(toolLayout),
                contextCallback,
                toolOptionsViewController,
                toolPaint,
                workspace,
                idlingResource,
                commandManager,
                DRAW_TIME_INIT
            )
            ToolType.CLIPBOARD -> ClipboardTool(
                DefaultClipboardToolOptionsView(toolLayout),
                contextCallback,
                toolOptionsViewController,
                toolPaint,
                workspace,
                idlingResource,
                commandManager,
                DRAW_TIME_INIT
            )
            ToolType.IMPORTPNG -> ImportTool(
                contextCallback,
                toolOptionsViewController,
                toolPaint,
                workspace,
                idlingResource,
                commandManager,
                DRAW_TIME_INIT
            )
            ToolType.PIPETTE -> PipetteTool(
                contextCallback,
                toolOptionsViewController,
                toolPaint,
                workspace,
                idlingResource,
                commandManager,
                onColorPickedListener,
            )
            ToolType.FILL -> FillTool(
                DefaultFillToolOptionsView(toolLayout),
                contextCallback,
                toolOptionsViewController,
                toolPaint,
                workspace,
                idlingResource,
                commandManager,
                DRAW_TIME_INIT
            )
            ToolType.TRANSFORM -> TransformTool(
                DefaultTransformToolOptionsView(toolLayout),
                contextCallback,
                toolOptionsViewController,
                toolPaint,
                workspace,
                idlingResource,
                commandManager,
                DRAW_TIME_INIT
            )
            ToolType.SHAPE -> ShapeTool(
                DefaultShapeToolOptionsView(toolLayout),
                contextCallback,
                toolOptionsViewController,
                toolPaint,
                workspace,
                idlingResource,
                commandManager,
                DRAW_TIME_INIT
            )
            ToolType.ERASER -> EraserTool(
                DefaultBrushToolOptionsView(toolLayout),
                contextCallback,
                toolOptionsViewController,
                toolPaint,
                workspace,
                idlingResource,
                commandManager,
                DRAW_TIME_INIT
            )
            ToolType.LINE -> LineTool(
                DefaultBrushToolOptionsView(toolLayout),
                contextCallback,
                toolOptionsViewController,
                toolPaint,
                workspace,
                idlingResource,
                commandManager,
                DRAW_TIME_INIT
            )
            ToolType.TEXT -> TextTool(
                DefaultTextToolOptionsView(toolLayout),
                contextCallback,
                toolOptionsViewController,
                toolPaint,
                workspace,
                idlingResource,
                commandManager,
                DRAW_TIME_INIT
            )
            ToolType.HAND -> HandTool(
                contextCallback,
                toolOptionsViewController,
                toolPaint,
                workspace,
                idlingResource,
                commandManager,
                DRAW_TIME_INIT
            )
            ToolType.SPRAY -> SprayTool(
                DefaultSprayToolOptionsView(toolLayout),
                contextCallback,
                toolOptionsViewController,
                toolPaint,
                workspace,
                idlingResource,
                commandManager,
                DRAW_TIME_INIT
            )
            ToolType.WATERCOLOR -> WatercolorTool(
                DefaultBrushToolOptionsView(toolLayout),
                contextCallback,
                toolOptionsViewController,
                toolPaint,
                workspace,
                idlingResource,
                commandManager,
                DRAW_TIME_INIT
            )
            ToolType.SMUDGE -> SmudgeTool(
                DefaultSmudgeToolOptionsView(toolLayout),
                contextCallback,
                toolOptionsViewController,
                toolPaint,
                workspace,
                idlingResource,
                commandManager,
            )
            ToolType.CLIP -> ClippingTool(
                DefaultBrushToolOptionsView(toolLayout),
                contextCallback,
                toolOptionsViewController,
                toolPaint,
                workspace,
                idlingResource,
                commandManager,
                DRAW_TIME_INIT,
                mainActivity
            )
            else -> BrushTool(
                DefaultBrushToolOptionsView(toolLayout),
                contextCallback,
                toolOptionsViewController,
                toolPaint,
                workspace,
                idlingResource,
                commandManager,
                DRAW_TIME_INIT
            )
        }
    }
}
