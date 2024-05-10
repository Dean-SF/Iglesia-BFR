package com.iglesiabfr.iglesiabfrnaranjo.login

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.iglesiabfr.iglesiabfrnaranjo.R
import com.iglesiabfr.iglesiabfrnaranjo.database.DatabaseConnector
import io.realm.kotlin.mongodb.App
import io.realm.kotlin.mongodb.User

class ResetPassword : AppCompatActivity() {

    private lateinit var email: String
    private lateinit var password1: EditText
    private lateinit var password2: EditText
    private lateinit var emailInput: EditText
    private val app: App = App.create("iglesiabfr-pigqi")
    private var user: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password)

        user = app.currentUser
        password1 = findViewById(R.id.inputNewPassword)
        password2 = findViewById(R.id.inputNewPasswordConfirm)
        emailInput = findViewById(R.id.inputEmail)

        val resetPasswordButton: Button = findViewById(R.id.resetBtn)
        resetPasswordButton.setOnClickListener {
            checkInputs()
        }
    }

    private fun checkInputs() {
        val password1Text = password1.text.toString()
        val password2Text = password2.text.toString()
        email = emailInput.text.toString()

        // Verify that all fields were entered
        if (password1Text.isEmpty() || password2Text.isEmpty() || email.isEmpty()) {
            Toast.makeText(this, "Los datos están incompletos", Toast.LENGTH_LONG).show()
        } else {
            // Verify that both passwords match
            if (password1Text != password2Text) {
                Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_LONG).show()
            } else {
                // TODO: Call function to reset password
                Toast.makeText(this, "LLAMAR FUNCOIN PARA RESETEAR (TODO)", Toast.LENGTH_LONG).show()
            }
        }
    }
}