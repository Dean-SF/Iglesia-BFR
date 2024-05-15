package com.iglesiabfr.iglesiabfrnaranjo.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.lifecycleScope
import com.iglesiabfr.iglesiabfrnaranjo.R
import com.iglesiabfr.iglesiabfrnaranjo.dataStore
import com.iglesiabfr.iglesiabfrnaranjo.database.AppConnector
import com.iglesiabfr.iglesiabfrnaranjo.database.DatabaseConnector
import com.iglesiabfr.iglesiabfrnaranjo.dialogs.ConfirmDialog
import com.iglesiabfr.iglesiabfrnaranjo.dialogs.LoadingDialog
import com.iglesiabfr.iglesiabfrnaranjo.homepage.Homepage
import com.iglesiabfr.iglesiabfrnaranjo.schema.UserData
import io.realm.kotlin.ext.query
import io.realm.kotlin.mongodb.App
import io.realm.kotlin.mongodb.Credentials
import io.realm.kotlin.mongodb.User
import kotlinx.coroutines.launch
import java.io.File


class LoginActivity : AppCompatActivity() {

    private val app : App = AppConnector.app
    private var user : User? = null
    private lateinit var email: String
    private lateinit var loadingDialog : LoadingDialog
    private lateinit var confirmDialog : ConfirmDialog
    private lateinit var rememberSessionCheckbox: CheckBox
    private var rememberSessionValue: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        loadingDialog = LoadingDialog(this)
        confirmDialog = ConfirmDialog(this)

        val loginBtn: Button = findViewById(R.id.loginBtn)
        loginBtn.setOnClickListener {
            checkInputs()
        }

        val forgotPasswordText: TextView = findViewById(R.id.forgotPassTxt)
        forgotPasswordText.setOnClickListener {
            confirmDialog.confirmation(getString(R.string.cambiarContra))
                .setOnConfirmationListener {
                    callResetPassword()
                }
        }

        // Si esta activado recuerda la sesion, si esta desactivado debe hacer login cada vez
        rememberSessionCheckbox = findViewById(R.id.sessionCheckbox)
        rememberSessionCheckbox.setOnCheckedChangeListener { _, isChecked ->
            rememberSessionValue = isChecked
        }
    }

    // Llamada de ventanas
    private fun callMainMenu(){
        loadingDialog.stopLoading()
        val intent = Intent(this, Homepage::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }
    private fun callResetPassword() {
        loadingDialog.stopLoading()
        val intent = Intent(this, ResetPassword::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }

    // Verificar que la informacion esta completa
    private fun checkInputs() {
        loadingDialog.startLoading()
        val emailInput: EditText = findViewById(R.id.inputEmail)
        val passwordInput: EditText = findViewById(R.id.inputPassword)

        email = emailInput.text.toString().trim()
        val password = passwordInput.text.toString()

        if (email.isEmpty() || password.isEmpty()) {
            loadingDialog.stopLoading()
            Toast.makeText(this, R.string.incompleteDataWarning, Toast.LENGTH_SHORT).show()
            return
        }


        lifecycleScope.launch {
            user = login(password)
            if (user == null) {
                loadingDialog.stopLoading()
                Toast.makeText(
                    this@LoginActivity,
                    R.string.incorrectDataWarning,
                    Toast.LENGTH_SHORT
                ).show()
                return@launch
            }

            DatabaseConnector.setOnFinishedListener {
                if (!it) {
                    loadingDialog.stopLoading()
                    Toast.makeText(
                        this@LoginActivity,
                        R.string.ErrorCargaMsg,
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setOnFinishedListener
                }

                val userQuery = user?.let { DatabaseConnector.db.query<UserData>("email == $0", email).find().firstOrNull() }

                if (userQuery == null) {
                    loadingDialog.stopLoading()
                    Toast.makeText(
                        this@LoginActivity,
                        R.string.incorrectDataWarning,
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setOnFinishedListener
                }
                if (user!!.state == User.State.REMOVED){
                    loadingDialog.stopLoading()
                    Toast.makeText(
                        this@LoginActivity,
                        R.string.userNotValid,
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setOnFinishedListener
                } else {
                    lifecycleScope.launch {
                        setRememberSession(userQuery)
                        DatabaseConnector.email = email
                        DatabaseConnector.setUserData()
                        DatabaseConnector.setIsAdmin()
                        loadingDialog.stopLoading()
                        callMainMenu()
                    }
                }
            }

            DatabaseConnector.connect(lifecycleScope)
        }
    }

    private suspend fun setRememberSession(userQuery: UserData?) {
        lifecycleScope.launch {
            runCatching {
                DatabaseConnector.db.write {
                    if (userQuery != null) {
                        findLatest(userQuery)?.let {
                            it.rememberSession = rememberSessionValue
                        }
                    }
                }
            }.onFailure { error ->
                Log.e("LoginActivity", "Error: ${error.message}")
            }
        }
        // Si se desactivó la opción de recordar sesión, entonces se elimina el archivo de preferencias
        val originalValue = userQuery?.rememberSession
        if (originalValue == true && !rememberSessionValue) {
            applicationContext.deleteDataStoreFile("credentials")
        }
    }

    // Función para eliminar el archivo de DataStore
    private fun Context.deleteDataStoreFile(name: String) {
        val dataStoreFile = File(filesDir, "${name}.preferences_pb")
        if (dataStoreFile.exists()) {
            val deleted = dataStoreFile.delete()
            if (deleted) {
                Log.d("RememberSession", "Las preferencias se eliminaron con éxito.")
            } else {
                Log.d("RememberSession", "Las preferencias no se pudieron eliminar.")
            }
        } else {
            Log.d("RememberSession", "El archivo de preferencias no existe.")
        }
    }

    private suspend fun saveCredentials(passwordInput: String) {
        dataStore.edit { preferences ->
            preferences[stringPreferencesKey("email")] = email
            preferences[stringPreferencesKey("password")] = passwordInput
        }
    }

    private suspend fun login(passwordInput: String): User? {
        return try {
            DatabaseConnector.credentials = Credentials.emailPassword(email, passwordInput)

            // Si va a recordar la sesión, se guardan las credenciales en DataStore
            if (rememberSessionValue) {
                saveCredentials(passwordInput)
            }

            app.login(Credentials.emailPassword(email, passwordInput))

        } catch (e: Exception) {
            null
        }
    }

}