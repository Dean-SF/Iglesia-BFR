package com.iglesiabfr.iglesiabfrnaranjo.homepage1

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.iglesiabfr.iglesiabfrnaranjo.R
import com.iglesiabfr.iglesiabfrnaranjo.database.DatabaseConnector
import com.iglesiabfr.iglesiabfrnaranjo.login.ResetPassword
import com.iglesiabfr.iglesiabfrnaranjo.login.StartingPage
import com.iglesiabfr.iglesiabfrnaranjo.schema.UserData
import io.realm.kotlin.ext.query
import io.realm.kotlin.mongodb.User
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class MyProfile : AppCompatActivity() {

    private var user : User? = null
    private lateinit var email: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_profile)

        DatabaseConnector.connect()
        user = DatabaseConnector.getLogCurrent()
        email = DatabaseConnector.email
        getUserData()

        val editBtn: ImageView = findViewById(R.id.editBtn)
        editBtn.setOnClickListener {
            callEditProfile()
        }

        val logOut: TextView = findViewById(R.id.logOut)
        logOut.setOnClickListener {
            logOut()
        }

        val resetPassword: TextView = findViewById(R.id.changePasswordlb)
        resetPassword.setOnClickListener {
            callResetPassword()
        }

        val deleteAccount: TextView = findViewById(R.id.deleteAccount)
        deleteAccount.setOnClickListener {
            showConfirmationDialog()
        }
    }

    private fun callEditProfile() {
        val intent = Intent(this, EditProfile::class.java)
        startActivity(intent)
    }

    private fun logOut() {
        lifecycleScope.launch {
            runCatching {
                user?.logOut()
            }.onSuccess {
                val intent = Intent(this@MyProfile, StartingPage::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
            }.onFailure {
                Toast.makeText(this@MyProfile, "Hubo un error al cerrar sesión", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun callResetPassword() {
        val intent = Intent(this, ResetPassword::class.java)
        startActivity(intent)
    }

    private fun deleteAccount() {
        val userQuery = user?.let { DatabaseConnector.db.query<UserData>("email == $0", email).find().first() }

        lifecycleScope.launch {
            runCatching {
                DatabaseConnector.db.write {
                    if (userQuery != null) {
                        findLatest(userQuery)
                            ?.also { delete(it) }
                    }
                }

                user?.remove()
            }.onSuccess {
                val intent = Intent(this@MyProfile, StartingPage::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
            }.onFailure {
                Toast.makeText(this@MyProfile, "Hubo un error al eliminar cuenta", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun showConfirmationDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("¿Está seguro que desea eliminar su cuenta?")
        builder.setMessage("¿Desea continuar con esta acción?")

        builder.setPositiveButton("Sí") { dialog, which ->
            deleteAccount()
        }

        builder.setNegativeButton("No") { dialog, which ->
            dialog.dismiss()
        }

        builder.show()
    }

    private fun getUserData() {
        val userQuery = user?.let { DatabaseConnector.db.query<UserData>("email == $0", email).find() }
        if (userQuery != null) {
            if(userQuery.isEmpty()) {
                Toast.makeText(this,"Error al obtener información del usuario",Toast.LENGTH_SHORT).show()
                finish()
            }
        }

        val userData = userQuery?.get(0)
        val username = userData?.name.toString()
        val userBirthdate = userData?.birthdate

        val localDateTime = LocalDateTime.ofInstant(
            userBirthdate?.let { Instant.ofEpochSecond(it.epochSeconds) },
            ZoneId.systemDefault()
        )

        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        val formattedDate = localDateTime.format(formatter)

        findViewById<TextView>(R.id.nameInfo).text = username
        findViewById<TextView>(R.id.emailInfo).text = email
        findViewById<TextView>(R.id.birthdateInfo).text = formattedDate
    }
}
