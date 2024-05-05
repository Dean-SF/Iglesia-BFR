package com.iglesiabfr.iglesiabfrnaranjo

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.iglesiabfr.iglesiabfrnaranjo.database.DatabaseConnector
import com.iglesiabfr.iglesiabfrnaranjo.dialogs.LoadingDialog
import com.iglesiabfr.iglesiabfrnaranjo.homepage1.Homepage

class InitializerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val loadingDialog = LoadingDialog(this)
        loadingDialog.startLoading()
        DatabaseConnector.setOnFinishedListener {
            if (it) {
                loadingDialog.stopLoading()
                val i =  Intent(this, Homepage::class.java)//StartingPage::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(i)
            }
            else{
                loadingDialog.stopLoading()
                val intent = Intent(this, InitializerActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                finish()
                startActivity(intent)
                Toast.makeText(this,getString(R.string.ErrorCargaMsg),Toast.LENGTH_SHORT).show()
            }
        }
        DatabaseConnector.connect()

    }
}