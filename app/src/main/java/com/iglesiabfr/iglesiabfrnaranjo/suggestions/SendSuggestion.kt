package com.iglesiabfr.iglesiabfrnaranjo.suggestions

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.iglesiabfr.iglesiabfrnaranjo.R
import com.iglesiabfr.iglesiabfrnaranjo.database.DatabaseConnector
import com.iglesiabfr.iglesiabfrnaranjo.dialogs.ConfirmDialog
import com.iglesiabfr.iglesiabfrnaranjo.dialogs.LoadingDialog
import com.iglesiabfr.iglesiabfrnaranjo.homepage.Homepage
import com.iglesiabfr.iglesiabfrnaranjo.schema.Suggestion
import com.iglesiabfr.iglesiabfrnaranjo.schema.UserData
import io.realm.kotlin.ext.query
import io.realm.kotlin.mongodb.User
import io.realm.kotlin.types.RealmInstant
import kotlinx.coroutines.launch

class SendSuggestion : AppCompatActivity() {

    private var user : User? = null
    private lateinit var email: String
    private lateinit var loadingDialog : LoadingDialog
    private lateinit var confirmDialog : ConfirmDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mandar_sugerencias)
        loadingDialog = LoadingDialog(this)
        confirmDialog = ConfirmDialog(this)
        user = DatabaseConnector.getLogCurrent()
        email = DatabaseConnector.getCurrentEmail()

        val sendBtn: Button = findViewById(R.id.enviarbtn)
        sendBtn.setOnClickListener {
            checkInputs()
        }
    }

    // Llamada de ventanas
    private fun callMainMenu(){
        loadingDialog.stopLoading()
        val intent = Intent(this, Homepage::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }

    private fun checkInputs() {
        loadingDialog.startLoading()
        val suggestionInput: EditText = findViewById(R.id.sugerenciaInput)
        val suggestion = suggestionInput.text.toString().trim()

        if (suggestion.isEmpty()) {
            loadingDialog.stopLoading()
            Toast.makeText(this, R.string.warningSugerencia, Toast.LENGTH_SHORT).show()
            return
        } else {
            confirmDialog.confirmation(getString(R.string.confirmSugerencia))
                .setOnConfirmationListener {
                    sendSuggestion(suggestion)
                }
        }
    }

    private fun sendSuggestion(suggestionInput: String) {
        lifecycleScope.launch {
            val userQuery = user?.let { DatabaseConnector.db.query<UserData>("email == $0", email).find().firstOrNull() }

            if (user == null || userQuery == null) {
                loadingDialog.stopLoading()
                Toast.makeText(
                    this@SendSuggestion,
                    R.string.errorSugerencia,
                    Toast.LENGTH_LONG
                ).show()
                return@launch
            } else {
                val event = Suggestion().apply {
                    suggestion = suggestionInput
                    dateSent = RealmInstant.now()
                }
                DatabaseConnector.db.writeBlocking {
                    copyToRealm(event)
                }
                loadingDialog.stopLoading()
                Toast.makeText(
                    this@SendSuggestion,
                    R.string.exitoSugerencia,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}