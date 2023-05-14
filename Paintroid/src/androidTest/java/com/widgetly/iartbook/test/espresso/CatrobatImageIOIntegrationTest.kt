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
@file:Suppress("DEPRECATION")

package com.widgetly.iartbook.test.espresso

import android.net.Uri
import android.os.Environment
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.RootMatchers
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import androidx.test.rule.GrantPermissionRule
import org.catrobat.paintroid.MainActivity
import org.catrobat.paintroid.command.serialization.CommandSerializer
import org.catrobat.paintroid.R
import org.catrobat.paintroid.common.CATROBAT_IMAGE_ENDING
import org.catrobat.paintroid.test.espresso.util.EspressoUtils
import com.widgetly.iartbook.test.espresso.util.wrappers.DrawingSurfaceInteraction.onDrawingSurfaceView
import org.catrobat.paintroid.test.utils.ScreenshotOnFailRule
import org.hamcrest.Matchers
import org.hamcrest.core.AllOf
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File

@RunWith(AndroidJUnit4::class)
class CatrobatImageIOIntegrationTest {

    @get:Rule
    val launchActivityRule = ActivityTestRule(MainActivity::class.java)

    @get:Rule
    val screenshotOnFailRule = ScreenshotOnFailRule()

    @get:Rule
    val grantPermissionRule: GrantPermissionRule = EspressoUtils.grantPermissionRulesVersionCheck()

    private var uriFile: Uri? = null
    private lateinit var activity: MainActivity

    companion object {
        private const val IMAGE_NAME = "fileName"
    }

    @Before
    fun setUp() { activity = launchActivityRule.activity }

    @After
    fun tearDown() {
        val imagesDirectory =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString()
        val pathToFile = imagesDirectory + File.separator + com.widgetly.iartbook.test.espresso.CatrobatImageIOIntegrationTest.Companion.IMAGE_NAME + "." + CATROBAT_IMAGE_ENDING
        val imageFile = File(pathToFile)
        if (imageFile.exists()) {
            imageFile.delete()
        }
    }

    @Test
    fun testWriteAndReadCatrobatImage() {
        onDrawingSurfaceView()
            .perform(com.widgetly.iartbook.test.espresso.util.UiInteractions.touchAt(com.widgetly.iartbook.test.espresso.util.DrawingSurfaceLocationProvider.MIDDLE))
        com.widgetly.iartbook.test.espresso.util.wrappers.TopBarViewInteraction.onTopBarView()
            .performOpenMoreOptions()
        onView(withText(R.string.menu_save_image))
            .perform(ViewActions.click())
        onView(withId(R.id.pocketpaint_save_dialog_spinner))
            .perform(ViewActions.click())
        Espresso.onData(
            AllOf.allOf(
                Matchers.`is`(Matchers.instanceOf<Any>(String::class.java)),
                Matchers.`is`<String>(_root_ide_package_.com.widgetly.iartbook.FileIO.FileType.CATROBAT.value)
            )
        ).inRoot(RootMatchers.isPlatformPopup()).perform(ViewActions.click())
        onView(withId(R.id.pocketpaint_image_name_save_text))
            .perform(replaceText(com.widgetly.iartbook.test.espresso.CatrobatImageIOIntegrationTest.Companion.IMAGE_NAME))
        onView(withText(R.string.save_button_text)).check(matches(isDisplayed()))
            .perform(ViewActions.click())
        uriFile = activity.model.savedPictureUri!!
        Assert.assertNotNull(uriFile)
        Assert.assertNotNull(CommandSerializer(activity, activity.commandManager, activity.model).readFromFile(uriFile!!))
    }
}
