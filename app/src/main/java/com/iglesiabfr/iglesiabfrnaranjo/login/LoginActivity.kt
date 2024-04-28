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
import com.iglesiabfr.iglesiabfrnaranjo.homepage.Homepage
import io.realm.kotlin.mongodb.App
import io.realm.kotlin.mongodb.Credentials
import io.realm.kotlin.mongodb.User
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private val app : App = App.create("iglesiabfr-pigqi")
    private var user : User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        DatabaseConnector.connect() // Conectar a bd

        val loginBtn: Button = findViewById(R.id.loginBtn)
        loginBtn.setOnClickListener {
            checkInputs()
        }

        val forgotPasswordText: TextView = findViewById(R.id.forgotPassTxt)
        forgotPasswordText.setOnClickListener {
            checkInputs()
        }
    }

    // Llamada de ventanas
    private fun callMainMenu(){
        val intent = Intent(this, Homepage::class.java)
        startActivity(intent)
    }

    // Verificar que la informacion esta completa
    private fun checkInputs() {
        val emailInput: EditText = findViewById(R.id.inputEmail)
        val passwordInput: EditText = findViewById(R.id.inputPassword)

        val email = emailInput.text.toString().trim()
        val password = passwordInput.text.toString()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, R.string.incompleteDataWarning, Toast.LENGTH_SHORT).show()
            return
        }

        // All inputs are valid, proceed with login from coroutine
        lifecycleScope.launch {
            user = login(email, password)
            if (user == null) {
                Toast.makeText(this@LoginActivity, R.string.incorrectDataWarning, Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this@LoginActivity, DatabaseConnector.getLogCurrent().id, Toast.LENGTH_SHORT).show()
                callMainMenu()
            }
        }
    }

    private suspend fun forgotPassword(email: String) {
        try {

            // Get the EmailPasswordAuth instance
            val emailPasswordAuth = user?.app?.emailPasswordAuth

            // Send reset password email
            emailPasswordAuth?.sendResetPasswordEmail(email)?.await().onSuccess {
                // Éxito al enviar el correo electrónico de restablecimiento de contraseña
                Toast.makeText(this@LoginActivity, R.string.resetPasswordEmailSent, Toast.LENGTH_SHORT).show()
            }?.onFailure { error ->
                // Error al enviar el correo electrónico de restablecimiento de contraseña
                Toast.makeText(this@LoginActivity, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            // Manejar cualquier excepción que pueda ocurrir durante el proceso
            Toast.makeText(this@LoginActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }


    private suspend fun login(emailInput: String, passwordInput: String): User? {
        return try {
            app.login(Credentials.emailPassword(emailInput, passwordInput))
        } catch (e: Exception) {
            null
        }
    }

}