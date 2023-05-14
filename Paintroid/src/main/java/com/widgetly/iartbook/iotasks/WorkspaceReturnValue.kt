package com.widgetly.iartbook.iotasks

import com.widgetly.iartbookid.colorpicker.ColorHistory
import com.widgetly.iartbookid.model.CommandManagerModel

data class WorkspaceReturnValue(
    val commandManagerModel: CommandManagerModel?,
    val colorHistory: ColorHistory?
)
