package com.iglesiabfr.iglesiabfrnaranjo

import android.content.Context
import android.util.AttributeSet
import com.google.android.material.bottomnavigation.BottomNavigationView

class CustomBottNavBar(context: Context, attrs: AttributeSet) : BottomNavigationView(context, attrs) {
    override fun getMaxItemCount(): Int {
        return 6
    }
}
