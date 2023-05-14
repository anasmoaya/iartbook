package com.widgetly.iartbook.dialog

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SwitchCompat
import com.widgetly.iartbookid.R
import com.widgetly.iartbookid.tools.helper.AdvancedSettingsAlgorithms.smoothing
import com.widgetly.iartbookid.tools.implementation.DefaultToolPaint.Companion.antialiasing

class AdvancedSettingsDialog : MainActivityDialogFragment() {
    private var initValueAntialiasing: Boolean = antialiasing
    private var initValueSmoothing: Boolean = smoothing

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val antialiasingSwitch = view.findViewById<SwitchCompat>(R.id.pocketpaint_antialiasing)
        val smoothSwitch = view.findViewById<SwitchCompat>(R.id.pocketpaint_smoothing)

        antialiasingSwitch.isChecked = antialiasing
        smoothSwitch.isChecked = smoothing

        antialiasingSwitch.setOnCheckedChangeListener { _, isChecked ->
            antialiasing = isChecked
        }

        smoothSwitch?.setOnCheckedChangeListener { _, isChecked ->
            smoothing = isChecked
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val inflater = requireActivity().layoutInflater
        val layout = inflater.inflate(R.layout.dialog_pocketpaint_advanced_settings, null)
        onViewCreated(layout, savedInstanceState)

        return AlertDialog.Builder(requireContext(), R.style.PocketPaintAlertDialog)
            .setTitle(R.string.menu_advanced)
            .setView(layout)
            .setPositiveButton(R.string.pocketpaint_ok) { _, _ ->
                presenter.setAntialiasingOnOkClicked()
                dismiss()
            }
            .setNegativeButton(R.string.cancel_button_text) { _, _ ->
                antialiasing = initValueAntialiasing
                smoothing = initValueSmoothing
                dismiss()
            }
            .create()
    }

    override fun onCancel(dialog: DialogInterface) {
        antialiasing = initValueAntialiasing
        smoothing = initValueSmoothing
        super.onCancel(dialog)
    }
}
