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
package com.widgetly.iartbook

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.widgetly.iartbookid.command.CommandManager
import com.widgetly.iartbookid.contract.LayerContracts
import com.widgetly.iartbookid.tools.ToolPaint
import com.widgetly.iartbookid.tools.ToolReference
import com.widgetly.iartbookid.ui.Perspective

class PaintroidApplicationFragment : Fragment() {
    var commandManager: CommandManager? = null
    var currentTool: ToolReference? = null
    var perspective: Perspective? = null
    var layerModel: LayerContracts.Model? = null
    var toolPaint: ToolPaint? = null
    override fun onCreate(savedInstance: Bundle?) {
        super.onCreate(savedInstance)
        retainInstance = true
    }
}
