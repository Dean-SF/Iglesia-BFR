package com.iglesiabfr.iglesiabfrnaranjo.homepage

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.iglesiabfr.iglesiabfrnaranjo.R
import com.iglesiabfr.iglesiabfrnaranjo.database.DatabaseConnector
import com.iglesiabfr.iglesiabfrnaranjo.dialogs.ConfirmDialog
import com.iglesiabfr.iglesiabfrnaranjo.dialogs.LoadingDialog
import com.iglesiabfr.iglesiabfrnaranjo.login.ResetPassword
import com.iglesiabfr.iglesiabfrnaranjo.picker.CustomDatePicker
import com.iglesiabfr.iglesiabfrnaranjo.schema.UserData
import io.realm.kotlin.ext.query
import io.realm.kotlin.mongodb.User
import io.realm.kotlin.types.RealmInstant
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

class EditProfile : AppCompatActivity() {

    private var user : User? = null
    private lateinit var email: String
    private lateinit var confirmDialog : ConfirmDialog
    private lateinit var loadingDialog : LoadingDialog
    private var fullBirthdate = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)
        loadingDialog = LoadingDialog(this)
        confirmDialog = ConfirmDialog(this)
        this.email = DatabaseConnector.email
        this.user = DatabaseConnector.getLogCurrent()

        val changePasswordText: TextView = findViewById(R.id.changePasswordlb)
        changePasswordText.setOnClickListener {
            confirmDialog.confirmation(getString(R.string.cambiarContra))
                .setOnConfirmationListener {
                    callResetPassword()
                }
        }

        val confirmBtn: TextView = findViewById(R.id.confirmBtn)
        confirmBtn.setOnClickListener {
            confirmDialog.confirmation(getString(R.string.confirmarCambios))
                .setOnConfirmationListener {
                    updateUserData()
                }
        }

        val customDatePicker = CustomDatePicker(false)

        customDatePicker.setOnPickListener { localdate, s ->
            val formattedDate = localdate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
            this.fullBirthdate = formattedDate + "T00:00:00"
        }

        val datePicker: ImageButton = findViewById(R.id.datePicker)
        datePicker.setOnClickListener {
            customDatePicker.show(supportFragmentManager,"tag")
        }
    }

    private fun callResetPassword() {
        val intent = Intent(this, ResetPassword::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }

    private fun callMyProfile() {
        val intent = Intent(this, MyProfile::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }

    private fun updateUserData() {
        loadingDialog.startLoading()
        val nameInput: EditText = findViewById(R.id.nameInput)
        val newName = nameInput.text.toString()
        val userQuery = user?.let { DatabaseConnector.db.query<UserData>("email == $0", email).find().firstOrNull() }

        if (newName.isNotEmpty()) {
            lifecycleScope.launch {
                runCatching {
                    DatabaseConnector.db.write {
                        if (userQuery != null) {
                            findLatest(userQuery)
                                ?.let {
                                    it.name = newName
                                }
                        }
                    }
                }.onSuccess {
                    loadingDialog.stopLoading()
                    callMyProfile()
                }.onFailure {
                    loadingDialog.stopLoading()
                    Toast.makeText(this@EditProfile,getString(R.string.errorName), Toast.LENGTH_SHORT).show()
                }
            }
        }

        if (fullBirthdate.isNotEmpty()) {
            lifecycleScope.launch {
                lifecycleScope.launch {
                    runCatching {
                        DatabaseConnector.db.write {
                            if (userQuery != null) {
                                findLatest(userQuery)
                                    ?.let {
                                        val localDP = LocalDateTime.parse(fullBirthdate)
                                        val realmDP = RealmInstant.from(localDP.toEpochSecond(ZoneOffset.UTC), localDP.nano)
                                        it.birthdate = realmDP
                                    }
                            }
                        }
                    }.onSuccess {
                        loadingDialog.stopLoading()
                        callMyProfile()
                    }.onFailure {
                        loadingDialog.stopLoading()
                        Toast.makeText(this@EditProfile,getString(R.string.errorBirthdate), Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        loadingDialog.stopLoading()
        callMyProfile()
    }
}