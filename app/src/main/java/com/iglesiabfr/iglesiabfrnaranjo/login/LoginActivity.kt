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
import com.iglesiabfr.iglesiabfrnaranjo.homepage1.Homepage
import io.realm.kotlin.mongodb.App
import io.realm.kotlin.mongodb.Credentials
import io.realm.kotlin.mongodb.User
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private val app : App = App.create("iglesiabfr-pigqi")
    private var user : User? = null
    private lateinit var email: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val loginBtn: Button = findViewById(R.id.loginBtn)
        loginBtn.setOnClickListener {
            checkInputs()
        }

        val forgotPasswordText: TextView = findViewById(R.id.forgotPassTxt)
        forgotPasswordText.setOnClickListener {
            callResetPassword()
        }
    }

    // Llamada de ventanas
    private fun callMainMenu(){
        val intent = Intent(this, Homepage::class.java)
        startActivity(intent)
    }
    private fun callResetPassword() {
        val intent = Intent(this, ResetPassword::class.java)
        startActivity(intent)
    }

    // Verificar que la informacion esta completa
    private fun checkInputs() {
        val emailInput: EditText = findViewById(R.id.inputEmail)
        val passwordInput: EditText = findViewById(R.id.inputPassword)

        email = emailInput.text.toString().trim()
        val password = passwordInput.text.toString()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, R.string.incompleteDataWarning, Toast.LENGTH_SHORT).show()
            return
        }

        // All inputs are valid, proceed with login from coroutine
        lifecycleScope.launch {
            user = login(password)
            if (user == null) {
                Toast.makeText(
                    this@LoginActivity,
                    R.string.incorrectDataWarning,
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                DatabaseConnector.email = email
                callMainMenu()
            }
        }
    }

    private suspend fun login(passwordInput: String): User? {
        return try {
            app.login(Credentials.emailPassword(email, passwordInput))
        } catch (e: Exception) {
            null
        }
    }

}