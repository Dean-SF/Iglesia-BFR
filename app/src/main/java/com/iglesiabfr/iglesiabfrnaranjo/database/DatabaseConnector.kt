package com.iglesiabfr.iglesiabfrnaranjo.database

import android.util.Log
import androidx.lifecycle.LifecycleCoroutineScope
import com.iglesiabfr.iglesiabfrnaranjo.schema.Activity
import com.iglesiabfr.iglesiabfrnaranjo.schema.CounselingSession
import com.iglesiabfr.iglesiabfrnaranjo.schema.Cult
import com.iglesiabfr.iglesiabfrnaranjo.schema.Emotion
import com.iglesiabfr.iglesiabfrnaranjo.schema.Event
import com.iglesiabfr.iglesiabfrnaranjo.schema.Suggestion
import com.iglesiabfr.iglesiabfrnaranjo.schema.UserData
import io.realm.kotlin.Realm
import io.realm.kotlin.ext.query
import io.realm.kotlin.mongodb.Credentials
import io.realm.kotlin.mongodb.User
import io.realm.kotlin.mongodb.exceptions.SyncException
import io.realm.kotlin.mongodb.subscriptions
import io.realm.kotlin.mongodb.sync.SyncConfiguration
import io.realm.kotlin.mongodb.sync.SyncSession
import kotlinx.coroutines.launch

object DatabaseConnector {
    lateinit var db : Realm
    var email = ""
    private var isAdmin : Boolean = false
    lateinit var credentials: Credentials
    private var userData: UserData? = null

    private var onFinished: ((Boolean) -> Unit)? = null

    fun getLogCurrent() : User {
        return AppConnector.app.currentUser!!
    }

    fun getCurrentEmail() : String {
        return email
    }

    fun getIsAdmin() : Boolean {
        return isAdmin
    }

    fun setIsAdmin() {
        this.isAdmin = getUserData()?.isAdmin == true
    }

    fun getUserData(): UserData? {
        return userData
    }

    fun setUserData() {
        userData = getLogCurrent().let { db.query<UserData>("email == $0", email).find().firstOrNull() }
    }

    private suspend fun logAnonymous(): User {
        return AppConnector.app.login(Credentials.anonymous())
    }

    fun setOnFinishedListener(listener : ((Boolean)->Unit)) : DatabaseConnector {
        onFinished = listener
        return this
    }

    fun connect(lifecyclescope: LifecycleCoroutineScope) {
        lifecyclescope.launch {
            runCatching {
                getLogCurrent()
            }.onSuccess {
                Log.d("Info", "Sync Started")
                val config = SyncConfiguration.Builder(
                    it, setOf(
                        Event::class,
                        Activity::class,
                        UserData::class,
                        Cult::class,
                        Suggestion::class,
                        Emotion::class,
                        CounselingSession::class
                ))
                    .initialSubscriptions(rerunOnOpen = true) {realm->
                        add(realm.query<Event>(), "subEvent",updateExisting = true)
                        add(realm.query<Cult>(), "subCult",updateExisting = true)
                        add(realm.query<Activity>(), "subActivity",updateExisting = true)
                        add(realm.query<UserData>(), "userData",updateExisting = true)
                        add(realm.query<Suggestion>(), "suggestion",updateExisting = true)
                        add(realm.query<Emotion>(), "emotion",updateExisting = true)
                        add(realm.query<CounselingSession>(), "counsellingSession",updateExisting = true)
                    }
                    .errorHandler { session: SyncSession, error: SyncException ->
                        Log.d("IglesiaError", error.message.toString())
                    }
                    //.waitForInitialRemoteData()
                    .build()
                db = Realm.open(config)
                db.subscriptions.waitForSynchronization()
                onFinished?.invoke(true)
                Log.d("IglesiaInfo", "Sync Finished")
            }.onFailure {
                onFinished?.invoke(false)
                Log.d("IglesiaError", it.message.toString())
            }
        }
    }
}