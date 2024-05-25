package com.iglesiabfr.iglesiabfrnaranjo.homepage

import UserAdapter
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.iglesiabfr.iglesiabfrnaranjo.R
import com.iglesiabfr.iglesiabfrnaranjo.database.DatabaseConnector
import com.iglesiabfr.iglesiabfrnaranjo.dialogs.ConfirmDialog
import com.iglesiabfr.iglesiabfrnaranjo.dialogs.LoadingDialog
import com.iglesiabfr.iglesiabfrnaranjo.schema.UserData
import io.realm.kotlin.ext.query
import io.realm.kotlin.query.RealmResults
import kotlinx.coroutines.launch

class AdminPermissions : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var confirmDialog : ConfirmDialog
    private lateinit var loadingDialog : LoadingDialog
    private var modeGivePermissions = true
    private lateinit var allUsers: RealmResults<UserData>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_permissions)
        loadingDialog = LoadingDialog(this)
        confirmDialog = ConfirmDialog(this)

        recyclerView = findViewById(R.id.userList)
        recyclerView.layoutManager = LinearLayoutManager(this)

        allUsers = DatabaseConnector.db.query<UserData>("isAdmin == $0", false).find()
        var adapter = UserAdapter(allUsers, object : UserAdapter.OnItemClickListener {
            override fun onItemClick(user: UserData) {
                confirmDialog.confirmation(getString(R.string.confirmNewAdmin))
                    .setOnConfirmationListener {
                        toggleAdmin(user)
                    }
            }
        })
        recyclerView.adapter = adapter

        val permissionMode = findViewById<TextView>(R.id.permissionMode)
        permissionMode.text = getString(R.string.mode2)

        val switchPermission = findViewById<SwitchCompat>(R.id.switchPermission)
        switchPermission.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {    // Para quitar permisos muestra los que son admin
                modeGivePermissions = false
                permissionMode.text = getString(R.string.mode1)
                allUsers = DatabaseConnector.getUserData()?.let { DatabaseConnector.db.query<UserData>("isAdmin == $0 AND _id != $1", true, it._id).find() }!!
                adapter = UserAdapter(allUsers, object : UserAdapter.OnItemClickListener {
                    override fun onItemClick(user: UserData) {
                        confirmDialog.confirmation(getString(R.string.deleteAdmin))
                            .setOnConfirmationListener {
                                toggleAdmin(user)
                            }
                    }
                })
                recyclerView.adapter = adapter
            } else {        // Para dar permisos muestra los que no son admin
                modeGivePermissions = true
                permissionMode.text = getString(R.string.mode2)
                allUsers = DatabaseConnector.db.query<UserData>("isAdmin == $0", false).find()
                adapter = UserAdapter(allUsers, object : UserAdapter.OnItemClickListener {
                    override fun onItemClick(user: UserData) {
                        confirmDialog.confirmation(getString(R.string.confirmNewAdmin))
                            .setOnConfirmationListener {
                                toggleAdmin(user)
                            }
                    }
                })
                recyclerView.adapter = adapter
            }
        }
    }

    // Funcion que quita o brinda permisos segun el modo seleccionado
    private fun toggleAdmin(user: UserData) {
        loadingDialog.startLoading()
        val userQuery = DatabaseConnector.getLogCurrent().let {
            DatabaseConnector.db.query<UserData>("name == $0", user.name).find().firstOrNull() }

        lifecycleScope.launch {
            runCatching {
                DatabaseConnector.db.write {
                    if (userQuery != null) {
                        findLatest(userQuery)
                            ?.let {
                                it.isAdmin = modeGivePermissions
                            }
                    }
                }
            }.onSuccess {
                loadingDialog.stopLoading()
                callMyProfile()
            }.onFailure {
                loadingDialog.stopLoading()
                Toast.makeText(this@AdminPermissions,getString(R.string.errorNewAdmin), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun callMyProfile() {
        val intent = Intent(this, MyProfile::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }
}