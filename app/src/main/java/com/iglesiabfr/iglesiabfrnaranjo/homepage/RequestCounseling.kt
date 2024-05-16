package com.iglesiabfr.iglesiabfrnaranjo.homepage

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.iglesiabfr.iglesiabfrnaranjo.R
import com.iglesiabfr.iglesiabfrnaranjo.database.DatabaseConnector
import com.iglesiabfr.iglesiabfrnaranjo.schema.CounselingSession
import com.iglesiabfr.iglesiabfrnaranjo.schema.UserData
import io.realm.kotlin.ext.query
import io.realm.kotlin.mongodb.User
import io.realm.kotlin.types.RealmInstant
import kotlinx.coroutines.launch

class RequestCounseling : Fragment() {

    private var user : User? = DatabaseConnector.getLogCurrent()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_request_counseling, container, false)
        val requestBtn: Button = view.findViewById(R.id.requestBtn)
        requestBtn.setOnClickListener {
            requestSession()
        }

        return view
    }

    private fun requestSession() {
        val currentEmail = DatabaseConnector.getCurrentEmail()
        lifecycleScope.launch {
            val userQuery =
                user?.let { DatabaseConnector.db.query<UserData>("email == $0", currentEmail).find().firstOrNull() }

            try {
                DatabaseConnector.db.write {
                    if (userQuery != null) {
                        val event = CounselingSession().apply {
                            findLatest(userQuery)
                                ?. let {
                                    user = it
                                }
                                postDateTime = RealmInstant.now()
                                scheduled = false
                        }
                        copyToRealm(event)
                    }
                }
                Toast.makeText(
                    requireContext(),
                    R.string.counselingSuccessRequest,
                    Toast.LENGTH_SHORT
                ).show()

            } catch (e: Exception) {
                Log.d("ERROR", e.toString())
                Toast.makeText(
                    requireContext(),
                    R.string.counselingErrorRequest,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}
