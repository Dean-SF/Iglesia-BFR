package com.iglesiabfr.iglesiabfrnaranjo.dialogs

import android.content.Context
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import com.iglesiabfr.iglesiabfrnaranjo.R

class LoadingDialog(context: Context) {
    private val dialog : AlertDialog
    private val currentContext: Context

    init {
        currentContext = context
        val builder = AlertDialog.Builder(currentContext)
        val inflater = LayoutInflater.from(currentContext)

        builder.setView(inflater.inflate(R.layout.loading_dialog, null))
        builder.setCancelable(false)


        dialog = builder.create()
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
    }

    fun startLoading() {
        dialog.show()
    }

    fun stopLoading() {
        dialog.dismiss()
    }
}