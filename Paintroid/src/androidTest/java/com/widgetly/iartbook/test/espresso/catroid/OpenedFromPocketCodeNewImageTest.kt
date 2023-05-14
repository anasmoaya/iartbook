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

package com.widgetly.iartbook.test.espresso.catroid

import org.catrobat.paintroid.test.espresso.util.EspressoUtils.grantPermissionRulesVersionCheck
import org.catrobat.paintroid.FileIO.createNewEmptyPictureFile
import org.junit.runner.RunWith
import androidx.test.espresso.intent.rule.IntentsTestRule
import org.catrobat.paintroid.MainActivity
import org.catrobat.paintroid.test.utils.ScreenshotOnFailRule
import org.junit.Before
import android.content.Intent
import org.catrobat.paintroid.tools.ToolType
import androidx.test.espresso.Espresso
import androidx.test.espresso.matcher.ViewMatchers
import org.catrobat.paintroid.R
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import android.app.Instrumentation.ActivityResult
import android.app.Activity
import androidx.test.espresso.intent.matcher.IntentMatchers
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.net.Uri
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.idling.CountingIdlingResource
import androidx.test.espresso.intent.Intents
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.ClassRule
import org.catrobat.paintroid.common.PAINTROID_PICTURE_NAME
import org.catrobat.paintroid.common.PAINTROID_PICTURE_PATH
import org.hamcrest.Matchers
import org.junit.After
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import java.io.File
import java.io.IOException
import java.lang.AssertionError
import java.lang.NullPointerException
import java.util.ArrayList
import java.util.Objects

@RunWith(AndroidJUnit4::class)
class OpenedFromPocketCodeNewImageTest {
    @JvmField
    @Rule
    var launchActivityRule = IntentsTestRule(MainActivity::class.java, false, false)

    @JvmField
    @Rule
    var screenshotOnFailRule = ScreenshotOnFailRule()
    private var imageFile: File? = null
    private var activity: MainActivity? = null
    private lateinit var deletionFileList: ArrayList<File?>
    private var idlingResource: CountingIdlingResource? = null

    @Before
    fun setUp() {
        val intent = Intent()
        intent.putExtra(PAINTROID_PICTURE_PATH, "")
        intent.putExtra(PAINTROID_PICTURE_NAME, IMAGE_NAME)
        launchActivityRule.launchActivity(intent)
        deletionFileList = ArrayList()
        activity = launchActivityRule.activity
        idlingResource = activity?.idlingResource
        IdlingRegistry.getInstance().register(idlingResource)
        imageFile = getNewImageFile(IMAGE_NAME)
        deletionFileList.add(imageFile)
        com.widgetly.iartbook.test.espresso.util.wrappers.ToolBarViewInteraction.onToolBarView().performSelectTool(ToolType.BRUSH)
    }

    @After
    fun tearDown() {
        IdlingRegistry.getInstance().unregister(idlingResource)
        for (file in deletionFileList) {
            if (file != null && file.exists()) { Assert.assertTrue(file.delete()) }
        }
    }

    @Test
    fun testSave() {
        com.widgetly.iartbook.test.espresso.util.wrappers.DrawingSurfaceInteraction.onDrawingSurfaceView()
            .perform(com.widgetly.iartbook.test.espresso.util.UiInteractions.touchAt(com.widgetly.iartbook.test.espresso.util.DrawingSurfaceLocationProvider.MIDDLE))
        Espresso.pressBackUnconditionally()
        verifyImageFile()
    }

    @Test
    fun testLoadWithoutChange() {
        createImageIntent()
        com.widgetly.iartbook.test.espresso.util.wrappers.TopBarViewInteraction.onTopBarView()
            .performOpenMoreOptions()
        Espresso.onView(ViewMatchers.withText(R.string.menu_load_image))
            .perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withText(R.string.menu_replace_image))
            .perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withText(R.string.dialog_warning_new_image))
            .check(ViewAssertions.doesNotExist())
        com.widgetly.iartbook.test.espresso.util.wrappers.DrawingSurfaceInteraction.onDrawingSurfaceView()
            .checkPixelColor(Color.WHITE, com.widgetly.iartbook.test.espresso.util.BitmapLocationProvider.MIDDLE)
        Espresso.pressBackUnconditionally()
        verifyImageFile()
    }

    @Test
    fun testLoadWithChange() {
        createImageIntent()
        com.widgetly.iartbook.test.espresso.util.wrappers.TopBarViewInteraction.onTopBarView()
            .performOpenMoreOptions()
        Espresso.onView(ViewMatchers.withText(R.string.menu_load_image))
            .perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withText(R.string.menu_replace_image))
            .perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withText(R.string.dialog_warning_new_image))
            .check(ViewAssertions.doesNotExist())
        com.widgetly.iartbook.test.espresso.util.wrappers.DrawingSurfaceInteraction.onDrawingSurfaceView()
            .checkPixelColor(Color.WHITE, com.widgetly.iartbook.test.espresso.util.BitmapLocationProvider.MIDDLE)
        com.widgetly.iartbook.test.espresso.util.wrappers.DrawingSurfaceInteraction.onDrawingSurfaceView()
            .perform(com.widgetly.iartbook.test.espresso.util.UiInteractions.touchAt(com.widgetly.iartbook.test.espresso.util.DrawingSurfaceLocationProvider.MIDDLE))
        com.widgetly.iartbook.test.espresso.util.wrappers.DrawingSurfaceInteraction.onDrawingSurfaceView()
            .checkPixelColor(Color.BLACK, com.widgetly.iartbook.test.espresso.util.BitmapLocationProvider.MIDDLE)
        Espresso.pressBackUnconditionally()
        verifyImageFile()
    }

    private fun getNewImageFile(filename: String): File {
        return try {
            createNewEmptyPictureFile(filename, launchActivityRule.activity)
        } catch (e: NullPointerException) {
            throw AssertionError("Could not create temp file", e)
        }
    }

    private fun createImageIntent() {
        val intent = Intent()
        intent.data = createTestImageFile()
        val resultOK = ActivityResult(Activity.RESULT_OK, intent)
        Intents.intending(IntentMatchers.hasAction(Intent.ACTION_GET_CONTENT)).respondWith(resultOK)
    }

    private fun createTestImageFile(): Uri {
        val bitmap = Bitmap.createBitmap(400, 400, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        canvas.drawColor(Color.WHITE)
        canvas.drawBitmap(bitmap, 0f, 0f, null)

        val imageFile = File(activity?.getExternalFilesDir(null)!!.absolutePath, "$IMAGE_TO_LOAD_NAME.jpg"
        )
        val imageUri = Uri.fromFile(imageFile)
        try {
            val fos = activity?.contentResolver?.openOutputStream(Objects.requireNonNull(imageUri))
            Assert.assertTrue(bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos))
            assert(fos != null)
            fos?.close()
        } catch (e: IOException) {
            throw AssertionError("Picture file could not be created.", e)
        }
        deletionFileList.add(imageFile)
        return imageUri
    }

    private fun verifyImageFile() {
        val path =
            launchActivityRule.activityResult.resultData.getStringExtra(PAINTROID_PICTURE_PATH)
        Assert.assertEquals(imageFile?.absolutePath, path)
        imageFile?.exists()?.let { Assert.assertTrue(it) }
        Assert.assertThat(imageFile?.length(), Matchers.greaterThan(0L))
    }

    companion object {
        private const val IMAGE_NAME = "testFile"
        private const val IMAGE_TO_LOAD_NAME = "loadFile"

        @JvmField
        @ClassRule
        var grantPermissionRule = grantPermissionRulesVersionCheck()
    }
}
