/*
 * Paintroid: An image manipulation application for Android.
 * Copyright (C) 2010-2015 The Catrobat Team
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
package com.widgetly.iartbook.test.junit.tools

import androidx.test.espresso.idling.CountingIdlingResource
import androidx.test.rule.ActivityTestRule
import com.widgetly.iartbookid.MainActivity
import com.widgetly.iartbookid.command.CommandFactory
import com.widgetly.iartbookid.command.CommandManager
import com.widgetly.iartbookid.contract.LayerContracts
import com.widgetly.iartbookid.tools.ContextCallback
import com.widgetly.iartbookid.tools.ToolPaint
import com.widgetly.iartbookid.tools.ToolType
import com.widgetly.iartbookid.tools.Workspace
import com.widgetly.iartbookid.tools.implementation.FillTool
import com.widgetly.iartbookid.tools.options.FillToolOptionsView
import com.widgetly.iartbookid.tools.options.ToolOptionsViewController
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class FillToolTest {
    @Mock
    var fillToolOptions: FillToolOptionsView? = null

    @Mock
    var contextCallback: ContextCallback? = null

    @Mock
    private val fillToolOptionsView: FillToolOptionsView? = null
    @Mock
    var toolOptionsViewController: ToolOptionsViewController? = null

    @Mock
    var workspace: Workspace? = null

    @Mock
    var toolPaint: ToolPaint? = null

    @Mock
    var commandManager: CommandManager? = null

    @Mock
    var commandFactory: CommandFactory? = null

    @Mock
    var layerModel: LayerContracts.Model? = null

    private var toolToTest: FillTool? = null

    private var idlingResource: CountingIdlingResource? = null

    @Rule
    @JvmField
    var launchActivityRule = ActivityTestRule(MainActivity::class.java)
    @Before
    fun setUp() {
        idlingResource = launchActivityRule.activity.idlingResource
        toolToTest = FillTool(
            fillToolOptionsView!!,
            contextCallback!!, toolOptionsViewController!!, toolPaint!!, workspace!!,
            idlingResource!!, commandManager!!, 0
        )
    }
    @Test
    fun testShouldReturnCorrectToolType() {
        val toolType = toolToTest?.toolType
        Assert.assertEquals(ToolType.FILL, toolType)
    }
}
