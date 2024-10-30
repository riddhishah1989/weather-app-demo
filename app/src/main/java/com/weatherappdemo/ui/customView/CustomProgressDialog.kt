package com.weatherappdemo.ui.customView

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import com.weatherappdemo.R

class CustomProgressDialog(private val context: Context) {

    private var dialog: AlertDialog? = null

    fun show() {
        if (dialog == null) {
            val builder = AlertDialog.Builder(context)
            val inflater = LayoutInflater.from(context)
            val dialogView: View = inflater.inflate(R.layout.dialog_progress, null)
            builder.setView(dialogView)
            builder.setCancelable(false) // Prevent dialog from being dismissed by back button
            dialog = builder.create()
            dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
        dialog?.show()
    }

    fun dismiss() {
        dialog?.dismiss()
    }
}