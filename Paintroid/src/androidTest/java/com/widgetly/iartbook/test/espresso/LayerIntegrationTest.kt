/*
 * Paintroid: An image manipulation application for Android.
 * Copyright (C) 2010-2022 The Catrobat Team
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
package com.widgetly.iartbook.test.espresso

import android.app.Activity
import android.app.Instrumentation.ActivityResult
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.net.Uri
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.scrollToPosition
import androidx.test.espresso.idling.CountingIdlingResource
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.ViewMatchers.isClickable
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.isEnabled
import androidx.test.espresso.matcher.ViewMatchers.isRoot
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import androidx.test.rule.GrantPermissionRule
import junit.framework.AssertionFailedError
import org.catrobat.paintroid.MainActivity
import org.catrobat.paintroid.R
import org.catrobat.paintroid.test.espresso.util.EspressoUtils
import com.widgetly.iartbook.test.espresso.util.UiInteractions.waitFor
import org.catrobat.paintroid.test.utils.ScreenshotOnFailRule
import org.catrobat.paintroid.test.utils.ToastMatcher
import org.catrobat.paintroid.tools.ToolType
import org.hamcrest.Matchers
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File

private const val FOUR_LAYERS = 4

@RunWith(AndroidJUnit4::class)
class LayerIntegrationTest {
    @get:Rule
    var launchActivityRule = ActivityTestRule(MainActivity::class.java)

    @get:Rule
    var screenshotOnFailRule = ScreenshotOnFailRule()

    @get:Rule
    var grantPermissionRule: GrantPermissionRule = EspressoUtils.grantPermissionRulesVersionCheck()

    private var bitmapHeight = 0
    private var bitmapWidth = 0
    private lateinit var activity: Activity
    private lateinit var deletionFileList: ArrayList<File?>
    private lateinit var idlingResource: CountingIdlingResource

    @Before
    fun setUp() {
        activity = launchActivityRule.activity
        deletionFileList = ArrayList()
        val workspace = launchActivityRule.activity.workspace
        bitmapHeight = workspace.height
        bitmapWidth = workspace.width
        idlingResource = launchActivityRule.activity.idlingResource
        IdlingRegistry.getInstance().register(idlingResource)
    }

    @After
    fun tearDown() {
        deletionFileList.forEach { file ->
            if (file != null && file.exists()) {
                Assert.assertTrue(file.delete())
            }
        }
        IdlingRegistry.getInstance().unregister(idlingResource)
    }

    @Test
    fun testShowLayerMenu() {
        com.widgetly.iartbook.test.espresso.util.wrappers.LayerMenuViewInteraction.onLayerMenuView()
            .performOpen()
            .check(matches(isDisplayed()))
    }

    @Test
    fun testInitialSetup() {
        com.widgetly.iartbook.test.espresso.util.wrappers.LayerMenuViewInteraction.onLayerMenuView()
            .check(matches(Matchers.not(isDisplayed())))
        assertIfLayerAddButtonIsEnabled()
        assertIfLayerDeleteButtonIsDisabled()
    }

    @Test
    fun testAddOneLayer() {
        com.widgetly.iartbook.test.espresso.util.wrappers.LayerMenuViewInteraction.onLayerMenuView()
            .checkLayerCount(1)
            .performOpen()
            .performAddLayer()
            .checkLayerCount(2)
    }

    @Test
    fun testButtonsAddOneLayer() {
        com.widgetly.iartbook.test.espresso.util.wrappers.LayerMenuViewInteraction.onLayerMenuView()
            .performOpen()
            .performAddLayer()
            .checkLayerCount(2)
        com.widgetly.iartbook.test.espresso.util.wrappers.LayerMenuViewInteraction.onLayerMenuView().onButtonAdd()
            .check(
                assertIfAddLayerButtonIsEnabled()
            )
        assertIfDeleteLayerButtonIsDisabled()
        com.widgetly.iartbook.test.espresso.util.wrappers.LayerMenuViewInteraction.onLayerMenuView()
            .performAddLayer()
            .performAddLayer()
            .checkLayerCount(4)
        com.widgetly.iartbook.test.espresso.util.wrappers.LayerMenuViewInteraction.onLayerMenuView()
            .performDeleteLayer()
            .performDeleteLayer()
            .performDeleteLayer()
            .checkLayerCount(1)
    }

    private fun assertIfLayerAddButtonIsEnabled() {
        com.widgetly.iartbook.test.espresso.util.wrappers.LayerMenuViewInteraction.onLayerMenuView().onButtonAdd()
            .check(
                matches(
                    Matchers.allOf(
                        isEnabled(),
                        com.widgetly.iartbook.test.espresso.util.UiMatcher.withDrawable(R.drawable.ic_pocketpaint_layers_add)
                    )
                )
            )
    }

    @Test
    fun testButtonsAfterNewImage() {
        com.widgetly.iartbook.test.espresso.util.wrappers.LayerMenuViewInteraction.onLayerMenuView()
            .performOpen()
            .performAddLayer()
            .performAddLayer()
            .performAddLayer()
            .performClose()
            .checkLayerCount(4)
        com.widgetly.iartbook.test.espresso.util.wrappers.TopBarViewInteraction.onTopBarView()
            .performOpenMoreOptions()

        onView(withText(R.string.menu_new_image))
            .perform(click())
        onView(withText(R.string.discard_button_text))
            .perform(click())
        com.widgetly.iartbook.test.espresso.util.wrappers.LayerMenuViewInteraction.onLayerMenuView().onButtonAdd()
            .check(
                assertIfAddLayerButtonIsEnabled()
            )
        assertIfLayerDeleteButtonIsDisabled()
        com.widgetly.iartbook.test.espresso.util.wrappers.LayerMenuViewInteraction.onLayerMenuView()
            .checkLayerCount(1)
    }

    private fun assertIfDeleteLayerButtonIsDisabled() {
        com.widgetly.iartbook.test.espresso.util.wrappers.LayerMenuViewInteraction.onLayerMenuView().onButtonDelete()
            .check(
                matches(
                    Matchers.allOf(
                        isEnabled(),
                        com.widgetly.iartbook.test.espresso.util.UiMatcher.withDrawable(R.drawable.ic_pocketpaint_layers_delete)
                    )
                )
            )
    }

    private fun assertIfLayerAddButtonIsDisabled() {
        com.widgetly.iartbook.test.espresso.util.wrappers.LayerMenuViewInteraction.onLayerMenuView().onButtonAdd()
            .check(
                matches(
                    Matchers.allOf(
                        Matchers.not(isEnabled()),
                        com.widgetly.iartbook.test.espresso.util.UiMatcher.withDrawable(R.drawable.ic_pocketpaint_layers_add_disabled)
                    )
                )
            )
    }

    private fun assertIfLayerDeleteButtonIsDisabled() {
        com.widgetly.iartbook.test.espresso.util.wrappers.LayerMenuViewInteraction.onLayerMenuView().onButtonDelete()
            .check(
                matches(
                    Matchers.allOf(
                        Matchers.not(isEnabled()),
                        com.widgetly.iartbook.test.espresso.util.UiMatcher.withDrawable(R.drawable.ic_pocketpaint_layers_delete_disabled)
                    )
                )
            )
    }

    private fun assertIfAddLayerButtonIsEnabled() = matches(
        Matchers.allOf(
            isEnabled(),
            com.widgetly.iartbook.test.espresso.util.UiMatcher.withDrawable(R.drawable.ic_pocketpaint_layers_add)
        )
    )

    @Test
    fun testUndoRedoLayerAdd() {
        com.widgetly.iartbook.test.espresso.util.wrappers.LayerMenuViewInteraction.onLayerMenuView()
            .performOpen()
            .performAddLayer()
            .performClose()
            .checkLayerCount(2)
        com.widgetly.iartbook.test.espresso.util.wrappers.TopBarViewInteraction.onTopBarView()
            .performUndo()
        com.widgetly.iartbook.test.espresso.util.wrappers.LayerMenuViewInteraction.onLayerMenuView()
            .checkLayerCount(1)
        com.widgetly.iartbook.test.espresso.util.wrappers.TopBarViewInteraction.onTopBarView()
            .performRedo()
        com.widgetly.iartbook.test.espresso.util.wrappers.LayerMenuViewInteraction.onLayerMenuView()
            .checkLayerCount(2)
    }

    @Test
    fun testDeleteEmptyLayer() {
        com.widgetly.iartbook.test.espresso.util.wrappers.LayerMenuViewInteraction.onLayerMenuView()
            .checkLayerCount(1)
            .performOpen()
            .performAddLayer()
            .checkLayerCount(2)
            .performDeleteLayer()
            .checkLayerCount(1)
    }

    @Test
    fun testDeleteFilledLayer() {
        com.widgetly.iartbook.test.espresso.util.wrappers.LayerMenuViewInteraction.onLayerMenuView()
            .checkLayerCount(1)
            .performOpen()
            .performAddLayer()
            .performClose()
        com.widgetly.iartbook.test.espresso.util.wrappers.ToolBarViewInteraction.onToolBarView()
            .performSelectTool(ToolType.PIPETTE)
        com.widgetly.iartbook.test.espresso.util.wrappers.DrawingSurfaceInteraction.onDrawingSurfaceView()
            .perform(com.widgetly.iartbook.test.espresso.util.UiInteractions.touchAt(com.widgetly.iartbook.test.espresso.util.DrawingSurfaceLocationProvider.MIDDLE))
        com.widgetly.iartbook.test.espresso.util.wrappers.ToolPropertiesInteraction.onToolProperties()
            .checkMatchesColor(Color.TRANSPARENT)
        com.widgetly.iartbook.test.espresso.util.wrappers.ToolBarViewInteraction.onToolBarView()
            .performSelectTool(ToolType.FILL)
        com.widgetly.iartbook.test.espresso.util.wrappers.ToolPropertiesInteraction.onToolProperties()
            .setColor(Color.BLACK)
        com.widgetly.iartbook.test.espresso.util.wrappers.DrawingSurfaceInteraction.onDrawingSurfaceView()
            .perform(com.widgetly.iartbook.test.espresso.util.UiInteractions.touchAt(com.widgetly.iartbook.test.espresso.util.DrawingSurfaceLocationProvider.MIDDLE))
        com.widgetly.iartbook.test.espresso.util.wrappers.ToolBarViewInteraction.onToolBarView()
            .performSelectTool(ToolType.PIPETTE)
        com.widgetly.iartbook.test.espresso.util.wrappers.DrawingSurfaceInteraction.onDrawingSurfaceView()
            .perform(com.widgetly.iartbook.test.espresso.util.UiInteractions.touchAt(com.widgetly.iartbook.test.espresso.util.DrawingSurfaceLocationProvider.MIDDLE))
        com.widgetly.iartbook.test.espresso.util.wrappers.ToolPropertiesInteraction.onToolProperties()
            .checkMatchesColor(Color.BLACK)
        com.widgetly.iartbook.test.espresso.util.wrappers.LayerMenuViewInteraction.onLayerMenuView()
            .checkLayerCount(2)
            .performOpen()
            .performDeleteLayer()
            .performClose()
            .checkLayerCount(1)
        com.widgetly.iartbook.test.espresso.util.wrappers.ToolBarViewInteraction.onToolBarView()
            .performSelectTool(ToolType.PIPETTE)
        com.widgetly.iartbook.test.espresso.util.wrappers.DrawingSurfaceInteraction.onDrawingSurfaceView()
            .perform(com.widgetly.iartbook.test.espresso.util.UiInteractions.touchAt(com.widgetly.iartbook.test.espresso.util.DrawingSurfaceLocationProvider.MIDDLE))
        com.widgetly.iartbook.test.espresso.util.wrappers.ToolPropertiesInteraction.onToolProperties()
            .checkMatchesColor(Color.TRANSPARENT)
    }

    @Test
    fun testTryDeleteOnlyLayer() {
        com.widgetly.iartbook.test.espresso.util.wrappers.LayerMenuViewInteraction.onLayerMenuView()
            .checkLayerCount(1)
            .performOpen()
            .performDeleteLayer()
            .checkLayerCount(1)
    }

    @Test
    fun testSwitchBetweenFilledLayers() {
        com.widgetly.iartbook.test.espresso.util.wrappers.ToolBarViewInteraction.onToolBarView()
            .performSelectTool(ToolType.FILL)
        com.widgetly.iartbook.test.espresso.util.wrappers.DrawingSurfaceInteraction.onDrawingSurfaceView()
            .perform(com.widgetly.iartbook.test.espresso.util.UiInteractions.touchAt(com.widgetly.iartbook.test.espresso.util.DrawingSurfaceLocationProvider.MIDDLE))
        com.widgetly.iartbook.test.espresso.util.wrappers.ToolBarViewInteraction.onToolBarView()
            .performSelectTool(ToolType.PIPETTE)
        com.widgetly.iartbook.test.espresso.util.wrappers.DrawingSurfaceInteraction.onDrawingSurfaceView()
            .perform(com.widgetly.iartbook.test.espresso.util.UiInteractions.touchAt(com.widgetly.iartbook.test.espresso.util.DrawingSurfaceLocationProvider.MIDDLE))
        com.widgetly.iartbook.test.espresso.util.wrappers.ToolPropertiesInteraction.onToolProperties()
            .checkMatchesColor(Color.BLACK)
        com.widgetly.iartbook.test.espresso.util.wrappers.LayerMenuViewInteraction.onLayerMenuView()
            .performOpen()
            .performAddLayer()
            .performClose()
        com.widgetly.iartbook.test.espresso.util.wrappers.ToolBarViewInteraction.onToolBarView()
            .performSelectTool(ToolType.FILL)
        com.widgetly.iartbook.test.espresso.util.wrappers.ToolPropertiesInteraction.onToolProperties()
            .setColor(Color.WHITE)
        com.widgetly.iartbook.test.espresso.util.wrappers.DrawingSurfaceInteraction.onDrawingSurfaceView()
            .perform(com.widgetly.iartbook.test.espresso.util.UiInteractions.touchAt(com.widgetly.iartbook.test.espresso.util.DrawingSurfaceLocationProvider.MIDDLE))
        com.widgetly.iartbook.test.espresso.util.wrappers.ToolBarViewInteraction.onToolBarView()
            .performSelectTool(ToolType.PIPETTE)
        com.widgetly.iartbook.test.espresso.util.wrappers.DrawingSurfaceInteraction.onDrawingSurfaceView()
            .perform(com.widgetly.iartbook.test.espresso.util.UiInteractions.touchAt(com.widgetly.iartbook.test.espresso.util.DrawingSurfaceLocationProvider.MIDDLE))
        com.widgetly.iartbook.test.espresso.util.wrappers.ToolPropertiesInteraction.onToolProperties()
            .checkMatchesColor(Color.WHITE)
        com.widgetly.iartbook.test.espresso.util.wrappers.LayerMenuViewInteraction.onLayerMenuView()
            .performOpen()
            .performSelectLayer(1)
            .performClose()
        com.widgetly.iartbook.test.espresso.util.wrappers.DrawingSurfaceInteraction.onDrawingSurfaceView()
            .perform(com.widgetly.iartbook.test.espresso.util.UiInteractions.touchAt(com.widgetly.iartbook.test.espresso.util.DrawingSurfaceLocationProvider.MIDDLE))
        com.widgetly.iartbook.test.espresso.util.wrappers.ToolPropertiesInteraction.onToolProperties()
            .checkMatchesColor(Color.WHITE)
    }

    @Test
    fun testMultipleLayersNewImageDiscardOld() {
        com.widgetly.iartbook.test.espresso.util.wrappers.DrawingSurfaceInteraction.onDrawingSurfaceView()
            .perform(com.widgetly.iartbook.test.espresso.util.UiInteractions.touchAt(com.widgetly.iartbook.test.espresso.util.DrawingSurfaceLocationProvider.MIDDLE))
        com.widgetly.iartbook.test.espresso.util.wrappers.LayerMenuViewInteraction.onLayerMenuView()
            .performOpen()
            .performAddLayer()
            .performAddLayer()
            .performAddLayer()
            .checkLayerCount(4)
            .performClose()
        com.widgetly.iartbook.test.espresso.util.wrappers.TopBarViewInteraction.onTopBarView()
            .performOpenMoreOptions()
        onView(withText(R.string.menu_new_image))
            .perform(click())
        onView(withText(R.string.discard_button_text))
            .perform(click())
        com.widgetly.iartbook.test.espresso.util.wrappers.ToolBarViewInteraction.onToolBarView()
            .performSelectTool(ToolType.PIPETTE)
        com.widgetly.iartbook.test.espresso.util.wrappers.DrawingSurfaceInteraction.onDrawingSurfaceView()
            .perform(com.widgetly.iartbook.test.espresso.util.UiInteractions.touchAt(com.widgetly.iartbook.test.espresso.util.DrawingSurfaceLocationProvider.MIDDLE))
        com.widgetly.iartbook.test.espresso.util.wrappers.ToolPropertiesInteraction.onToolProperties()
            .checkMatchesColor(Color.TRANSPARENT)
        com.widgetly.iartbook.test.espresso.util.wrappers.LayerMenuViewInteraction.onLayerMenuView()
            .checkLayerCount(1)
    }

    @Test
    fun testMultipleLayersNewImageSaveOld() {
        com.widgetly.iartbook.test.espresso.util.wrappers.DrawingSurfaceInteraction.onDrawingSurfaceView()
            .perform(com.widgetly.iartbook.test.espresso.util.UiInteractions.touchAt(com.widgetly.iartbook.test.espresso.util.DrawingSurfaceLocationProvider.MIDDLE))
        com.widgetly.iartbook.test.espresso.util.wrappers.LayerMenuViewInteraction.onLayerMenuView()
            .performOpen()
            .performAddLayer()
            .performAddLayer()
            .performAddLayer()
            .checkLayerCount(4)
            .performClose()
        com.widgetly.iartbook.test.espresso.util.wrappers.TopBarViewInteraction.onTopBarView()
            .performOpenMoreOptions()
        onView(withText(R.string.menu_new_image))
            .perform(click())
        onView(withText(R.string.save_button_text))
            .perform(click())
        onView(withText(R.string.save_button_text))
            .perform(click())
        onView(isRoot()).perform(waitFor(100))
        com.widgetly.iartbook.test.espresso.util.wrappers.ToolBarViewInteraction.onToolBarView()
            .performSelectTool(ToolType.PIPETTE)
        com.widgetly.iartbook.test.espresso.util.wrappers.DrawingSurfaceInteraction.onDrawingSurfaceView()
            .perform(com.widgetly.iartbook.test.espresso.util.UiInteractions.touchAt(com.widgetly.iartbook.test.espresso.util.DrawingSurfaceLocationProvider.MIDDLE))
        com.widgetly.iartbook.test.espresso.util.wrappers.ToolPropertiesInteraction.onToolProperties()
            .checkMatchesColor(Color.TRANSPARENT)
        com.widgetly.iartbook.test.espresso.util.wrappers.LayerMenuViewInteraction.onLayerMenuView()
            .checkLayerCount(1)
    }

    @Test
    fun testResizingThroughAllLayers() {
        com.widgetly.iartbook.test.espresso.util.wrappers.LayerMenuViewInteraction.onLayerMenuView()
            .performOpen()
            .performAddLayer()
            .performAddLayer()
            .performAddLayer()
            .performClose()
        com.widgetly.iartbook.test.espresso.util.wrappers.DrawingSurfaceInteraction.onDrawingSurfaceView()
            .perform(com.widgetly.iartbook.test.espresso.util.UiInteractions.touchAt(com.widgetly.iartbook.test.espresso.util.DrawingSurfaceLocationProvider.MIDDLE))
        com.widgetly.iartbook.test.espresso.util.wrappers.ToolBarViewInteraction.onToolBarView()
            .performSelectTool(ToolType.TRANSFORM)
        com.widgetly.iartbook.test.espresso.util.wrappers.TransformToolOptionsViewInteraction.onTransformToolOptionsView()
            .performAutoCrop()
        com.widgetly.iartbook.test.espresso.util.wrappers.TopBarViewInteraction.onTopBarView()
            .performClickCheckmark()
        com.widgetly.iartbook.test.espresso.util.wrappers.ToolBarViewInteraction.onToolBarView()
            .performOpenToolOptionsView()
        com.widgetly.iartbook.test.espresso.util.wrappers.TransformToolOptionsViewInteraction.onTransformToolOptionsView()
            .checkLayerHeightMatches(26)
            .checkLayerWidthMatches(26)
        com.widgetly.iartbook.test.espresso.util.wrappers.LayerMenuViewInteraction.onLayerMenuView()
            .performOpen()
            .performSelectLayer(1)
            .performClose()
        com.widgetly.iartbook.test.espresso.util.wrappers.TransformToolOptionsViewInteraction.onTransformToolOptionsView()
            .checkLayerHeightMatches(26)
            .checkLayerWidthMatches(26)
        com.widgetly.iartbook.test.espresso.util.wrappers.LayerMenuViewInteraction.onLayerMenuView()
            .performOpen()
            .performSelectLayer(2)
            .performClose()
        com.widgetly.iartbook.test.espresso.util.wrappers.TransformToolOptionsViewInteraction.onTransformToolOptionsView()
            .checkLayerHeightMatches(26)
            .checkLayerWidthMatches(26)
        com.widgetly.iartbook.test.espresso.util.wrappers.LayerMenuViewInteraction.onLayerMenuView()
            .performOpen()
            .performScrollToPositionInLayerNavigation(3)
            .performSelectLayer(3)
            .performClose()
        com.widgetly.iartbook.test.espresso.util.wrappers.TransformToolOptionsViewInteraction.onTransformToolOptionsView()
            .checkLayerHeightMatches(26)
            .checkLayerWidthMatches(26)
    }

    @Test
    fun testRotatingThroughAllLayers() {
        com.widgetly.iartbook.test.espresso.util.wrappers.DrawingSurfaceInteraction.onDrawingSurfaceView()
            .perform(
                com.widgetly.iartbook.test.espresso.util.UiInteractions.swipe(
                    com.widgetly.iartbook.test.espresso.util.DrawingSurfaceLocationProvider.HALFWAY_LEFT_MIDDLE,
                    com.widgetly.iartbook.test.espresso.util.DrawingSurfaceLocationProvider.HALFWAY_RIGHT_MIDDLE
                )
            )
        com.widgetly.iartbook.test.espresso.util.wrappers.LayerMenuViewInteraction.onLayerMenuView()
            .performOpen()
            .performAddLayer()
            .performAddLayer()
            .performAddLayer()
            .performClose()
        com.widgetly.iartbook.test.espresso.util.wrappers.ToolBarViewInteraction.onToolBarView()
            .performSelectTool(ToolType.TRANSFORM)
        com.widgetly.iartbook.test.espresso.util.wrappers.TransformToolOptionsViewInteraction.onTransformToolOptionsView()
            .performRotateClockwise()
        com.widgetly.iartbook.test.espresso.util.wrappers.ToolBarViewInteraction.onToolBarView()
            .performCloseToolOptionsView()
        com.widgetly.iartbook.test.espresso.util.wrappers.ToolBarViewInteraction.onToolBarView()
            .performOpenToolOptionsView()
        com.widgetly.iartbook.test.espresso.util.wrappers.TransformToolOptionsViewInteraction.onTransformToolOptionsView()
            .checkLayerHeightMatches(bitmapWidth)
            .checkLayerWidthMatches(bitmapHeight)
        com.widgetly.iartbook.test.espresso.util.wrappers.LayerMenuViewInteraction.onLayerMenuView()
            .performOpen()
            .performSelectLayer(1)
            .performClose()
        com.widgetly.iartbook.test.espresso.util.wrappers.TransformToolOptionsViewInteraction.onTransformToolOptionsView()
            .checkLayerHeightMatches(bitmapWidth)
            .checkLayerWidthMatches(bitmapHeight)
        com.widgetly.iartbook.test.espresso.util.wrappers.LayerMenuViewInteraction.onLayerMenuView()
            .performOpen()
            .performSelectLayer(2)
            .performClose()
        com.widgetly.iartbook.test.espresso.util.wrappers.TransformToolOptionsViewInteraction.onTransformToolOptionsView()
            .checkLayerHeightMatches(bitmapWidth)
            .checkLayerWidthMatches(bitmapHeight)
        com.widgetly.iartbook.test.espresso.util.wrappers.LayerMenuViewInteraction.onLayerMenuView()
            .performOpen()
            .performScrollToPositionInLayerNavigation(3)
            .performSelectLayer(3)
            .performClose()
        com.widgetly.iartbook.test.espresso.util.wrappers.TransformToolOptionsViewInteraction.onTransformToolOptionsView()
            .checkLayerHeightMatches(bitmapWidth)
            .checkLayerWidthMatches(bitmapHeight)
    }

    @Test
    fun testReflectingOnlyCurrentLayer() {
        com.widgetly.iartbook.test.espresso.util.wrappers.DrawingSurfaceInteraction.onDrawingSurfaceView()
            .perform(com.widgetly.iartbook.test.espresso.util.UiInteractions.touchAt(com.widgetly.iartbook.test.espresso.util.DrawingSurfaceLocationProvider.HALFWAY_LEFT_MIDDLE))
        com.widgetly.iartbook.test.espresso.util.wrappers.LayerMenuViewInteraction.onLayerMenuView()
            .performOpen()
            .performAddLayer()
            .performClose()
        com.widgetly.iartbook.test.espresso.util.wrappers.DrawingSurfaceInteraction.onDrawingSurfaceView()
            .perform(com.widgetly.iartbook.test.espresso.util.UiInteractions.touchAt(com.widgetly.iartbook.test.espresso.util.DrawingSurfaceLocationProvider.HALFWAY_RIGHT_MIDDLE))
        com.widgetly.iartbook.test.espresso.util.wrappers.ToolBarViewInteraction.onToolBarView()
            .performSelectTool(ToolType.TRANSFORM)
        com.widgetly.iartbook.test.espresso.util.wrappers.TransformToolOptionsViewInteraction.onTransformToolOptionsView()
            .performFlipVertical()
        com.widgetly.iartbook.test.espresso.util.wrappers.ToolBarViewInteraction.onToolBarView().performSelectTool(ToolType.PIPETTE)
        com.widgetly.iartbook.test.espresso.util.wrappers.DrawingSurfaceInteraction.onDrawingSurfaceView()
            .perform(com.widgetly.iartbook.test.espresso.util.UiInteractions.touchAt(com.widgetly.iartbook.test.espresso.util.DrawingSurfaceLocationProvider.HALFWAY_RIGHT_MIDDLE))
        com.widgetly.iartbook.test.espresso.util.wrappers.ToolPropertiesInteraction.onToolProperties()
            .checkMatchesColor(Color.TRANSPARENT)
        com.widgetly.iartbook.test.espresso.util.wrappers.TopBarViewInteraction.onTopBarView()
            .performUndo()
        com.widgetly.iartbook.test.espresso.util.wrappers.DrawingSurfaceInteraction.onDrawingSurfaceView()
            .perform(com.widgetly.iartbook.test.espresso.util.UiInteractions.touchAt(com.widgetly.iartbook.test.espresso.util.DrawingSurfaceLocationProvider.HALFWAY_RIGHT_MIDDLE))
        com.widgetly.iartbook.test.espresso.util.wrappers.ToolPropertiesInteraction.onToolProperties()
            .checkMatchesColor(Color.BLACK)
        com.widgetly.iartbook.test.espresso.util.wrappers.TopBarViewInteraction.onTopBarView()
            .performRedo()
        com.widgetly.iartbook.test.espresso.util.wrappers.DrawingSurfaceInteraction.onDrawingSurfaceView()
            .perform(com.widgetly.iartbook.test.espresso.util.UiInteractions.touchAt(com.widgetly.iartbook.test.espresso.util.DrawingSurfaceLocationProvider.HALFWAY_RIGHT_MIDDLE))
        com.widgetly.iartbook.test.espresso.util.wrappers.ToolPropertiesInteraction.onToolProperties()
            .checkMatchesColor(Color.TRANSPARENT)
    }

    @Test
    fun testUndoRedoLayerDelete() {
        com.widgetly.iartbook.test.espresso.util.wrappers.LayerMenuViewInteraction.onLayerMenuView()
            .performOpen()
            .performAddLayer()
            .checkLayerCount(2)
            .performDeleteLayer()
            .checkLayerCount(1)
            .performClose()
        com.widgetly.iartbook.test.espresso.util.wrappers.TopBarViewInteraction.onTopBarView()
            .performUndo()
        com.widgetly.iartbook.test.espresso.util.wrappers.LayerMenuViewInteraction.onLayerMenuView()
            .checkLayerCount(2)
        com.widgetly.iartbook.test.espresso.util.wrappers.TopBarViewInteraction.onTopBarView()
            .performRedo()
        com.widgetly.iartbook.test.espresso.util.wrappers.LayerMenuViewInteraction.onLayerMenuView()
            .checkLayerCount(1)
    }

    @Test
    fun testLayerOpacity() {
        com.widgetly.iartbook.test.espresso.util.wrappers.ToolBarViewInteraction.onToolBarView()
            .performSelectTool(ToolType.FILL)

        com.widgetly.iartbook.test.espresso.util.wrappers.DrawingSurfaceInteraction.onDrawingSurfaceView()
            .perform(com.widgetly.iartbook.test.espresso.util.UiInteractions.touchAt(com.widgetly.iartbook.test.espresso.util.DrawingSurfaceLocationProvider.MIDDLE))

        com.widgetly.iartbook.test.espresso.util.wrappers.DrawingSurfaceInteraction.onDrawingSurfaceView()
            .perform(com.widgetly.iartbook.test.espresso.util.UiInteractions.touchAt(com.widgetly.iartbook.test.espresso.util.DrawingSurfaceLocationProvider.MIDDLE))

        com.widgetly.iartbook.test.espresso.util.wrappers.DrawingSurfaceInteraction.onDrawingSurfaceView()
            .checkPixelColor(Color.BLACK, com.widgetly.iartbook.test.espresso.util.BitmapLocationProvider.MIDDLE)

        com.widgetly.iartbook.test.espresso.util.wrappers.LayerMenuViewInteraction.onLayerMenuView()
            .performOpen()
            .performSetOpacityTo(50, 0)
            .performClose()

        com.widgetly.iartbook.test.espresso.util.wrappers.ToolBarViewInteraction.onToolBarView()
            .performSelectTool(ToolType.PIPETTE)

        val fiftyPercentOpacityBlack = Color.argb(255 / 2, 0, 0, 0)
        com.widgetly.iartbook.test.espresso.util.wrappers.DrawingSurfaceInteraction.onDrawingSurfaceView()
            .checkPixelColor(fiftyPercentOpacityBlack, com.widgetly.iartbook.test.espresso.util.BitmapLocationProvider.MIDDLE)

        com.widgetly.iartbook.test.espresso.util.wrappers.LayerMenuViewInteraction.onLayerMenuView()
            .performOpen()
            .performSetOpacityTo(0, 0)
            .performClose()

        com.widgetly.iartbook.test.espresso.util.wrappers.DrawingSurfaceInteraction.onDrawingSurfaceView()
            .checkPixelColor(Color.TRANSPARENT, com.widgetly.iartbook.test.espresso.util.BitmapLocationProvider.MIDDLE)
    }

    @Test
    fun testOpacityAndVisibilityIconDisabled() {
        com.widgetly.iartbook.test.espresso.util.wrappers.LayerMenuViewInteraction.onLayerMenuView()
            .performOpen()

        onView(withId(R.id.pocketpaint_layer_side_nav_button_visibility))
                .check(matches(Matchers.not(isEnabled())))
        onView(withId(R.id.pocketpaint_layer_side_nav_button_opacity))
                .check(matches(Matchers.not(isEnabled())))
    }

    @Test
    fun testLayerOrderUndoDelete() {
        com.widgetly.iartbook.test.espresso.util.wrappers.ToolBarViewInteraction.onToolBarView()
            .performSelectTool(ToolType.FILL)
        com.widgetly.iartbook.test.espresso.util.wrappers.DrawingSurfaceInteraction.onDrawingSurfaceView()
            .perform(com.widgetly.iartbook.test.espresso.util.UiInteractions.touchAt(com.widgetly.iartbook.test.espresso.util.DrawingSurfaceLocationProvider.MIDDLE))
        com.widgetly.iartbook.test.espresso.util.wrappers.LayerMenuViewInteraction.onLayerMenuView()
            .performOpen()
            .performAddLayer()
            .checkLayerCount(2)
            .performClose()
        com.widgetly.iartbook.test.espresso.util.wrappers.ToolPropertiesInteraction.onToolProperties()
            .setColorResource(R.color.pocketpaint_color_merge_layer)
            .checkMatchesColorResource(R.color.pocketpaint_color_merge_layer)
        com.widgetly.iartbook.test.espresso.util.wrappers.DrawingSurfaceInteraction.onDrawingSurfaceView()
            .perform(com.widgetly.iartbook.test.espresso.util.UiInteractions.touchAt(com.widgetly.iartbook.test.espresso.util.DrawingSurfaceLocationProvider.MIDDLE))
        com.widgetly.iartbook.test.espresso.util.wrappers.LayerMenuViewInteraction.onLayerMenuView()
            .performOpen()
            .performSelectLayer(1)
            .performDeleteLayer()
            .checkLayerCount(1)
            .performClose()
        com.widgetly.iartbook.test.espresso.util.wrappers.TopBarViewInteraction.onTopBarView()
            .performUndo()
        com.widgetly.iartbook.test.espresso.util.wrappers.LayerMenuViewInteraction.onLayerMenuView()
            .performOpen()
            .checkLayerCount(2)
            .performClose()
        com.widgetly.iartbook.test.espresso.util.wrappers.ToolBarViewInteraction.onToolBarView()
            .performSelectTool(ToolType.PIPETTE)
        com.widgetly.iartbook.test.espresso.util.wrappers.DrawingSurfaceInteraction.onDrawingSurfaceView()
            .perform(com.widgetly.iartbook.test.espresso.util.UiInteractions.touchAt(com.widgetly.iartbook.test.espresso.util.DrawingSurfaceLocationProvider.MIDDLE))
        com.widgetly.iartbook.test.espresso.util.wrappers.ToolPropertiesInteraction.onToolProperties()
            .checkMatchesColorResource(R.color.pocketpaint_color_merge_layer)
    }

    @Test
    fun testLayerPreviewKeepsBitmapAfterOrientationChange() {
        com.widgetly.iartbook.test.espresso.util.wrappers.ToolBarViewInteraction.onToolBarView()
            .performSelectTool(ToolType.FILL)
        com.widgetly.iartbook.test.espresso.util.wrappers.DrawingSurfaceInteraction.onDrawingSurfaceView()
            .perform(com.widgetly.iartbook.test.espresso.util.UiInteractions.touchAt(com.widgetly.iartbook.test.espresso.util.DrawingSurfaceLocationProvider.MIDDLE))
        com.widgetly.iartbook.test.espresso.util.wrappers.LayerMenuViewInteraction.onLayerMenuView()
            .performOpen()
            .checkLayerAtPositionHasTopLeftPixelWithColor(0, Color.BLACK)

        activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

        com.widgetly.iartbook.test.espresso.util.wrappers.LayerMenuViewInteraction.onLayerMenuView()
            .checkLayerAtPositionHasTopLeftPixelWithColor(0, Color.BLACK)
    }

    @Test
    fun testUndoRedoLayerRotate() {
        com.widgetly.iartbook.test.espresso.util.wrappers.ToolBarViewInteraction.onToolBarView()
            .performSelectTool(ToolType.TRANSFORM)
        com.widgetly.iartbook.test.espresso.util.wrappers.TransformToolOptionsViewInteraction.onTransformToolOptionsView()
            .performRotateClockwise()
        com.widgetly.iartbook.test.espresso.util.wrappers.ToolBarViewInteraction.onToolBarView()
            .performCloseToolOptionsView()
        com.widgetly.iartbook.test.espresso.util.wrappers.ToolBarViewInteraction.onToolBarView()
            .performOpenToolOptionsView()
        com.widgetly.iartbook.test.espresso.util.wrappers.TransformToolOptionsViewInteraction.onTransformToolOptionsView()
            .checkLayerHeightMatches(bitmapWidth)
            .checkLayerWidthMatches(bitmapHeight)
        com.widgetly.iartbook.test.espresso.util.wrappers.ToolBarViewInteraction.onToolBarView()
            .performCloseToolOptionsView()
        com.widgetly.iartbook.test.espresso.util.wrappers.TopBarViewInteraction.onTopBarView()
            .performUndo()
        com.widgetly.iartbook.test.espresso.util.wrappers.ToolBarViewInteraction.onToolBarView()
            .performOpenToolOptionsView()
        com.widgetly.iartbook.test.espresso.util.wrappers.TransformToolOptionsViewInteraction.onTransformToolOptionsView()
            .checkLayerHeightMatches(bitmapHeight)
            .checkLayerWidthMatches(bitmapWidth)
        com.widgetly.iartbook.test.espresso.util.wrappers.ToolBarViewInteraction.onToolBarView()
            .performCloseToolOptionsView()
        com.widgetly.iartbook.test.espresso.util.wrappers.TopBarViewInteraction.onTopBarView()
            .performRedo()
        com.widgetly.iartbook.test.espresso.util.wrappers.ToolBarViewInteraction.onToolBarView()
            .performOpenToolOptionsView()
        com.widgetly.iartbook.test.espresso.util.wrappers.TransformToolOptionsViewInteraction.onTransformToolOptionsView()
            .checkLayerHeightMatches(bitmapWidth)
            .checkLayerWidthMatches(bitmapHeight)
        com.widgetly.iartbook.test.espresso.util.wrappers.ToolBarViewInteraction.onToolBarView()
            .performCloseToolOptionsView()
    }

    @Test
    fun testHideLayer() {
        com.widgetly.iartbook.test.espresso.util.wrappers.LayerMenuViewInteraction.onLayerMenuView()
            .performOpen()
            .performAddLayer()
            .checkLayerCount(2)
            .performClose()
        com.widgetly.iartbook.test.espresso.util.wrappers.ToolBarViewInteraction.onToolBarView()
            .performSelectTool(ToolType.FILL)
        com.widgetly.iartbook.test.espresso.util.wrappers.DrawingSurfaceInteraction.onDrawingSurfaceView().perform(click())
        com.widgetly.iartbook.test.espresso.util.wrappers.DrawingSurfaceInteraction.onDrawingSurfaceView().checkPixelColor(Color.BLACK, 1f, 1f)
        com.widgetly.iartbook.test.espresso.util.wrappers.LayerMenuViewInteraction.onLayerMenuView()
            .performOpen()
            .performToggleLayerVisibility(0)
            .performClose()
        com.widgetly.iartbook.test.espresso.util.wrappers.DrawingSurfaceInteraction.onDrawingSurfaceView().checkPixelColor(Color.TRANSPARENT, 1f, 1f)
    }

    @Test
    fun testHideThenUnhideLayer() {
        com.widgetly.iartbook.test.espresso.util.wrappers.LayerMenuViewInteraction.onLayerMenuView()
            .performOpen()
            .performAddLayer()
            .checkLayerCount(2)
            .performClose()
        com.widgetly.iartbook.test.espresso.util.wrappers.ToolBarViewInteraction.onToolBarView()
            .performSelectTool(ToolType.FILL)
        com.widgetly.iartbook.test.espresso.util.wrappers.DrawingSurfaceInteraction.onDrawingSurfaceView().perform(click())
        com.widgetly.iartbook.test.espresso.util.wrappers.DrawingSurfaceInteraction.onDrawingSurfaceView().checkPixelColor(Color.BLACK, 1f, 1f)
        com.widgetly.iartbook.test.espresso.util.wrappers.LayerMenuViewInteraction.onLayerMenuView()
            .performOpen()
            .performToggleLayerVisibility(0)
            .performClose()
        com.widgetly.iartbook.test.espresso.util.wrappers.DrawingSurfaceInteraction.onDrawingSurfaceView().checkPixelColor(Color.TRANSPARENT, 1f, 1f)
        com.widgetly.iartbook.test.espresso.util.wrappers.LayerMenuViewInteraction.onLayerMenuView()
            .performOpen()
            .performToggleLayerVisibility(0)
            .performClose()
        com.widgetly.iartbook.test.espresso.util.wrappers.DrawingSurfaceInteraction.onDrawingSurfaceView().checkPixelColor(Color.BLACK, 1f, 1f)
    }

    @Test
    fun testTryMergeOrReorderWhileALayerIsHidden() {
        com.widgetly.iartbook.test.espresso.util.wrappers.LayerMenuViewInteraction.onLayerMenuView()
            .performOpen()
            .performAddLayer()
            .checkLayerCount(2)
            .performToggleLayerVisibility(0)
            .performStartDragging(0)
        onView(withText(R.string.no_longclick_on_hidden_layer))
            .inRoot(
                ToastMatcher().apply {
                    matches(isDisplayed())
                }
            )
    }

    @Test
    fun testTryChangeToolWhileALayerIsHidden() {
        com.widgetly.iartbook.test.espresso.util.wrappers.LayerMenuViewInteraction.onLayerMenuView()
            .performOpen()
            .performAddLayer()
            .checkLayerCount(2)
            .performToggleLayerVisibility(0)
            .performClose()
        com.widgetly.iartbook.test.espresso.util.wrappers.ToolBarViewInteraction.onToolBarView()
            .onToolsClicked()
        onView(
            withText(R.string.no_tools_on_hidden_layer)
        ).inRoot(
            ToastMatcher().apply {
                matches(isDisplayed())
            }
        )
    }

    @Test
    fun testLoadLargeBitmapAndAddMaxLayerMenu() {
        val intent = Intent().apply {
            data = createTestImageFile()
        }
        Intents.init()
        val intentResult = ActivityResult(Activity.RESULT_OK, intent)

        Intents.intending(IntentMatchers.anyIntent()).respondWith(intentResult)
        com.widgetly.iartbook.test.espresso.util.wrappers.TopBarViewInteraction.onTopBarView()
            .performOpenMoreOptions()
        onView(withText(R.string.menu_load_image)).perform(click())
        onView(withText(R.string.menu_replace_image)).perform(click())
        Intents.release()
        onView(withText(R.string.dialog_warning_new_image)).check(ViewAssertions.doesNotExist())
        onView(withText(R.string.pocketpaint_ok)).perform(click())
        com.widgetly.iartbook.test.espresso.util.wrappers.DrawingSurfaceInteraction.onDrawingSurfaceView()
            .checkPixelColor(Color.BLACK, com.widgetly.iartbook.test.espresso.util.BitmapLocationProvider.MIDDLE)
        com.widgetly.iartbook.test.espresso.util.wrappers.LayerMenuViewInteraction.onLayerMenuView()
            .checkLayerCount(1)
            .performOpen()
            .performAddLayer()
            .performAddLayer()
            .performAddLayer()
            .checkLayerCount(FOUR_LAYERS)
    }

    @Test
    fun testAddTenLayers() {
        com.widgetly.iartbook.test.espresso.util.wrappers.LayerMenuViewInteraction.onLayerMenuView()
            .checkLayerCount(1)
            .performOpen()
            .performAddLayer()
            .checkLayerCount(2)
            .performAddLayer()
            .checkLayerCount(3)
            .performAddLayer()
            .checkLayerCount(4)
            .performAddLayer()
            .checkLayerCount(5)
            .performAddLayer()
            .checkLayerCount(6)
            .performAddLayer()
            .checkLayerCount(7)
            .performAddLayer()
            .checkLayerCount(8)
            .performAddLayer()
            .checkLayerCount(9)
            .performAddLayer()
            .checkLayerCount(10)
    }

    @Test
    fun testAddAsManyLayersAsPossibleAndScrollToEndOfList() {
        com.widgetly.iartbook.test.espresso.util.wrappers.LayerMenuViewInteraction.onLayerMenuView()
            .checkLayerCount(1)
            .performOpen()
            .performAddLayer()
            .checkLayerCount(2)
        var layerCount = 3
        while (true) {
            try {
                com.widgetly.iartbook.test.espresso.util.wrappers.LayerMenuViewInteraction.onLayerMenuView()
                    .performAddLayer()
                    .checkLayerCount(layerCount)
                onView(withId(R.id.pocketpaint_layer_side_nav_button_add)).check(matches(isClickable()))
                layerCount++
            } catch (ignore: AssertionFailedError) {
                break
            }
        }
        assertIfLayerAddButtonIsDisabled()
        com.widgetly.iartbook.test.espresso.util.wrappers.LayerMenuViewInteraction.onLayerMenuView()
            .checkLayerCount(--layerCount)
        onView(withId(R.id.pocketpaint_layer_side_nav_list)).perform(scrollToPosition<RecyclerView.ViewHolder>(99))
    }

    private fun createTestImageFile(): Uri {
        val bitmap = Bitmap.createBitmap(7000, 7000, Bitmap.Config.ARGB_8888)
        with(Canvas(bitmap)) {
            drawColor(Color.BLACK)
            drawBitmap(bitmap, 0f, 0f, null)
        }
        val imageFile = File(
            launchActivityRule.activity.getExternalFilesDir(null)!!.absolutePath,
            "loadImage.jpg"
        )
        val imageUri = Uri.fromFile(imageFile)
        launchActivityRule.activity.myContentResolver.openOutputStream(imageUri).use { fos ->
            Assert.assertTrue(bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos))
        }
        deletionFileList.add(imageFile)
        return imageUri
    }
}
