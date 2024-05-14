package com.iglesiabfr.iglesiabfrnaranjo

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.iglesiabfr.iglesiabfrnaranjo.admin.notifHandler.NotifHandler
import com.iglesiabfr.iglesiabfrnaranjo.database.DatabaseConnector
import com.iglesiabfr.iglesiabfrnaranjo.login.StartingPage

class InitializerActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        NotifHandler.initNotifs(this)
        DatabaseConnector.setOnFinishedListener {
            if (it) {
                val i =  Intent(this, StartingPage::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(i)
                finish()
            }
            else{
                val intent = Intent(this, InitializerActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                finish()
                startActivity(intent)
                Toast.makeText(this,getString(R.string.ErrorCargaMsgRtr),Toast.LENGTH_SHORT).show()
            }
        }
        DatabaseConnector.connect(lifecycleScope)
    }
}