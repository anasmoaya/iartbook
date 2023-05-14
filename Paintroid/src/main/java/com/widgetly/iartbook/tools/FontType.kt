package com.widgetly.iartbook.tools

import androidx.annotation.StringRes
import com.widgetly.iartbookid.R

enum class FontType(@StringRes val nameResource: Int) {
    SANS_SERIF(R.string.text_tool_dialog_font_sans_serif),
    SERIF(R.string.text_tool_dialog_font_serif),
    MONOSPACE(R.string.text_tool_dialog_font_monospace),
    STC(R.string.text_tool_dialog_font_arabic_stc),
    DUBAI(R.string.text_tool_dialog_font_dubai);
}
