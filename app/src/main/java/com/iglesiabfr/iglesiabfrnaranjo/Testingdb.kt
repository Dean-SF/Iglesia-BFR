package com.iglesiabfr.iglesiabfrnaranjo

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.iglesiabfr.iglesiabfrnaranjo.database.DatabaseConnector
import com.iglesiabfr.iglesiabfrnaranjo.schema.Activity
import io.realm.kotlin.Realm
import io.realm.kotlin.ext.query
import io.realm.kotlin.mongodb.App
import io.realm.kotlin.mongodb.Credentials
import io.realm.kotlin.mongodb.User
import io.realm.kotlin.mongodb.exceptions.SyncException
import io.realm.kotlin.mongodb.subscriptions
import io.realm.kotlin.mongodb.sync.SyncConfiguration
import io.realm.kotlin.mongodb.sync.SyncSession
import io.realm.kotlin.types.RealmInstant
import kotlinx.coroutines.launch

class Testingdb : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_testingdb)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val button : Button = findViewById(R.id.elboton)

        button.setOnClickListener {
            buttonAction(button)
        }

        lifecycleScope.launch {
            runCatching {
                login()
            }.onSuccess {
                Log.d("Info","Pase1")
                val config = SyncConfiguration.Builder(it, setOf(Activity::class,Activity::class))
                    .initialSubscriptions(rerunOnOpen = true) {realm->
                        add(realm.query<Activity>(), updateExisting = true)
                        add(realm.query<Activity>(), updateExisting = true)
                    }
                    .errorHandler { session: SyncSession, error: SyncException ->
                        Log.d("Debuggeador",error.message.toString())
                    }
                    .waitForInitialRemoteData()
                    .build()
                realm = Realm.open(config)
                realm.subscriptions.waitForSynchronization()
                button.isEnabled = true
                Log.d("Info","Pase2")
            }.onFailure {
                Log.d("Err",it.message.toString())
            }
        }

    }

    private fun buttonAction(view : View) {
        val evento = Activity().apply {
            name = "activitasa"
            date = RealmInstant.now()
            desc = "el mero evento mi pana"
        }
        DatabaseConnector.db.writeBlocking {
            copyToRealm(evento)
        }
    }
}