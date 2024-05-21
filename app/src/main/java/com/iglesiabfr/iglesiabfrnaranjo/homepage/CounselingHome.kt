package com.iglesiabfr.iglesiabfrnaranjo.homepage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.navigation.NavigationBarView
import com.iglesiabfr.iglesiabfrnaranjo.R

class CounselingHome: Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_normal_counseling, container, false)
        replaceFragment(RequestCounseling())

        val navBar : NavigationBarView = view.findViewById(R.id.bottomNav)

        navBar.setOnItemSelectedListener { item ->
            when(item.itemId) {
                R.id.item_schedule -> {
                    replaceFragment(RequestCounseling())
                    true
                }
                R.id.item_record -> {
                    replaceFragment(RecordCounseling())
                    true
                }

                else -> false
            }
        }
        return view
    }

    private fun replaceFragment(fragment: Fragment) {
        val fragTrans = childFragmentManager.beginTransaction()
        fragTrans.replace(R.id.cousenlingFrame,fragment)
        fragTrans.commit()
    }
}