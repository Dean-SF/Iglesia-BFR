package com.iglesiabfr.iglesiabfrnaranjo.homepage

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.iglesiabfr.iglesiabfrnaranjo.R
import com.iglesiabfr.iglesiabfrnaranjo.database.AppConnector.app
import com.iglesiabfr.iglesiabfrnaranjo.database.DatabaseConnector
import com.iglesiabfr.iglesiabfrnaranjo.dialogs.ConfirmDialog
import com.iglesiabfr.iglesiabfrnaranjo.login.ResetPasswordSendEmail
import com.iglesiabfr.iglesiabfrnaranjo.login.StartingPage
import com.iglesiabfr.iglesiabfrnaranjo.schema.UserData
import io.realm.kotlin.ext.query
import io.realm.kotlin.mongodb.User
import kotlinx.coroutines.launch
import java.io.File
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class MyProfile : AppCompatActivity() {

    private var user : User? = null
    private lateinit var email: String
    private lateinit var confirmDialog : ConfirmDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!DatabaseConnector.getIsAdmin()) {
            setContentView(R.layout.activity_my_profile)
        } else {
            setContentView(R.layout.activity_admin_profile)
            val openFragBtn: TextView = findViewById(R.id.givePermissionBtn)
            openFragBtn.setOnClickListener {
                val intent = Intent(this, AdminPermissions::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
            }
        }

        confirmDialog = ConfirmDialog(this)
        user = DatabaseConnector.getLogCurrent()
        email = DatabaseConnector.email
        getUserData()

        val editBtn: ImageView = findViewById(R.id.editBtn)
        editBtn.setOnClickListener {
            callEditProfile()
        }

        val logOut: TextView = findViewById(R.id.logOut)
        logOut.setOnClickListener {
            confirmDialog.confirmation(getString(R.string.cerrarSesion))
                .setOnConfirmationListener {
                    logOut()
                }
        }

        val resetPassword: TextView = findViewById(R.id.changePasswordlb)
        resetPassword.setOnClickListener {
            confirmDialog.confirmation(getString(R.string.cambiarContra))
                .setOnConfirmationListener {
                    callResetPassword()
                }
        }

        val deleteAccount: TextView = findViewById(R.id.deleteAccount)
        deleteAccount.setOnClickListener {
            confirmDialog.confirmation(getString(R.string.borrarCuenta))
                .setOnConfirmationListener {
                    deleteAccount()
                }
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
                applicationContext.deleteDataStoreFile("credentials.preferences_pb")
                val intent = Intent(this@MyProfile, StartingPage::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
            }.onFailure {
                Toast.makeText(this@MyProfile, "Hubo un error al cerrar sesión", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun Context.deleteDataStoreFile(fileName: String) {
        val dataStoreDirectory = File("/data/data/com.iglesiabfr.iglesiabfrnaranjo/files/datastore/")
        if (dataStoreDirectory.exists() && dataStoreDirectory.isDirectory) {
            val fileList = dataStoreDirectory.listFiles()
            fileList?.forEach { file ->
                if (file.name == fileName) {
                    val deleted = file.delete()
                    if (deleted) {
                        Log.d("RememberSession", "El archivo $fileName se eliminó con éxito.")
                    } else {
                        Log.d("RememberSession", "No se pudo eliminar el archivo $fileName.")
                    }
                    return@forEach
                }
            }
        } else {
            Log.d("RememberSession", "El directorio de datastore no existe o no es un directorio.")
        }
    }

    private fun callResetPassword() {
        val intent = Intent(this, ResetPasswordSendEmail::class.java)
        startActivity(intent)
    }

    private fun deleteAccount() {
        val userQuery = user?.let { DatabaseConnector.db.query<UserData>("email == $0", email).find().firstOrNull() }

        lifecycleScope.launch {
            runCatching {
                app.login(DatabaseConnector.credentials)
                DatabaseConnector.db.writeBlocking {
                    if (userQuery != null) {
                        findLatest(userQuery)
                            ?.also { delete(it) }
                    }
                }
                user?.delete()
            }.onSuccess {
                val intent = Intent(this@MyProfile, StartingPage::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
            }.onFailure { error ->
                error.printStackTrace()
                Toast.makeText(this@MyProfile, "Hubo un error al eliminar cuenta", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun getUserData() {
        val userQuery = user?.let { DatabaseConnector.db.query<UserData>("email == $0", email).find().first() }
        if (userQuery == null) {
            Toast.makeText(this,"Error al obtener información del usuario",Toast.LENGTH_SHORT).show()
            finish()

        }

        val username = userQuery?.name.toString()
        val userBirthdate = userQuery?.birthdate

        val localDateTime = userBirthdate?.let {
            LocalDateTime.ofInstant(Instant.ofEpochSecond(it.epochSeconds), ZoneId.of("UTC"))
        }

        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        val formattedDate = localDateTime?.format(formatter)

        findViewById<TextView>(R.id.nameInfo).text = username
        findViewById<TextView>(R.id.emailInfo).text = email
        findViewById<TextView>(R.id.birthdateInfo).text = formattedDate
    }
}
