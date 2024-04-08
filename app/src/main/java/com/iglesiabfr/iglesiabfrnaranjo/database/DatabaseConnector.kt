package com.iglesiabfr.iglesiabfrnaranjo.database

import android.util.Log
import com.iglesiabfr.iglesiabfrnaranjo.schema.Activity
import com.iglesiabfr.iglesiabfrnaranjo.schema.Event
import io.realm.kotlin.Realm
import io.realm.kotlin.ext.query
import io.realm.kotlin.mongodb.App
import io.realm.kotlin.mongodb.Credentials
import io.realm.kotlin.mongodb.User
import io.realm.kotlin.mongodb.exceptions.SyncException
import io.realm.kotlin.mongodb.subscriptions
import io.realm.kotlin.mongodb.sync.SyncConfiguration
import io.realm.kotlin.mongodb.sync.SyncSession
import kotlinx.coroutines.runBlocking

object DatabaseConnector {
    private val app : App = App.create("iglesiabfr-pigqi")
    lateinit var db : Realm

    private fun getLogCurrent() : User {
        return app.currentUser!!
    }

    private suspend fun logAnonymous(): User {
        return app.login(Credentials.anonymous())
    }

    fun connect() {
        runBlocking {
            runCatching {
                logAnonymous()
            }.onSuccess {
                Log.d("Info","Sync Started")
                val config = SyncConfiguration.Builder(it, setOf(Activity::class,Event::class))
                    .initialSubscriptions(rerunOnOpen = true) {realm->
                        add(realm.query<Event>(), "subEvent",updateExisting = true)
                        add(realm.query<Activity>(), "subActivity",updateExisting = true)
                    }
                    .errorHandler { session: SyncSession, error: SyncException ->
                        Log.d("IglesiaError",error.message.toString())
                    }
                    .waitForInitialRemoteData()
                    .build()
                db = Realm.open(config)
                db.subscriptions.waitForSynchronization()
                Log.d("IglesiaInfo","Sync Finished")
            }.onFailure {
                Log.d("IglesiaError",it.message.toString())
            }
        }
    }
}