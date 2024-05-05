package com.iglesiabfr.iglesiabfrnaranjo

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

class CustomSpinnerAdapter(context: Context, resource: Int, items: List<String>) : ArrayAdapter<String>(context, resource, items) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = super.getView(position, convertView, parent) as TextView
        view.textSize = 20f // Tamaño del texto en sp
        return view
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = super.getDropDownView(position, convertView, parent) as TextView
        view.textSize = 20f // Tamaño del texto en sp para la lista desplegable
        return view
    }
}
