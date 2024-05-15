package com.iglesiabfr.iglesiabfrnaranjo.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
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

class ResetPassword : AppCompatActivity() {

    private lateinit var link: String
    private lateinit var password1: EditText
    private lateinit var password2: EditText
    private lateinit var linkInput: EditText
    private val app: App = AppConnector.app
    private var user: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password)

        user = app.currentUser
        password1 = findViewById(R.id.inputNewPassword)
        password2 = findViewById(R.id.inputNewPasswordConfirm)
        linkInput = findViewById(R.id.inputLink)

        val resetPasswordButton: Button = findViewById(R.id.resetBtn)
        resetPasswordButton.setOnClickListener {
            lifecycleScope.launch {
                checkInputs()
            }
        }
    }

    private fun checkInputs() {
        val password1Text = password1.text.toString()
        val password2Text = password2.text.toString()
        link = linkInput.text.toString()

        // Verify that all fields were entered
        if (password1Text.isEmpty() || password2Text.isEmpty() || link.isEmpty()) {
            Toast.makeText(this, "Los datos están incompletos", Toast.LENGTH_LONG).show()
        } else {
            // Verify that both passwords match
            if (password1Text != password2Text) {
                Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_LONG).show()
            } else {
                val (token, tokenId) = parseLink(link) ?: run {
                    println("Unable to parse URL")
                    return
                }
                Log.d("TOKEN","Token: $token")
                Log.d("TOKEN ID", "Token ID: $tokenId")
                resetPassword(token, tokenId, password1Text)
            }
        }
    }

    private fun parseLink(url: String): Pair<String?, String?>? {
        // Define the regular expression pattern to match the token and tokenId
        val pattern = Regex("""token=([^&]+)&tokenId=([^&]+)""")

        // Match the pattern against the URL
        val matchResult = pattern.find(url)

        // If there's a match, extract the token and tokenId and return them
        return matchResult?.let {
            val token = it.groups[1]?.value
            val tokenId = it.groups[2]?.value
            token to tokenId
        }
    }

    private fun  resetPassword(token: String?, tokenId: String?, newPassword: String) {
        lifecycleScope.launch {
            runCatching {
                if (token != null) {
                    if (tokenId != null) {
                        app.emailPasswordAuth.resetPassword(token, tokenId, newPassword)
                    }
                }
            }.onSuccess {
                Toast.makeText(this@ResetPassword, R.string.resetPassSuccess, Toast.LENGTH_LONG).show()
                val intent = Intent(this@ResetPassword, StartingPage::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
            }.onFailure {
                Toast.makeText(this@ResetPassword, it.message, Toast.LENGTH_LONG).show()
            }
        }
    }
}