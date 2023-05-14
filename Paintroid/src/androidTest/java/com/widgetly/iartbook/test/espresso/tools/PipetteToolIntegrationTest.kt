/*
 *  Paintroid: An image manipulation application for Android.
 *  Copyright (C) 2010-2022 The Catrobat Team
 *  (<http://developer.catrobat.org/credits>)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.widgetly.iartbook.test.espresso.tools

import android.graphics.Color
import androidx.test.espresso.action.GeneralLocation
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.catrobat.paintroid.MainActivity
import org.catrobat.paintroid.R
import com.widgetly.iartbook.test.espresso.util.wrappers.ColorPickerPreviewInteraction.onColorPickerPreview
import com.widgetly.iartbook.test.espresso.util.wrappers.ColorPickerViewInteraction.onColorPickerView
import com.widgetly.iartbook.test.espresso.util.wrappers.DrawingSurfaceInteraction.onDrawingSurfaceView
import com.widgetly.iartbook.test.espresso.util.wrappers.LayerMenuViewInteraction.onLayerMenuView
import com.widgetly.iartbook.test.espresso.util.wrappers.ToolBarViewInteraction.onToolBarView
import com.widgetly.iartbook.test.espresso.util.wrappers.ToolPropertiesInteraction.onToolProperties
import org.catrobat.paintroid.test.utils.ScreenshotOnFailRule
import org.catrobat.paintroid.tools.ToolType
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PipetteToolIntegrationTest {
    @get:Rule
    var launchActivityRule = ActivityScenarioRule(MainActivity::class.java)

    @get:Rule
    var screenshotOnFailRule = ScreenshotOnFailRule()

    @Before
    fun setUp() { onToolBarView().performSelectTool(ToolType.BRUSH) }

    @Test
    fun testOnEmptyBitmapPipetteTools() {
        onToolProperties().checkMatchesColor(Color.BLACK)
        onDrawingSurfaceView().checkPixelColor(Color.TRANSPARENT, com.widgetly.iartbook.test.espresso.util.BitmapLocationProvider.MIDDLE)
        onToolBarView().performSelectTool(ToolType.PIPETTE)
        onDrawingSurfaceView().perform(
            com.widgetly.iartbook.test.espresso.util.UiInteractions.touchAt(
                com.widgetly.iartbook.test.espresso.util.DrawingSurfaceLocationProvider.MIDDLE))
        onToolProperties().checkMatchesColor(Color.TRANSPARENT)
    }

    @Test
    fun testOnEmptyBitmapPipetteColorPicker() {
        onToolProperties().checkMatchesColor(Color.BLACK)
        onDrawingSurfaceView().checkPixelColor(Color.TRANSPARENT, com.widgetly.iartbook.test.espresso.util.BitmapLocationProvider.MIDDLE)
        onColorPickerView()
            .performOpenColorPicker()
            .perform(
                com.widgetly.iartbook.test.espresso.util.UiInteractions.swipe(
                    com.widgetly.iartbook.test.espresso.util.DrawingSurfaceLocationProvider.TOP_MIDDLE,
                    com.widgetly.iartbook.test.espresso.util.DrawingSurfaceLocationProvider.BOTTOM_MIDDLE
                )
            )
        onColorPickerView().clickPipetteButton()
        onColorPickerPreview().perform(com.widgetly.iartbook.test.espresso.util.UiInteractions.touchAt(GeneralLocation.CENTER))
        onColorPickerPreview().checkColorPreviewColor(Color.TRANSPARENT)
    }

    @Test
    fun testPipetteToolAfterBrushOnSingleLayer() {
        onToolProperties().setColor(Color.RED)
        onDrawingSurfaceView().perform(
            com.widgetly.iartbook.test.espresso.util.UiInteractions.touchAt(
                com.widgetly.iartbook.test.espresso.util.DrawingSurfaceLocationProvider.MIDDLE))
        onDrawingSurfaceView().checkPixelColor(Color.RED, com.widgetly.iartbook.test.espresso.util.BitmapLocationProvider.MIDDLE)
        onToolProperties()
            .setColorResource(R.color.pocketpaint_color_picker_transparent)
            .checkMatchesColor(Color.TRANSPARENT)
        onToolBarView().performSelectTool(ToolType.PIPETTE)
        onDrawingSurfaceView().perform(
            com.widgetly.iartbook.test.espresso.util.UiInteractions.touchAt(
                com.widgetly.iartbook.test.espresso.util.DrawingSurfaceLocationProvider.MIDDLE))
        onToolProperties().checkMatchesColor(Color.RED)
    }

    @Test
    fun testPipetteColorPickerAfterBrushOnSingleLayer() {
        onToolProperties().setColor(Color.RED)
        onDrawingSurfaceView().perform(
            com.widgetly.iartbook.test.espresso.util.UiInteractions.touchAt(
                com.widgetly.iartbook.test.espresso.util.DrawingSurfaceLocationProvider.MIDDLE))
        onDrawingSurfaceView().checkPixelColor(Color.RED, com.widgetly.iartbook.test.espresso.util.BitmapLocationProvider.MIDDLE)
        onToolProperties()
            .setColorResource(R.color.pocketpaint_color_picker_transparent)
            .checkMatchesColor(Color.TRANSPARENT)
        onColorPickerView()
            .performOpenColorPicker()
            .perform(
                com.widgetly.iartbook.test.espresso.util.UiInteractions.swipe(
                    com.widgetly.iartbook.test.espresso.util.DrawingSurfaceLocationProvider.TOP_MIDDLE,
                    com.widgetly.iartbook.test.espresso.util.DrawingSurfaceLocationProvider.BOTTOM_MIDDLE
                )
            )
        onColorPickerView().clickPipetteButton()
        onColorPickerPreview().perform(com.widgetly.iartbook.test.espresso.util.UiInteractions.touchAt(GeneralLocation.CENTER))
        onColorPickerPreview().checkColorPreviewColor(Color.RED)
    }

    @Test
    fun testPipetteToolAfterBrushOnMultiLayer() {
        onDrawingSurfaceView().perform(
            com.widgetly.iartbook.test.espresso.util.UiInteractions.touchAt(
                com.widgetly.iartbook.test.espresso.util.DrawingSurfaceLocationProvider.MIDDLE))
        onDrawingSurfaceView().checkPixelColor(Color.BLACK, com.widgetly.iartbook.test.espresso.util.BitmapLocationProvider.MIDDLE)
        onLayerMenuView()
            .performOpen()
            .performAddLayer()
            .performClose()
        onToolProperties().setColor(Color.TRANSPARENT)
        onToolBarView().performSelectTool(ToolType.PIPETTE)
        onToolProperties().checkMatchesColor(Color.TRANSPARENT)
        onDrawingSurfaceView()
            .checkPixelColor(Color.BLACK, com.widgetly.iartbook.test.espresso.util.BitmapLocationProvider.MIDDLE)
            .perform(com.widgetly.iartbook.test.espresso.util.UiInteractions.touchAt(com.widgetly.iartbook.test.espresso.util.DrawingSurfaceLocationProvider.MIDDLE))
        onToolProperties().checkMatchesColor(Color.BLACK)
    }

    @Test
    fun testPipetteColorPickerAfterBrushOnMultiLayer() {
        onDrawingSurfaceView().perform(
            com.widgetly.iartbook.test.espresso.util.UiInteractions.touchAt(
                com.widgetly.iartbook.test.espresso.util.DrawingSurfaceLocationProvider.MIDDLE))
        onDrawingSurfaceView().checkPixelColor(Color.BLACK, com.widgetly.iartbook.test.espresso.util.BitmapLocationProvider.MIDDLE)
        onLayerMenuView()
            .performOpen()
            .performAddLayer()
            .performClose()
        onToolProperties().setColor(Color.TRANSPARENT)
        onColorPickerView()
            .performOpenColorPicker()
            .checkCurrentViewColor(Color.TRANSPARENT)
        onColorPickerView()
            .perform(
                com.widgetly.iartbook.test.espresso.util.UiInteractions.swipe(
                    com.widgetly.iartbook.test.espresso.util.DrawingSurfaceLocationProvider.TOP_MIDDLE,
                    com.widgetly.iartbook.test.espresso.util.DrawingSurfaceLocationProvider.BOTTOM_MIDDLE
                )
            )
        onColorPickerView().clickPipetteButton()
        onColorPickerPreview().checkColorPreviewColor(Color.TRANSPARENT)
        onColorPickerPreview().perform(com.widgetly.iartbook.test.espresso.util.UiInteractions.touchAt(GeneralLocation.CENTER))
        onColorPickerPreview().checkColorPreviewColor(Color.BLACK)
    }

    @Test
    fun testPipetteColorPickerAfterBrushOnSingleLayerAcceptColor() {
        onToolProperties().setColor(Color.RED)
        onDrawingSurfaceView().perform(
            com.widgetly.iartbook.test.espresso.util.UiInteractions.touchAt(
                com.widgetly.iartbook.test.espresso.util.DrawingSurfaceLocationProvider.MIDDLE))
        onDrawingSurfaceView().checkPixelColor(Color.RED, com.widgetly.iartbook.test.espresso.util.BitmapLocationProvider.MIDDLE)
        onToolProperties()
            .setColorResource(R.color.pocketpaint_color_picker_transparent)
            .checkMatchesColor(Color.TRANSPARENT)
        onColorPickerView()
            .performOpenColorPicker()
            .perform(
                com.widgetly.iartbook.test.espresso.util.UiInteractions.swipe(
                    com.widgetly.iartbook.test.espresso.util.DrawingSurfaceLocationProvider.TOP_MIDDLE,
                    com.widgetly.iartbook.test.espresso.util.DrawingSurfaceLocationProvider.BOTTOM_MIDDLE
                )
            )
        onColorPickerView().clickPipetteButton()
        onColorPickerPreview().perform(com.widgetly.iartbook.test.espresso.util.UiInteractions.touchAt(GeneralLocation.CENTER))
        onColorPickerPreview().performCloseColorPickerPreviewWithDoneButton()
        onColorPickerView().checkNewColorViewColor(Color.RED)
    }

    @Test
    fun testPipetteColorPickerShowDoneDialog() {
        onToolProperties().setColor(Color.BLACK)
        onColorPickerView()
            .performOpenColorPicker()
            .perform(
                com.widgetly.iartbook.test.espresso.util.UiInteractions.swipe(
                    com.widgetly.iartbook.test.espresso.util.DrawingSurfaceLocationProvider.TOP_MIDDLE,
                    com.widgetly.iartbook.test.espresso.util.DrawingSurfaceLocationProvider.BOTTOM_MIDDLE
                )
            )
        onColorPickerView().clickPipetteButton()
        onColorPickerPreview().perform(com.widgetly.iartbook.test.espresso.util.UiInteractions.touchAt(GeneralLocation.CENTER))
        onColorPickerPreview().assertShowColorPickerPreviewBackDialog()
    }

    @Test
    fun testPipetteColorPickerAfterBrushOnSingleLayerRejectColorWithDoneDialog() {
        onToolProperties().setColor(Color.RED)
        onDrawingSurfaceView().perform(
            com.widgetly.iartbook.test.espresso.util.UiInteractions.touchAt(
                com.widgetly.iartbook.test.espresso.util.DrawingSurfaceLocationProvider.MIDDLE))
        onDrawingSurfaceView().checkPixelColor(Color.RED, com.widgetly.iartbook.test.espresso.util.BitmapLocationProvider.MIDDLE)
        onToolProperties()
            .setColorResource(R.color.pocketpaint_color_picker_transparent)
            .checkMatchesColor(Color.TRANSPARENT)
        onColorPickerView()
            .performOpenColorPicker()
            .perform(
                com.widgetly.iartbook.test.espresso.util.UiInteractions.swipe(
                    com.widgetly.iartbook.test.espresso.util.DrawingSurfaceLocationProvider.TOP_MIDDLE,
                    com.widgetly.iartbook.test.espresso.util.DrawingSurfaceLocationProvider.BOTTOM_MIDDLE
                )
            )
        onColorPickerView().clickPipetteButton()
        onColorPickerPreview().perform(com.widgetly.iartbook.test.espresso.util.UiInteractions.touchAt(GeneralLocation.CENTER))
        onColorPickerPreview().performCloseColorPickerPreviewWithBackButtonDecline()
        onColorPickerView().checkNewColorViewColor(Color.TRANSPARENT)
    }

    @Test
    fun testPipetteColorPickerAfterBrushOnSingleLayerAcceptColorWithDoneDialog() {
        onToolProperties().setColor(Color.RED)
        onDrawingSurfaceView().perform(
            com.widgetly.iartbook.test.espresso.util.UiInteractions.touchAt(
                com.widgetly.iartbook.test.espresso.util.DrawingSurfaceLocationProvider.MIDDLE))
        onDrawingSurfaceView().checkPixelColor(Color.RED, com.widgetly.iartbook.test.espresso.util.BitmapLocationProvider.MIDDLE)
        onToolProperties()
            .setColorResource(R.color.pocketpaint_color_picker_transparent)
            .checkMatchesColor(Color.TRANSPARENT)
        onColorPickerView()
            .performOpenColorPicker()
            .perform(
                com.widgetly.iartbook.test.espresso.util.UiInteractions.swipe(
                    com.widgetly.iartbook.test.espresso.util.DrawingSurfaceLocationProvider.TOP_MIDDLE,
                    com.widgetly.iartbook.test.espresso.util.DrawingSurfaceLocationProvider.BOTTOM_MIDDLE
                )
            )
        onColorPickerView().clickPipetteButton()
        onColorPickerPreview().perform(com.widgetly.iartbook.test.espresso.util.UiInteractions.touchAt(GeneralLocation.CENTER))
        onColorPickerPreview().performCloseColorPickerPreviewWithBackButtonAccept()
        onColorPickerView().checkNewColorViewColor(Color.RED)
    }

    @Test
    fun testPipetteAfterUndo() {
        onDrawingSurfaceView().perform(
            com.widgetly.iartbook.test.espresso.util.UiInteractions.touchAt(
                com.widgetly.iartbook.test.espresso.util.DrawingSurfaceLocationProvider.MIDDLE))
        onToolBarView().performSelectTool(ToolType.PIPETTE)
        onDrawingSurfaceView().perform(
            com.widgetly.iartbook.test.espresso.util.UiInteractions.touchAt(
                com.widgetly.iartbook.test.espresso.util.DrawingSurfaceLocationProvider.MIDDLE))
        onToolProperties().checkMatchesColor(Color.BLACK)
        com.widgetly.iartbook.test.espresso.util.wrappers.TopBarViewInteraction.onTopBarView().performUndo()
        onDrawingSurfaceView().perform(
            com.widgetly.iartbook.test.espresso.util.UiInteractions.touchAt(
                com.widgetly.iartbook.test.espresso.util.DrawingSurfaceLocationProvider.MIDDLE))
        onToolProperties().checkMatchesColor(Color.TRANSPARENT)
    }

    @Test
    fun testPipetteAfterRedo() {
        onDrawingSurfaceView().perform(
            com.widgetly.iartbook.test.espresso.util.UiInteractions.touchAt(
                com.widgetly.iartbook.test.espresso.util.DrawingSurfaceLocationProvider.MIDDLE))
        onToolBarView().performSelectTool(ToolType.PIPETTE)
        onDrawingSurfaceView().perform(
            com.widgetly.iartbook.test.espresso.util.UiInteractions.touchAt(
                com.widgetly.iartbook.test.espresso.util.DrawingSurfaceLocationProvider.MIDDLE))
        onToolProperties().checkMatchesColor(Color.BLACK)
        com.widgetly.iartbook.test.espresso.util.wrappers.TopBarViewInteraction.onTopBarView().performUndo()
        com.widgetly.iartbook.test.espresso.util.wrappers.TopBarViewInteraction.onTopBarView().performRedo()
        onDrawingSurfaceView().perform(
            com.widgetly.iartbook.test.espresso.util.UiInteractions.touchAt(
                com.widgetly.iartbook.test.espresso.util.DrawingSurfaceLocationProvider.MIDDLE))
        onToolProperties().checkMatchesColor(Color.BLACK)
    }
}
