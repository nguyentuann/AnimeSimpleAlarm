package com.app.base.components

import android.app.AlertDialog
import android.content.Context
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.RatingBar
import android.widget.Toast
import com.app.base.R
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder

object CommonComponents {
    fun confirmDialog(context: Context, title: String, message: String, onConfirm: () -> Unit) {
        MaterialAlertDialogBuilder(context, R.style.CustomAlertDialog).apply {
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
        MaterialAlertDialogBuilder(context, R.style.CustomAlertDialog)
            .setTitle(title)
            .setSingleChoiceItems(options, selectedIndex) { dialog, which ->
                onSelected(which)
                dialog.dismiss()
            }
            .setNegativeButton(cancel, null)
            .show()
    }

    fun showRatingDialog(context: Context, onSubmit: (rating: Int, feedback: String) -> Unit) {
        val dialog = BottomSheetDialog(context, R.style.CustomBottomSheetDialog)
        val view = LayoutInflater.from(context).inflate(R.layout.feed_back_dialog, null)
        dialog.setContentView(view)

        val ratingBar = view.findViewById<RatingBar>(R.id.ratingBar)
        val etFeedback = view.findViewById<EditText>(R.id.feedback)
        val btnSubmit = view.findViewById<Button>(R.id.btnSend)

        btnSubmit.setOnClickListener {
            val rating = ratingBar.rating.toInt()
            val feedback = etFeedback.text.toString().trim()

            if (rating == 0) {
                Toast.makeText(context, R.string.select_star, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            onSubmit(rating, feedback)
            dialog.dismiss()
        }

        dialog.show()
    }
}