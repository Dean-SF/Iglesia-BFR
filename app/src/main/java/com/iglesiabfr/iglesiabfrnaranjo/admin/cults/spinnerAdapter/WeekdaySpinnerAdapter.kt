package com.iglesiabfr.iglesiabfrnaranjo.admin.cults.spinnerAdapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.iglesiabfr.iglesiabfrnaranjo.R

class WeekdaySpinnerAdapter(private val context: Context, private val items: Array<String>) : ArrayAdapter<String>(context, 0, items) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return initView(position, convertView, parent)
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return initView(position, convertView, parent)
    }

    private fun initView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView?: LayoutInflater.from(context).inflate(R.layout.dropdown_weekday, parent, false)
        val textView : TextView = view.findViewById(R.id.weekday)
        textView.text = items[position]
        return view
    }
}