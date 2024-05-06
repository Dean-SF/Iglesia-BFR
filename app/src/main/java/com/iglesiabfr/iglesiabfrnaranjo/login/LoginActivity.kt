package com.iglesiabfr.iglesiabfrnaranjo.login

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.iglesiabfr.iglesiabfrnaranjo.R
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

class LoginActivity : AppCompatActivity() {

    private val app : App = App.create("iglesiabfr-pigqi")
    private var user : User? = null
    private lateinit var email: String
    private lateinit var loadingDialog : LoadingDialog
    private lateinit var confirmDialog : ConfirmDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        loadingDialog = LoadingDialog(this)
        confirmDialog = ConfirmDialog(this)
        DatabaseConnector.connect()

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
            Toast.makeText(this, R.string.incompleteDataWarning, Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            user = login(password)
            val userQuery = user?.let { DatabaseConnector.db.query<UserData>("email == $0", email).find().firstOrNull() }

            if (user == null || userQuery == null) {
                loadingDialog.stopLoading()
                Toast.makeText(
                    this@LoginActivity,
                    R.string.incorrectDataWarning,
                    Toast.LENGTH_SHORT
                ).show()
                return@launch
            }
            if (user!!.state == User.State.REMOVED){
                loadingDialog.stopLoading()
                Toast.makeText(
                    this@LoginActivity,
                    R.string.userNotValid,
                    Toast.LENGTH_SHORT
                ).show()
                return@launch
            } else {
                DatabaseConnector.email = email
                loadingDialog.stopLoading()
                callMainMenu()
            }
        }
    }

    private suspend fun login(passwordInput: String): User? {
        return try {
            DatabaseConnector.credentials = Credentials.emailPassword(email, passwordInput)
            app.login(Credentials.emailPassword(email, passwordInput))
        } catch (e: Exception) {
            null
        }
    }

}