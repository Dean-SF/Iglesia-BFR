package com.iglesiabfr.iglesiabfrnaranjo.homepage

import AdminCounselingHome
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.android.material.navigation.NavigationBarView
import com.google.firebase.Firebase
import com.google.firebase.messaging.messaging
import com.iglesiabfr.iglesiabfrnaranjo.R
import com.iglesiabfr.iglesiabfrnaranjo.admin.notifHandler.NotifHandler
import com.iglesiabfr.iglesiabfrnaranjo.database.DatabaseConnector
import kotlinx.coroutines.launch

class Homepage : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_homepage)
        replaceFragment(Mainpage())

        NotifHandler.updateToken = {token ->
            val user = DatabaseConnector.getUserData()
            lifecycleScope.launch {
                DatabaseConnector.db.write {
                    findLatest(user!!).let {
                        it?.notifToken = token
                    }
                }
            }
        }
        Firebase.messaging.token.addOnSuccessListener {
            it?.let { NotifHandler.updateToken?.invoke(it) }
        }
        if(DatabaseConnector.getIsAdmin()) {
            Firebase.messaging.subscribeToTopic("admin")
        }

        val navBar : NavigationBarView = findViewById(R.id.homepageNavbar)

        navBar.setOnItemSelectedListener { item ->
            when(item.itemId) {
                R.id.item_calendar -> {
                    replaceFragment(Mainpage())
                    true
                }
                R.id.item_grabaciones -> {
                    replaceFragment(VideoPage())
                    true
                }
                R.id.item_admin -> {
                    replaceFragment(Adminpage())
                    true
                }
                R.id.item_consejeria -> {
                    if (DatabaseConnector.getIsAdmin()) {
                        replaceFragment(AdminCounselingHome())
                    } else {
                        replaceFragment(CounselingHome())
                    }
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