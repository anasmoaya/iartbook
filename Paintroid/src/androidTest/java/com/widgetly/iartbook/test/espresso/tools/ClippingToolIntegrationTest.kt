package com.widgetly.iartbook.test.espresso.tools

import android.graphics.Color
import android.graphics.PointF
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.idling.CountingIdlingResource
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import org.catrobat.paintroid.MainActivity
import com.widgetly.iartbook.test.espresso.util.wrappers.ToolPropertiesInteraction.onToolProperties
import org.catrobat.paintroid.test.utils.ScreenshotOnFailRule
import org.catrobat.paintroid.tools.ToolReference
import org.catrobat.paintroid.tools.ToolType
import org.catrobat.paintroid.tools.Workspace
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ClippingToolIntegrationTest {
    @get:Rule
    var launchActivityRule: ActivityTestRule<MainActivity> = ActivityTestRule(
        MainActivity::class.java
    )

    @get:Rule
    var screenshotOnFailRule = ScreenshotOnFailRule()

    private lateinit var idlingResource: CountingIdlingResource
    private lateinit var toolReference: ToolReference
    private lateinit var mainActivity: MainActivity
    private lateinit var workspace: Workspace
    private lateinit var middle: PointF
    private lateinit var middleLeft: PointF
    private lateinit var middleTop: PointF
    private lateinit var middleBot: PointF
    private lateinit var middleRight: PointF

    private lateinit var middlePoint1: PointF
    private lateinit var middlePoint2: PointF
    private lateinit var middlePoint3: PointF
    private lateinit var middlePoint4: PointF

    @Before
    fun setUp() {
        mainActivity = launchActivityRule.activity
        idlingResource = mainActivity.idlingResource
        IdlingRegistry.getInstance().register(idlingResource)
        workspace = mainActivity.workspace
        toolReference = mainActivity.toolReference
        middle = PointF((workspace.width / 2).toFloat(), (workspace.height / 2).toFloat())
        middleLeft = PointF(middle.x - workspace.width / 4, middle.y)
        middleTop = PointF(middle.x, middle.y - workspace.height / 4)
        middleBot = PointF(middle.x, middle.y + workspace.height / 4)
        middleRight = PointF(middle.x + workspace.width / 4, middle.y)

        middlePoint1 = PointF(middle.x - workspace.width / 4, middle.y - workspace.height / 4)
        middlePoint2 = PointF(middle.x + workspace.width / 4, middle.y - workspace.height / 4)
        middlePoint3 = PointF(middle.x + workspace.width / 4, middle.y + workspace.height / 4)
        middlePoint4 = PointF(middle.x - workspace.width / 4, middle.y + workspace.height / 4)

        com.widgetly.iartbook.test.espresso.util.wrappers.ToolBarViewInteraction.onToolBarView()
            .performSelectTool(ToolType.FILL)
    }

    @After
    fun tearDown() {
        IdlingRegistry.getInstance().unregister(idlingResource)
    }

    @Test
    fun testClipOnBlackBitmap() {
        onToolProperties().setColor(Color.BLACK)

        com.widgetly.iartbook.test.espresso.util.wrappers.DrawingSurfaceInteraction.onDrawingSurfaceView()
            .checkPixelColor(Color.TRANSPARENT, com.widgetly.iartbook.test.espresso.util.BitmapLocationProvider.MIDDLE)

        com.widgetly.iartbook.test.espresso.util.wrappers.DrawingSurfaceInteraction.onDrawingSurfaceView()
            .perform(com.widgetly.iartbook.test.espresso.util.UiInteractions.touchAt(com.widgetly.iartbook.test.espresso.util.DrawingSurfaceLocationProvider.MIDDLE))

        com.widgetly.iartbook.test.espresso.util.wrappers.DrawingSurfaceInteraction.onDrawingSurfaceView()
            .checkPixelColor(Color.BLACK, com.widgetly.iartbook.test.espresso.util.BitmapLocationProvider.MIDDLE)

        com.widgetly.iartbook.test.espresso.util.wrappers.ToolBarViewInteraction.onToolBarView()
            .performSelectTool(ToolType.CLIP)

        onToolProperties().setColor(Color.YELLOW)

        mainActivity.runOnUiThread {
            toolReference.tool?.handleDown(middleLeft)
            toolReference.tool?.handleMove(middlePoint1)
            toolReference.tool?.handleMove(middleTop)
            toolReference.tool?.handleMove(middlePoint2)
            toolReference.tool?.handleMove(middleRight)
            toolReference.tool?.handleMove(middlePoint3)
            toolReference.tool?.handleMove(middleBot)
            toolReference.tool?.handleMove(middlePoint4)
            toolReference.tool?.handleUp(middleLeft)
        }

        com.widgetly.iartbook.test.espresso.util.wrappers.TopBarViewInteraction.onTopBarView()
            .performClickCheckmark()

        val inAreaX = middle.x - 10
        val inAreaY = middle.y - 10

        val outOfAreaX = 20
        val outOfAreaY = workspace.height / 2

        val colorInArea = workspace.bitmapOfCurrentLayer?.getPixel(inAreaX.toInt(), inAreaY.toInt())
        val colorOutOfArea = workspace.bitmapOfCurrentLayer?.getPixel(outOfAreaX, outOfAreaY)

        assertEquals(colorInArea, Color.BLACK)
        assertEquals(colorOutOfArea, Color.TRANSPARENT)
    }

    @Test
    fun testClipOnBlackBitmapOnlyAppliedOnCurrentLayer() {
        onToolProperties().setColor(Color.BLACK)

        com.widgetly.iartbook.test.espresso.util.wrappers.DrawingSurfaceInteraction.onDrawingSurfaceView()
            .checkPixelColor(Color.TRANSPARENT, com.widgetly.iartbook.test.espresso.util.BitmapLocationProvider.MIDDLE)

        com.widgetly.iartbook.test.espresso.util.wrappers.DrawingSurfaceInteraction.onDrawingSurfaceView()
            .perform(com.widgetly.iartbook.test.espresso.util.UiInteractions.touchAt(com.widgetly.iartbook.test.espresso.util.DrawingSurfaceLocationProvider.MIDDLE))

        com.widgetly.iartbook.test.espresso.util.wrappers.DrawingSurfaceInteraction.onDrawingSurfaceView()
            .checkPixelColor(Color.BLACK, com.widgetly.iartbook.test.espresso.util.BitmapLocationProvider.MIDDLE)

        onToolProperties().setColor(Color.YELLOW)

        com.widgetly.iartbook.test.espresso.util.wrappers.LayerMenuViewInteraction.onLayerMenuView()
            .performOpen()
            .performAddLayer()
            .performSelectLayer(0)
            .performClose()

        com.widgetly.iartbook.test.espresso.util.wrappers.DrawingSurfaceInteraction.onDrawingSurfaceView()
            .checkPixelColorOnLayer(Color.TRANSPARENT, com.widgetly.iartbook.test.espresso.util.BitmapLocationProvider.MIDDLE)

        com.widgetly.iartbook.test.espresso.util.wrappers.DrawingSurfaceInteraction.onDrawingSurfaceView()
            .perform(com.widgetly.iartbook.test.espresso.util.UiInteractions.touchAt(com.widgetly.iartbook.test.espresso.util.DrawingSurfaceLocationProvider.MIDDLE))

        com.widgetly.iartbook.test.espresso.util.wrappers.DrawingSurfaceInteraction.onDrawingSurfaceView()
            .checkPixelColor(Color.YELLOW, com.widgetly.iartbook.test.espresso.util.BitmapLocationProvider.MIDDLE)

        com.widgetly.iartbook.test.espresso.util.wrappers.ToolBarViewInteraction.onToolBarView()
            .performSelectTool(ToolType.CLIP)

        mainActivity.runOnUiThread {
            toolReference.tool?.handleDown(middleLeft)
            toolReference.tool?.handleMove(middlePoint1)
            toolReference.tool?.handleMove(middleTop)
            toolReference.tool?.handleMove(middlePoint2)
            toolReference.tool?.handleMove(middleRight)
            toolReference.tool?.handleMove(middlePoint3)
            toolReference.tool?.handleMove(middleBot)
            toolReference.tool?.handleMove(middlePoint4)
            toolReference.tool?.handleUp(middleLeft)
        }

        com.widgetly.iartbook.test.espresso.util.wrappers.TopBarViewInteraction.onTopBarView()
            .performClickCheckmark()

        val inAreaX = middle.x - 10
        val inAreaY = middle.y - 10

        val outOfAreaX = 20
        val outOfAreaY = workspace.height / 2

        val colorInAreaCurrentLayer = workspace.bitmapOfCurrentLayer?.getPixel(inAreaX.toInt(), inAreaY.toInt())
        val colorOutOfAreaCurrentLayer = workspace.bitmapOfCurrentLayer?.getPixel(outOfAreaX, outOfAreaY)

        val colorInAreaSecondLayer = workspace.bitmapListOfAllLayers[1]?.getPixel(inAreaX.toInt(), inAreaY.toInt())
        val colorOutOfAreaSecondLayer = workspace.bitmapListOfAllLayers[1]?.getPixel(outOfAreaX, outOfAreaY)

        assertEquals(colorInAreaCurrentLayer, Color.YELLOW)
        assertEquals(colorOutOfAreaCurrentLayer, Color.TRANSPARENT)
        assertEquals(colorInAreaSecondLayer, Color.BLACK)
        assertEquals(colorOutOfAreaSecondLayer, Color.BLACK)
    }

    @Test
    fun testIfPathGetsDrawnWhenUsingClippingTool() {
        com.widgetly.iartbook.test.espresso.util.wrappers.DrawingSurfaceInteraction.onDrawingSurfaceView()
            .checkPixelColor(Color.TRANSPARENT, com.widgetly.iartbook.test.espresso.util.BitmapLocationProvider.MIDDLE)

        com.widgetly.iartbook.test.espresso.util.wrappers.ToolBarViewInteraction.onToolBarView()
            .performSelectTool(ToolType.CLIP)

        onToolProperties().setColor(Color.BLACK)

        onToolProperties().setStrokeWidth(20f)

        mainActivity.runOnUiThread {
            toolReference.tool?.handleDown(middleTop)
            toolReference.tool?.handleMove(middleLeft)
            toolReference.tool?.handleMove(middleRight)
            toolReference.tool?.handleUp(middleTop)
        }

        com.widgetly.iartbook.test.espresso.util.wrappers.LayerMenuViewInteraction.onLayerMenuView()
            .performOpen()
            .performClose()

        val bitmapColor = workspace.bitmapOfCurrentLayer?.getPixel(middleTop.x.toInt(), middleTop.y.toInt())
        assertEquals(bitmapColor, Color.BLACK)
    }

    @Test
    fun testClipBlackBitmapAndThenUndo() {
        onToolProperties().setColor(Color.BLACK)

        com.widgetly.iartbook.test.espresso.util.wrappers.DrawingSurfaceInteraction.onDrawingSurfaceView()
            .checkPixelColor(Color.TRANSPARENT, com.widgetly.iartbook.test.espresso.util.BitmapLocationProvider.MIDDLE)

        com.widgetly.iartbook.test.espresso.util.wrappers.DrawingSurfaceInteraction.onDrawingSurfaceView()
            .perform(com.widgetly.iartbook.test.espresso.util.UiInteractions.touchAt(com.widgetly.iartbook.test.espresso.util.DrawingSurfaceLocationProvider.MIDDLE))

        com.widgetly.iartbook.test.espresso.util.wrappers.DrawingSurfaceInteraction.onDrawingSurfaceView()
            .checkPixelColor(Color.BLACK, com.widgetly.iartbook.test.espresso.util.BitmapLocationProvider.MIDDLE)

        com.widgetly.iartbook.test.espresso.util.wrappers.ToolBarViewInteraction.onToolBarView()
            .performSelectTool(ToolType.CLIP)

        onToolProperties().setColor(Color.YELLOW)

        mainActivity.runOnUiThread {
            toolReference.tool?.handleDown(middleLeft)
            toolReference.tool?.handleMove(middlePoint1)
            toolReference.tool?.handleMove(middleTop)
            toolReference.tool?.handleMove(middlePoint2)
            toolReference.tool?.handleMove(middleRight)
            toolReference.tool?.handleMove(middlePoint3)
            toolReference.tool?.handleMove(middleBot)
            toolReference.tool?.handleMove(middlePoint4)
            toolReference.tool?.handleUp(middleLeft)
        }

        com.widgetly.iartbook.test.espresso.util.wrappers.TopBarViewInteraction.onTopBarView()
            .performClickCheckmark()

        val inAreaX = middle.x - 10
        val inAreaY = middle.y - 10

        val outOfAreaX = 20
        val outOfAreaY = workspace.height / 2

        val colorInArea = workspace.bitmapOfCurrentLayer?.getPixel(inAreaX.toInt(), inAreaY.toInt())
        val colorOutOfArea = workspace.bitmapOfCurrentLayer?.getPixel(outOfAreaX, outOfAreaY)

        assertEquals(colorInArea, Color.BLACK)
        assertEquals(colorOutOfArea, Color.TRANSPARENT)

        com.widgetly.iartbook.test.espresso.util.wrappers.TopBarViewInteraction.onTopBarView()
            .performUndo()

        val colorOutOfAreaAfterUndo = workspace.bitmapOfCurrentLayer?.getPixel(outOfAreaX, outOfAreaY)
        assertEquals(colorOutOfAreaAfterUndo, Color.BLACK)
    }

    @Test
    fun testClipBlackBitmapAndThenUndoAndRedo() {
        onToolProperties().setColor(Color.BLACK)

        com.widgetly.iartbook.test.espresso.util.wrappers.DrawingSurfaceInteraction.onDrawingSurfaceView()
            .checkPixelColor(Color.TRANSPARENT, com.widgetly.iartbook.test.espresso.util.BitmapLocationProvider.MIDDLE)

        com.widgetly.iartbook.test.espresso.util.wrappers.DrawingSurfaceInteraction.onDrawingSurfaceView()
            .perform(com.widgetly.iartbook.test.espresso.util.UiInteractions.touchAt(com.widgetly.iartbook.test.espresso.util.DrawingSurfaceLocationProvider.MIDDLE))

        com.widgetly.iartbook.test.espresso.util.wrappers.DrawingSurfaceInteraction.onDrawingSurfaceView()
            .checkPixelColor(Color.BLACK, com.widgetly.iartbook.test.espresso.util.BitmapLocationProvider.MIDDLE)

        com.widgetly.iartbook.test.espresso.util.wrappers.ToolBarViewInteraction.onToolBarView()
            .performSelectTool(ToolType.CLIP)

        onToolProperties().setColor(Color.YELLOW)

        mainActivity.runOnUiThread {
            toolReference.tool?.handleDown(middleLeft)
            toolReference.tool?.handleMove(middlePoint1)
            toolReference.tool?.handleMove(middleTop)
            toolReference.tool?.handleMove(middlePoint2)
            toolReference.tool?.handleMove(middleRight)
            toolReference.tool?.handleMove(middlePoint3)
            toolReference.tool?.handleMove(middleBot)
            toolReference.tool?.handleMove(middlePoint4)
            toolReference.tool?.handleUp(middleLeft)
        }

        com.widgetly.iartbook.test.espresso.util.wrappers.TopBarViewInteraction.onTopBarView()
            .performClickCheckmark()

        val inAreaX = middle.x - 10
        val inAreaY = middle.y - 10

        val outOfAreaX = 20
        val outOfAreaY = workspace.height / 2

        val colorInArea = workspace.bitmapOfCurrentLayer?.getPixel(inAreaX.toInt(), inAreaY.toInt())
        val colorOutOfArea = workspace.bitmapOfCurrentLayer?.getPixel(outOfAreaX, outOfAreaY)

        assertEquals(colorInArea, Color.BLACK)
        assertEquals(colorOutOfArea, Color.TRANSPARENT)

        com.widgetly.iartbook.test.espresso.util.wrappers.TopBarViewInteraction.onTopBarView()
            .performUndo()

        val colorOutOfAreaAfterUndo = workspace.bitmapOfCurrentLayer?.getPixel(outOfAreaX, outOfAreaY)
        assertEquals(colorOutOfAreaAfterUndo, Color.BLACK)

        com.widgetly.iartbook.test.espresso.util.wrappers.TopBarViewInteraction.onTopBarView()
            .performRedo()

        val colorOutOfAreaAfterRedo = workspace.bitmapOfCurrentLayer?.getPixel(outOfAreaX, outOfAreaY)
        assertEquals(colorOutOfAreaAfterRedo, Color.TRANSPARENT)
    }
}
