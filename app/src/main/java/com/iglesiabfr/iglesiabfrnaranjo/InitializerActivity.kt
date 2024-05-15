package com.iglesiabfr.iglesiabfrnaranjo

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.lifecycleScope
import com.iglesiabfr.iglesiabfrnaranjo.admin.notifHandler.NotifHandler
import com.iglesiabfr.iglesiabfrnaranjo.database.AppConnector
import com.iglesiabfr.iglesiabfrnaranjo.database.DatabaseConnector
import com.iglesiabfr.iglesiabfrnaranjo.homepage.Homepage
import com.iglesiabfr.iglesiabfrnaranjo.login.StartingPage
import io.realm.kotlin.mongodb.Credentials
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import kotlin.properties.Delegates

val Context.dataStore by preferencesDataStore(name = "credentials")

class InitializerActivity : AppCompatActivity() {

    private var hasCredentialsDataStore by Delegates.notNull<Boolean>()
    private lateinit var emailFromPreferences: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        hasCredentialsDataStore = hasCredentialsDataStore()

        NotifHandler.initNotifs(this)

        val context = this
        if (hasCredentialsDataStore) {
            Log.d("aa","aaa")
            lifecycleScope.launch(Dispatchers.IO) {
                rememberSession().collect{
                    withContext(Dispatchers.Main) {
                        AppConnector.app.login(Credentials.emailPassword(it.email, it.password))
                        emailFromPreferences = it.email
                    }
                }

                DatabaseConnector.setOnFinishedListener {
                    DatabaseConnector.email = emailFromPreferences
                    DatabaseConnector.setUserData()
                    DatabaseConnector.setIsAdmin()
                    Log.d("TESTINGGGGGG", "USER DATA NAME: ${DatabaseConnector.getUserData()?.name}")
                    val homeIntent = Intent(context, Homepage::class.java)
                    homeIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(homeIntent)
                    finish()
                }
                DatabaseConnector.connect(lifecycleScope)

            }

        } else {
            val i =  Intent(this, StartingPage::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(i)
            finish()
        }
    }

    @SuppressLint("SdCardPath")
    private fun hasCredentialsDataStore(): Boolean {
        val directoryPath = "/data/data/com.iglesiabfr.iglesiabfrnaranjo/files/datastore/"
        val directory = File(directoryPath)
        if (directory.exists() && directory.isDirectory) {
            val fileList = directory.listFiles()
            if (fileList != null) {
                for (file in fileList) {
                    if (file.name == "credentials.preferences_pb") {
                        return true
                    }
                }
            }
        }
        return false
    }


    // Realiza el login automaticamente si el usuario tiene determinado que se recuerde la sesión
    private fun rememberSession() = dataStore.data.map { preferences ->
        UserSession (
            email = preferences[stringPreferencesKey("email")].orEmpty(),
            password = preferences[stringPreferencesKey("password")].orEmpty()
        )
    }

}