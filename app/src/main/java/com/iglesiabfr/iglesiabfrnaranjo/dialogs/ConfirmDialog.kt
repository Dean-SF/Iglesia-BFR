package com.iglesiabfr.iglesiabfrnaranjo.dialogs

import android.content.Context
import android.view.LayoutInflater
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.iglesiabfr.iglesiabfrnaranjo.R

class ConfirmDialog(context: Context) {
    private val dialog : AlertDialog
    private val currentContext: Context

    private var onDenialListener: (() -> Unit)? = null
    private var onConfirmationListener: (() -> Unit)? = null

    private val question : TextView

    init {
        currentContext = context
        val builder = AlertDialog.Builder(currentContext)
        val inflater = LayoutInflater.from(currentContext)
        val view = inflater.inflate(R.layout.confirmation_dialog, null)

        question  = view.findViewById(R.id.confir_question)
        val confBut : Button = view.findViewById(R.id.confirmButt)
        val denyBut : Button = view.findViewById(R.id.denyButt)

        builder.setView(view)

        dialog = builder.create()
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        confBut.setOnClickListener {
            onConfirmationListener?.invoke()
            dialog.dismiss()
        }

        denyBut.setOnClickListener {
            onDenialListener?.invoke()
            dialog.dismiss()
        }
    }

    fun setOnDenialListener(listener : () -> Unit) : ConfirmDialog {
        onDenialListener = listener
        return this
    }

    fun setOnConfirmationListener(listener : () -> Unit) : ConfirmDialog {
        onConfirmationListener = listener
        return this
    }


    fun confirmation(message:String) : ConfirmDialog {
        question.text = message
        dialog.show()
        return this
    }
}