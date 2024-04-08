package com.iglesiabfr.iglesiabfrnaranjo

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.iglesiabfr.iglesiabfrnaranjo.Bible.BibleBooks
import com.iglesiabfr.iglesiabfrnaranjo.database.DatabaseConnector

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        DatabaseConnector.connect()

        //val i =  Intent(this,DailyVers::class.java)
//        val i =  Intent(this,Testingdb::class.java)
        val i =  Intent(this, BibleBooks::class.java)
        startActivity(i)
    }
}