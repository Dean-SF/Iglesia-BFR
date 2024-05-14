package com.iglesiabfr.iglesiabfrnaranjo.homepage

import AdminCounselingHome
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.navigation.NavigationBarView
import com.iglesiabfr.iglesiabfrnaranjo.Bible.BibleBooksFragment
import com.iglesiabfr.iglesiabfrnaranjo.R
import com.iglesiabfr.iglesiabfrnaranjo.database.DatabaseConnector
import com.iglesiabfr.iglesiabfrnaranjo.SharedViewModel
import com.iglesiabfr.iglesiabfrnaranjo.forums.ForumsFragment

class Homepage : AppCompatActivity() {
    private val sharedViewModel: SharedViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val email = intent.getStringExtra("email")
        if (email != null) {
            sharedViewModel.setEmail(email)
        }
        setContentView(R.layout.activity_homepage)
        replaceFragment(Mainpage())

        val navBar : NavigationBarView = findViewById(R.id.homepageNavbar)

        navBar.setOnItemSelectedListener { item ->
            when(item.itemId) {
                R.id.item_calendar -> {
                    replaceFragment(Mainpage())
                    true
                }
                R.id.item_admin -> {
                    replaceFragment(Adminpage())
                    true
                }
                R.id.item_consejeria -> {
                    if (DatabaseConnector.getIsAdmin()) {
                        replaceFragment(AdminCounselingHome())
                    }
                true
                }
                R.id.item_biblia -> {
                    replaceFragment(BibleBooksFragment())
                    true
                }
                R.id.item_foro -> {
                    replaceFragment(ForumsFragment())
                    true
                }

                else -> false
            }
        }
    }

    private fun replaceFragment(fragment:Fragment) {
        val fragTrans = supportFragmentManager.beginTransaction()
        fragTrans.replace(R.id.framelayout,fragment)
        fragTrans.commit()
    }
}