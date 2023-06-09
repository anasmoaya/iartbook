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

package com.widgetly.iartbook.test.espresso.util.wrappers;

import org.catrobat.paintroid.R;

import androidx.test.espresso.ViewInteraction;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

public final class BrushPickerViewInteraction extends CustomViewInteraction {
	private BrushPickerViewInteraction() {
		super(onView(withId(R.id.pocketpaint_layout_tool_options)));
	}

	public static BrushPickerViewInteraction onBrushPickerView() {
		return new BrushPickerViewInteraction();
	}

	public ViewInteraction onStrokeWidthSeekBar() {
		return onView(withId(R.id.pocketpaint_stroke_width_seek_bar));
	}

	public ViewInteraction onStrokeWidthTextView() {
		return onView(withId(R.id.pocketpaint_stroke_width_width_text));
	}

	public ViewInteraction onStrokeCapSquareView() {
		return onView(withId(R.id.pocketpaint_stroke_ibtn_rect));
	}

	public ViewInteraction onStrokeCapRoundView() {
		return onView(withId(R.id.pocketpaint_stroke_ibtn_circle));
	}
}
