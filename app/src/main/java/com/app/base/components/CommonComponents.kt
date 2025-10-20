package com.app.base.components

import android.app.AlertDialog
import android.content.Context
import android.view.ContextThemeWrapper
import android.widget.Toast
import com.app.base.R

object CommonComponents {
    fun confirmDialog(context: Context, title: String, message: String, onConfirm: () -> Unit) {
        AlertDialog.Builder(ContextThemeWrapper(context, R.style.CustomAlertDialog)).apply {
            setTitle(title)
            setMessage(message)
            setPositiveButton(R.string.confirm) { dialog, _ ->
                onConfirm()
                dialog.dismiss()
            }
            setNegativeButton(R.string.cancel) { dialog, _ ->
                dialog.dismiss()
            }
        }.show()
    }

    fun toastText(context: Context, message: String) {
        Toast.makeText(
            context, message, Toast.LENGTH_SHORT
        ).show()
    }

    fun showSingleChoiceDialog(
        context: Context,
        title: String,
        options: Array<String>,
        selectedIndex: Int,
        onSelected: (index: Int) -> Unit,
        cancel: String
    ) {
        AlertDialog.Builder(context)
            .setTitle(title)
            .setSingleChoiceItems(options, selectedIndex) { dialog, which ->
                onSelected(which)
                dialog.dismiss()
            }
            .setNegativeButton(cancel, null)
            .show()
    }

}