package com.iglesiabfr.iglesiabfrnaranjo.login

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.iglesiabfr.iglesiabfrnaranjo.R
import com.iglesiabfr.iglesiabfrnaranjo.database.AppConnector
import io.realm.kotlin.mongodb.App
import io.realm.kotlin.mongodb.User
import kotlinx.coroutines.launch

class ResetPasswordSendEmail : AppCompatActivity() {

    private lateinit var email: String
    private lateinit var emailInput: EditText
    private val app: App = AppConnector.app
    private var user: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password_email)

        user = app.currentUser
        emailInput = findViewById(R.id.inputEmail)

        val resetPasswordButton: Button = findViewById(R.id.resetBtn)
        resetPasswordButton.setOnClickListener {
            lifecycleScope.launch {
                checkInputs()
            }
        }
    }

    private suspend fun checkInputs() {
        email = emailInput.text.toString()

        // Verify that all fields were entered
        if (email.isEmpty()) {
            Toast.makeText(this, "Los datos están incompletos", Toast.LENGTH_LONG).show()
        } else {
            app.emailPasswordAuth.sendResetPasswordEmail(email)
            val intent = Intent(this@ResetPasswordSendEmail, ResetPassword::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        }
    }

//    private suspend fun resetPassword(newPassword: String) {
//        lifecycleScope.launch {
//            runCatching {
//                app.emailPasswordAuth.sendResetPasswordEmail(email)
//            }.onSuccess {
//                val intent = Intent(this@ResetPasswordSendEmail, StartingPage::class.java)
//                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
//                startActivity(intent)
//            }.onFailure {
//                Toast.makeText(this@ResetPasswordSendEmail, it.message, Toast.LENGTH_LONG).show()
//            }
//        }
//    }
}